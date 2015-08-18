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
package com.samsung.sec.dexter.eclipse.ui.view;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.eclipse.ui.DexterUIActivator;

public class RootAnalysisLog {
	private Queue<AnalysisLog> children = new LinkedList<AnalysisLog>();
	
	/**
	 * @return 
	 */
    public void loadFromLogFiles() {
    	final String dexterHome = DexterConfig.getInstance().getDexterHome();
    	final String resultPath = dexterHome + "/" + DexterConfig.RESULT_FOLDER_NAME;
    	
    	final File resultDir = new File(resultPath);
    	
    	if(resultDir.isDirectory() == false || resultDir.exists() == false){
    		return;
    	}
    	
    	for(File sub : resultDir.listFiles()){
    		if(sub.isFile()){
    			addAnalysisLogFromFile(sub);
    		}
    	}
    	
    	final String resultOldPath = dexterHome + "/" + DexterConfig.RESULT_FOLDER_NAME + "/" + DexterConfig.OLD_FOLDER_NAME;
    	final File resultOldDir = new File(resultOldPath);
    	
    	if(resultOldDir.isDirectory() == false || resultOldDir.exists() == false){
    		return;
    	}
    	
    	for(final File sub : resultOldDir.listFiles()){
    		if(sub.isFile()){
    			addAnalysisLogFromFile(sub);
    		}
    	}
    }
    
    /**
	 * @param root 
	 */
    @SuppressWarnings("unchecked")
    private void addAnalysisLogFromFile(File file) {
    	final String ext = Files.getFileExtension(file.toString()).toLowerCase();
    	if(!"json".equals(ext) || file.length() <=0 ){
    		return;
    	}
    	
    	final Gson gson = new Gson();
    	try {
	        for(final String content : Files.readLines(file, Charsets.UTF_8)){
	        	if(!content.startsWith("{") || content.indexOf("defectCount") == -1){
	        		continue;
	        	}
	        	
	        	@SuppressWarnings("rawtypes")
	        	final Map map = gson.fromJson(content, Map.class);
	        	@SuppressWarnings("rawtypes")
	        	final List<LinkedTreeMap> defectList = (List<LinkedTreeMap>) map.get("defectList");
	        	
	        	long datetime = -1;
	        	final AnalysisLog log = new AnalysisLog();
	        	log.setFileName((String) map.get("fileName"));
	        	log.setFileFullPath((String) map.get("fullFilePath"));
	        	log.setDefectCount(Integer.parseInt((String)map.get("defectCount")));
	        	
	        	final String fileStr = file.toString();
	        	final int sp = fileStr.lastIndexOf("_") + 1;
	        	final int ep = fileStr.lastIndexOf(".");
	        	log.setCreatedTimeStr(fileStr.substring(sp, ep));
	        	
	        	for(final LinkedTreeMap<String, Object> defectMap : defectList){
	        		final Defect defect = Defect.fromMap(defectMap);
	        		
	        		datetime = defect.getModifiedDateTime();
	        		if(datetime <= 0){
	        			datetime = defect.getCreatedDateTime();
	        		}
	        		
	        		log.addDefectLog(defect);
	        		
	        	}
	        	
	        	log.setCreatedTime(new Date(datetime));
	        	addChild(log);
	        }
        } catch (IOException e) {
	        DexterUIActivator.LOG.error(e.getMessage(), e);
        }
    }
    
    /**
	 * @param log 
	 * @return AnalsysisLog removed AnalysisLog, if exists
	 * 			if not exists, retrun null
	 */
    public AnalysisLog addChild(final AnalysisLog log) {
    	AnalysisLog removedLog = null;
    	if(this.children.size() > DexterConfig.MAX_LOG_COUNT){
    		removedLog = removeFirstChild();
    	}
    	
    	log.setRootLog(this);
    	this.children.add(log);
    	
    	return removedLog;
    }
    
    /**
	 *  
	 */
    public AnalysisLog[] getChildren() {
    	return this.children.toArray(new AnalysisLog[this.children.size()]);
    }
    
    public AnalysisLog removeFirstChild() {
   		return this.children.poll();
    }
}
