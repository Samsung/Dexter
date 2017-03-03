package com.samsung.sec.dexter.peerreview.plugin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisEntityFactory;
import com.samsung.sec.dexter.core.analyzer.IAnalysisEntityFactory;
import com.samsung.sec.dexter.core.config.DexterConfig.AnalysisType;
import com.samsung.sec.dexter.core.config.DexterConfig.LANGUAGE;

import net.xeoh.plugins.base.Plugin;

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

	private AnalysisConfig createTestConfig() {
        IAnalysisEntityFactory configFactory = new AnalysisEntityFactory();
        AnalysisConfig analysisConfig = configFactory.createAnalysisConfig();

        analysisConfig.setProjectName("test");
        analysisConfig.setProjectFullPath("/test/");
        analysisConfig.setAnalysisType(AnalysisType.FILE);

        return analysisConfig;
	}

	@Test
	public void test_getAllCommentFromSourcecode_basic_success_one_multi_line_comment(){
		DexterPeerReviewPlugin plugin = new DexterPeerReviewPlugin();
		
		// given
		String sourcecode = "abcdef /* DPR: include 하지 마세요\r\n* MULTI_CASE-1\r\n*/";
		int[] offsets = plugin.makeOffsetArray(sourcecode);
		
		String expectedFullComment = "/* DPR: include 하지 마세요* MULTI_CASE-1*/";
		String expectedSeverity = "CRC";
		String expectedComment = "include 하지 마세요* MULTI_CASE-1*/";
		
		
		// when
		ArrayList<DPRComment> result = plugin.getAllDPRCommentFromSourcecode(offsets, sourcecode);
		
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
	public void test_getAllCommentFromSourcecode_basic_success_with_severity(){
		DexterPeerReviewPlugin plugin = new DexterPeerReviewPlugin();
		
		// given
		String sourcecode = "abcdef /* DPR: [CRI] include 하지 마세요\r\n* MULTI_CASE-1\r\n*/";
		int[] offsets = plugin.makeOffsetArray(sourcecode);
		
		String expectedFullComment = "/* DPR: [CRI] include 하지 마세요* MULTI_CASE-1*/";
		String expectedSeverity = "CRI";
		String expectedComment = "include 하지 마세요* MULTI_CASE-1*/";
		
		
		// when
		ArrayList<DPRComment> result = plugin.getAllDPRCommentFromSourcecode(offsets, sourcecode);
		
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
	public void test_getAllCommentFromSourcecode_basic_success_multi_line_with_severity(){
		DexterPeerReviewPlugin plugin = new DexterPeerReviewPlugin();
		
		// given
		String sourcecode = "abcdef /* DPR: [CRI] \r\ninclude 하지 마세요\r\n* MULTI_CASE-1\r\n*/";
		int[] offsets = plugin.makeOffsetArray(sourcecode);
		
		String expectedFullComment = "/* DPR: [CRI] include 하지 마세요* MULTI_CASE-1*/";
		String expectedSeverity = "CRI";
		String expectedComment = "include 하지 마세요* MULTI_CASE-1*/";
		
		
		// when
		ArrayList<DPRComment> result = plugin.getAllDPRCommentFromSourcecode(offsets, sourcecode);
		
		// then
		assertEquals(1, result.size());	
		DPRComment comment = result.get(0);
		assertEquals(1, comment.getStartLine());
		assertEquals(4, comment.getEndLine());
		assertEquals(expectedFullComment, comment.getFullComment());
		assertEquals(expectedSeverity, comment.getSeverity());
		assertEquals(expectedComment, comment.getReviewComment());
	}
	
	@Test
	public void test_getAllCommentFromSourcecode_basic_success_complex_comments(){
		DexterPeerReviewPlugin plugin = new DexterPeerReviewPlugin();
		
		// given
		String sourcecode = "asdfasdf /* DPR: sdf  * MULTI_CASE-2 */\r\nasdfasfd //      DPR:      asdfasdfa";
		int[] offsets = plugin.makeOffsetArray(sourcecode);
		
		String expectedFullComment1 = "/* DPR: sdf * MULTI_CASE-2 */";
		String expectedFullComment2 = "// DPR: asdfasdfa";
		String expectedSeverity = "CRC";
		String expectedComment1 = "sdf * MULTI_CASE-2 */";
		String expectedComment2 = "asdfasdfa";
		
		// when
		ArrayList<DPRComment> result = plugin.getAllDPRCommentFromSourcecode(offsets, sourcecode);
		
		// then
		assertEquals(2, result.size());	
		DPRComment comment = result.get(0);
		assertEquals(1, comment.getStartLine());
		assertEquals(1, comment.getEndLine());
		assertEquals(expectedSeverity,comment.getSeverity());
		assertEquals(expectedFullComment1, comment.getFullComment());
		assertEquals(expectedComment1, comment.getReviewComment());
		
		comment = result.get(1);
		assertEquals(2, comment.getStartLine());
		assertEquals(2, comment.getEndLine());
		assertEquals(expectedSeverity,comment.getSeverity());
		assertEquals(expectedFullComment2, comment.getFullComment());
		assertEquals(expectedComment2, comment.getReviewComment());
	}
	
	@Test
	public void test_getAllCommentFromSourcecode_basic_success_complex_comments_without_blank(){
		DexterPeerReviewPlugin plugin = new DexterPeerReviewPlugin();
		
		// given
		String sourcecode = "asdfasdf /* DPR: sdf  * MULTI_CASE-2 */\r\nasdfasfd //      DPR:      asdfasd             fa";
		int[] offsets = plugin.makeOffsetArray(sourcecode);
		
		String expectedFullComment1 = "/* DPR: sdf * MULTI_CASE-2 */";
		String expectedFullComment2 = "// DPR: asdfasd fa";
		String expectedSeverity = "CRC";
		String expectedComment1 = "sdf * MULTI_CASE-2 */";
		String expectedComment2 = "asdfasd fa";
		
		// when
		ArrayList<DPRComment> result = plugin.getAllDPRCommentFromSourcecode(offsets, sourcecode);
		
		// then
		assertEquals(2, result.size());	
		DPRComment comment = result.get(0);
		assertEquals(1, comment.getStartLine());
		assertEquals(1, comment.getEndLine());
		assertEquals(expectedSeverity,comment.getSeverity());
		assertEquals(expectedFullComment1, comment.getFullComment());
		assertEquals(expectedComment1, comment.getReviewComment());
		
		comment = result.get(1);
		assertEquals(2, comment.getStartLine());
		assertEquals(2, comment.getEndLine());
		assertEquals(expectedSeverity,comment.getSeverity());
		assertEquals(expectedFullComment2, comment.getFullComment());
		assertEquals(expectedComment2, comment.getReviewComment());
	}
	
	@Test
	public void test_makeOffsetArray_basic_success() {
		DexterPeerReviewPlugin plugin = new DexterPeerReviewPlugin();

		// given
		String sourcecode = "asdfasdf /* DPR: sdf  * MULTI_CASE-2 */\r\nasdfasfd //      DPR:      asdfasdfa";

		// when
		int[] offsets = plugin.makeOffsetArray(sourcecode);

		// then
		assertEquals(3, offsets.length);
		assertEquals(0, offsets[1]);
		assertEquals(41, offsets[2]);
	}
	
	@Test
	public void test_makeOffsetArray_basic_success_7_lines_sourcecode() {
		DexterPeerReviewPlugin plugin = new DexterPeerReviewPlugin();

		// given
		String sourcecode = "asdfasdf /* DPR: sdf  * MULTI_CASE-2 */\r\nasdfasfd //      DPR:      asdfasdfa\r\ntest3\r\ntest4\r\n\r\nTest";

		// when
		int[] offsets = plugin.makeOffsetArray(sourcecode);

		// then
		assertEquals(7, offsets.length);
		assertEquals(0, offsets[1]);
		assertEquals(41, offsets[2]);
		assertEquals(79, offsets[3]);
		assertEquals(86, offsets[4]);
		assertEquals(93, offsets[5]);
		assertEquals(95, offsets[6]);
	}
	
	@Test
	public void test_getLineFromOffset_basic_success_perfect_match(){
		DexterPeerReviewPlugin plugin = new DexterPeerReviewPlugin();
		
		// given
		String sourcecode = "asdfasdf /* DPR: sdf  * MULTI_CASE-2 */\r\nasdfasfd //      DPR:      asdfasdfa\r\ntest3\r\ntest4";
		int[] offsets = plugin.makeOffsetArray(sourcecode);
		
		// when
		int line = plugin.getLineFromOffset(offsets, 42);
		
		// then
		assertEquals(2, line);
	}
	
	@Test
	public void test_getLineFromOffset_basic_success_near_match(){
		DexterPeerReviewPlugin plugin = new DexterPeerReviewPlugin();
		
		// given
		String sourcecode = "asdfasdf /* DPR: sdf  * MULTI_CASE-2 */\r\nasdfasfd //      DPR:      asdfasdfa\r\ntest3\r\ntest4";
		int[] offsets = plugin.makeOffsetArray(sourcecode);

		
		// when
		int line1 = plugin.getLineFromOffset(offsets, 31);
		int line2 = plugin.getLineFromOffset(offsets, 48);
		int line3 = plugin.getLineFromOffset(offsets, 83);
		int line4 = plugin.getLineFromOffset(offsets, 89);
		
		// then
		assertEquals(1, line1);
		assertEquals(2, line2);
		assertEquals(3, line3);
		assertEquals(4, line4);
	}
	
	@Test
	public void test_getLineFromOffset_basic_success_near_match_boundary_test(){
		DexterPeerReviewPlugin plugin = new DexterPeerReviewPlugin();
		
		// given
		String sourcecode = "abcdef /* DPR: include 하지 마세요\r\n* MULTI_CASE-1\r\n*/";;
		int[] offsets = plugin.makeOffsetArray(sourcecode);

		// when
		int line1 = plugin.getLineFromOffset(offsets, 30);
		int line2_start = plugin.getLineFromOffset(offsets, 32);
		int line2_end = plugin.getLineFromOffset(offsets, 46);
		int line3_start = plugin.getLineFromOffset(offsets, 49);
		int line3_end = plugin.getLineFromOffset(offsets, 50);
		
		// then
		assertEquals(1, line1);
		assertEquals(2, line2_start);
		assertEquals(2, line2_end);
		assertEquals(3, line3_start);
		assertEquals(3, line3_end);
	}
}
