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
package com.samsung.sec.dexter.core.config;

public class ProjectAnalysisConfiguration  {

	private String projectName;
	private String projectFullPath;
	private String[] sourceDirs;
	private String[] headerDirs;
	private String[] targetDirs;
	private String type;

	
	public String getCfgKey(){
		return projectName + " - " + projectFullPath;   
	}
	
	/**
	 * @param text 
	 */
    public void setProjectName(final String projectName) {
    	this.projectName = projectName;
    }

	/**
	 * @param text 
	 */
    public void setProjectFullPath(final String projectFullPath) {
    	this.projectFullPath = projectFullPath;
    }

	/**
	 * @param items 
	 */
    public void setSourceDirs(final String[] items) {
    	this.sourceDirs = items;
    }

	/**
	 * @param items 
	 */
    public void setHeaderDirs(final String[] items) {
    	this.headerDirs = items;
    }

	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @return the projectFullPath
	 */
	public String getProjectFullPath() {
		return projectFullPath;
	}

	/**
	 * @return the sourceDirs
	 */
	public String[] getSourceDirs() {
		return sourceDirs == null ? new String[0] : sourceDirs;
	}

	/**
	 * @return the headerDirs
	 */
	public String[] getHeaderDirs() {
		return headerDirs == null ? new String[0] : headerDirs;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(final String type) {
		this.type = type;
	}

	/**
	 * @return the targetDirs
	 */
	public String[] getTargetDirs() {
		return targetDirs == null ? new String[0] : targetDirs;
	}

	/**
	 * @param targetDirs the targetDirs to set
	 */
	public void setTargetDirs(final String[] targetDirs) {
		this.targetDirs = targetDirs;
	}
}
