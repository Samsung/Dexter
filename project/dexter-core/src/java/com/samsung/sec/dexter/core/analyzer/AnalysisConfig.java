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
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.samsung.sec.dexter.core.BaseAnalysisEntity;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.DexterUtil;

public class AnalysisConfig extends BaseAnalysisEntity{
	static Logger logger = Logger.getLogger(AnalysisConfig.class);
	
	/** 
	 *  not mandatory
	 *  only for snapshot and CLI
	 */
	transient private String sourcecode = "";
	
	/** 
	 * eg)  absolute full base directory path for source code files 
	 * 		C:/dev/workspace/project-a/src/java
	 * 		/home/dev/project-a/src 
	 */
	private List<String> sourceBaseDirList = new ArrayList<String>(0);
	
	/** 
	 * only for C/C++
	 * eg)  absolute full base path for C/C++ header files
	 * 		C:/dev/workspace/project-a/inc
	 * 		/home/dev/project-a/include
	 */
	private List<String> headerBaseDirList = new ArrayList<String>(0);
	
	/**  
	 * eg)  absolute full base directory path for compiled files such as *.class, *.obj, *.o
	 * 		C:/dev/workspace/project-a/bin
	 * 		/home/dev/project-a/build/classes
	 * 		/home/dev/project-a/build
	 */
	private String outputDir = "";
	
	/**  
	 * for full base directory path for lib files such as *.lib, *.jar
	 * eg)
	 * 		C:/dev/workspace/project-a/lib/mylibrary.jar
	 * 		/home/dev/project-a/lib/mylibrary.lib
	 */
	private List<String> libDirList = new ArrayList<String>(0);
	
	/**  
	 * full path
	 * ex) C:/dev/workspace/project-a/lib/log4j.jar
	 */
	private List<String> libFileList = new ArrayList<String>(0);

	private boolean shouldSendSourceCode = false;
	
	/** this object will be called after making AnalysisResult such as defectList */
	protected transient EndOfAnalysisHandler resultHandler;
	
    protected AnalysisConfig() {
    	initAllListTypeFields();
    }
    
    public AnalysisConfig(final AnalysisConfig other){
    	super(other);
    	
    	setShouldSendSourceCode(other.shouldSendSourceCode());
    	this.sourceBaseDirList = Lists.newArrayList(other.getSourceBaseDirList());
    	this.headerBaseDirList = Lists.newArrayList(other.getHeaderBaseDirList());
    	setOutputDir(other.getOutputDir());
    	this.libDirList = Lists.newArrayList(other.getLibDirList());
    	this.libFileList = Lists.newArrayList(other.getLibFileList());
    	
    	this.sourcecode = other.sourcecode;
    }
 
	public void initAllListTypeFields(){
		sourceBaseDirList = new ArrayList<String>(0);
		headerBaseDirList = new ArrayList<String>(0);
		libDirList = new ArrayList<String>(0);
		libFileList = new ArrayList<String>(0);
	}
	
	/**
	 * @param libFilePath
	 */
    public void addlibFile(String libFilePath) {
    	if(Strings.isNullOrEmpty(libFilePath)){
    		logger.error("Invalid libFilePath parameter : null or empty");
    		return;
    	}
    	
    	libFilePath = libFilePath.replace("\\", "/").replace(DexterUtil.PATH_SEPARATOR, "/");
    	if(libFileList.contains(libFilePath)){
    		logger.warn("duplicated libFilePath : " + libFilePath);
    		return;
    	}
    	
    	if(libFilePath.endsWith("\\") || libFilePath.endsWith("/")){
			libFilePath = libFilePath.substring(0, libFilePath.length() -1);
		}
    	
    	libFileList.add(libFilePath);
    }

	/**
	 * @return the libFileList
	 */
	public List<String> getlibFileList() {
		return libFileList;
	}

	/**
	 * @return the sourceBaseDirList
	 * eg)  absolute full base path for source code files 
	 * 		C:/dev/workspace/project-a/src/java
	 * 		/home/dev/project-a/src
	 */
	public List<String> getSourceBaseDirList() {
		return sourceBaseDirList;
	}
	
	/**
	 * only for C/C++
	 * @return the headerBaseDirList
	 * eg)  absolute full base path for C/C++ header files 
	 * 		C:/dev/workspace/project-a/inc
	 * 		/home/dev/project-a/include
	 */
	public List<String> getHeaderBaseDirList() {
		return headerBaseDirList;
	}

	/**
	 * @param sourceBaseDirList the sourceBaseDirList to set
	 * eg)  absolute full base path for source code files 
	 * 		C:/dev/workspace/project-a/src/java
	 * 		/home/dev/project-a/src
	 */
	public void setSourceBaseDirList(final List<String> baseSourceFolderList) {
		this.sourceBaseDirList = baseSourceFolderList;
	}
	
	/**
	 * only for C/C++
	 * @param sourceBaseDirList the headerBaseDirList to set
	 * eg)  absolute full base path for C/C++ Header files 
	 * 		C:/dev/workspace/project-a/inc
	 * 		/home/dev/project-a/include
	 */
	public void setHeaderBaseDirList(final List<String> baseHeaderFolderList) {
		this.headerBaseDirList = baseHeaderFolderList;
	}

	/**
	 * @return the outputDir
	 * eg)  absolute full base directory path for compiled files such as *.class, *.obj, *.o
	 * 		C:/dev/workspace/project-a/bin
	 * 		/home/dev/project-a/build/classes
	 * 		/home/dev/project-a/build
	 */
	public String getOutputDir() {
		return outputDir;
	}

	/**
	 * @return the libDirList
	 * for full base directory path for lib files such as *.lib, *.jar
	 * eg)
	 * 		C:/dev/workspace/project-a/lib/mylibrary.jar
	 * 		/home/dev/project-a/lib/mylibrary.lib
	 */
	public List<String> getLibDirList() {
		return libDirList;
	}

	/**
	 * @return the libFileList
	 */
	public List<String> getLibFileList() {
		return libFileList;
	}


	/**
	 * @param outputDir the outputDir to set
	 * eg)  absolute full base directory path for compiled files such as *.class, *.obj, *.o
	 * 		C:/dev/workspace/project-a/bin
	 * 		/home/dev/project-a/build/classes
	 * 		/home/dev/project-a/build
	 */
	public void setOutputDir(String dir) {
		if(Strings.isNullOrEmpty(dir)){
			logger.debug("If you use Java, outputDir parameter(or binDir attribute in "
					+ DexterConfig.DEXTER_CFG_FILENAME + ") is invalid. But if you use C/C++, or Javascript, ignore this message.");
			this.outputDir = "";
			return;
		}
		
		dir = dir.replace("\\", "/").replace(DexterUtil.PATH_SEPARATOR, "/");
		
		if(dir.endsWith("\\") || dir.endsWith("/")){
			dir = dir.substring(0, dir.length() -1);
		}
		
		this.outputDir = dir;
	}

	/**
	 * @param libDirList the libDirList to set
	 * for full base directory path for lib files such as *.lib, *.jar
	 * eg)
	 * 		C:/dev/workspace/project-a/lib/mylibrary.jar
	 * 		/home/dev/project-a/lib/mylibrary.lib
	 */
	public void setLibDirList(final List<String> libDirList) {
		this.libDirList = libDirList;
	}

	/**
	 * @param libFileList the libFileList to set
	 */
	public void setLibFileList(final List<String> libFileList) {
		this.libFileList = libFileList;
	}

	/**
	 * @param dir
	 * eg)  absolute full base path for source code files 
	 * 		C:/dev/workspace/project-a/src/java
	 * 		/home/dev/project-a/src
	 */
    public void addSourceBaseDirList(String dir) {
    	if(Strings.isNullOrEmpty(dir) || this.sourceBaseDirList.contains(dir)){
    		return;
    	}
    	
    	dir = dir.replace("\\", "/").replace(DexterUtil.PATH_SEPARATOR, "/");
    	
    	if(dir.endsWith("\\") || dir.endsWith("/") || dir.endsWith(DexterUtil.PATH_SEPARATOR)){
    		dir = dir.substring(0, dir.length() -1);
		}
    	
    	this.sourceBaseDirList.add(dir);
    }
    
    /**
	 * only for C/C++
	 * @param dir
	 * eg)  absolute full base path for C/C++ header files 
	 * 		C:/dev/workspace/project-a/inc
	 * 		/home/dev/project-a/include
	 */
    public void addHeaderBaseDirList(String dir) {
    	if(Strings.isNullOrEmpty(dir) || this.headerBaseDirList.contains(dir)){
    		return;
    	}
    	
    	dir = dir.replace("\\", "/").replace(DexterUtil.PATH_SEPARATOR, "/");
    	
    	if(dir.endsWith("\\") || dir.endsWith("/") || dir.endsWith(DexterUtil.PATH_SEPARATOR)){
			dir = dir.substring(0, dir.length() -1);
		}
    	
    	this.headerBaseDirList.add(dir);
    }

	/**
	 * for full base directory path for lib files such as *.lib, *.jar
	 * eg)
	 * 		C:/dev/workspace/project-a/lib/mylibrary.jar
	 * 		/home/dev/project-a/lib/mylibrary.lib
	 * 
	 * @param dir
	 */
    public void addLibDirList(String dir) {
	    if(Strings.isNullOrEmpty(dir) || libDirList.contains(dir)){
	    	return;
	    }
	    
	    dir = dir.replace("\\", "/").replace(DexterUtil.PATH_SEPARATOR, "/");
	    
    	if(dir.endsWith("\\") || dir.endsWith("/") || dir.endsWith(DexterUtil.PATH_SEPARATOR)){
			this.libDirList.add(dir.substring(0, dir.length() -1));
		} else {
			this.libDirList.add(dir);
		}
    }

	/**
	 * for full base directory path for lib files such as *.lib, *.jar
	 * eg)
	 * 		C:/dev/workspace/project-a/lib/mylibrary.jar
	 * 		/home/dev/project-a/lib/mylibrary.lib
	 * 
	 * @param file
	 */
    public void addLibFile(String file) {
    	if(Strings.isNullOrEmpty(file) || libFileList.contains(file)){
    		return;
    	}
    	
    	file = file.replace("\\", "/").replace(DexterUtil.PATH_SEPARATOR, "/");
    	
    	libFileList.add(file);
    }


	/**
	 * not mandatory
	 *  only for snapshot and CLI
	 *  
	 * @return the sourcecode
	 */
	public synchronized String getSourcecodeThatReadIfNotExist() {
		if(Strings.isNullOrEmpty(sourcecode)){
			this.sourcecode = DexterUtil.getContentsFromFile(getSourceFileFullPath(), DexterConfig.getInstance().getSourceEncoding());
		}
		
		return sourcecode;
	}

	/**
	 * not mandatory
	 *  only for snapshot and CLI
	 *  
	 * @param sourcecode the sourcecode to set
	 */
//	public void setSourcecode(final String sourcecode) {
//		this.sourcecode = sourcecode;
//	}

    /**
     * @param srcList source folder list : projectName\sourceFolderName
     * @return String
     */
    public String extractBinPathAfterSettingModulePath1(final List<String> srcList, String modulePath, final String fileName) {
    	if(Strings.isNullOrEmpty(outputDir)){
    		logger.error("Can't get return value because of invalid ouputDir value");
    		return "";
    	}
    	
    	String binPath = outputDir + "/";

    	if(Strings.isNullOrEmpty(modulePath) == false){
    		
    	}
    	
		for (String dir : srcList) {
			if(modulePath.startsWith(dir)){
				modulePath = modulePath.replace(dir + "/", "");
				modulePath = modulePath.replace("/" + fileName, "");
				modulePath = modulePath.replace(fileName, "");

				if(Strings.isNullOrEmpty(modulePath) == false){
					setModulePath(modulePath);
					binPath += modulePath + "/";
				}
				break;
			}
		}
		
		return binPath;
    }
    
    /**
     *  @Precondition : outputDir and modulePath should be set
     */
    public String getOutputDirWithModulePath(){
    	return this.outputDir + (Strings.isNullOrEmpty(getModulePath()) ? "/" : "/" + getModulePath() + "/"); 
    }
    
    /**
     * @Precondition : sourceBaseDirList field should be set 
     * 
     * @param projectBaseFileFullPath 
     */
    public void setModulePathWithFileFullPath(final String projectBaseFileFullPath){
    	if(Strings.isNullOrEmpty(projectBaseFileFullPath)){
    		logger.error("Invalid Parameter");
    		return;
    	}
    	
    	String filePath = projectBaseFileFullPath.replace("\\", "/").replace(DexterUtil.PATH_SEPARATOR, "/");
    	
    	for(String srcDir : this.sourceBaseDirList) {
    		if(filePath.startsWith(srcDir)){
    			filePath = filePath.replace(srcDir, "/").replace("//", "/");
    			
    			if(filePath.endsWith("/")){
    				filePath = filePath.substring(0, filePath.length() - 1);
    			}
    			
    			if(filePath.lastIndexOf("/") > 0){
    				filePath = filePath.substring(0, filePath.lastIndexOf("/") -1);
    			}
    			
    			setModulePath(filePath);
    			break;
    		}
    	}
    	
    }
    
    public void setModulePathWithOthers(final String projectBasedFilePath, final String fileName) {
	    for(String srcDir : sourceBaseDirList){
	    	String prjBaseSrcDir = srcDir.replace(getProjectFullPath() + "/", "").replace("//", "/");
	    	if(projectBasedFilePath.startsWith(prjBaseSrcDir)){
	    		prjBaseSrcDir = projectBasedFilePath.replace(prjBaseSrcDir + "/", "").replace("//", "/");
	    		
	    		if(prjBaseSrcDir.indexOf(fileName) > 0){
	    			prjBaseSrcDir = prjBaseSrcDir.replace(fileName, "");
	    			if(Strings.isNullOrEmpty(prjBaseSrcDir) == false && !"/".equals(prjBaseSrcDir)){
	    				setModulePath(prjBaseSrcDir);
	    				return;
	    			}
	    		}
	    	}
	    }
    }
    
    /**
     * create key with using modulePath and source file name
     * @return String
     */
    public String getKey(){
    	String key = "";
		if (Strings.isNullOrEmpty(getModulePath())) {
			key = getFileName();
		} else {
			key = getModulePath() + "/" + getFileName();
		}
		
		return key;
    }
    
	/**
	 * @return String
	 * C:/dev/workspace/project-a/src/MyClass.java
	 * 	/home/dev/project-a/src/Main.cpp
	 */
    public String getOrGenerateSourceFileFullPath() {
    	if(Strings.isNullOrEmpty(getSourceFileFullPath())){
    		generateSourceFileFullPath();
    	}
    	
    	return getSourceFileFullPath();
    }
    
	/**
	 *  @Precondition :  sourceBaseDirList, modulePath, sourceFileName field should be set.
	 *  
	 *  @Description
	 *  make source file full path with using sourceBaseDirList, module path, and source file name
	 *  eg)  absolute full base path for source code files 
	 * 		C:/dev/workspace/project-a/src/java
	 * 		/home/dev/project-a/src
	 */
    public void generateSourceFileFullPath() {
    	assert Strings.isNullOrEmpty(getFileName()) == false;
    	
    	for(String src : this.sourceBaseDirList){
    		String filePath = DexterUtil.makePath(new String[]{src, getModulePath(), getFileName()});
    		
    		if((new File(filePath)).exists()){
    			setSourceFileFullPath(filePath);
    			return;
    		}
    	}
    	
    	logger.error("Could not generate file path for the file : " + getFileName());
    }
    
    public void generateFileNameWithSourceFileFullPath(){
    	assert Strings.isNullOrEmpty(getSourceFileFullPath()) == false;
    	
    	setFileName(new File(getSourceFileFullPath()).getName());
    }
    
    public void generateModulePath() {
    	assert Strings.isNullOrEmpty(getSourceFileFullPath()) == false;
    	assert Strings.isNullOrEmpty(getFileName()) == false;
    	assert this.sourceBaseDirList.size() > 0;
    	
    	for(String sourceDir : sourceBaseDirList){
    		if(handleGeneratingModulePath(sourceDir)) break;
    	}
    }

	private boolean handleGeneratingModulePath(String sourceDir) {
	    if(getSourceFileFullPath().startsWith(sourceDir)){
	    	final int baseIndex = sourceDir.length();
	    	final int endIndex = getSourceFileFullPath().length() - getFileName().length();
	    	
	    	if(endIndex < baseIndex || endIndex < 0 || baseIndex < 0){
	    		throw new DexterRuntimeException("cannot calculate the positions of module path:" 
	    				+ " sourceDir:" + sourceDir
	    				+ " sourceFileFullPath:" + getSourceFileFullPath()
	    				+ " fileName:" + getFileName());
	    		
	    	}
	    	
	    	setModulePath(getSourceFileFullPath().substring(baseIndex, endIndex));
	    	return true;
	    }
	    
	    return false;
    }

	/**
	 * @return 
	 */
    public boolean shouldSendSourceCode() {
	    return this.shouldSendSourceCode;
    }
    
    public void setShouldSendSourceCode(final boolean need){
    	this.shouldSendSourceCode = need;
    }

	public void validateFields() {
		DexterUtil.checkStringField(getFileName());
		DexterUtil.checkStringField(getProjectName());
		DexterUtil.checkStringField(getSourceFileFullPath());
		DexterUtil.checkNullField(resultHandler);
		DexterUtil.checkListFieldHasMoreThanOne(sourceBaseDirList);
    }
	
	public void setResultHandler(final EndOfAnalysisHandler resultHandler) {
		this.resultHandler = resultHandler;
	}
	
	public EndOfAnalysisHandler getResultHandler(){
		return this.resultHandler;
	}
}

