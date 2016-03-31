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
package com.samsung.sec.dexter.findbugs.plugin; 

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisEntityFactory;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.analyzer.IAnalysisEntityFactory;
import com.samsung.sec.dexter.core.checker.Checker;
import com.samsung.sec.dexter.core.checker.CheckerConfig;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;

import edu.umd.cs.findbugs.BugRanker;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.DetectorFactory;
import edu.umd.cs.findbugs.DetectorFactoryCollection;
import edu.umd.cs.findbugs.FindBugs2;
import edu.umd.cs.findbugs.Project;
import edu.umd.cs.findbugs.config.ProjectFilterSettings;
import edu.umd.cs.findbugs.config.UserPreferences;

public class FindBugsWrapper {
	private static DetectorFactoryCollection DETECTOR_FACTORY_COLLECTION = DetectorFactoryCollection.instance();
	private static UserPreferences PREFERENCES = UserPreferences.createDefaultUserPreferences();

	private CheckerConfig checkerConfig = new CheckerConfig(FindbugsDexterPlugin.PLUGIN_NAME, DexterConfig.LANGUAGE.JAVA);

	private Project project = new Project();
	
	private final static Logger LOG = Logger.getLogger(FindBugsWrapper.class);

	public FindBugsWrapper() {
	}


	public synchronized AnalysisResult execute(AnalysisConfig config){
		assert config != null;
		
		
		IAnalysisEntityFactory factory = new AnalysisEntityFactory();
		AnalysisResult result = factory.createAnalysisResult(config);
		
		LOG.debug(result.getFileName() + "is being analyzed");

		FindBugs2 engine = new FindBugs2();
		engine.setDetectorFactoryCollection(DETECTOR_FACTORY_COLLECTION);
		
//		Project project = new Project();
		engine.setProject(project);
		engine.setNoClassOk(true);
		engine.setUserPreferences(PREFERENCES);
		
		setFindBugsProject(project, config);

		try {
			JsonBugReporter bugReporter = new JsonBugReporter(project, checkerConfig, result);
			bugReporter.setPriorityThreshold(Detector.LOW_PRIORITY); // LOW :
			                                                         // More
			                                                         // defects
			bugReporter.setRankThreshold(BugRanker.VISIBLE_RANK_MAX); // MAX :
			                                                          // More
			                                                          // defects
			engine.setBugReporter(bugReporter);
			result.setProjectName(engine.getProject().getProjectName());

			engine.execute();
			
			LOG.debug(result.getFileName() + "is analyzed completely.");
			return result;
		} catch (IOException e) {
			throw new DexterRuntimeException(e.getMessage() + " file:" + result.getSourceFileFullPath(), e);
		} catch (InterruptedException e) {
			throw new DexterRuntimeException(e.getMessage() + " file:" + result.getSourceFileFullPath(), e);
		} catch (Exception e) {
			throw new DexterRuntimeException(e.getMessage() + " file:" + result.getSourceFileFullPath(), e);
		}
	}
	
	/**
	 * Project has a analysis target information.
	 * create project info from Analysis Config
	 */
	private void setFindBugsProject(Project project, AnalysisConfig config) {
		project.getFileList().clear();
		addClassFileToBeAnalyzed(project, config);				

		project.setProjectName(config.getProjectName());
		LOG.debug("FindBugsWrapper is being configured");

		project.addFile(config.getSourceFileFullPath());
		
		project.getSourceDirList().clear();
		for (String dir : config.getSourceBaseDirList()) {
			project.addSourceDir(dir);
		}

		project.getAuxClasspathEntryList().clear();
		
		if (Strings.isNullOrEmpty(config.getOutputDir()) == false) {
			project.addAuxClasspathEntry(config.getOutputDir());
		}

		for (String libFile : config.getLibFileList()) {
			project.addAuxClasspathEntry(libFile);
		}

		for (String libDir : config.getLibDirList()) {
			addAuxClasspathEntry(project, libDir);
		}
	}
	
	/**
	 *   In Java and CLI, we can't recognize the subclass or inner class in a file.
	 *   this method finds subclass and inner class in the target file.
	 */
	private void addClassFileToBeAnalyzed(final Project project, final AnalysisConfig config) {
		final List<String> classFileList = JdtUtil.getClassAndSubClassFullPathList(config.getOutputDir(),
				config.getModulePath(), config.getSourceFileFullPath());
		
		for(String file : classFileList){
			project.addFile(file);
		}
	}
	
	private void addAuxClasspathEntry(final Project project, final String baseLibDir){
		final File libDir = new File(baseLibDir);
		if(libDir.isDirectory() == false || libDir.listFiles() == null || libDir.listFiles().length <= 0){
			return;
		}
		
		for(final File libFile : libDir.listFiles()){
			if(libFile.isFile() == false || libFile.getName().toLowerCase().endsWith(".jar") == false){
				continue;
			}
			
			project.addAuxClasspathEntry(libFile.getPath());
		}
	}


	/**
	 * TODO should create this from DB or default json file
	 */
	protected synchronized void initCheckerConfig() {
		try{
			Reader reader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("checker-config.json"));
			
			Gson gson = new Gson();
			this.checkerConfig = gson.fromJson(reader, CheckerConfig.class);
			
			initEnableDetector();
		} catch (Exception e){
			throw new DexterRuntimeException(e.getMessage(), e);
		}
	}
	
	/**
	 * @param checkerConfig
	 *            void
	 */
	private void initEnableDetector() {
		PREFERENCES.enableAllDetectors(false);

		final ProjectFilterSettings filter = ProjectFilterSettings.createDefault();
		filter.clearAllCategories();

		for(final Checker checker : this.checkerConfig.getCheckerList()){
			if(!checker.isActive()){
				continue;
			}
			
			String detectorName = checker.getCategoryName();
			final DetectorFactory detectorFactory =  DETECTOR_FACTORY_COLLECTION.getFactory(detectorName);
			if(detectorFactory != null){
				PREFERENCES.enableDetector(detectorFactory, true);
			}
		}
	}

	/**
	 * @return CheckerConfig
	 */
	public CheckerConfig getCheckerConfig() {
		if (this.checkerConfig == null) {
			initCheckerConfig();
		}
		synchronized (this.checkerConfig) {	
			return this.checkerConfig;
		}
	}
	
	public void setCheckerConfig(CheckerConfig checkerConfig) {
		this.checkerConfig = checkerConfig;
    }
}
