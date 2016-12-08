package com.samsung.sec.dexter.executor.cli;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisEntityFactory;
import com.samsung.sec.dexter.core.config.PeerReviewHome;
import com.samsung.sec.dexter.core.plugin.IDexterPluginManager;
import com.samsung.sec.dexter.core.util.DexterServerConfig;
import com.samsung.sec.dexter.core.util.IDexterClient;
import com.samsung.sec.dexter.executor.DexterAnalyzer;

public class PeerReviewCLIAnalyzerTest {
	ICLILog cliLog;
	IDexterCLIOption cliOption;
	DexterAnalyzer dexterAnalyzer;
	PeerReviewCLIAnalyzer cliAnalyzer;
	IDexterPluginManager pluginManager;
	AnalysisEntityFactory analysisEntityFactory;
	
	@Before
	public void setUp() throws Exception {
		cliLog = mock(ICLILog.class);
		cliOption = mock(IDexterCLIOption.class);
		dexterAnalyzer = mock(DexterAnalyzer.class);
		pluginManager = mock(IDexterPluginManager.class);
		analysisEntityFactory = mock(AnalysisEntityFactory.class);
		
		cliAnalyzer = new PeerReviewCLIAnalyzer(cliOption, cliLog, dexterAnalyzer, 
				pluginManager, analysisEntityFactory);
	}
	
	@Test
	public void testAnalyze_callRunAsyncForFileList() {
		List<String> testFileList = getTestFileList();
		AnalysisConfig analysisConfig = mock(AnalysisConfig.class);
		PeerReviewHome home = mock(PeerReviewHome.class);
		when(home.getDexterServerConfig()).thenReturn(new DexterServerConfig("test", "test", "test"));
		when(cliOption.isStandAloneMode()).thenReturn(true);
		when(analysisEntityFactory.copyAnalysisConfigWithoutSourcecode(any(AnalysisConfig.class))).
			thenReturn(analysisConfig);
		
		cliAnalyzer.analyze(testFileList, home);
		
		verify(dexterAnalyzer, times(2)).runAsync(any(AnalysisConfig.class), eq(pluginManager), any(IDexterClient.class));
	}
	
	private List<String> getTestFileList() {
		String[] testFiles = { "file1", "file2" };
		return Arrays.asList(testFiles);
	}
}
