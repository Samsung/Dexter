/**
 * Copyright (c) 2014 Samsung Electronics, Inc.,
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.samsung.sec.dexter.cppcheck.plugin;

import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.checker.CheckerConfig;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.DexterUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

public class CppcheckWrapper {
    private CheckerConfig checkerConfig = new CheckerConfig(CppcheckDexterPlugin.PLUGIN_NAME,
            DexterConfig.LANGUAGE.CPP);
    private AnalysisConfig config;
    public final static String CPPCHECK_HOME_DIR = "/bin/cppcheck";

    private final static Logger logger = Logger.getLogger(CppcheckWrapper.class);

    /**
     * @return CheckerConfig
     */
    public CheckerConfig getCheckerConfig() {
        if (checkerConfig == null) {
            initCheckerConfig();
        }

        return checkerConfig;
    }

    /**
     * run static analysis
     * 
     * ## Other Options that we did not use ##
     * cmd.append(" -j 4 ");
     * cmd.append(" -f ");
     * String cfgFile = dexterHome + CPPCHECK_HOME_DIR + "/cfg/" + CPPCHECK_CFG_FILE;
     * cmd.append(" --library=").append(cfgFile).append(" ");
     * cmd.append(" 2> ").append(resultFile);
     * 
     * -rp=<path> --rule=<rule>, --rule-file=<file>, --template, -D占쎈��놅옙占�-U占쎈챶逾믭옙��뵥
     * 
     * @param result
     * void
     * @throws Exception
     */
    public void analyze(final AnalysisResult result) {
        logger.debug(result.getFileName() + "is being analyzed");

        Stopwatch sw = Stopwatch.createStarted();
        // 1. Read AnalysisConfig and initialize AnalysisResult
        initializeAnalysisResult(result);

        // 2. Execute at CMD
        final String sourceFileFullPath = this.config.getSourceFileFullPath();

        // 3. Create Command
        final StringBuilder cmd = new StringBuilder(1024);

        setCppcheckCommand(cmd);
        if (DexterUtil.getOsBit() == DexterUtil.OS_BIT.WIN32 || DexterUtil.getOsBit() == DexterUtil.OS_BIT.WIN64) {
            setCustomRuleOption(cmd);
        }
        cmd.append(" --inconclusive "); // for unreachableCode
        cmd.append(" --enable=all --xml --xml-version=2 --report-progress -v ");
        cmd.append(" --std=posix --std=c++03 "); // posix | c89 | c99 | c11 | c++03 | c++11 => VD - C++98
        cmd.append("  --suppress=missingInclude ");
        // cmd.append(" --template={file}:::{line}:::{severity}:::{id}:::{message} ");

        cmd.append(sourceFileFullPath);
        setPlatformOption(cmd);
        setLanguageOption(cmd);
        setHeaderFilesOption(cmd);

        System.out.println(cmd);
        System.out.println("created cmd : " + sw.elapsed(TimeUnit.MILLISECONDS));

        // 4. Run Command
        Process process = null;
        try {
            sw = Stopwatch.createStarted();
            process = Runtime.getRuntime().exec(cmd.toString());
            System.out.println("cmd exec : " + sw.elapsed(TimeUnit.MILLISECONDS));

            sw = Stopwatch.createStarted();
            analysisResultFile(process.getErrorStream(), result);
            System.out.println("result handle : " + sw.elapsed(TimeUnit.MILLISECONDS));

            /* TODO : use template instead of xml parsing
             --template='<text>'
               Format the error messages. E.g.
               '{file}:{line},{severity},{id},{message}' or
               '{file}({line}):({severity}) {message}'. Pre-defined templates:
               gcc, vs
             */

            logger.debug(result.getFileName() + " is analyzed completely.");

        } catch (IOException e) {
            throw new DexterRuntimeException(e.getMessage() + " cmd: " + cmd.toString(), e);
        } catch (Exception e) {
            throw new DexterRuntimeException(e.getMessage()
                    + "use the following command. if you see the error of absence of MSVCP120.dll. you have to install MS Visual C++ 2013 Redistributeable first.  cmd: "
                    + cmd.toString(), e);
        } finally {
            if (process != null) {
                try {
                    process.getErrorStream().close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
                process.destroy();
            }
        }
    }

    private void setCppcheckCommand(final StringBuilder cmd) {
        final String dexterHome = DexterConfig.getInstance().getDexterHome();
        final String tempFolder = dexterHome + DexterUtil.FILE_SEPARATOR + "temp";

        if ((new File(tempFolder)).exists() == false) {
            if (new File(tempFolder).mkdir() == false) {
                throw new DexterRuntimeException("Can't create temp folder to save cppcheck result: " + tempFolder);
            }
        }

        final String cppcheckHome = dexterHome + DexterUtil.FILE_SEPARATOR + "bin" + DexterUtil.FILE_SEPARATOR
                + "cppcheck";
        if (new File(cppcheckHome).exists() == false) {
            throw new DexterRuntimeException("There is no cppcheck home folder : " + cppcheckHome);
        }

        if (DexterUtil.getOsBit() == DexterUtil.OS_BIT.WIN32 || DexterUtil.getOsBit() == DexterUtil.OS_BIT.WIN64) {
            cmd.append("cmd /C ").append(cppcheckHome).append(DexterUtil.FILE_SEPARATOR).append("cppcheck");
        } else if (DexterUtil.getOsBit() == DexterUtil.OS_BIT.LINUX32
                || DexterUtil.getOsBit() == DexterUtil.OS_BIT.LINUX64) {
            cmd.append(cppcheckHome).append(DexterUtil.FILE_SEPARATOR).append("cppcheck");
        } else {
            throw new DexterRuntimeException("This command supports only Windows and Linux('bin/bash')");
        }

    }

    private void setCustomRuleOption(final StringBuilder cmd) {
        final String dexterHome = DexterConfig.getInstance().getDexterHome();
        final String cppcheckHome = dexterHome + DexterUtil.FILE_SEPARATOR + "bin" + DexterUtil.FILE_SEPARATOR
                + "cppcheck";

        final String customRuleFileName = "custom_rule.xml";
        cmd.append(" --rule-file=");
        cmd.append(cppcheckHome).append(DexterUtil.FILE_SEPARATOR).append(customRuleFileName);

    }

    private void setHeaderFilesOption(final StringBuilder cmd) {
        for (final String inc : config.getHeaderBaseDirList()) {
            if (inc.length() > 0) {
                cmd.append(" -I ").append(inc).append(" ");
            }
        }
    }

    private void setLanguageOption(final StringBuilder cmd) {
        if (config.getLanguageEnum() == DexterConfig.LANGUAGE.C) {
            cmd.append(" --language=c "); // c | c++
        } else {
            cmd.append(" --language=c++ "); // c | c++
        }
    }

    private void setPlatformOption(final StringBuilder cmd) {
        if (DexterUtil.getOsBit() == DexterUtil.OS_BIT.WIN32) {
            cmd.append(" --platform=win32W "); // unix32 | unix64 | win32A | win32W | win64
        } else if (DexterUtil.getOsBit() == DexterUtil.OS_BIT.WIN64) {
            cmd.append(" --platform=win64 ");
        } else if (DexterUtil.getOsBit() == DexterUtil.OS_BIT.LINUX32) {
            cmd.append(" --platform=unix32 ");
        } else if (DexterUtil.getOsBit() == DexterUtil.OS_BIT.LINUX64) {
            cmd.append(" --platform=unix64 ");
        }
    }

    private void initializeAnalysisResult(final AnalysisResult result) {
        if (this.config == null) {
            throw new DexterRuntimeException("there is no target to analysis");
        }

        result.setSnapshotId(config.getSnapshotId());
        result.setProjectName(config.getProjectName());
        result.setAnalysisType(config.getAnalysisType());
    }

    /**
     * @param resultFilePath
     * @param result
     * void
     * @throws Exception
     */
    private void analysisResultFile(final InputStream input, final AnalysisResult result) {
        if (input == null) {
            throw new DexterRuntimeException("No Result. It can be caused by Cppcheck installation.");
        }

        File file = new File(config.getSourceFileFullPath());
        if (file.length() > DexterConfig.SOURCE_FILE_SIZE_LIMIT) {
            logger.warn("Dexter can not analyze over " + DexterConfig.SOURCE_FILE_SIZE_LIMIT
                    + " byte of file:" + config.getSourceFileFullPath() + " (" + file.length() + " byte)");

            return;
        }

        final SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser parser = null;
        spf.setNamespaceAware(false);
        spf.setValidating(true);

        try {
            parser = spf.newSAXParser();
            final ResultFileHandler handler = new ResultFileHandler(result, config, checkerConfig);

            Stopwatch sw2 = Stopwatch.createStarted();
            parser.parse(input, handler);
            System.out.println("parsing : " + sw2.elapsed(TimeUnit.MILLISECONDS));
        } catch (ParserConfigurationException e) {
            throw new DexterRuntimeException(e.getMessage(), e);
        } catch (SAXException e) {
            throw new DexterRuntimeException(e.getMessage() + ". Error XML File: " + result.getSourceFileFullPath(), e);
        } catch (IOException e) {
            throw new DexterRuntimeException(e.getMessage(), e);
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * @param config
     * void
     */
    public void setAnalysisConfig(final AnalysisConfig config) {
        this.config = config;
    }

    /**
     * @param checkerConfig
     * void
     */
    public void setCheckerConfig(final CheckerConfig checkerConfig) {
        this.checkerConfig = checkerConfig;
    }

    protected void initCheckerConfig() {
        try {
            Reader reader = new InputStreamReader(
                    this.getClass().getClassLoader().getResourceAsStream("checker-config.json"));
            Gson gson = new Gson();
            this.checkerConfig = gson.fromJson(reader, CheckerConfig.class);
        } catch (Exception e) {
            throw new DexterRuntimeException(e.getMessage(), e);
        }
    }
}
