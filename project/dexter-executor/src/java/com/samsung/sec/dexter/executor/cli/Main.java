/**
 * Copyright (c) 2014 Samsung Electronics, Inc.,
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisEntityFactory;
import com.samsung.sec.dexter.core.analyzer.EndOfAnalysisHandler;
import com.samsung.sec.dexter.core.analyzer.IAnalysisEntityFactory;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.DexterConfig.RunMode;
import com.samsung.sec.dexter.core.config.DexterConfigFile;
import com.samsung.sec.dexter.core.config.EmptyDexterConfigFile;
import com.samsung.sec.dexter.core.config.IDexterConfigFile;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.job.DeleteResultLogJob;
import com.samsung.sec.dexter.core.job.SendResultJob;
import com.samsung.sec.dexter.core.plugin.IDexterPluginManager;
import com.samsung.sec.dexter.core.util.DexterClient;
import com.samsung.sec.dexter.core.util.IDexterClient;
import com.samsung.sec.dexter.executor.CLIPluginInitializer;
import com.samsung.sec.dexter.executor.DexterAnalyzer;

public class Main {
	private ICLILog cliLog = new CLILog(System.out);
	private IDexterCLIOption cliOption;
	private ICLIResultFile cliResultFile;
	private EndOfAnalysisHandler cliAnalysisResultHandler;

	private IAnalysisEntityFactory analysisEntityFactory = new AnalysisEntityFactory();
	private DexterConfig config = DexterConfig.getInstance();
	private DexterAnalyzer analyzer = DexterAnalyzer.getInstance();
	private IDexterClient client = DexterClient.getInstance();
	private IDexterPluginManager pluginManager;
	
	IAccount account;
	
	private AnalysisConfig baseAnalsysisConfig = null;
	private List<String> sourceFileFullPathList = null;
	private int totalSourceFileNumber = 0;
	
	public static void main(String[] args) {
		Main cliMain = null;
		
		try {
			cliMain = new Main(args);
			final IDexterConfigFile configFile = cliMain.init();
			cliMain.run(configFile);
		} catch (DexterRuntimeException e){
			if(cliMain != null)
				cliMain.getCLILog().errorln(e.getMessage(), e);
			else
				e.printStackTrace();
		}
	}
	
	public Main(String[] args){
		DexterConfig.getInstance().setRunMode(RunMode.CLI);
		
		cliOption = new DexterCLIOption(args);
	}

	protected IDexterConfigFile init() {
		if(cliOption.isStandAloneMode() == false)
			account = new Account(cliLog);
		
		if(cliOption.isAccountCreationMode())
			return new EmptyDexterConfigFile();
			
		cliLog.printStartingAnalysisMessage();
		cliResultFile = new CLIResultFile();
		cliAnalysisResultHandler = new CLIAnalysisResultHandler(cliLog, cliOption, cliResultFile);
		
		initStandAloneMode();
		initRunOnlyEnabledCheckerMode();
		IDexterConfigFile configFile = createDexterConfigFile();
		initDexterHome(configFile);
		initDexterClient(configFile);
		initDexterPlugins();
		initSourceFileFullPathList(configFile);
		initFolderAndFiles();
		
		return configFile;
	}
	
	protected void run(final IDexterConfigFile configFile) {
		if(cliOption.isAccountCreationMode())
			createAccount();
		else
			runAnalysis(configFile);
	}
	
	private void initStandAloneMode() {
		config.setStandalone(cliOption.isStandAloneMode());
	}
	
	private void initRunOnlyEnabledCheckerMode() {
		config.setSpecifiedCheckerOptionEnabledByCli(cliOption.isSpecifiedCheckerEnabledMode());
	}

	private IDexterConfigFile createDexterConfigFile(){
		IDexterConfigFile configFile = new DexterConfigFile();
		configFile.loadFromFile(new File(cliOption.getConfigFilePath()));
		
		return configFile;
	}
	
	private void initDexterHome(final IDexterConfigFile configFile){
		config.setDexterHome(configFile.getDexterHome());	
	}
	
	private void initDexterClient(final IDexterConfigFile configFile) {
		if(cliOption.isStandAloneMode()) return;
		
		client.setDexterServer(configFile.getDexterServerIp(), configFile.getDexterServerPort());
		account.loginOrCreateAccount(cliOption.getUserId(), cliOption.getUserPassword());
	}
	
	private void initDexterPlugins(){
		pluginManager = new CLIDexterPluginManager(new CLIPluginInitializer(cliLog), cliLog, cliOption);
		pluginManager.initDexterPlugins();
	}
	
	private void initSourceFileFullPathList(final IDexterConfigFile configFile){
		if(cliOption.isTargetFilesOptionEnabled())
			this.sourceFileFullPathList = cliOption.getTargetFileFullPathList();
		else
			this.sourceFileFullPathList = configFile.generateSourceFileFullPathList();
		
		this.totalSourceFileNumber = this.sourceFileFullPathList.size();
	}

	private void initFolderAndFiles(){
		DexterConfig.getInstance().createInitialFolderAndFiles();
	}
	
	protected void createAccount() {
		
		// TODO client setup first
//		account.createAccount(cliOption.getServerHostIp(), cliOption.getServerPort(), 
//				cliOption.getUserId(), cliOption.getUserPassword());
		account.createAccount(cliOption.getUserId(), cliOption.getUserPassword());
    }
	
	protected void runAnalysis(final IDexterConfigFile configFile){
		final Stopwatch timer = Stopwatch.createStarted();
		
		this.baseAnalsysisConfig = configFile.toAnalysisConfig();
		setGroupAndSnapshotId(configFile);
		
		if(cliOption.isAsynchronousMode()){
			analysisAsync();
		} else {
			analysisSync();
		}
		
		if(cliOption.isStandAloneMode() == false){
			SendResultJob.send();
            DeleteResultLogJob.deleteOldLog();
		}
		
		cliLog.printElapsedTime(timer.elapsed(TimeUnit.SECONDS));
	}

	private void analysisSync() {
		assert pluginManager != null;
		
		cliLog.printMessagePreSyncAnalysis();
		handleBeginnigOfResultFile();
		
		for (final String fileFullPath : sourceFileFullPathList) {
			analyzer.runSync(createAnalysisConfig(fileFullPath), pluginManager);
		}
		
		handleEndOfResultFile();
	}
	
	private void analysisAsync() {
		assert pluginManager != null;
		
		cliLog.printMessagePreAsyncAnalysis();
		
		for (final String fileFullPath : sourceFileFullPathList) {
			analyzer.runAsync(createAnalysisConfig(fileFullPath), pluginManager);
		}
	}
	
	private AnalysisConfig createAnalysisConfig(final String fileFullPath){
		final AnalysisConfig config = analysisEntityFactory.copyAnalysisConfigWithoutSourcecode(this.baseAnalsysisConfig);

		config.setResultHandler(cliAnalysisResultHandler);
		config.setSourceFileFullPath(fileFullPath);
		config.generateFileNameWithSourceFileFullPath();
		config.generateModulePath();
		
		return config;
	}
	
	private void handleBeginnigOfResultFile() {
		try {
			if(cliOption.isJsonFile()){
				cliResultFile.writeJsonResultFilePrefix(cliOption.getJsonResultFile());
			}
			
			if(cliOption.isXmlFile()){
				cliResultFile.writeXmlResultFilePrefix(cliOption.getXmlResultFile());
			}
			
			if(cliOption.isXml2File()){
				cliResultFile.writeXml2ResultFilePrefix(cliOption.getXmlResultFile());
			}
		} catch (IOException e) {
			cliLog.errorln(e.getMessage(), e);
		}
    }
	
	private void handleEndOfResultFile(){
		try {
			if(cliOption.isJsonFile()){
				cliResultFile.writeJsonResultFilePostfix(cliOption.getJsonResultFile());
				cliLog.printResultFileLocation(cliOption.getJsonResultFile());
			}
			
			if(cliOption.isXmlFile()){
				cliResultFile.writeXmlResultFilePostfix(cliOption.getXmlResultFile());
				cliLog.printResultFileLocation(cliOption.getXmlResultFile());
			}
			
			if(cliOption.isXml2File()){
				cliResultFile.writeXml2ResultFilePostfix(cliOption.getXmlResultFile());
				cliLog.printResultFileLocation(cliOption.getXml2ResultFile());
			}
        } catch (IOException e) {
        	cliLog.errorln(e.getMessage(), e);
        }
	}
	
	private void setGroupAndSnapshotId(IDexterConfigFile configFile) {
		switch(configFile.getType()){
			case SNAPSHOT:
				final long defectGroupId = analyzer.getDefectGroupByCreating(this.baseAnalsysisConfig.getProjectName());
				this.baseAnalsysisConfig.setDefectGroupId(defectGroupId);
				final long snapshotId = System.currentTimeMillis();
				this.baseAnalsysisConfig.setSnapshotId(snapshotId);
				break;
			default:
				this.baseAnalsysisConfig.setNoDefectGroupAndSnapshotId();
		}
    }

	public void setCLILog(ICLILog cLILog) {
		this.cliLog = cLILog;
    }
	
	public ICLILog getCLILog() {
		return this.cliLog;
	}
	
	public void setAccount(IAccount account){
		this.account = account;
	}
	
	protected void setDexterConfig(DexterConfig dexterConfig){
		this.config = dexterConfig;
	}
	
	protected void setAnalysisEntityFactory(IAnalysisEntityFactory analysisEntityFactory){
		this.analysisEntityFactory = analysisEntityFactory;
	}
	
	protected void setDexterAnalyzer(DexterAnalyzer analyzer){
		this.analyzer = analyzer;
	}
	
	protected void setDexterClient(IDexterClient client){
		this.client = client;
	}
	
	protected void setDexterPluginManager(IDexterPluginManager pluginManager){
		this.pluginManager = pluginManager;
	}

	protected IDexterCLIOption getCLIOption() {
		return this.cliOption;
	}
}
