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
package com.samsung.sec.dexter.eclipse.ui.analysis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisEntityFactory;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.analyzer.EndOfAnalysisHandler;
import com.samsung.sec.dexter.core.analyzer.IAnalysisEntityAbstractFactory;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.defect.Occurence;
import com.samsung.sec.dexter.core.filter.AnalysisFilterHandler;
import com.samsung.sec.dexter.core.util.DexterUtil;
import com.samsung.sec.dexter.eclipse.ui.DexterUIActivator;
import com.samsung.sec.dexter.executor.DexterAnalyzer;
import com.samsung.sec.dexter.executor.ProjectAnalysisConfiguration;

public class ProjectOrFolderAnalyzer {
	private ProjectAnalysisConfiguration projectCfg;
	private IProgressMonitor monitor;
	private int totalCount = 0;
	private List<File> targetFileList = new ArrayList<File>(100);
	
	public ProjectOrFolderAnalyzer(ProjectAnalysisConfiguration projectCfg, IProgressMonitor monitor){
		this.projectCfg = projectCfg;
		this.monitor = monitor;
	}
	
    public void run() {
    	monitor.setTaskName("Static Analysis...");
    	
    	final AnalysisConfig config = createAnalysisConfig();
    	createTargetFileList(config);
		monitor.beginTask("Static Analysis...", targetFileList.size());
		analysisTargetFileList(config);
//		monitor.done();
		
		targetFileList.clear();
    }

	private AnalysisConfig createAnalysisConfig() {
        IAnalysisEntityAbstractFactory configFactory = new AnalysisEntityFactory();
    	final AnalysisConfig config = configFactory.createAnalysisConfig();
    	
		config.setProjectName(projectCfg.getProjectName());
		config.setProjectFullPath(projectCfg.getProjectFullPath());
		config.setSnapshotId(-1);
		
		addSourceBaseDirList(config);
		addHeaderBaseDirList(config);
		
        return config;
    }
	
	private void addSourceBaseDirList(final AnalysisConfig config) {
		for(final String dir : projectCfg.getSourceDirs()){
			config.addSourceBaseDirList(dir);
		}
    }
	
	private void addHeaderBaseDirList(final AnalysisConfig config) {
		for(final String dir : projectCfg.getHeaderDirs()){
			config.addHeaderBaseDirList(dir);
		}
    }
	
	private void createTargetFileList(final AnalysisConfig config){
		final String type = projectCfg.getType();
		
		if("PROJECT".equals(type) || "SNAPSHOT".equals(type)){
			for(final String dir : config.getSourceBaseDirList()){
				addTargetFile(new File(dir));
			}
		} else if("FOLDER".equals(type)){
			for(final String dir : projectCfg.getTargetDirs()){
				addTargetFile(new File(dir));
			}
		}
	}
    
    private void addTargetFile(File file){
    	if(file.isDirectory()){
    		for(final File sub : file.listFiles()){
    			addTargetFile(sub);
    		}
		} else if(DexterConfig.getInstance().isAnalysisAllowedFile(file.getName())){
			targetFileList.add(file);
		}
	}
    
    private void analysisTargetFileList(final AnalysisConfig config) {
        int index = targetFileList.size();
		totalCount = targetFileList.size();
		
		setSnapshotIdAndGroupId(config);
		while(--index >= 0){
			final File file = targetFileList.remove(index);
			analysisFile(file, config);
			monitor.worked(1);
		}
    }

	private void analysisFile(File file, AnalysisConfig baseConfig){
		monitor.setTaskName("remaining " + targetFileList.size() + " of " + totalCount + " : " + file.getPath());
		
		if(monitor.isCanceled()){
			monitor.done();
			return;
		}
		
		IAnalysisEntityAbstractFactory factory = new AnalysisEntityFactory();
		AnalysisConfig config = factory.copyAnalysisConfigWithoutSourcecode(baseConfig);
		
		setShouldSendSourceCode(config);
		setResultHandler(config);
		config.setFileName(file.getName());
		config.setSourceFileFullPath(file.getPath());
		config.createResultFileFullPath();
		config.generateModulePath();
		
		DexterAnalyzer.getInstance().runSync(config);
	}
	
	private void setShouldSendSourceCode(AnalysisConfig config) {
	    if("PROJECT".equals(projectCfg.getType()) || "SNAPSHOT".equals(projectCfg.getType())){
			config.setShouldSendSourceCode(true);
		}
    }

	private void setSnapshotIdAndGroupId(AnalysisConfig config) {
	    if("SNAPSHOT".equals(projectCfg.getType())){
			config.setSnapshotId(System.currentTimeMillis());
			config.setDefectGroupId(DexterAnalyzer.getInstance().getDefectGroupByCreating(config.getProjectName()));
		}
    }

	private void setResultHandler(AnalysisConfig config) {
	    config.setResultHandler(new EndOfAnalysisHandler() {
			@Override
			public void handleAnalysisResult(final List<AnalysisResult> resultList) {
				try {
    				List<Defect> allDefectList = DexterAnalyzer.getAllDefectList(resultList);
    				
    				final StringBuilder msg = new StringBuilder();
    				addDefectInfo(allDefectList, msg);

					final String sourceFileFullPath = DexterAnalyzer.getSourceFileFullPath(resultList);
					msg.append("E|").append(sourceFileFullPath).append("|").append(System.currentTimeMillis());
					
					// TODO 일반적인 파일 쓰기 방법으로 변경 - read lock 처리 포함
					File resultFile = DexterAnalyzer.getResultFile(resultList);
					Files.createParentDirs(resultFile);
					Files.write(msg.toString(), resultFile, Charsets.UTF_8);
				} catch (IOException e) {
					DexterUIActivator.LOG.error( e.getMessage(), e);
				}
			}

			private void addDefectInfo(List<Defect> allDefectList, final StringBuilder msg) {
                for (final Defect defect : allDefectList) {
                	
                	boolean isDefectDissmissed = AnalysisFilterHandler.getInstance().isDefectDismissed(defect);
                	
                	if (isDefectDissmissed) {
                		continue;
                	}

                	for (Occurence oc : defect.getOccurences()) {
                		msg.append("" + oc.getStartLine()).append(":").append(defect.getCheckerCode()).append(":")
                	        .append(defect.getToolName()).append(":").append(defect.getLanguage()).append(":")
                	        .append(defect.getModulePath()).append(":").append(defect.getFileName()).append(":")
                	        .append(defect.getClassName()).append(":").append(defect.getMethodName()).append(":")
                	        .append(defect.getSeverityCode()).append(":").append(oc.getMessage())
                	        .append(DexterUtil.LINE_SEPARATOR);
                	}
                }
            }
		});
    }
}
