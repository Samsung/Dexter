package com.samsung.sec.dexter.core.config;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.samsung.sec.dexter.core.util.DexterServerConfig;

public class PeerReviewHomeTest {
	DexterServerConfig dexterServerConfig;
	PeerReviewHome peerReviewHome;
	
	@Before
	public void setUp() throws Exception {
		dexterServerConfig = mock(DexterServerConfig.class);
		peerReviewHome = new PeerReviewHome(dexterServerConfig, "testProject", "/test", true);
	}

	@Test
	public void testInit() {
		assertEquals(dexterServerConfig, peerReviewHome.getDexterServerConfig());
		assertEquals("testProject", peerReviewHome.getProjectName());
		assertEquals("/test", peerReviewHome.getSourceDir());
		assertEquals(true, peerReviewHome.isActive());
	}

}
