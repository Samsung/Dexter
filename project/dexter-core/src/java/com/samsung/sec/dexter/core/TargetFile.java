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

import org.apache.log4j.Logger;

import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.DexterConfig.LANGUAGE;
import com.samsung.sec.dexter.core.util.DexterUtil;

public class TargetFile {
	static Logger logger = Logger.getLogger(TargetFile.class);

	/** 
	 * only source code file name. except path.
	 * eg) MyClass.java, Main.c, Main.cpp 
	 */
	private String fileName = "";
	
	/** 
	 * it can be used for making a key
	 * in Java : package path  eg) com/samsung/sec/dexter 
	 * in C/C++, Javascript : sub paths from base source directory  eg) Coral/Common/Spi/Debug
	 */
	private String modulePath = "";
	
	private String fileStatus ="";
	
	protected TargetFile(){
		
	}
	
	protected TargetFile(TargetFile other){
		setFileName(other.getFileName());
		setModulePath(other.getModulePath());
		setFileStatus(other.getFileStatus());
	}
	
	/**
	 * Preconditions fileName field should be set befor calling this method.
	 * @param modulePath 
	 * those are relative source file paths related to base source folder instead of absolute path
	 * ex) com/samsung/sec/dexter
	 */
    public void setModulePath(String path) {
    	if(Strings.isNullOrEmpty(path)){
    		//logger.debug("Invalid path parameter : null or empty");
    		return;
    	}
    	
    	path = path.replace("\\", "/");
    	if(getLanguageEnum() == LANGUAGE.JAVA){
    		path = path.replace(".", "/");
    	}
    	
    	path = path.replace(DexterUtil.PATH_SEPARATOR, "/");
    	
    	
    	if(path.endsWith("/")){
			path = path.substring(0, path.length() -1);
		}
    	
    	if(path.startsWith("/")){
			path = path.substring(1, path.length());
		}
    	
    	this.modulePath = path;
    }
    
    
    public void setFileStatus(String fileStatus){
    	this.fileStatus = fileStatus;
    }
    
    /**
	 * @return modulePath
	 * 
	 * in Java : package path  eg) com/samsung/sec/dexter 
	 * in C/C++, Javascript : sub paths from base source directory  eg) Coral/Common/Spi/Debug
	 */
    public String getModulePath() {
	    return this.modulePath;
    }
    
    public String getFileStatus(){
    	return this.fileStatus;
    }
    
    /**
	 * @param fileName
	 * only source code file name. except path.
	 * eg) MyClass.java, Main.c, Main.cpp
	 */
    public void setFileName(String fileName) {
    	fileName = fileName.replace("\\", "").replace("/", "").replace(DexterUtil.PATH_SEPARATOR, "");
    	
	    this.fileName = fileName.replace("/", "");
    }
    
    /**
	 * @param fileName should be only source code file name without path
	 */
    /*
    public void setFileName(String sourceFileName) {
    	if(Strings.isNullOrEmpty(sourceFileName)){
    		logger.error("Invalid parameter of sourceFileName : null or empty");
    		return;
    	}
    	
    	if(sourceFileName.startsWith("/") || sourceFileName.startsWith("\\") || sourceFileName.startsWith(DexterUtil.PATH_SEPARATOR)){
    		sourceFileName = sourceFileName.substring(1, sourceFileName.length());
    	}
    	
    	if(sourceFileName.endsWith("/") || sourceFileName.endsWith("\\") || sourceFileName.endsWith(DexterUtil.PATH_SEPARATOR)){
    		sourceFileName = sourceFileName.substring(0, sourceFileName.length()-1);
    	}
    	
    	this.fileName = sourceFileName;
    }
    */

	/**
	 * @return String fileName
	 * only source code file name. except path.
	 * eg) MyClass.java, Main.c, Main.cpp
	 */
    public String getFileName() {
	    return this.fileName;
    }
    
    /**
	 * @return 
	 */
    public DexterConfig.LANGUAGE getLanguageEnum() {
    	if(Strings.isNullOrEmpty(this.fileName)){
    		logger.error("fileName field is not set yet.");
    		DexterUtil.dumpAllStackTraces(logger);
    		return DexterConfig.LANGUAGE.UNKNOWN;
    	}
    	
    	if(fileName.toLowerCase().endsWith(".java")){
    		return DexterConfig.LANGUAGE.JAVA;
    	} else if(fileName.toLowerCase().endsWith(".cpp") || fileName.toLowerCase().endsWith(".hpp")
    			|| fileName.toLowerCase().endsWith(".c") || fileName.toLowerCase().endsWith(".h")){
    		return DexterConfig.LANGUAGE.CPP;
    	} else if(fileName.toLowerCase().endsWith(".js")){
    		return DexterConfig.LANGUAGE.JAVASCRIPT;
    	} else if(fileName.toLowerCase().endsWith(".cs")){
    		return DexterConfig.LANGUAGE.C_SHARP;
    	} else {
    		return DexterConfig.LANGUAGE.UNKNOWN;
    	}
    }
}
