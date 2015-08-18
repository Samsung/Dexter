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

import com.google.common.base.Objects;
import com.google.common.base.Strings;

public class BaseDefect extends TargetFile {
	protected String checkerCode = "";
	protected String className = "";
	protected String methodName = "";
	protected String toolName = "";
	protected String language = "";
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if(obj == null){
			return false;
		}
		
		if(!(obj instanceof BaseDefect)){
			return false;
		}
		
		/////////////////////////////////////////////////////////////
		
		BaseDefect other = (BaseDefect) obj;
		
		if(!Objects.equal(checkerCode, other.getCheckerCode())){
			return false;
		}
		
		if(!Strings.isNullOrEmpty(methodName) && !Objects.equal(methodName, other.getMethodName())){
			return false;
		}
		
		if(!Strings.isNullOrEmpty(className) && !Objects.equal(className, other.getClassName())){
			return false;
		}
		
		if(!Strings.isNullOrEmpty(getFileName()) && !Objects.equal(getFileName(), other.getFileName())){
			return false;
		}
		
		if(!Strings.isNullOrEmpty(getModulePath()) && !Objects.equal(getModulePath(), other.getModulePath())){
			return false;
		}
		
		if(!Strings.isNullOrEmpty(toolName) && !Objects.equal(toolName, other.getToolName())){
			return false;
		}
		
		if(language != null && !Objects.equal(language, other.getLanguage())){
			return false;
		}
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
	    return Objects.hashCode(getFileName(), getModulePath(), checkerCode, className, methodName);
	}
	
	/**
	 * @param className void
	 */
    public void setClassName(final String className) {
    	if("null".equals(className) || Strings.isNullOrEmpty(className)){
    		this.className = "";
    	} else {
    		this.className = className;
    	}
    }
    
    /**
	 * @param methodName void
	 */
    public void setMethodName(final String methodName) {
    	if("null".equals(methodName) || Strings.isNullOrEmpty(methodName)){
    		this.methodName = "";
    	} else {
    		this.methodName = methodName;
    	}
    }
    /**
	 * @param abbrev void
	 */
    public void setCheckerCode(final String checkerCode) {
    	if(Strings.isNullOrEmpty(checkerCode)){
			logger.error("Invalid Parameter : checkerCode is null or empty");
			return;
		}
    	
    	this.checkerCode = checkerCode;
    }
    /**
	 * @return the toolName
	 */
	public String getToolName() {
		return toolName;
	}

	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}
	
	
	
    
    /**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @return the methodName
	 */
	public String getMethodName() {
		return methodName;
	}
	
	/**
	 * @return the checkerCode
	 */
	public String getCheckerCode() {
		return checkerCode;
	}
	
	/**
	 * @param toolName the toolName to set
	 */
	public void setToolName(final String toolName) {
		this.toolName = toolName;
	}

	/**
	 * @param language the language to set
	 */
	public void setLanguage(final String language) {
		this.language = language;
	}
}
