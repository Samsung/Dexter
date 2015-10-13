package com.samsung.sec.dexter.opensource.plugin;

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
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.util.DexterUtil;

public class DexterOpensourcePluginTest {
	private final IAnalysisEntityFactory factory = new AnalysisEntityFactory();
	
	@Test
	public void test_creating_Plugin_instance() {
		DexterOpensourcePlugin plugin = new DexterOpensourcePlugin();
		plugin.init();
		
		assertNotNull(plugin.getCheckerConfig());
	}
	
	@Test
	public void test_correctness_of_CheckerConfig(){
		DexterOpensourcePlugin plugin = new DexterOpensourcePlugin();
		plugin.init();
		
		CheckerConfig config = plugin.getCheckerConfig();
		assertEquals(13, config.getCheckerList().size());
		assertEquals("dexter-opensource", config.getToolName());
		
		Checker firstChecker = config.getCheckerList().get(0);
		assertEquals("GPL", firstChecker.getCategoryName());
		assertEquals("GPL_2_0", firstChecker.getCode());
		assertEquals(0, firstChecker.getCwe());
		assertTrue(firstChecker.isActive());
		assertEquals("MAJ", firstChecker.getSeverityCode());
		assertEquals("BOTH", firstChecker.getType());
		assertEquals("2.5.30", firstChecker.getVersion().toString());
	}
	
	@Test
	public void analyze_method_should_work_for_GPL_2_0(){
		analyze("src/GPL_v2/COPYING", "GPL_2_0", 1);
		analyze("src/GPL_v2/if_bridge.h", "GPL_2_0", 1);
		analyze("src/GPL_v2/libxt_osf.c", "GPL_2_0", 1);
		analyze("src/GPL_v2/mpi-scan.c", "GPL_2_0", 1);
		analyze("src/GPL_v2/posix_mutex.hpp", "GPL_2_0", 0);//  ==> 검출 안됨
	}
	
	@Test
	public void analyze_method_should_work_for_GPL_3_0(){
		analyze("src/GPL_v3/COPYING.GPLv3", "GPL_3_0", 1);
	}
	
	@Test
	public void analyze_method_should_work_for_LGPL_2_0(){
		analyze("src/LGPL_v2/pthread.h", "LGPL_2_0", 2);
	}
	
	@Test
	public void analyze_method_should_work_for_LGPL_2_1(){
		analyze("src/LGPL_v2.1/COPYING", "LGPL_2_1", 3);	// 중복검출: LGPL_2_1, GPL_2_0, LGPL_2_0
		//analyze("src/LGPL_v2.1/ffmpeg.c", "LGPL_2_1", 1); // ==> 검출 안됨
		//analyze("src/LGPL_v2.1/nl-list-caches.c", "LGPL_2_1", 1); // ==> 검출 안됨
	}
	
	@Test
	public void analyze_method_should_work_for_LGPL_3_0(){
		AnalysisResult result = analyze("src/LGPL_v3.0/COPYING.LGPLv3", "LGPL_3_0", 3);	// 중복검출: GPL_2_0, LGPL_3_0, GPL_3_0
		testResult("src/LGPL_v3.0/COPYING.LGPLv3", "LGPL_3_0", 3, result);
	}
	
	private AnalysisResult analyze(final String testFilePath, final String expectedCheckerCode, final int count){
		DexterOpensourcePlugin plugin = new DexterOpensourcePlugin();
		plugin.init();
		
		final AnalysisConfig config = createAnalysisConfigTestData(testFilePath, expectedCheckerCode, count);
		
		return plugin.analyze(config);
	}

	private AnalysisConfig createAnalysisConfigTestData(final String testFilePath, 
			final String expectedCheckerCode, final int count) {
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
	
	private void testResult(final String testFilePath, 
			final String expectedCheckerCode, final int count, final AnalysisResult result){
		assertEquals(count, result.getDefectList().size());
		
		boolean hasCheckerResult = false;
		for(Defect defect : result.getDefectList()){
			if(defect.getCheckerCode().equals(expectedCheckerCode)){
				hasCheckerResult = true;
				
				System.out.println("defect msg: " + defect.getMessage());
				System.out.println("occ msg: " + defect.getOccurences().get(0).getMessage());
			}
		}
		
		assertTrue(hasCheckerResult);
	}
}
