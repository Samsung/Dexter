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

import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.util.DexterUtil;

public class AnalysisResultFileManager {
	private static Logger LOG = Logger.getLogger(AnalysisResultFileManager.class);

	private AnalysisResultFileManager() {
		LOG.debug("AnalysisResultFileManager");
	}
	
	private static class LazyHolder {
		private static final AnalysisResultFileManager INSTANCE = new AnalysisResultFileManager(); 
	}

	public static AnalysisResultFileManager getInstance() {
		return LazyHolder.INSTANCE;
	}

	/**
	 * @param result
	 *            void
	 */
	public void writeJson(final List<AnalysisResult> resultList) {
		if(resultList.size() == 0) return;
		
		final String resultFolderStr = DexterConfig.getInstance().getDexterHome() + "/" + DexterConfig.RESULT_FOLDER_NAME;
		DexterUtil.createDirectoryIfNotExist(resultFolderStr);
		
		final IAnalysisEntityFactory factory = new AnalysisEntityFactory();
		AnalysisResult baseResult = factory.createAnalysisResult(resultList);
		removeOldResultFile(baseResult, resultFolderStr);
		writeJsonResult(baseResult, resultFolderStr);
	}

	private void removeOldResultFile(final AnalysisResult baseResult, final String resultFolderStr) {
		final File resultFolder = new File(resultFolderStr);
		
		final String resultFileName = ResultFileConstant.RESULF_FILE_PREFIX 
				+ baseResult.getFileName() + "_";
		File[] oldResultFiles = DexterUtil.getSubFiles(resultFolder, resultFileName);
		if(oldResultFiles == null || oldResultFiles.length == 0)
			return;
		
		for(int i=0; i<oldResultFiles.length; i++){
			if(oldResultFiles[i].delete() == false){
				LOG.warn("cannot delete the old result file : " + oldResultFiles[i].getAbsolutePath());
			}
		}
	}

	private void writeJsonResult(final AnalysisResult result, final String resultFolderStr) {
		final StringBuilder contents = createJson(result);
		final File resultFile = getResultFilePath(result, resultFolderStr);
		DexterUtil.writeFileContents(contents.toString(), resultFile);
    }
	
	private StringBuilder createJson(final AnalysisResult result) {
		final Gson gson = new Gson();
	    final StringBuilder contents = new StringBuilder(200);
		
		addGeneralContent(result, contents);
		addMetricsContent(result, gson, contents);
		addFunctionMetricsContent(result, gson, contents);
		addDefectContent(result, gson, contents);
		
		return contents.append(DexterUtil.LINE_SEPARATOR);
    }

	private void addMetricsContent(final AnalysisResult result,
			final Gson gson, final StringBuilder contents) {
		contents.append(",\"").append(ResultFileConstant.CODE_METRICS).append("\":")
			.append(gson.toJson(result.getCodeMetrics().getMetrics()));
	}
	
	private void addFunctionMetricsContent(final AnalysisResult result,
			final Gson gson, final StringBuilder contents){
		contents.append(",\"").append(ResultFileConstant.FUNCTION_METRICS).append("\":")
		.append(gson.toJson(result.getFunctionMetrics().getFunctionMetrics()));		
	}

	private void addGeneralContent(final AnalysisResult result,
			final StringBuilder contents) {
		contents.append("{\"").append(ResultFileConstant.SNAPSHOT_ID).append("\":\"")
			.append(result.getSnapshotId()).append("\"");
		
		addOptionalContent(contents, ResultFileConstant.MODULE_PATH, result.getModulePath());
		addOptionalContent(contents, ResultFileConstant.FILE_NAME, result.getFileName());
		addOptionalContent(contents, ResultFileConstant.FULL_FILE_PATH, result.getSourceFileFullPath());
		addOptionalContent(contents, ResultFileConstant.PROJECT_NAME, result.getProjectName());
		
		contents.append(",\"").append(ResultFileConstant.GROUP_ID)
				.append("\":\"").append(result.getDefectGroupId()).append("\"")
		        .append(",\"").append(ResultFileConstant.DEFECT_COUNT)
		        .append("\":\"").append(result.getDefectList().size()).append("\"");
	}

	private void addDefectContent(final AnalysisResult result, final Gson gson,
			final StringBuilder contents) {
		contents.append(",\"").append(ResultFileConstant.DEFECT_LIST).append("\":[");
		
		int defectSize = result.getDefectList().size();
		for(int i=0; i<defectSize; i++){
			final Defect defect = result.getDefectList().get(i);
			
			if(i != 0) contents.append(",");
			
			contents.append(gson.toJson(defect));
		}
		
		contents.append("]}");
	}

	private void addOptionalContent(final StringBuilder contents, final String key, final String value) {
		if(Strings.isNullOrEmpty(value)) return;
		contents.append(",\"").append(key).append("\":\"").append(value).append("\"");
	}

	private File getResultFilePath(final AnalysisResult result,
			final String resultFolderStr) {
		
		final String path =  resultFolderStr + "/" + ResultFileConstant.RESULF_FILE_PREFIX + result.getFileName()
		        + "_" + DexterUtil.currentDateTimeMillis() + ResultFileConstant.RESULT_FILE_EXTENSION;
		
		final File resultFile = DexterUtil.createEmptyFileIfNotExist(path);
		
		return resultFile;
	} 

	public String getJson(final AnalysisResult result) {
		StringBuilder contents = createJson(result);
		
		return contents.toString();
	}
}
