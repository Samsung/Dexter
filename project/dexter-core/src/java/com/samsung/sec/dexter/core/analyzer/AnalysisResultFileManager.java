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
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.Files;
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
		String resultFolderStr = DexterConfig.getInstance().getDexterHome() + "/" + DexterConfig.RESULT_FOLDER_NAME;
		
		IAnalysisEntityFactory factory = new AnalysisEntityFactory();
		AnalysisResult baseResult = factory.createAnalysisResult(resultList);
		writeJsonResult(baseResult, resultFolderStr);
	}

	private void writeJsonResult(final AnalysisResult result, final String resultFolderStr) {
	    File resultFolder = new File(resultFolderStr);
		if (resultFolder.exists() == false) {
			resultFolder.mkdirs();
		}

		String resultFilePath = resultFolderStr + "/" + "result_" + result.getFileName()
		        + "_" + DexterUtil.currentDateTimeMillis() + ".json";
		File resultFile = new File(resultFilePath);

		if (resultFile.exists() == false) {
			try {
				resultFile.createNewFile();
			} catch (IOException e) {
				LOG.error(e.getMessage() + " : " + resultFilePath, e);
				return;
			}
		}

		StringBuilder contents = createJson(result);

		try {
			Files.write(contents.toString() + DexterUtil.LINE_SEPARATOR, resultFile, Charsets.UTF_8);
		} catch (IOException e1) {
			LOG.error(e1.getMessage(), e1);
		}
    } 

	private StringBuilder createJson(final AnalysisResult result) {
		final Gson gson = new Gson();
	    final StringBuilder contents = new StringBuilder(200);
		
		contents.append("{\"snapshotId\":\"").append(result.getSnapshotId()).append("\"");
		
		if(Strings.isNullOrEmpty(result.getModulePath()) == false){
			contents.append(",\"modulePath\":\"").append(result.getModulePath()).append("\"");
		}
		
		if(Strings.isNullOrEmpty(result.getFileName()) == false){
			contents.append(",\"fileName\":\"").append(result.getFileName()).append("\"");
		}
		
		if(Strings.isNullOrEmpty(result.getSourceFileFullPath()) == false){
			contents.append(",\"fullFilePath\":\"").append(result.getSourceFileFullPath()).append("\"");
		}
		
		if(Strings.isNullOrEmpty(result.getProjectName()) == false){
			contents.append(",\"projectName\":\"").append(result.getProjectName()).append("\"");
		}
		
		contents.append(",\"groupId\":\"").append(result.getDefectGroupId()).append("\"")
				.append(",\"toolName\":\"").append(result.getToolName()).append("\"")
				.append(",\"language\":\"").append(result.getLanguage()).append("\"")
		        .append(",\"defectCount\":\"").append(result.getDefectList().size()).append("\"");
		
		contents.append(",\"codeMetrics\":").append(gson.toJson(result.getCodeMetrics().getMetrics()));

		contents.append(",\"defectList\":[");

		final Iterator<Defect> iter = result.getDefectList().iterator();

		int index = 0;
		while (iter.hasNext()) {
			final Defect defect = iter.next();
			if (index++ == 0) {
				contents.append(gson.toJson(defect));
			} else {
				contents.append(",").append(gson.toJson(defect));
			}
		}

		contents.append("]}");
	    return contents;
    }

	public String getJson(final AnalysisResult result) {
		StringBuilder contents = createJson(result);
		
		return contents.toString();
	}
}
