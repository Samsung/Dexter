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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisEntityFactory;
import com.samsung.sec.dexter.core.analyzer.IAnalysisEntityFactory;
import com.samsung.sec.dexter.core.analyzer.ResultFileConstant;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.DexterClient;
import com.samsung.sec.dexter.core.util.DexterUtil;

public class DexterConfigFile {
	static Logger logger = Logger.getLogger(DexterConfigFile.class);
	private String dexterHome;
	private String dexterServerIp;
	private int dexterServerPort;
	private String projectName;
	private String projectFullPath;
	private List<String> sourceDirList;
	private List<String> headerDirList;
	private List<String> functionList;
	private String sourceEncoding;
	private List<String> libDirList;
	private String binDir;
	private String language;
	private String modulePath;
	private List<String> fileNameList;
	private String resultFileFullPath;
	private String snapshotId;
	
	private Type type = Type.FILE;

	public enum Type {
		FILE, FOLDER, PROJECT, SNAPSHOT;
		
		public static boolean hasValue(final String value){
			if(Strings.isNullOrEmpty(value)) return false;
			
			for(Type type : Type.values()){
				if(type.toString().equals(value)){
					return true;
				}
			}
			
			return false;
		}
	};
	
	public void loadFromFile(final File file) {
		Map<String, Object> configMap = getConfigurationMap(file);
		setFields(configMap);

	}

	protected Map<String, Object> getConfigurationMap(final File confFile) {
		if (confFile.exists() == false) {
			throw new DexterRuntimeException("There is no " + DexterConfig.DEXTER_CFG_FILENAME 
					+ " file : " + confFile.getName());
		}

		final StringBuilder confJson = new StringBuilder(50);

		try {
			for (String content : Files.readLines(confFile, Charsets.UTF_8)) {
				confJson.append(content.replace('\\', '/').replace("//", "/"));
				if (content.indexOf("}") >= 0) {
					break;
				}
			}

			final Gson gson = new Gson();
            @SuppressWarnings("unchecked")
			Map<String, Object> configMap = gson.fromJson(confJson.toString(), Map.class);
			addAdditionalInfoWhenDaemon(configMap);
			
			return configMap;
		} catch (IOException e) {
			throw new DexterRuntimeException(e.getMessage(), e);
		} catch (com.google.gson.JsonSyntaxException e) {
			throw new DexterRuntimeException("Dexter Configuration Json file is not valid. " + e.getMessage(), e);
		}
	}

	private void addAdditionalInfoWhenDaemon(Map<String, Object> configMap) {
		if(DexterConfig.getInstance().getRunMode() != DexterConfig.RunMode.DAEMON){
			return;
		}
		
		configMap.put("dexterHome", DexterConfig.getInstance().getDexterHome());
		configMap.put("sourceDir", generateSourceDirListForDaemon(configMap));
		configMap.put("dexterServerPort", DexterClient.getInstance().getServerPort());
		
		try {
			configMap.put("dexterServerIp", DexterClient.getInstance().getServerHost());
		} catch (DexterRuntimeException e) {
			configMap.put("dexterServerIp", "");
		}
		
    }

	private Object generateSourceDirListForDaemon(Map<String, Object> configMap) {
		String projectFullPath = (String) configMap.get("projectFullPath");
		List<String> srcDirList = new ArrayList<String>(1);
		srcDirList.add(projectFullPath);
		
		return srcDirList;
    }

	public AnalysisConfig toAnalysisConfig() {
		IAnalysisEntityFactory configFactory = new AnalysisEntityFactory();
		final AnalysisConfig analysisConfig = configFactory.createAnalysisConfig();
		
		analysisConfig.setProjectName(getProjectName());
		analysisConfig.setProjectFullPath(getProjectFullPath() + "/");
		analysisConfig.setOutputDir(getBinDir());
		analysisConfig.setModulePath(getModulePath());
		analysisConfig.setSourceBaseDirList(getSourceDirList());
		analysisConfig.setHeaderBaseDirList(getHeaderDirList());
		analysisConfig.setLibDirList(getLibDirList());
		analysisConfig.setSnapshotId(generateSnapshotId());
		analysisConfig.setResultFileFullPath(getResultFileFullPath());
		analysisConfig.setFunctionList(getFunctionList());

		return analysisConfig;
	}

	public void setFields(final Map<String, Object> params) {
		checkDexterConfigMap(params);

		setDexterHome((String) params.get("dexterHome"));
		setDexterServerIp((String) params.get("dexterServerIp"));
		try{
			setDexterServerPort(DexterUtil.getIntFromMap(params, "dexterServerPort"));
		}catch (DexterRuntimeException e){
			// do nothing
		}
		
		setProjectName((String) params.get(ResultFileConstant.PROJECT_NAME));
		setProjectFullPath((String) params.get("projectFullPath") + "/");
		setSourceDirList(getStringListFromMap(params, "sourceDir"));
		setHeaderDirList(getStringListFromMap(params, "headerDir"));
		setSourceEncoding((String) params.get("sourceEncoding"));
		setLibDirList(getStringListFromMap(params, "libDir"));
		setBinDir((String) params.get("binDir"));
		setType((String) params.get("type"));
		setModulePath((String) params.get(ResultFileConstant.MODULE_PATH));
		setFileNameList(getStringListFromMap(params, ResultFileConstant.FILE_NAME));
		setResultFileFullPath((String) params.get("resultFileFullPath"));
		setFunctionList(getStringListFromMap(params, "functionList"));
		setSnapshotId((String) params.get(ResultFileConstant.SNAPSHOT_ID));
	}

	private void setType(String value) {
		if(Type.hasValue(value)){
			this.type = Type.valueOf(value);
		}
	}
	

	@SuppressWarnings("unchecked")
	private List<String> getStringListFromMap(final Map<String, Object> map, String key) {
		if (null == map.get(key) || (map.get(key) instanceof ArrayList) == false) {
			return new ArrayList<String>(0);
		}

		return (ArrayList<String>) map.get(key);
	}

	private long generateSnapshotId() {
		if ("SNAPSHOT".equals(type.toString()) == false){
			return -1;
		}
		
		if(Strings.isNullOrEmpty(this.snapshotId)){
			return System.currentTimeMillis();
		} else {
			return Integer.parseInt(this.snapshotId);
		}
	} 

	private void checkDexterConfigMap(final Map<String, Object> map) {
		checkNullofMap(map);
		checkFieldExistence(map);
		checkFolderExistence(map);
		checkTypeAndFollowingFields(map);
	}

	private void checkNullofMap(final Map<String, Object> map) {
		if (map == null || map.size() == 0)
			throw new DexterRuntimeException("Dexter Configuration Error : empty");
	}

	private void checkFieldExistence(final Map<String, Object> map) {
		checkFieldEmptyInDexterConfigurationMap(map, ResultFileConstant.PROJECT_NAME);
		checkFieldEmptyInDexterConfigurationMap(map, "projectFullPath");
		checkFieldEmptyInDexterConfigurationMap(map, "sourceEncoding");
		checkFieldEmptyInDexterConfigurationMap(map, "type");
	}

	private void checkFolderExistence(final Map<String, Object> map) {
		DexterUtil.checkFolderExistence(map, "projectFullPath");
	}

	private void checkTypeAndFollowingFields(final Map<String, Object> map) {
		final String type = (String) map.get("type");

		if (!"FILE".equalsIgnoreCase(type) && !"FOLDER".equalsIgnoreCase(type) && !"PROJECT".equalsIgnoreCase(type)
		        && !"SNAPSHOT".equalsIgnoreCase(type)) {
			throw new DexterRuntimeException("'type' field can be {FILE,FOLDER,PROJECT,SNAPSHOT}. your input : " + type);
		}

		if ("FILE".equalsIgnoreCase(type)) {
			checkFieldEmptyInDexterConfigurationMap(map, ResultFileConstant.FILE_NAME);
		}
	}

	private void checkFieldEmptyInDexterConfigurationMap(final Map<String, Object> map, final String key) {
		if (null == map.get(key))
			throw new DexterRuntimeException("Dexter Configuration Error : '" + key + "' field is empty");
	}

	public String getDexterHome() {
		return dexterHome;
	}

	public void setDexterHome(String dexterHome) {
		this.dexterHome = dexterHome;
	}

	public String getDexterServerIp() {
		return dexterServerIp;
	}

	public void setDexterServerIp(String dexterServerIp) {
		this.dexterServerIp = dexterServerIp;
	}

	public int getDexterServerPort() {
		return dexterServerPort;
	}

	public void setDexterServerPort(int dexterServerPort) {
		this.dexterServerPort = dexterServerPort;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectFullPath() {
		return projectFullPath;
	}

	public void setProjectFullPath(String projectFullPath) {
		this.projectFullPath = projectFullPath;
	}

	public List<String> getSourceDirList() {
		return sourceDirList;
	}

	public void setSourceDirList(List<String> sourceDirList) {
		this.sourceDirList = sourceDirList;
	}

	public List<String> getHeaderDirList() {
		return headerDirList;
	}

	public void setHeaderDirList(List<String> headerDirList) {
		this.headerDirList = headerDirList;
	}

	public List<String> getFunctionList(){
		return this.functionList;
	}
	
	public void setFunctionList(List<String> functionList){
		this.functionList = functionList;
	}

	public String getSourceEncoding() {
		return sourceEncoding;
	}

	public void setSourceEncoding(String sourceEncoding) {
		this.sourceEncoding = sourceEncoding;
	}

	public List<String> getLibDirList() {
		return libDirList;
	}

	public void setLibDirList(List<String> libDirList) {
		this.libDirList = libDirList;
	}

	public String getBinDir() {
		return binDir;
	}

	public void setBinDir(String binDir) {
		this.binDir = binDir;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getModulePath() {
		return modulePath;
	}

	public void setModulePath(String modulePath) {
		this.modulePath = (modulePath == null) ? "" : modulePath;
	}

	public List<String> getFileNameList() {
		return fileNameList;
	}

	public void setFileNameList(List<String> fileNameList) {
		this.fileNameList = fileNameList;
	}

	public String getResultFileFullPath() {
    	return resultFileFullPath;
    }

	public void setResultFileFullPath(String resultFileFullPath) {
    	this.resultFileFullPath = resultFileFullPath;
    }

	public Type getType() {
    	return type;
    }

	public void setType(Type type) {
    	this.type = type;
    }

	public List<String> generateSourceFileFullPathList() {
		assert type != null;
		
		List<String> sourceFileFullPathList = new ArrayList<String>(0);
		
		if(this.fileNameList == null) return sourceFileFullPathList;
		
		if(type == Type.FILE){
			sourceFileFullPathList = generateSourceFileFullPathListAsFileType();
		} else if(type == Type.FOLDER){
			sourceFileFullPathList = generateSourceFileFullPathListAsFolderType();
		} else {
			sourceFileFullPathList = generateSourceFileFullPathListAsProjectType();
		}
		
		return sourceFileFullPathList;
	}

	private List<String> generateSourceFileFullPathListAsFileType() {
		final List<String> sourceFileFullPathList = new ArrayList<String>(this.fileNameList.size());
		
		for(String fileName : this.fileNameList){
			String filePathFromModule = this.modulePath + "/" + fileName;
			String fileFullPath = getExistingFileFullPathWithSourceDirList(filePathFromModule);
			if(!"".equals(fileFullPath)){
				sourceFileFullPathList.add(fileFullPath);
			}
		}
		
		return sourceFileFullPathList;
    }

	private String getExistingFileFullPathWithSourceDirList(String filePathFromModule){
		for(String srcDir : this.sourceDirList){
			String fileFullPath = DexterUtil.refinePath(srcDir + "/" + filePathFromModule);
			
			if(new File(fileFullPath).exists()){
				return fileFullPath;
			}
		}
		
		return "";
	}
	
	private List<String> generateSourceFileFullPathListAsFolderType() {
		final String moduleFullPath = getExistingModuleFullPathWithSourceDirList();
		
		if("".equals(moduleFullPath)){
			return new ArrayList<String>(0);
		}
		
		List<String> sourceFileFullPathList = new ArrayList<String>(10);
		for(String filePath : new File(moduleFullPath).list()){
			final File file = new File(filePath);
			
			if(file.isDirectory()){
				continue;
			}
			
			if(file.length() > 0L){
				sourceFileFullPathList.add(filePath);
			}
		}
		
		return sourceFileFullPathList;
    }
	
	private String getExistingModuleFullPathWithSourceDirList(){
		for(String srcDir : this.sourceDirList){
			String moduleFullPath = DexterUtil.refinePath(srcDir + "/" + this.modulePath);
			
			if(new File(moduleFullPath).exists()){
				return moduleFullPath;
			}
		}
		
		return "";
	}
	
	private List<String> generateSourceFileFullPathListAsProjectType() {
		List<String> sourceFileFullPathList = new ArrayList<String>(50);
		
		for(String srcDir : this.sourceDirList){
			File baseFile = new File(srcDir);
			addSourceFileFullPathHierachy(baseFile, sourceFileFullPathList);
		}
		
		return sourceFileFullPathList;
    }

	private void addSourceFileFullPathHierachy(File baseFile, List<String> sourceFileFullPathList) {
		if(baseFile.isFile()){
			sourceFileFullPathList.add(DexterUtil.refinePath(baseFile.getAbsolutePath()));
		} else {
			for(File subFile : baseFile.listFiles()){
				addSourceFileFullPathHierachy(subFile, sourceFileFullPathList);
			}
		}
    }

	public String getFirstFileName() {
	    return this.fileNameList.get(0);
    }

	public String getSnapshotId() {
    	return snapshotId;
    }

	public void setSnapshotId(String snapshotId) {
    	this.snapshotId = snapshotId;
    }
}
