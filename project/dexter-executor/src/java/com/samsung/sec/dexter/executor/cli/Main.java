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
package com.samsung.sec.dexter.executor.cli;

import com.google.common.base.Stopwatch;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisEntityFactory;
import com.samsung.sec.dexter.core.analyzer.EndOfAnalysisHandler;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.DexterConfig.RunMode;
import com.samsung.sec.dexter.core.config.DexterConfigFile;
import com.samsung.sec.dexter.core.config.IDexterConfigFile;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.plugin.IDexterPluginInitializer;
import com.samsung.sec.dexter.core.plugin.IDexterPluginManager;
import com.samsung.sec.dexter.core.util.DexterClient;
import com.samsung.sec.dexter.core.util.EmptyDexterClient;
import com.samsung.sec.dexter.core.util.IDexterClient;
import com.samsung.sec.dexter.executor.CLIPluginInitializer;
import com.samsung.sec.dexter.executor.DexterAnalyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class Main {
    private ICLILog cliLog = new CLILog(System.out);
    private DexterConfig config = DexterConfig.getInstance();
    private List<String> sourceFileFullPathList = new ArrayList<String>();
    private final static Logger log = Logger.getLogger(Main.class);

    private int totalSourceFileNumber = 0;

    public static void main(String[] args) {
        Main cliMain = new Main();

        try {
            final IDexterCLIOption cliOption = new DexterCLIOption(args);

            switch (cliOption.getCommandMode()) {
                case CREATE_ACCOUNT:
                    cliMain.createAccount(cliOption);
                    break;
                case STATIC_ANALYSIS:
                    cliMain.analyze(cliOption);
                    break;
                default:
                    break;
            }
        } catch (DexterRuntimeException e) {
            cliMain.getCLILog().errorln(e.getMessage(), e);
            log.error(e.getMessage(), e);
        }
    }

    public Main() {
        DexterConfig.getInstance().setRunMode(RunMode.CLI);
    }

    public void createAccount(final IDexterCLIOption cliOption) {
        final IDexterConfigFile configFile = createDexterConfigFile(cliOption);
        final IDexterClient client = createDexterClient(cliOption, configFile);
        final IAccountHandler accountHandler = createAccountHandler(client, cliOption);
        accountHandler.createAccount(cliOption.getUserId(), cliOption.getUserPassword());
    }

    protected IAccountHandler createAccountHandler(final IDexterClient client, final IDexterCLIOption cliOption) {
        if (cliOption.isStandAloneMode()) {
            return new EmptyAccountHandler();
        } else {
            return new AccountHandler(client, cliLog);
        }
    }

    protected IDexterClient createDexterClient(final IDexterCLIOption cliOption, final IDexterConfigFile configFile) {
        if (cliOption.isStandAloneMode()) {
            return new EmptyDexterClient();
        } else {
            return new DexterClient.DexterClientBuilder(cliOption.getUserId(), cliOption.getUserPassword())
                    .dexterServerIp(configFile.getDexterServerIp()).dexterServerPort(configFile.getDexterServerPort())
                    .build();
        }
    }

    public void analyze(final IDexterCLIOption cliOption) {
        final Stopwatch timer = Stopwatch.createStarted();
        cliLog.printStartingAnalysisMessage();

        final IDexterConfigFile configFile = createDexterConfigFile(cliOption);
        final IDexterClient client = createDexterClient(cliOption, configFile);
        loginOrCreateAccount(client, cliOption);

        final AnalysisConfig baseAnalysisConfig = createBaseAnalysisConfig(client, cliOption, configFile);
        final EndOfAnalysisHandler cliAnalysisResultHandler = createCLIAnalysisResultHandler(client.getDexterWebUrl(),
                cliOption);
        final IDexterPluginManager pluginManager = createDexterPlugins(client, cliOption);
        initSourceFileFullPathList(configFile, cliOption);

        if (cliOption.isAsynchronousMode()) {
            analyzeAsynchronously(pluginManager, cliAnalysisResultHandler, baseAnalysisConfig, client);
        } else {
            analyzeSynchronously(pluginManager, cliAnalysisResultHandler, baseAnalysisConfig, client);
            cliLog.printElapsedTime(timer.elapsed(TimeUnit.SECONDS));
        }

    }

    private void loginOrCreateAccount(final IDexterClient client, final IDexterCLIOption cliOption) {
        final IAccountHandler accountHandler = createAccountHandler(client, cliOption);
        if (accountHandler.loginOrCreateAccount() == false) {
            System.exit(1);
        }
    }

    private IDexterPluginManager createDexterPlugins(final IDexterClient client, final IDexterCLIOption cliOption) {
        IDexterPluginInitializer initializer = new CLIPluginInitializer(cliLog);
        IDexterPluginManager pluginManager = new CLIDexterPluginManager(initializer, client, cliLog, cliOption);
        pluginManager.initDexterPlugins();

        return pluginManager;
    }

    protected EndOfAnalysisHandler createCLIAnalysisResultHandler(final String dexterWebUrl,
            final IDexterCLIOption cliOption) {
        ICLIResultFile cliResultFile = new CLIResultFile();
        return new CLIAnalysisResultHandler(dexterWebUrl, cliResultFile, cliOption, cliLog);
    }

    private AnalysisConfig createBaseAnalysisConfig(final IDexterClient client, final IDexterCLIOption cliOption,
            final IDexterConfigFile configFile) {
        initDexterConfig(cliOption, configFile);

        final AnalysisConfig baseAnalysisConfig = configFile.toAnalysisConfig();

        baseAnalysisConfig.setSnapshotId(
                configFile.getType() == IDexterConfigFile.Type.SNAPSHOT ? System.currentTimeMillis() : -1L);

        return baseAnalysisConfig;
    }

    private IDexterConfigFile createDexterConfigFile(final IDexterCLIOption cliOption) {
        IDexterConfigFile configFile = new DexterConfigFile();
        configFile.loadFromFile(new File(cliOption.getConfigFilePath()));

        return configFile;
    }

    private void initDexterConfig(final IDexterCLIOption cliOption, final IDexterConfigFile configFile) {
        config.setStandalone(cliOption.isStandAloneMode());
        config.setSpecifiedCheckerOptionEnabledByCli(cliOption.isSpecifiedCheckerEnabledMode());
        config.setDexterHome(configFile.getDexterHome());
        config.createInitialFolderAndFiles();
    }

    private void initSourceFileFullPathList(final IDexterConfigFile configFile, final IDexterCLIOption cliOption) {
        if (cliOption.isTargetFilesOptionEnabled())
            this.sourceFileFullPathList = cliOption.getTargetFileFullPathList();
        else
            this.sourceFileFullPathList = configFile.generateSourceFileFullPathList();

        this.totalSourceFileNumber = this.sourceFileFullPathList.size();
    }

    private void analyzeSynchronously(final IDexterPluginManager pluginManager,
            final EndOfAnalysisHandler cliAnalysisResultHandler, final AnalysisConfig baseAnalysisConfig,
            final IDexterClient client) {
        assert pluginManager != null;

        cliLog.printMessagePreSyncAnalysis();

        for (final String fileFullPath : sourceFileFullPathList) {
            final AnalysisConfig analysisConfig = createAnalysisConfig(fileFullPath, cliAnalysisResultHandler,
                    baseAnalysisConfig);
            DexterAnalyzer.getInstance().runSync(analysisConfig, pluginManager, client);
        }

        cliAnalysisResultHandler.printLogAfterAnalyze();
    }

    private void analyzeAsynchronously(final IDexterPluginManager pluginManager,
            final EndOfAnalysisHandler cliAnalysisResultHandler, final AnalysisConfig baseAnalysisConfig,
            final IDexterClient client) {
        assert pluginManager != null;

        cliLog.printMessagePreAsyncAnalysis(client.getDexterWebUrl());

        for (final String fileFullPath : sourceFileFullPathList) {
            final AnalysisConfig analysisConfig = createAnalysisConfig(fileFullPath, cliAnalysisResultHandler,
                    baseAnalysisConfig);
            DexterAnalyzer.getInstance().runAsync(analysisConfig, pluginManager, client);
        }
    }

    private AnalysisConfig createAnalysisConfig(final String fileFullPath,
            final EndOfAnalysisHandler cliAnalysisResultHandler, final AnalysisConfig baseAnalysisConfig) {
        final AnalysisConfig config = new AnalysisEntityFactory()
                .copyAnalysisConfigWithoutSourcecode(baseAnalysisConfig);

        config.setResultHandler(cliAnalysisResultHandler);
        config.setSourceFileFullPath(fileFullPath);
        config.generateFileNameWithSourceFileFullPath();
        config.generateModulePath();

        return config;
    }

    public void setCLILog(final ICLILog cliLog) {
        this.cliLog = cliLog;
    }

    public ICLILog getCLILog() {
        return this.cliLog;
    }

    protected void setDexterConfig(final DexterConfig dexterConfig) {
        this.config = dexterConfig;
    }
}