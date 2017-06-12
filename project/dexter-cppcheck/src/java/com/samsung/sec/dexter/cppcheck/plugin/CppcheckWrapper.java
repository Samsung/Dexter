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

import com.google.gson.Gson;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.checker.CheckerConfig;
import com.samsung.sec.dexter.core.checker.ICheckerConfig;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.DexterUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

public class CppcheckWrapper {
    protected ICheckerConfig checkerConfig = new CheckerConfig(CppcheckDexterPlugin.PLUGIN_NAME,
            DexterConfig.LANGUAGE.CPP);
    protected AnalysisConfig config;
    protected String baseCommandString = "";

    private final static Logger logger = Logger.getLogger(CppcheckWrapper.class);

    public void initBaseCommand() {
        final StringBuilder cmd = new StringBuilder(1024);

        setCppcheckCommand(cmd);
        setCustomRuleOption(cmd);
        setBasicOption(cmd);
        setCppcheckCheckersOption(cmd);
        setPlatformOption(cmd);
        cmd.trimToSize();

        baseCommandString = cmd.toString();
    }

    protected void setCppcheckCommand(final StringBuilder cmd) {
        switch (DexterUtil.getOsBit()) {
            case WIN32:
            case WIN64:
                cmd.append("cmd /C ").append(CppcheckDexterPlugin.getCppcheckHomePath())
                        .append(DexterUtil.FILE_SEPARATOR)
                        .append("cppcheck");
                break;
            case LINUX32:
            case LINUX64:
                cmd.append(CppcheckDexterPlugin.getCppcheckHomePath()).append(DexterUtil.FILE_SEPARATOR)
                        .append("cppcheck");
                break;
            default:
                throw new DexterRuntimeException("This command supports only Windows and Linux('bin/bash')");
        }
    }

    // TODO: why does it need for only windows
    protected void setCustomRuleOption(final StringBuilder cmd) {
        if (DexterUtil.getOsBit() != DexterUtil.OS_BIT.WIN32 && DexterUtil.getOsBit() != DexterUtil.OS_BIT.WIN64)
            return;

        cmd.append(" --rule-file=").append(DexterConfig.getInstance().getDexterHome())
                .append(DexterUtil.FILE_SEPARATOR).append("bin").append(DexterUtil.FILE_SEPARATOR).append("cppcheck")
                .append(DexterUtil.FILE_SEPARATOR).append("custom_rule.xml");
    }

    protected void setBasicOption(final StringBuilder cmd) {
        cmd.append(" --inconclusive ") // for unreachableCode
                .append(" --std=posix --std=c++03 ") // posix | c89 | c99 | c11 | c++03 | c++11 => VD - C++98
                .append(" --suppress=missingInclude ");
    }

    // all, warning, style, performance, portability, information, unusedFunction, missingInclude
    protected void setCppcheckCheckersOption(final StringBuilder cmd) {
        cmd.append(" --enable=all ");
    }

    protected void setPlatformOption(final StringBuilder cmd) {
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

    /**
     * @return CheckerConfig
     */
    public ICheckerConfig getCheckerConfig() {
        if (checkerConfig == null) {
            initCheckerConfig();
        }

        return checkerConfig;
    }

    /**
     * run static analysis
     * 
     * @param result
     * @throws Exception
     */
    public void analyze(final AnalysisResult result) {
        runCommand(result, makeCommandString());
    }

    /*
    * ## Other Options that we did not use ##
    * cmd.append(" -j 4 ");
    * cmd.append(" -f ");
    * String cfgFile = dexterHome + CPPCHECK_HOME_DIR + "/cfg/" + CPPCHECK_CFG_FILE;
    * cmd.append(" --library=").append(cfgFile).append(" ");
    * cmd.append(" 2> ").append(resultFile);
    * 
    * -rp=<path> --rule=<rule>, --rule-file=<file>, --template,
    */
    protected StringBuilder makeCommandString() {
        final StringBuilder cmd = new StringBuilder(1024);

        cmd.append(this.baseCommandString);
        setLanguageOption(cmd);
        setHeaderFilesOption(cmd);
        setReportTypeOption(cmd);
        setSourcecodeFullPath(cmd);

        return cmd;
    }

    protected void runCommand(final AnalysisResult result, final StringBuilder cmd) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmd.toString());
            analysisResultFile(process.getErrorStream(), result);
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

    protected void setSourcecodeFullPath(final StringBuilder cmd) {
        cmd.append(this.config.getSourceFileFullPath());
    }

    protected void setReportTypeOption(final StringBuilder cmd) {
        cmd.append(" --xml --xml-version=2 --report-progress -v ");
        // cmd.append(" --template={file}:::{line}:::{severity}:::{id}:::{message}:::{verbose} ");
    }

    protected void setHeaderFilesOption(final StringBuilder cmd) {
        for (final String inc : config.getHeaderBaseDirList()) {
            if (inc.length() > 0) {
                cmd.append(" -I ").append(inc).append(" ");
            }
        }
    }

    protected void setLanguageOption(final StringBuilder cmd) {
        if (config.getLanguageEnum() == DexterConfig.LANGUAGE.C) {
            cmd.append(" --language=c "); // c | c++
        } else {
            cmd.append(" --language=c++ "); // c | c++
        }
    }

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

            parser.parse(input, handler);
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

    public void setAnalysisConfig(final AnalysisConfig config) {
        this.config = config;
    }

    public void setCheckerConfig(final ICheckerConfig checkerConfig) {
        this.checkerConfig = checkerConfig;
    }

    protected void initCheckerConfig() {
        try {
            Reader reader = new InputStreamReader(
                    this.getClass().getClassLoader().getResourceAsStream("checker-config.json"));
            Gson gson = new Gson();
            this.checkerConfig = gson.fromJson(reader, CheckerConfig.class);
            this.checkerConfig.checkerListToMap();
        } catch (Exception e) {
            throw new DexterRuntimeException(e.getMessage(), e);
        }
    }
}
