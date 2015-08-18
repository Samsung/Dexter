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
package com.samsung.sec.dexter.executor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.analyzer.AnalysisResultFileManager;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.IDexterHomeListener;
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.exception.DexterException;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.filter.AnalysisFilterHandler;
import com.samsung.sec.dexter.core.plugin.DexterPluginManager;
import com.samsung.sec.dexter.core.util.DexterClient;
import com.samsung.sec.dexter.core.util.DexterUtil;
import com.samsung.sec.dexter.core.util.PersistenceProperty;

public class DexterAnalyzer implements IDexterHomeListener{
    private static final String CFG_PARM_JSON_FILE = "/cfg/dexter-config-parameter.json";
	private final static Logger LOG = Logger.getLogger(DexterAnalyzer.class);

	private List<IDexterAnalyzerListener> listenerList = new ArrayList<IDexterAnalyzerListener>(1);
	private List<ProjectAnalysisConfiguration> projectAnalysisConfigurationList = null;

	private DexterAnalyzer() {
		DexterConfig.getInstance().addDexterHomeListener(this);
		LOG.debug("DexterAnalyzer");
		initSingletons();
	}
	
	private void initSingletons() {
		PersistenceProperty.getInstance();
		DexterPluginManager.getInstance();
		AnalysisFilterHandler.getInstance();
		AnalysisResultFileManager.getInstance();
		DexterClient.getInstance();
	}

	private static class SaExecutorHolder {
		private static final DexterAnalyzer INSTANCE = new DexterAnalyzer();
	}

	/**
	 * @return DexterAnalyzer
	 */
	public static DexterAnalyzer getInstance() {
		return SaExecutorHolder.INSTANCE;
	}

	public void runSync(final AnalysisConfig config) {
		new DexterAnalyzerThread(config).run();
	}
	
	public void runAsync(final AnalysisConfig config) {
		new DexterAnalyzerThread(config).start();
	}
	
	public void addHeaderAndSourceConfiguration(final AnalysisConfig config) {
		for(final ProjectAnalysisConfiguration param : projectAnalysisConfigurationList){
			if(param.getProjectName().equals(config.getProjectName()) 
					&& DexterUtil.refinePath(param.getProjectFullPath()).equals(config.getProjectFullPath())){
				for(final String dir : param.getSourceDirs()){
					config.addSourceBaseDirList(dir);
				}
				
				for(final String dir : param.getHeaderDirs()){
					config.addHeaderBaseDirList(dir);
				}
			}
		}
    }

	protected void preSendSourceCode(final AnalysisConfig config) {
		for (final IDexterAnalyzerListener listener : listenerList) {
			listener.handlePreSendSourceCode(config);
		}
	}

	protected void postSendSourceCode(final AnalysisConfig config) {
		for (final IDexterAnalyzerListener listener : listenerList) {
			listener.handlePostSendSourceCode(config);
		}
	}

	protected void preRunCodeMetrics(final AnalysisConfig config) {
		for (final IDexterAnalyzerListener listener : listenerList) {
			listener.handlePreRunCodeMetrics(config);
		}
	}

	protected void postRunCodeMetrics(AnalysisConfig config) {
		for (final IDexterAnalyzerListener listener : listenerList) {
			listener.handlePostRunCodeMetrics(config);
		}
	}

	protected void preRunStaticAnalysis(final AnalysisConfig config) {
		for (final IDexterAnalyzerListener listener : listenerList) {
			listener.handlePreRunStaticAnalysis(config);
		}
	}

	protected void postRunStaticAnalysis(final AnalysisConfig config, final List<AnalysisResult> resultList) {
		for (final IDexterAnalyzerListener listener : listenerList) {
			listener.handlePostRunStaticAnalysis(config, resultList);
		}
	}

	/**
	 * 파일 이름으로부터 fileName, fullFilePath, modulePath를 추출한다. 경로명을 구분자를 "/"로 통일
	 * 시킨다.
	 * @param config
	 * @param fileFullPath	should be file full path
	 * @return
	 * @throws DexterException  
	 */
	// TODO: 아래 함수 불필요 시, 테스트 후 삭제
	private void initSourceFileInfo(final AnalysisConfig config, String fileFullPath) {
		String sourceFileName = "";
		if (fileFullPath.indexOf("/") == -1) {
			throw new DexterRuntimeException("target file name should be set as a full file path");
		}
		
		config.setSourceFileFullPath(fileFullPath);
		if (fileFullPath.lastIndexOf("/") + 1 <= fileFullPath.length()) {
			sourceFileName = fileFullPath.substring(fileFullPath.lastIndexOf("/") + 1, fileFullPath.length());
		} else {
			sourceFileName = fileFullPath.substring(fileFullPath.lastIndexOf("/"), fileFullPath.length());
		}

		config.addHeaderBaseDirList(fileFullPath.substring(0, fileFullPath.length() - sourceFileName.length()));
		config.setFileName(sourceFileName);
		
		if (Strings.isNullOrEmpty(config.getModulePath())) {
			for (String src : config.getSourceBaseDirList()) {
				if (fileFullPath.indexOf(src) >= 0) {
					config.setModulePath(fileFullPath.substring(src.length(), fileFullPath.lastIndexOf("/")));
				}
			}

		}

		if (sourceFileName.endsWith(".class")) {
			sourceFileName = sourceFileName.replace(".class", ".java");
		}

		config.setFileName(sourceFileName);

		if (Strings.isNullOrEmpty(config.getSourceFileFullPath())) {
			config.generateSourceFileFullPath();
		}

		final File sourceFile = new File(config.getSourceFileFullPath());
		if (sourceFile.exists() == false) {
			throw new DexterRuntimeException("the source file is not exist : " + config.getSourceFileFullPath());
		}
	}

	public long getDefectGroup(final String projectName) throws DexterException{ 
		return -1;
		// TODO 나중에 구현
//		List<DefectGroup> results;
//        results = DexterClient.getInstance().getDefectGroupByGroupName(projectName);
//        return results.get(0).getId();
	}
	
	public void createDefectGroup(final String projectName) throws DexterException {
		// TODO 나중에 구현
//		final DefectGroup group = new DefectGroup();
//		group.setGroupName(projectName);
//		group.setGroupType("PRJ");
//		DexterClient.getInstance().insertDefectGroup(group);
	}
	
	/**
	 * return defect group id of project name
	 * 
	 * @param projectName
	 * @return long if not exist, create defect group with project name
	 */
	public long getDefectGroupByCreating(final String projectName){
		return 1l;
		// TODO 나중에 구현
//		long defectGroupId = -1l;
//		
//		try{
//			defectGroupId = getDefectGroup(projectName);
//		} catch (DexterException e){
//			try {
//	            createDefectGroup(projectName);
//	            defectGroupId = getDefectGroup(projectName);
//            } catch (DexterException e1) {
//            	try {
//	                defectGroupId = getDefectGroup(projectName);
//                } catch (DexterException e2) {
//                	LOG.error(e2.getMessage(), e2);
//                	return -1;
//                }
//            }
//		}
//		
//		return defectGroupId;
	}

	public void addListener(final IDexterAnalyzerListener listener) {
		if (!listenerList.contains(listener)) {
			listenerList.add(listener);
		}
	}

	public void removeListener(final IDexterAnalyzerListener listener) {
		listenerList.remove(listener);
	}
	
	public void addProjectAnalysisConfiguration(final ProjectAnalysisConfiguration param){
		if(!projectAnalysisConfigurationList.contains(param)){
			projectAnalysisConfigurationList.add(param);
			writeCfgParamToJsonFile();
		}
	}
	
	
	public void removeCfgParam(final ProjectAnalysisConfiguration param){
		projectAnalysisConfigurationList.remove(param);
		writeCfgParamToJsonFile();
	}
	
	private void writeCfgParamToJsonFile(){
		final String dexterHome = DexterConfig.getInstance().getDexterHome();
    	if(Strings.isNullOrEmpty(dexterHome)){
    		LOG.error("cannot write ProjectAnalysisConfiguration List because of Invalid of Dexter Home");
    		return;
    	}
    	
    	final File file = new File(dexterHome + CFG_PARM_JSON_FILE);
    	
    	try {
	        Files.write(new Gson().toJson(this.projectAnalysisConfigurationList), file, Charsets.UTF_8);
        } catch (IOException e) {
        	LOG.error(e.getMessage(), e);
        }
	}
	
	public List<ProjectAnalysisConfiguration> getProjectAnalysisConfigurationList(){
		return projectAnalysisConfigurationList;
	}

	/**
	 * @param key 
	 */
    public void removeCfgParam(final String key) {
    	for(int i = 0; i < projectAnalysisConfigurationList.size(); i++){
    		ProjectAnalysisConfiguration param = projectAnalysisConfigurationList.get(i);
    		if(param.getCfgKey().equals(key)){
    			projectAnalysisConfigurationList.remove(i);
    			break;
    		}
    	}
    }

	/**
	 *  
	 */
    public void removeAllCfgParam() {
    	projectAnalysisConfigurationList.clear();
    	writeCfgParamToJsonFile();
    }

	/**
	 * @param parameter 
	 */
    public void setCfgParam(final ProjectAnalysisConfiguration parameter) {
    	final String key = parameter.getCfgKey();
    	for(int i =0; i<projectAnalysisConfigurationList.size(); i++){
    		ProjectAnalysisConfiguration p = projectAnalysisConfigurationList.get(i);
    		if(p.getCfgKey().equals(key)){
    			projectAnalysisConfigurationList.remove(i);
    			break;
    		}
    	}
    	
    	projectAnalysisConfigurationList.add(parameter);
    	writeCfgParamToJsonFile();
    }

	/**
	 * @param key
	 * @return 
	 */
    public ProjectAnalysisConfiguration getConfParamByKey(final String key) {
    	for(final ProjectAnalysisConfiguration p : projectAnalysisConfigurationList){
    		if(p.getCfgKey().equals(key)){
    			return p;
    		}
    	}
    	
    	return null;
    }

	@Override
    public void handleDexterHomeChanged() {
		initProjectAnalysisConfiguration();
    }
	
	private void initProjectAnalysisConfiguration() {
		final boolean isFirstLoading = projectAnalysisConfigurationList == null;
		
		if(isFirstLoading){
			loadProjectAnalysisConfiguration();
		} else {
			writePreviousConfiguration();
		}
    }

	private void writePreviousConfiguration() {
	    final String cfgFilePath = DexterConfig.getInstance().getDexterHome() + CFG_PARM_JSON_FILE;
	    try {
	    	final Gson gson = new Gson();
	        FileUtils.writeStringToFile(new File(cfgFilePath), gson.toJson(projectAnalysisConfigurationList));
	    } catch (IOException e) {
	        throw new DexterRuntimeException(e.getMessage(), e);
	    }
    }

	private void loadProjectAnalysisConfiguration() {
	    projectAnalysisConfigurationList = new ArrayList<ProjectAnalysisConfiguration>();
	    
	    final String cfgFilePath = DexterConfig.getInstance().getDexterHome() + CFG_PARM_JSON_FILE;
	    DexterUtil.createEmptyFileIfNotExist(cfgFilePath);
	    
	    final String content = DexterUtil.getContentsFromFile(cfgFilePath, Charsets.UTF_8);
	    if(Strings.isNullOrEmpty(content))	return;
	    
	    final Gson gson = new Gson();
	    @SuppressWarnings({ "unchecked", "rawtypes" })
	    final List<Map> list = gson.fromJson(content, List.class);
	    
	    for(@SuppressWarnings("rawtypes") final Map map : list){
	    	final String jsonStr = gson.toJson(map);
	    	ProjectAnalysisConfiguration cfg = gson.fromJson(jsonStr, ProjectAnalysisConfiguration.class);
	    	addProjectAnalysisConfiguration(cfg);
	    }
    }

	public static List<Defect> getAllDefectList(List<AnalysisResult> resultList) {
		assert resultList != null && resultList.size() > 0;
		
		List<Defect> allDefectList = new ArrayList<Defect>();
		
		for(AnalysisResult result : resultList){
			allDefectList.addAll(result.getDefectList());
		}
		
		return allDefectList;
    }

	public static String getSourceFileFullPath(List<AnalysisResult> resultList) {
		assert resultList != null && resultList.size() > 0;
		
		return resultList.get(0).getSourceFileFullPath();
    }

	public static File getResultFile(List<AnalysisResult> resultList) {
		assert resultList != null && resultList.size() > 0 
				&& Strings.isNullOrEmpty(resultList.get(0).getResultFileFullPath()) == false;
		
		return new File(resultList.get(0).getResultFileFullPath());
    }

	public static String getFileName(List<AnalysisResult> resultList) {
		assert resultList != null && resultList.size() > 0 
				&& Strings.isNullOrEmpty(resultList.get(0).getFileName()) == false;
		
		return resultList.get(0).getFileName();
    }
}
