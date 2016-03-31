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
package com.samsung.sec.dexter.executor;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.analyzer.AnalysisResultFileManager;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.DexterConfig.RunMode;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.plugin.DexterPluginManager;
import com.samsung.sec.dexter.core.util.DexterClient;
import com.samsung.sec.dexter.core.util.DexterUtil;
import com.samsung.sec.dexter.metrics.CodeMetricsGenerator;

public class DexterAnalyzerThread extends Thread{
	private final static Logger logger = Logger.getLogger(DexterAnalyzerThread.class);
	private AnalysisConfig config;
//	private AnalysisResult result;
	private DexterAnalyzer analyzer = DexterAnalyzer.getInstance();
	
	public DexterAnalyzerThread(AnalysisConfig config){
		this.config = config;
		//this.result = result;
	}
	
	@Override
	public void run() {
		try{
			// TODO: Dismissed / Excluding Scope 대상 파일인지 검사
			// 0. Check parameters
			if (checkAnalsysConfig(config) == false) {
				logger.error("cannot analyze the file : " + config.getSourceFileFullPath());
				return;
			}
	
			// 1. add Header And Source Configuration
			analyzer.addHeaderAndSourceConfiguration(config);
	
			// 2. decide whether save source code or not
			analyzer.preSendSourceCode(config);
			if (config.shouldSendSourceCode() || config.getSnapshotId() > 0
			        || DexterConfig.getInstance().getRunMode() == RunMode.CLI) {
				sendSourceCode(config);
			}
			analyzer.postSendSourceCode(config);
	
			// 3. check code metrics
			analyzer.preRunCodeMetrics(config);
			config.getCodeMetrics().setFileName(config.getFileName());
			config.getCodeMetrics().setModulePath(config.getModulePath());
			CodeMetricsGenerator.getCodeMetrics(config.getLanguageEnum(), config.getSourceFileFullPath(),
			        config.getCodeMetrics());
			analyzer.postRunCodeMetrics(config);
	
			// 4. call plugin's analyzer (static analysis)
			analyzer.preRunStaticAnalysis(config);
			List<AnalysisResult> resultList = DexterPluginManager.getInstance().analyze(config);
			AnalysisResultFileManager.getInstance().writeJson(resultList);
			logger.info("analyzed " + config.getSourceFileFullPath());
			config.getResultHandler().handleAnalysisResult(resultList);
			analyzer.postRunStaticAnalysis(config, resultList);
		} catch (DexterRuntimeException e){
			e.printStackTrace();
			logger.error("analyzed failed  : " 	+ config.getSourceFileFullPath());
		} catch (NoClassDefFoundError e) {
			e.printStackTrace();
			logger.error("analyzed failed  : " 	+ config.getSourceFileFullPath(), e);
		}
	}

	private boolean checkAnalsysConfig(final AnalysisConfig config) {
		if (Strings.isNullOrEmpty(config.getSourceFileFullPath()) || Strings.isNullOrEmpty(config.getFileName())) {
			logger.error("Invalid Analysis Config : fileName or sourceFileFullPath is null or empty");
			return false;
		}

		final File f = new File(config.getSourceFileFullPath());
		if (f.isFile() == false || f.exists() == false || f.length() <= 0) {
			logger.error("Invalid Analsis Config : file is not exist or 0 size : " + config.getSourceFileFullPath());
			return false;
		}

		if (Strings.isNullOrEmpty(config.getProjectName()) || Strings.isNullOrEmpty(config.getProjectFullPath())) {
			logger.error("Invalid Analysis Config : projectName or projectFullPath is null or empty");
			return false;
		}

		final File p = new File(config.getProjectFullPath());
		if (p.isDirectory() == false || p.exists() == false) {
			logger.error("Invalid Analsis Config : project full path is not exist or not directory : "
			        + config.getProjectFullPath());
			return false;
		}

		return true;
	}

	// TODO 압축해서 보낼 것
	private void sendSourceCode(final AnalysisConfig config) {
		if (DexterClient.getInstance().isLogin() == false) {
			return;
		}
		
		for (final String base : config.getSourceBaseDirList()) {
			final File file = DexterUtil.getFileFullPath(base, config.getModulePath(), config.getFileName());
			
			if(file.exists() == false || file.isFile() == false){
				continue;
			}
			
			try {
				final StringBuilder src = new StringBuilder(1000);
				for (final String content : Files.readLines(file, DexterConfig.getInstance().getSourceEncoding())) {
					src.append(content).append(DexterUtil.LINE_SEPARATOR);
				}

				DexterClient.getInstance().insertSourceCode(config.getSnapshotId(), config.getDefectGroupId(),
				        config.getModulePath(), config.getFileName(), src.toString());
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			} catch (DexterRuntimeException e) {
				logger.error(e.getMessage(), e);
            }

			break;
		}
	}
}
