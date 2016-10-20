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
package com.samsung.sec.dexter.executor;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.analyzer.AnalysisResultFileManager;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.DexterConfig.RunMode;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.plugin.IDexterPluginManager;
import com.samsung.sec.dexter.core.util.IDexterClient;
import com.samsung.sec.dexter.metrics.CodeMetricsGenerator;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

public class DexterAnalyzerThread extends Thread {
    private final static Logger logger = Logger.getLogger(DexterAnalyzerThread.class);

    private AnalysisConfig config;
    private IDexterPluginManager pluginManager;
    private IDexterClient client;

    public void setFields(final AnalysisConfig config, final IDexterPluginManager pluginManager,
            final IDexterClient client) {
        assert config != null;
        assert pluginManager != null;
        assert client != null;

        this.config = config;
        this.pluginManager = pluginManager;
        this.client = client;
    }

    @Override
    public void run() {
        analyze(this.config, this.pluginManager, this.client);
    }

    // TODO: Dismissed / Excluding Scope 대상 파일인지 검사
    protected void analyze(final AnalysisConfig analysisConfig, final IDexterPluginManager dexterPluginManager,
            final IDexterClient dexterClient) {
        assert analysisConfig != null;
        assert dexterPluginManager != null;

        try {
            DexterAnalyzer analyzer = DexterAnalyzer.getInstance();

            checkAnalsysConfig(analysisConfig);
            analysisConfig.addHeaderAndSourceConfiguration(analyzer.getProjectAnalysisConfigurationList());

            analyzer.preSendSourceCode(analysisConfig);
            sendSourceCode(analysisConfig, dexterClient);
            analyzer.postSendSourceCode(analysisConfig);

            analyzer.preRunCodeMetrics(analysisConfig);
            generateCodeMetrics(analysisConfig);
            analyzer.postRunCodeMetrics(analysisConfig);

            analyzer.preRunStaticAnalysis(analysisConfig);
            List<AnalysisResult> resultList = runStaticAnalysis(analysisConfig, dexterPluginManager, dexterClient);
            analyzer.postRunStaticAnalysis(analysisConfig, resultList);
        } catch (DexterRuntimeException | NoClassDefFoundError e) {
            logger.error("Dexter Analysis Failed : " + analysisConfig.getSourceFileFullPath(), e);
            logger.error(new Gson().toJson(analysisConfig));
        }
    }

    private void checkAnalsysConfig(final AnalysisConfig config) {
        if (Strings.isNullOrEmpty(config.getSourceFileFullPath()) || Strings.isNullOrEmpty(config.getFileName())) {
            throw new DexterRuntimeException(
                    "Invalid Analysis Config : fileName or sourceFileFullPath is null or empty");
        }

        final File f = new File(config.getSourceFileFullPath());
        if (f.isFile() == false || f.exists() == false || f.length() <= 0) {
            throw new DexterRuntimeException(
                    "Invalid Analysis Config : file is not exist or 0 size : " + config.getSourceFileFullPath());
        }

        if (f.length() > DexterConfig.SOURCE_FILE_SIZE_LIMIT) {
            throw new DexterRuntimeException(
                    "Dexter can't analyze a big file over " + DexterConfig.SOURCE_FILE_SIZE_LIMIT + " byte: "
                            + config.getSourceFileFullPath() + "(" + f.length() + " byte)");
        }

        if (Strings.isNullOrEmpty(config.getProjectName()) || Strings.isNullOrEmpty(config.getProjectFullPath())) {
            throw new DexterRuntimeException(
                    "Invalid Analysis Config : projectName or projectFullPath is null or empty");
        }

        final File p = new File(config.getProjectFullPath());
        if (p.isDirectory() == false || p.exists() == false) {
            throw new DexterRuntimeException(
                    "Invalid Analsis Config : project full path is not exist or not directory : "
                            + config.getProjectFullPath());
        }
    }

    // TODO 압축해서 보낼 것
    private void sendSourceCode(final AnalysisConfig config, final IDexterClient client) {
        if (isInvalidConditionToSendSourcecodes(config, client))
            return;

        try {
            client.insertSourceCodeCharSequence(config.getSnapshotId(), config.getDefectGroupId(),
                    config.getModulePath(),
                    config.getFileName(), config.getSourcecodeThatReadIfNotExist().toString());
        } catch (DexterRuntimeException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private boolean isInvalidConditionToSendSourcecodes(final AnalysisConfig config, final IDexterClient client) {
        if (config.shouldSendSourceCode() == false && config.getSnapshotId() <= 0
                && DexterConfig.getInstance().getRunMode() != RunMode.CLI) {
            return true;
        }

        if (client.isLogin() == false) {
            return true;
        }

        final File file = new File(config.getSourceFileFullPath());
        if (file.exists() == false || file.isFile() == false) {
            logger.error(
                    "cann't send a file to Dexter Server because it doesn't exist:" + config.getSourceFileFullPath());
            return true;
        }

        return false;
    }

    private void generateCodeMetrics(final AnalysisConfig analysisConfig) {
        analysisConfig.getCodeMetrics().setFileName(analysisConfig.getFileName());
        analysisConfig.getCodeMetrics().setModulePath(analysisConfig.getModulePath());
        CodeMetricsGenerator.getCodeMetrics(analysisConfig.getLanguageEnum(),
                analysisConfig.getSourceFileFullPath(),
                analysisConfig.getCodeMetrics(), analysisConfig.getFunctionMetrics(),
                analysisConfig.getFunctionList());
    }

    private List<AnalysisResult> runStaticAnalysis(final AnalysisConfig analysisConfig,
            final IDexterPluginManager dexterPluginManager, final IDexterClient dexterClient) {
        List<AnalysisResult> resultList = dexterPluginManager.analyze(analysisConfig);
        AnalysisResultFileManager.getInstance().writeJson(resultList);
        logger.info("analyzed " + analysisConfig.getSourceFileFullPath());
        analysisConfig.getResultHandler().handleAnalysisResult(resultList, dexterClient);
        return resultList;
    }
}
