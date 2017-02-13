package com.samsung.sec.dexter.peerreview.plugin;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.DexterConfig.AnalysisType;
import com.samsung.sec.dexter.core.config.DexterConfig.LANGUAGE;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisEntityFactory;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.analyzer.IAnalysisEntityFactory;

public class DexterPeerReviewPlugInTest {
	DexterPeerReviewPlugIn peerReviewPlugIn;
	
	@Before
	public void setUp() throws Exception {
		peerReviewPlugIn = new DexterPeerReviewPlugIn();
	}

	@Test
	public void supportLanguage_supportC() {
		assertEquals(true, peerReviewPlugIn.supportLanguage(LANGUAGE.C));
	}
	
	@Test
	public void supportLanguage_supportCPP() {
		assertEquals(true, peerReviewPlugIn.supportLanguage(LANGUAGE.CPP));
	}
	
	@Test
	public void supportLanguage_supportJAVA() {
		assertEquals(true, peerReviewPlugIn.supportLanguage(LANGUAGE.JAVA));
	}
	
	@Test
	public void supportLanguage_dontSupportJAVASCRIPT() {
		assertEquals(false, peerReviewPlugIn.supportLanguage(LANGUAGE.JAVASCRIPT));
	}

	@Test
	public void analyze_returnsAnalysisResult() {
		AnalysisConfig config = createTestConfig();
		
		assertNotNull(peerReviewPlugIn.analyze(config));
	}
	
	private AnalysisConfig createTestConfig() {
        IAnalysisEntityFactory configFactory = new AnalysisEntityFactory();
        AnalysisConfig analysisConfig = configFactory.createAnalysisConfig();

        analysisConfig.setProjectName("test");
        analysisConfig.setProjectFullPath("/test/");
        analysisConfig.setAnalysisType(AnalysisType.FILE);

        return analysisConfig;
	}

}
