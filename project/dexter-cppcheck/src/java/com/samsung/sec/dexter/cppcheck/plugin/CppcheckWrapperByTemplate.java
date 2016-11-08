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

import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.analyzer.ResultFileConstant;
import com.samsung.sec.dexter.core.checker.Checker;
import com.samsung.sec.dexter.core.checker.EmptyChecker;
import com.samsung.sec.dexter.core.checker.IChecker;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.DexterConfig.LANGUAGE;
import com.samsung.sec.dexter.core.defect.PreOccurence;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.DexterUtil;
import com.samsung.sec.dexter.util.CppUtil;
import com.samsung.sec.dexter.util.TranslationUnitFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.parser.ParserLanguage;

public class CppcheckWrapperByTemplate extends CppcheckWrapper {
    private final static Logger logger = Logger.getLogger(CppcheckWrapperByTemplate.class);

    protected void setReportTypeOption(final StringBuilder cmd) {
        cmd.append(" --template={file}:::{line}:::{severity}:::{id}:::{message} ")
                .append(" --report-progress -v ");
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

    protected void analysisResultFile(InputStream errorStream, AnalysisResult result) {
        if (errorStream == null) {
            throw new DexterRuntimeException("No Result. It can be caused by Cppcheck installation.");
        }

        if (DexterConfig.isFileSizeTooBigToAnalyze(config.getSourceFileFullPath())) {
            logger.warn("Dexter can not analyze over " + DexterConfig.SOURCE_FILE_SIZE_LIMIT
                    + " byte of file:" + config.getSourceFileFullPath());
            return;
        }

        final CharSequence sourcecode = config.getSourcecodeThatReadIfNotExist();

        IASTTranslationUnit translationUnit = TranslationUnitFactory.getASTTranslationUnit(sourcecode.toString(),
                ParserLanguage.CPP,
                config.getSourceFileFullPath());

        try (BufferedReader br = new BufferedReader(new InputStreamReader(errorStream))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                addDefect(result, line, translationUnit);
            }
        } catch (DexterRuntimeException | IOException e) {
            logger.error(e);
        }
    }

    private void addDefect(final AnalysisResult result, final String defectLine,
            final IASTTranslationUnit translationUnit) {
        final String[] defectLines = defectLine.split(":::");
        if (defectLines.length != 5) {
            if (logger.isDebugEnabled())
                logger.debug("cppcheck defect string is invalid : " + defectLine);
            return;
        }

        final String filePath = defectLines[0].intern();
        final String line = defectLines[1].intern();
        //final String cppcheckSeverity = defectLines[2];
        final String checkerCode = defectLines[3].toLowerCase();
        final String message = defectLines[4].intern();

        IChecker checker = checkerConfig.getChecker(checkerCode);
        if (checker instanceof EmptyChecker) {
            createNewChecker(checkerCode);
        }

        if (checker.isActive() == false) {
            return;
        }

        PreOccurence preOcc = createPreOccurence(result, filePath.intern(), line.intern(), checkerCode.intern(),
                message.intern(), checker, translationUnit);

        result.addDefectWithPreOccurence(preOcc);
    }

    private PreOccurence createPreOccurence(final AnalysisResult result, final String filePath, final String line,
            final String checkerCode, final String message, IChecker checker,
            final IASTTranslationUnit translationUnit) {
        PreOccurence preOcc = new PreOccurence();
        preOcc.setLanguage(LANGUAGE.CPP.toString());
        preOcc.setToolName(CppcheckDexterPlugin.PLUGIN_NAME);
        preOcc.setFileName(config.getFileName());
        preOcc.setModulePath(config.getModulePath());
        preOcc.setCheckerCode(checkerCode);
        preOcc.setSeverityCode(checker.getSeverityCode());
        preOcc.setCategoryName(checker.getCategoryName());

        if (!result.getSourceFileFullPath().equals(DexterUtil.refinePath(filePath))) {
            if (logger.isDebugEnabled()) {
                logger.debug("target file and detected file are not same");
                logger.debug("target file: " + result.getSourceFileFullPath());
                logger.debug("detected file: " + DexterUtil.refinePath(filePath));
                logger.debug(preOcc.toString());
            }

            preOcc.setStartLine(-1);
            return preOcc;
        }

        preOcc.setMessage(message);
        preOcc.setStartLine(Integer.parseInt(line));
        preOcc.setEndLine(Integer.parseInt(line));
        preOcc.setCharStart(-1);
        preOcc.setCharEnd(-1);

        final CharSequence sourcecode = config.getSourcecodeThatReadIfNotExist();

        Map<String, String> nameMap = CppUtil.extractModuleName(translationUnit, sourcecode.toString(),
                preOcc.getStartLine());

        if (Strings.isNullOrEmpty(nameMap.get(ResultFileConstant.CLASS_NAME)) == false) {
            preOcc.setClassName(nameMap.get(ResultFileConstant.CLASS_NAME));
        }

        if (Strings.isNullOrEmpty(nameMap.get(ResultFileConstant.METHOD_NAME)) == false) {
            preOcc.setMethodName(nameMap.get(ResultFileConstant.METHOD_NAME));
        }
        return preOcc;
    }

    private void createNewChecker(final String checkerCode) {
        if (DexterConfig.getInstance().isSpecifiedCheckerOptionEnabledByCli()) {
            return;
        }

        Checker checker = new Checker(checkerCode, checkerCode,
                CppcheckDexterPlugin.PLUGIN_VERSION.getVersion(), true);
        checker.setSeverityCode("ETC");
        checker.setActive(true);

        checkerConfig.addChecker(checker);
    }
}
