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
package com.samsung.sec.dexter.core.analyzer;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import com.samsung.sec.dexter.core.config.DexterConfigFile;

public class AnalysisEntityFactory implements IAnalysisEntityAbstractFactory {
	private final static Logger log = Logger.getLogger(AnalysisEntityFactory.class);

	@Override
	public AnalysisConfig createAnalysisConfig() {
		return new AnalysisConfig();
	}

	@Override
	public AnalysisConfig copyAnalysisConfigWithoutSourcecode(final AnalysisConfig baseAnalysisConfig) {
		return new AnalysisConfig(baseAnalysisConfig);
	}
	
	public void test(){
		
	}
	
	@Override
    public AnalysisConfig createAnalsysiConfigFromDexterConfigFile(final File dexterConfigFilePath) {
		DexterConfigFile configFile = new DexterConfigFile();
		configFile.loadFromFile(dexterConfigFilePath);
		return configFile.toAnalysisConfig();
	}
	
    @Override
    public AnalysisResult createAnalysisResult(final AnalysisConfig config) {
		final AnalysisResult result = new AnalysisResult();
		
		result.setFileName(config.getFileName());
		result.setModulePath(config.getModulePath());
		result.setProjectName(config.getProjectName());
		result.setProjectFullPath(config.getProjectFullPath());
		result.setDefectGroupId(config.getDefectGroupId());
		result.setSnapshotId(config.getSnapshotId());
		result.setSourceFileFullPath(config.getSourceFileFullPath());
		result.setResultFileFullPath(config.getResultFileFullPath());
		result.setCodeMetrics(config.getCodeMetrics());
//		result.setResultHandler(config.getResultHandler());
		
		return result;
	}

	@Override
	public AnalysisResult createAnalysisResult(List<AnalysisResult> resultList) {
		final AnalysisResult result = new AnalysisResult();
		final AnalysisResult base = resultList.get(0);
		
		result.setFileName(base.getFileName());
		result.setModulePath(base.getModulePath());
		result.setProjectName(base.getProjectName());
		result.setProjectFullPath(base.getProjectFullPath());
		result.setDefectGroupId(base.getDefectGroupId());
		result.setSnapshotId(base.getSnapshotId());
		result.setSourceFileFullPath(base.getSourceFileFullPath());
		result.setResultFileFullPath(base.getResultFileFullPath());
		result.setCodeMetrics(base.getCodeMetrics());
		
		for(int i=0; i<resultList.size(); i++){
			result.getDefectList().addAll(resultList.get(i).getDefectList());
		}
		
		return result;
	}
}
