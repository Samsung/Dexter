package com.samsung.sec.dexter.core.config;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.config.DexterConfig.AnalysisType;
import com.samsung.sec.dexter.core.util.DexterServerConfig;

public class PeerReviewHomeTest {
	DexterServerConfig dexterServerConfig;
	PeerReviewHome peerReviewHome;
	
	@Before
	public void setUp() throws Exception {
		dexterServerConfig = mock(DexterServerConfig.class);
		peerReviewHome = createTestPeerReviewHome();
	}

	private PeerReviewHome createTestPeerReviewHome() {
		return new PeerReviewHome(dexterServerConfig, "testProject", "/test", true);
	}

	private PeerReviewHome createTestPeerReviewHomeWithNull() {
		return new PeerReviewHome(dexterServerConfig, null, "/test", true);
	}

	@Test
	public void testInit() {
		assertEquals(dexterServerConfig, peerReviewHome.getDexterServerConfig());
		assertEquals("testProject", peerReviewHome.getProjectName());
		assertEquals("/test", peerReviewHome.getSourceDir());
		assertEquals(true, peerReviewHome.isActive());
	}

	@Test
	public void testToAnalysisConfig() {
		AnalysisConfig analysisConfig = peerReviewHome.toAnalysisConfig();
		
		assertEquals("testProject", analysisConfig.getProjectName());
		assertEquals(AnalysisType.FILE, analysisConfig.getAnalysisType());
	}
	
	@Test
	public void equals_givenNull_returnFalse() {
		// when
		boolean result = peerReviewHome.equals(null);
		
		// then
		assertEquals(false, result);
	}

	@Test
	public void equals_givenSameObject_returnTrue() {
		// when
		boolean result = peerReviewHome.equals(peerReviewHome);
		
		// then
		assertEquals(true, result);
	}

	@Test
	public void equals_givenSameContents_returnTrue() {
		// given
		PeerReviewHome that = createTestPeerReviewHome();
		// when
		boolean result = peerReviewHome.equals(that);
		
		// then
		assertEquals(true, result);
	}

	@Test
	public void equals_givenSameContentsWithNullValue_returnTrue() {
		// given
		PeerReviewHome thisHome = createTestPeerReviewHomeWithNull();
		PeerReviewHome thatHome = createTestPeerReviewHomeWithNull();
		// when
		boolean result = thisHome.equals(thatHome);
		
		// then
		assertEquals(true, result);
	}

	@Test
	public void equals_givenDifferentContents_returnFalse() {
		// given
		PeerReviewHome thatHome = createTestPeerReviewHomeWithNull();
		// when
		boolean result = peerReviewHome.equals(thatHome);
		
		// then
		assertEquals(false, result);
	}

}
