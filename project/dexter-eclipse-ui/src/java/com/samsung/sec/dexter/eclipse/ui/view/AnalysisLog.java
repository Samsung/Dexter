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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.samsung.sec.dexter.core.defect.Defect;

public class AnalysisLog {
	private RootAnalysisLog rootLog;
	private Date createdTime = new Date();
	private String createdTimeStr = "";
	private String fileName = "";
	private String modulePath = "";
	private String fileFullPath = "";
	private String status =""; 
	private int defectCount;
	
	private List<DefectLog> defectLogList = new ArrayList<DefectLog>(1);
	
	private List<String> functionList = new ArrayList<String> (0);
	
	public List<String> getFunctionList() {
		return functionList;
	}

	public void setFunctionList(List<String> functionList) {
		this.functionList = functionList;
	}

	/**
	 * @param i 
	 */
    public void setDefectCount(final int count) {
    	this.defectCount = count;
    }

	/**
	 * @param substring 
	 */
    public void setCreatedTimeStr(final String timeStr) {
    	this.createdTimeStr = timeStr.substring(0,4) + "/" + timeStr.substring(4,6) + "/" + timeStr.substring(6,8)
    			+ " " + timeStr.substring(8,10) + ":" + timeStr.substring(10,12) + ":" + timeStr.substring(12,14);
    }

	/**
	 * @param object 
	 */
    public void setFileName(final String fileName) {
    	this.fileName = fileName;
    }

    
    public void setModulePath(final String modulePath){
    	this.modulePath = modulePath;
    }
    
    public void setStatus(final String status){
    	this.status = status;
    }
    
	/**
	 * @param defect 
	 */
    public void addDefectLog(final Defect defect) {
    	final DefectLog defectLog = new DefectLog(defect);
    	defectLog.setParent(this);
    	
    	this.defectLogList.add(defectLog);
    }

    public void addFunctionList(final String function){
    	this.functionList.add(function); 
    }
    
	/**
	 * @param date2 
	 */
    public void setCreatedTime(final Date date) {
    	this.createdTime = new Date(date.getTime());
    }

	/**
	 * @return the createdTime
	 */
	public Date getCreatedTime() {
		return new Date(createdTime.getTime());
	}
	
	/**
	 * @return 
	 */
    public List<DefectLog> getDefectLogList() {
	    return this.defectLogList;
    }

	/**
	 * @param columnIndex 
	 */
    public String getLabel(final int columnIndex) {
    	if(columnIndex == 0){
    		if(this.createdTime == null) {
    			return "ROOT";
    		} else {
    			return createdTimeStr + " " + this.fileName + " (" + this.defectCount + ")";
    		}
    	}
    	
    	return null;
    }
   

	/**
	 * @return 
	 */
    public String getFileFullPath() {
	    return this.fileFullPath;
    }
    
    public void setFileFullPath(final String fileFullPath) {
	    this.fileFullPath = fileFullPath;
    }

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	
	/**
	 * @return the fileName
	 */
	public String getModulePath() {
		return modulePath;
	}
	
	/**
	 * @return the status
	 */
	public String getStatus(){
		return status;
	}
	
	/**
	 * @return the createdTimeStr
	 */
	public String getCreatedTimeStr() {
		return createdTimeStr;
	}

	public Object getRootLog() {
		return this.rootLog;
	}

	
	public void setRootLog(RootAnalysisLog rootAnalysisLog) {
		this.rootLog = rootAnalysisLog;
	}
}
