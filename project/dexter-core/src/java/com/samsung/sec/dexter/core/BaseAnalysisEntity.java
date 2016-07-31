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
package com.samsung.sec.dexter.core;

import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.metrics.CodeMetrics;
import com.samsung.sec.dexter.core.metrics.FunctionMetrics;
import com.samsung.sec.dexter.core.util.DexterUtil;

public class BaseAnalysisEntity extends TargetFile {
	/** Logical Project Name (like PLM) */
	private String projectName = "";
	
	/** 
	 * Project full base directory path on developers' machine.
	 * eg) C:/dev/workspace/myproject  
	 */
	private transient String projectFullPath = "";
	
	/** 
	 * source file full path
	 *  
	 *  C:/dev/workspace/project-a/src/MyClass.java
	 * 	/home/dev/project-a/src/Main.cpp
	 */
	private transient String sourceFileFullPath = "";
	
	private transient String resultFileFullPath = "";
	
	/** static analysis group ID for the whole project. only for the whole project */
	private long defectGroupId = 1;	// TODO 현재 사용하지 않으므로 default project 1
	
	/** 
	 *   ID for the snapshot of static analysis for the whole project
	 *   you can use timestamp long value : System.currentTimeMillis()
	 *   default : -1 (means that this is not for snapshot)
	 */
	private long snapshotId = -1;
	
	/** the result of code metrics */
	private CodeMetrics codeMetrics = new CodeMetrics();
	private FunctionMetrics functionMetrics = new FunctionMetrics();
	
	protected BaseAnalysisEntity(){}
	
	protected BaseAnalysisEntity(BaseAnalysisEntity entity) {
		super(entity);
		
		setProjectName(entity.getProjectName());
		setProjectFullPath(entity.getProjectFullPath());
		setDefectGroupId(entity.getDefectGroupId());
		setSnapshotId(entity.getSnapshotId());
		setSourceFileFullPath(entity.getSourceFileFullPath());
		setResultFileFullPath(entity.getResultFileFullPath());
		setCodeMetrics(entity.getCodeMetrics());
		setFunctionMetrics(entity.getFunctionMetrics());
    }

	/**
	 * @return the projectName Logical Project Name (like PLM)
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @param projectName Logical Project Name (like PLM)
	 */
	public void setProjectName(final String projectName) {
		this.projectName = projectName;
	}

	/**
	 * @return the projectFullPath
	 * Project full base path on developers' machine.
	 * eg) C:/dev/workspace/project-a
	 */
	public String getProjectFullPath() {
		return projectFullPath;
	}
	
	/**
	 * @param projectFullPath
	 * Project full base path on developers' machine.
	 * eg) C:/dev/workspace/project-a
	 */
	public void setProjectFullPath(String projectFullPath) {
//		if(Strings.isNullOrEmpty(projectFullPath)){
//			logger.error("Invalid projectFullPath parameter : null or empty");
//			return;
//		}
		
		projectFullPath = projectFullPath.replace("\\", "/").replace(DexterUtil.FILE_SEPARATOR, "/");
		
		if(projectFullPath.endsWith("\\") || projectFullPath.endsWith("/")){
			this.projectFullPath = projectFullPath.substring(0, projectFullPath.length() -1);
		} else {
			this.projectFullPath = projectFullPath;
		}
	}
	
	/**
	 * @param snapshotId
	 * ID for the snapshot of static analysis for the whole project
	 *   you can use timestamp long value : System.currentTimeMillis()
	 *   default : -1 (means that this is not for snapshot)
	 */
    public void setSnapshotId(final long snapshotId) {
    	this.snapshotId = snapshotId;
    }

	/**
	 * @return the snapshotId
	 * ID for the snapshot of static analysis for the whole project
	 *   you can use timestamp long value : System.currentTimeMillis()
	 *   default : -1 (means that this is not for snapshot)
	 */
	public long getSnapshotId() {
		return snapshotId;
	}

	public String getSourceFileFullPath() {
    	return sourceFileFullPath;
    }

	public void setSourceFileFullPath(String sourceFileFullPath) {
    	this.sourceFileFullPath = DexterUtil.refinePath(sourceFileFullPath);
    }

	public String getResultFileFullPath() {
    	return resultFileFullPath;
    }

	public void setResultFileFullPath(String resultFileFullPath) {
    	this.resultFileFullPath = DexterUtil.refinePath(resultFileFullPath);
    }
	
    public void createResultFileFullPath() {
    	assert Strings.isNullOrEmpty(getProjectName()) == false;
    	assert Strings.isNullOrEmpty(getProjectFullPath()) == false;
    	assert Strings.isNullOrEmpty(getSourceFileFullPath()) == false;
    	
    	this.resultFileFullPath = DexterConfig.getInstance().getDexterHome() 
    			+ "/" + DexterConfig.RESULT_FOLDER_NAME + "/daemon/";
    	
    	if(sourceFileFullPath.startsWith("\\") || sourceFileFullPath.startsWith("/")){
    		this.resultFileFullPath += sourceFileFullPath;
    	} else if(sourceFileFullPath.indexOf(":") == 1){
    		this.resultFileFullPath +=  sourceFileFullPath.substring(3);
    	}
    	
    	this.resultFileFullPath = DexterUtil.refinePath(this.resultFileFullPath);
    }
    
    public CodeMetrics getCodeMetrics() {
	    return this.codeMetrics;
    }
    
    public FunctionMetrics getFunctionMetrics(){
    	return this.functionMetrics;
    }

    public void setCodeMetrics(final CodeMetrics metrics) {
	    this.codeMetrics = metrics;
    }
    
    public void setFunctionMetrics(final FunctionMetrics metrics){
    	this.functionMetrics = metrics;
    }
	public long getDefectGroupId() {
		return defectGroupId;
	}

	public void setDefectGroupId(final long defectGroupId) {
		// TODO : GroupID를 사용할 때 오픈하여 활성화
		// this.defectGroupId = defectGroupId;
	}
	
	public void setNoDefectGroupAndSnapshotId() {
		this.snapshotId = -1;
		this.defectGroupId = -1;
    }
	
	
}
