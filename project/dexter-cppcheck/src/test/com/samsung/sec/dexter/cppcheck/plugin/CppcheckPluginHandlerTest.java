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

package com.samsung.sec.dexter.cppcheck.plugin;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisEntityFactory;
import com.samsung.sec.dexter.core.analyzer.AnalysisResultChangeHandlerForUT;
import com.samsung.sec.dexter.core.analyzer.IAnalysisEntityFactory;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.DexterConfig.RunMode;
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.defect.Occurence;
import com.samsung.sec.dexter.core.plugin.PluginDescription;

public class CppcheckPluginHandlerTest {
	static {
		// DEXTER_HOME for only this test. If you use any Dexter Client, you don't need to setup this environment variable.
		DexterConfig.getInstance().setDexterHome("C:/DEV/temp/dexter-cli_1.1.0_64");
		DexterConfig.getInstance().setRunMode(RunMode.CLI);
	}
	

	@Test
	public void test_creation() {
		CppcheckDexterPlugin handler = new CppcheckDexterPlugin();
		//handler.setTestMode(true); // Only for testing - use temporary dexter home which has cppcheck-binaries
		
		handler.init();
	
		assertNotNull(handler);
		
		PluginDescription desc = handler.getDexterPluginDescription();
//		assertTrue("cppcheck".equals(desc.get3rdPartyName()));
		assertTrue(CppcheckDexterPlugin.PLUGIN_NAME.equals(desc.getPluginName()));
		assertTrue("Dexter plug-in for Cppcheck".equals(desc.getDescription()));
	}
	
	@Test
	public void test_analysis_single_file(){
		// 1. Basic Target Information
		String projectName = "test-cpp-project";
		// ##### before testing, change projectFullPath value for your test environment ####
		String projectFullPath = "C:/DEV/workspace/dexter/dexter-cppcheck/test-cpp-project";
//		String projectFullPath = "D:/P4/P4_Tizen/TZTV_2.2.1/Broadcast-mw_Prj/tvs-chms";
		String toolName = CppcheckDexterPlugin.PLUGIN_NAME;
		String language = DexterConfig.LANGUAGE.CPP.toString();
//		String fileName = "ChannelConverter.cpp";
		String fileName = "main.cpp";
		
		// 2. Parameter for Static Analysis
		IAnalysisEntityFactory analysisFactory = new AnalysisEntityFactory();
		AnalysisConfig config = analysisFactory.createAnalysisConfig();

		// 2.1 Initialize AnalysisResult : add testing data
		AnalysisResultChangeHandlerForUT resultHandler = new AnalysisResultChangeHandlerForUT();
		setTestData(projectName, toolName, language, fileName, resultHandler);
		config.setResultHandler(resultHandler);

		// 2.2 Initialize AnalysisConfig : configure target 
		config.setProjectName(projectName);
		config.setProjectFullPath(projectFullPath);	
		config.addHeaderBaseDirList(projectFullPath + "/inc");
		config.addSourceBaseDirList(projectFullPath + "/src");
//		config.setModulePath("api");	// if you have any folders between sourceBaseDir and target file name
		config.setFileName(fileName);
		config.generateSourceFileFullPath();
		
		// 3. Execute Static Analysis
		CppcheckDexterPlugin handler = new CppcheckDexterPlugin();
//		handler.setTestMode(true); // Only for testing - use temporary dexter home which has cppcheck-binaries
		
		handler.init();
		handler.analyze(config);
	}

	private void setTestData(String projectName, String toolName, String language, String fileName,
            AnalysisResultChangeHandlerForUT resultHandler) {
	    // SET the testing data (expected value)
		resultHandler.setExpectedDefectCount(4);
		resultHandler.setExpectedProjectName(projectName);
		resultHandler.setExpectedFileName(fileName);

		Defect d = new Defect();
		// don't set the message, because it will be created automatically by adding Occurence
		d.setSeverityCode("ETC");
		d.setCategoryName("security");
		d.setCheckerCode("unreachableCode");
		d.setToolName(toolName);
		d.setLanguage(language);
		d.setFileName(fileName);
		
		Occurence o = new Occurence();
		o.setStartLine(15);
		o.setEndLine(15);
		o.setMessage("Statements following return, break, continue, goto or throw will never be executed.");
		d.addOccurence(o);
		
		resultHandler.addExpectedDefect(d);
		
		d = new Defect();
		// don't set the message, because it will be created automatically by adding Occurence
		d.setSeverityCode("MIN");
		d.setCheckerCode("unreadVariable");
		d.setToolName(toolName);
		d.setLanguage(language);
		d.setFileName(fileName);
		
		o = new Occurence();
		o.setStartLine(22);
		o.setEndLine(22);
		o.setMessage("Variable \u0027pen\u0027 is assigned a value that is never used.");
		d.addOccurence(o);
		
		o = new Occurence();
		o.setStartLine(12);
		o.setEndLine(12);
		o.setMessage("Variable \u0027a\u0027 is assigned a value that is never used.");
		d.addOccurence(o);
		
		resultHandler.addExpectedDefect(d);
    }

	
	/**
	 * @throws java.lang.Exception void
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	/**
	 * @throws java.lang.Exception void
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception void
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception void
	 */
	@After
	public void tearDown() throws Exception {
	}
}
