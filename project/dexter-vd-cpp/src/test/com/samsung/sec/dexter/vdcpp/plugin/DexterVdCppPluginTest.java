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
package com.samsung.sec.dexter.vdcpp.plugin;

import static org.junit.Assert.*;

import java.io.File;
import org.junit.Test;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisEntityFactory;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.analyzer.AnalysisResultChangeHandlerForUT;
import com.samsung.sec.dexter.core.analyzer.IAnalysisEntityFactory;
import com.samsung.sec.dexter.core.analyzer.ITestHandlerAtTheEndOfHandleAnalysisResult;
import com.samsung.sec.dexter.core.checker.Checker;
import com.samsung.sec.dexter.core.checker.CheckerConfig;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.defect.Occurence;
import com.samsung.sec.dexter.core.plugin.PluginDescription;
import com.samsung.sec.dexter.core.util.DexterUtil;
import com.samsung.sec.dexter.vdcpp.checkerlogic.ICheckerLogic;

public class DexterVdCppPluginTest {
	private final IAnalysisEntityFactory factory = new AnalysisEntityFactory();
	
	@Test
	public void test_creating_Plugin_instance() {
		DexterVdCppPlugin plugin = new DexterVdCppPlugin();
		plugin.init();
		
		assertNotNull(plugin.getCheckerConfig());
	}
	
	@Test
	public void test_const_naming_checker_config(){
		DexterVdCppPlugin plugin = new DexterVdCppPlugin();
		plugin.init();
		
		CheckerConfig config = plugin.getCheckerConfig();
		//assertEquals(5, config.getCheckerList().size());
		assertEquals("dexter-vd-cpp", config.getToolName());
		
		Checker firstChecker = config.getCheckerList().get(0);
		assertEquals("CRC", firstChecker.getCategoryName());
		assertEquals("CONST_NAMING", firstChecker.getCode());
		assertEquals(0, firstChecker.getCwe());
		assertTrue(firstChecker.isActive());
		assertEquals("MAJ", firstChecker.getSeverityCode());
		assertEquals("BOTH", firstChecker.getType());
		assertEquals("2.5.30", firstChecker.getVersion().toString());
		assertEquals("The name of a const variable should be consist of Upper alphabet, underline, or number.", 
				firstChecker.getDescription());
		assertEquals("[A-Z][0-9_A-Z]+", firstChecker.getProperty("RegExp"));
	}
	
	@Test
	public void test_Plugin_Description(){
		DexterVdCppPlugin plugin = new DexterVdCppPlugin();
		plugin.init();
		
		PluginDescription desc = plugin.getDexterPluginDescription();
		
		assertEquals("Samsung Electroincs", desc.get3rdPartyName());
		assertEquals(DexterConfig.LANGUAGE.CPP, desc.getLanguage());
		assertEquals("dexter-vd-cpp", desc.getPluginName());
		assertEquals("2.5.31", desc.getVersion().toString());
	}
	
	@Test
	public void checkerlogic_should_be_initialized_when_called_init(){
		DexterVdCppPlugin plugin = new DexterVdCppPlugin();
		plugin.init();
		
		ICheckerLogic checkerLogic = plugin.getCheckerLogic("CONST_NAMING");
		assertEquals("com.samsung.sec.dexter.vdcpp.checkerlogic.ConstNamingCheckerLogic", checkerLogic.getClass().getName());
	}
	
	
	@Test
	public void analyze_method_should_work_for_CONSTANT_NAMING_CHECKER(){
		AnalysisResult result = analyze("src/crc/const_naming.cpp");	
		
		for(Defect defect : result.getDefectList()){
			if(defect.getCheckerCode().equals("CONST_NAMING")){
				assertEquals(1, defect.getOccurences().size());
				assertEquals("", defect.getClassName());
				assertEquals("", defect.getMethodName());
				assertEquals("[#1@5] const variable can be Upper Alphabet, underline, or number. your input is BADNaming1 ", defect.getMessage());
				
				Occurence occ = defect.getOccurences().get(0);
				assertEquals("BADNaming1", occ.getStringValue());
				assertEquals(5, occ.getStartLine());
				assertEquals(5, occ.getEndLine());
				assertEquals(139, occ.getCharStart());
				assertEquals(153, occ.getCharEnd());
				assertEquals("const variable can be Upper Alphabet, underline, or number. your input is BADNaming1", occ.getMessage());
			}
		}
	}
	
	
	@Test
	public void analyze_method_should_work_for_USleep(){		
		AnalysisResult result = analyze("src/crc/UsleepCheckerLogic.cpp");		
		
		for(Defect defect : result.getDefectList()){
			if(defect.getCheckerCode().equals("USLEEP")){
				assertEquals(1, defect.getOccurences().size());
				assertEquals("[#1@31] checkUsleep function aggument 1000  should be greater than 10000 to avoid performance issue; ", defect.getMessage());
				
				Occurence occ = defect.getOccurences().get(0);				
				assertEquals(31, occ.getStartLine());
				assertEquals(31, occ.getEndLine());			
			
			}
		}
		
	}
	
	
	@Test
	public void analyze_method_for_VECTOR_ERASE_FUNCTION_MISUSE(){		
		AnalysisResult result = analyze("src/crc/VectorEraseFunctionMisuse.cpp");
		
		//assertEquals(5, result.getDefectList().size());	
		for(Defect defect : result.getDefectList()){
			if(defect.getCheckerCode().equals("VECTOR_ERASE_FUNCTION_MISUSE"))
			{
				assertEquals(4, defect.getOccurences().size());
				assertEquals("[#1@44] Avoid vector erase function inside iterative block; [#2@49] Avoid vector erase function inside iterative block; [#3@54] Avoid vector erase function inside iterative block; [#4@59] Avoid vector erase function inside iterative block; ", defect.getMessage());
				
				Occurence occ = defect.getOccurences().get(0);				
				assertEquals(44, occ.getStartLine());
				assertEquals(44, occ.getEndLine());			
			
			}
		}
		
	}
	
	
	@Test
	public void analyze_method_for_SIGNED_UNSIGNED_ASSIGNMENT_ERROR(){		
		AnalysisResult result = analyze("src/crc/SignedUnsignedAssignmentError.cpp");
		
		//assertEquals(5, result.getDefectList().size());	
		for(Defect defect : result.getDefectList()){
			if(defect.getCheckerCode().equals("SIGNED_UNSIGNED_ASSIGNMENT_ERROR"))
			{
				assertEquals(3, defect.getOccurences().size());
				assertEquals("[#1@28] Signed Unsigned assignment error; [#2@29] Signed Unsigned assignment error; [#3@30] Signed Unsigned assignment error; ", defect.getMessage());
				
				Occurence occ = defect.getOccurences().get(0);				
				assertEquals(28, occ.getStartLine());
				assertEquals(28, occ.getEndLine());			
			
			}
		}
		
	}

	
	@Test
	public void analyze_method_for_CHECK_FREE_ON_RETURN_VALUE(){		
		AnalysisResult result = analyze("src/crc/NoFreeOfReturnValue.cpp");
		
		//assertEquals(5, result.getDefectList().size());	
		for(Defect defect : result.getDefectList()){
			if(defect.getCheckerCode().equals("CHECK_FREE_STMT"))
			{
				assertEquals(1, defect.getOccurences().size());
				Occurence occ = defect.getOccurences().get(0);	
				assertEquals("[#1@28] You should have free statement for a returned object by calling "+occ.getVariableName()+" ", defect.getMessage());
											
				assertEquals(28, occ.getStartLine());
				assertEquals(28, occ.getEndLine());			
			
			}
		}
		
	}
	
	@Test
	public void analyze_method_for_CHECK_FREE_PARAMETER_STMT(){		
		AnalysisResult result = analyze("src/crc/NoFreeOfParameterValue.cpp");
		
		//assertEquals(5, result.getDefectList().size());	
		for(Defect defect : result.getDefectList()){
			if(defect.getCheckerCode().equals("CHECK_FREE_STMT_PARAM"))
			{
				assertEquals(1, defect.getOccurences().size());
				Occurence occ = defect.getOccurences().get(0);	
				assertEquals("[#1@21] You should have free statement for a parameter by calling "+occ.getVariableName()+" ", defect.getMessage());
				
							
				assertEquals(21, occ.getStartLine());
				assertEquals(21, occ.getEndLine());			
			
			}
		}
		
	}
	
	
	@Test
	public void analyze_method_for_CHECK_THREAD_UNSAFE_FUNCTION(){		
		AnalysisResult result = analyze("src/crc/ThreadUnsafeFunction.cpp");
		
		//assertEquals(5, result.getDefectList().size());	
		for(Defect defect : result.getDefectList()){
			if(defect.getCheckerCode().equals("CHECK_THREAD_UNSAFE_FUNCTION_DBUS_GLIB"))
			{
				assertEquals(1, defect.getOccurences().size());
				Occurence occ = defect.getOccurences().get(0);	
				assertEquals("[#1@62] "+occ.getVariableName()+": dbus-glib is thread unsafe function. so VD recommends NOT to use dbus-glib in the multi-thread environment. ", defect.getMessage());
								
				assertEquals(62, occ.getStartLine());
				assertEquals(65, occ.getEndLine());	
				
			
			}
		}
		
	}
	
	
	
	@Test
	public void analyze_method_for_CHECK_DUID(){		
		AnalysisResult result = analyze("src/crc/CheckDUID.cpp");
		
		//assertEquals(5, result.getDefectList().size());	
		for(Defect defect : result.getDefectList()){
			if(defect.getCheckerCode().equals("CHECK_USAGE_DUID"))
			{
				assertEquals(3, defect.getOccurences().size());
				Occurence occ = defect.getOccurences().get(0);	
				assertEquals("[#1@9] VD recommends Not to use DUID. You can use PID instead of DUID [#2@13] VD recommends Not to use DUID. You can use PID instead of DUID [#3@17] VD recommends Not to use DUID. You can use PID instead of DUID ", defect.getMessage());
								
				assertEquals(9, occ.getStartLine());
				assertEquals(9, occ.getEndLine());	
				
			
			}
		}
		
	}
	
	private AnalysisResult analyze(final String testFilePath){
		DexterVdCppPlugin plugin = new DexterVdCppPlugin();
		plugin.init();
		
		final AnalysisConfig config = createAnalysisConfigTestData(testFilePath);
		return plugin.analyze(config);
	}

	private AnalysisConfig createAnalysisConfigTestData(final String testFilePath) {
	    final AnalysisConfig config = factory.createAnalysisConfig();
		
		final String projectFullPath = new File("testdata/DefectiveProject").getAbsolutePath();
		final String sourceFileFullPath = DexterUtil.addPaths(projectFullPath, testFilePath);
		final String sourceDir = DexterUtil.addPaths(projectFullPath, "src");
		
		config.setProjectName("DefectiveProject");
		config.setSourceFileFullPath(sourceFileFullPath);
		config.setProjectFullPath(projectFullPath);
		config.addSourceBaseDirList(sourceDir);
		config.generateFileNameWithSourceFileFullPath();
		config.generateModulePath();
		
		AnalysisResultChangeHandlerForUT handler = new AnalysisResultChangeHandlerForUT(
			new ITestHandlerAtTheEndOfHandleAnalysisResult() {
				@Override
				public void testAfterHandlingAnalysisResult(final AnalysisResult result) {
				}
		});
		
		config.setResultHandler(handler);
	    return config;
    }
   
}
