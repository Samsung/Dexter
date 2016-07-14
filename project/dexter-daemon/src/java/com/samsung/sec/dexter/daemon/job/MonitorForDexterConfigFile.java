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
package com.samsung.sec.dexter.daemon.job;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.analyzer.EndOfAnalysisHandler;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.DexterConfigFile;
import com.samsung.sec.dexter.core.config.IDexterConfigFile;
import com.samsung.sec.dexter.core.config.IDexterHomeListener;
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.defect.Occurence;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.filter.AnalysisFilterHandler;
import com.samsung.sec.dexter.core.util.DexterUtil;
import com.samsung.sec.dexter.daemon.DexterDaemonActivator;
import com.samsung.sec.dexter.eclipse.ui.DexterUIActivator;
import com.samsung.sec.dexter.executor.DexterAnalyzer;

public class MonitorForDexterConfigFile extends Job implements IDexterHomeListener{
	public static long LAST_CONF_CHANAGED_TIME = -1;
	private final static int MONITOR_DELAY = 500;
	private File configFile;

	public MonitorForDexterConfigFile() {
		super("Monitoring target configuration file");
		initDexterConfigFile();
		DexterConfig.getInstance().addDexterHomeListener(this);
	}
	
	private void initDexterConfigFile() {
    	final String dexterHome = DexterConfig.getInstance().getDexterHome();
    	String dexterConfigFilePath = dexterHome + "/bin/" + DexterConfig.DAEMON_FOLDER_NAME + "/" + DexterConfig.DEXTER_DAEMON_CFG_FILENAME;
    	DexterUtil.createEmptyFileIfNotExist(dexterConfigFilePath);
		configFile = new File(dexterConfigFilePath);
		LAST_CONF_CHANAGED_TIME = configFile.lastModified();
    }
	
	/* 
	 * @see org.eclipse.core.runtime.jobs.Job#canceling()
	 */
	@Override
	protected void canceling() {
		DexterConfig.getInstance().removeDexterHomeListener(this);
	    super.canceling();
	}
	
	/*
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		do {
			try {
				checkDexterConfigFileAndAnalyze();
				delayMonitor();
			} catch (DexterRuntimeException e) {
				DexterDaemonActivator.LOG.error(e.getMessage(), e);
			}
		} while (monitor.isCanceled() == false);

		DexterConfig.getInstance().removeDexterHomeListener(this);
		return Status.CANCEL_STATUS;
	}

	private void checkDexterConfigFileAndAnalyze() {
		final long lastModified = configFile.lastModified();
		
		if(LAST_CONF_CHANAGED_TIME == lastModified){
			return;
		}

		IDexterConfigFile dexterConfigFile = new DexterConfigFile();
		dexterConfigFile.loadFromFile(configFile);
		final AnalysisConfig analysisConfig = dexterConfigFile.toAnalysisConfig();

		analysisConfig.setSourceFileFullPath(dexterConfigFile.getFirstFileName());
		analysisConfig.generateFileNameWithSourceFileFullPath();
		handleConfigFileChanged(analysisConfig);
		
		LAST_CONF_CHANAGED_TIME = lastModified;
	}
	
	private void handleConfigFileChanged(final AnalysisConfig analysisConfig) {
		Job analysisJob = new Job("analyzing " + analysisConfig.getFileName()){
			@Override
            protected IStatus run(IProgressMonitor monitor) {
				analyzeFile(analysisConfig);
	            return Status.OK_STATUS;
            }
		};
		
		analysisJob.schedule();
	}
	
	private void analyzeFile(AnalysisConfig config) {
    	if (config.getSourceBaseDirList().size() == 0
    	        && DexterConfig.getInstance().getRunMode() == DexterConfig.RunMode.DAEMON) {
    		File parentDir = new File(config.getSourceFileFullPath()).getParentFile();
    		if (parentDir.exists() && parentDir.isDirectory()) {
    			addSourceDir(parentDir, config.getSourceBaseDirList());
    		}
    	}
    	
    	if(Strings.isNullOrEmpty(config.getModulePath())){
    		for(String src : config.getSourceBaseDirList()){
    			if(config.getSourceFileFullPath().indexOf(src) >= 0){
    				config.setModulePath(config.getSourceFileFullPath().substring(src.length(), 
    						config.getSourceFileFullPath().lastIndexOf('/')));
    			}
    		}
    		
    	}
    	
    	// XXX Performance is not good here
//				if (config.getHeaderBaseDirList().size() 
//				        && DexterConfig.getInstance().getRunMode() == DexterConfig.RunMode.DAEMON) {
//					File projectDir = new File(config.getProjectFullPath());
//					if(projectDir.exists() && projectDir.isDirectory()){
//						addHeaderDir(projectDir, config.getHeaderBaseDirList());
//					}
//				}

    	config.setResultHandler(createResultChanageHandler());
    	config.setShouldSendSourceCode(true);
    	DexterAnalyzer.getInstance().runAsync(config, DexterUIActivator.getDefault().getPluginManager());
    }

	private void delayMonitor() {
		try {
			Thread.sleep(MONITOR_DELAY);
		} catch (InterruptedException e) {
			// intentionally empty
		}
	}
	
	private void addSourceDir(final File dir, final List<String> sourceBaseDirList) {
		if (dir == null || !dir.isDirectory()) {
			return;
		}

		final String dirName = dir.getName().toLowerCase();

		if ("source".equals(dirName) || "src".equals(dirName)) {
			final String sourceDir = dir.getPath().replace("\\", "/").replace(DexterUtil.PATH_SEPARATOR, "/").replace("//", "/");
			if (!sourceBaseDirList.contains(sourceDir)) {
				sourceBaseDirList.add(sourceDir);
			}
			return;
		}

		addSourceDir(dir.getParentFile(), sourceBaseDirList);
	}

	private EndOfAnalysisHandler createResultChanageHandler() {
		return new EndOfAnalysisHandler() {
			@Override
			public void handleAnalysisResult(final List<AnalysisResult> resultList) {
				try {
					final List<Defect> allDefectList = DexterAnalyzer.getAllDefectList(resultList);
	
					final StringBuilder msg = new StringBuilder();
					for (final Defect defect : allDefectList) {
						boolean isDefectDissmissed = AnalysisFilterHandler.getInstance().isDefectDismissed(defect);
						if (isDefectDissmissed) {
							continue;
						}
	
						for (final Occurence oc : defect.getOccurences()) {
							msg.append("" + oc.getStartLine()).append(":").append(defect.getCheckerCode()).append(":")
							        .append(defect.getToolName()).append(":").append(defect.getLanguage()).append(":")
							        .append(defect.getModulePath()).append(":").append(defect.getFileName()).append(":")
							        .append(defect.getClassName()).append(":").append(defect.getMethodName()).append(":")
							        .append(defect.getSeverityCode()).append(":").append(oc.getMessage())
							        .append(DexterUtil.LINE_SEPARATOR);
						}
					}


					// TODO 일반적인 파일 쓰기 방법으로 변경 - read lock 처리 포함
					final String sourceFileFullPath = DexterAnalyzer.getSourceFileFullPath(resultList);
					final File resultFile = DexterAnalyzer.getResultFile(resultList);
					msg.append("E|").append(sourceFileFullPath).append("|").append(System.currentTimeMillis());
					Files.createParentDirs(resultFile);
					Files.write(msg.toString(), resultFile, Charsets.UTF_8);

				} catch (IOException e) {
					DexterDaemonActivator.LOG.error(e.getMessage(), e);
				}
			}
		};
	}

	/* (non-Javadoc)
	 * @see com.samsung.sec.dexter.core.config.IDexterHomeListener#handleDexterHomeChanged()
	 */
    @Override
    public void handleDexterHomeChanged(final String oldPath, final String newPath) {
    	initDexterConfigFile();	    
    }
}
