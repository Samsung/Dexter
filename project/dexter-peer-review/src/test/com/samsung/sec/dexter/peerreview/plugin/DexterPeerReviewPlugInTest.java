package com.samsung.sec.dexter.peerreview.plugin;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisEntityFactory;
import com.samsung.sec.dexter.core.analyzer.IAnalysisEntityFactory;
import com.samsung.sec.dexter.core.config.DexterConfig.AnalysisType;
import com.samsung.sec.dexter.core.config.DexterConfig.LANGUAGE;

public class DexterPeerReviewPlugInTest {
	DexterPeerReviewPlugin peerReviewPlugIn;
	
	@Before
	public void setUp() throws Exception {
		peerReviewPlugIn = new DexterPeerReviewPlugin();
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

//	@Test
//	public void analyze_returnsAnalysisResult() {
//		AnalysisConfig config = createTestConfig();
//		
//		assertNotNull(peerReviewPlugIn.analyze(config));
//	}
	
	private AnalysisConfig createTestConfig() {
        IAnalysisEntityFactory configFactory = new AnalysisEntityFactory();
        AnalysisConfig analysisConfig = configFactory.createAnalysisConfig();

        analysisConfig.setProjectName("test");
        analysisConfig.setProjectFullPath("/test/");
        analysisConfig.setAnalysisType(AnalysisType.FILE);

        return analysisConfig;
	}

	@Test
	public void test_getAllCommentFromSourcecode_basic_success1(){
		DexterPeerReviewPlugin plugin = new DexterPeerReviewPlugin();
		
		// given
		String sourcecode = "abcdef /* DPR: include 하지 마세요\r\n* MULTI_CASE-1\r\n*/";
		String expectedFullComment = "/* DPR: include 하지 마세요* MULTI_CASE-1*/";
		String expectedSeverity = "CRC";
		String expectedComment = "include 하지 마세요* MULTI_CASE-1*/";
		
		// when
		ArrayList<DPRComment> result = plugin.getAllDPRCommentFromSourcecode(sourcecode);
		
		// then
		assertEquals(1, result.size());	
		DPRComment comment = result.get(0);
		assertEquals(1, comment.getStartLine());
		assertEquals(3, comment.getEndLine());
		assertEquals(expectedFullComment, comment.getFullComment());
		assertEquals(expectedSeverity, comment.getSeverity());
		assertEquals(expectedComment, comment.getReviewComment());
	}
	
	@Test
	public void test_getAllCommentFromSourcecode_basic_success2(){
		DexterPeerReviewPlugin plugin = new DexterPeerReviewPlugin();
		
		// given
		String sourcecode = "asdfasdf /* DPR: sdf  * MULTI_CASE-2 */\r\nasdfasfd //      DPR:      asdfasdfa";
		String expected1 = "/* DPR: sdf  * MULTI_CASE-2 */";
		String expected2 = "//      DPR:      asdfasdfa";
		
		// when
		ArrayList<DPRComment> result = plugin.getAllDPRCommentFromSourcecode(sourcecode);
		
		// then
		assertEquals(2, result.size());	
		DPRComment comment = result.get(0);
		assertEquals(1, comment.getStartLine());
		assertEquals(1, comment.getEndLine());
		assertEquals(expected1, comment.getFullComment());
		
		comment = result.get(1);
		assertEquals(2, comment.getStartLine());
		assertEquals(2, comment.getEndLine());
		assertEquals(expected2, comment.getFullComment());
	}
}
