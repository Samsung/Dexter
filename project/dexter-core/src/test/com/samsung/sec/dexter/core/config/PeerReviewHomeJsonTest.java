package com.samsung.sec.dexter.core.config;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.samsung.sec.dexter.core.util.DexterServerConfig;

public class PeerReviewHomeJsonTest {
	private static final int TEST_PORT = 8080;
	private static final String TEST_ID = "testId";
	private static final String TEST_PW = "testPw";
	private static final String TEST_HOST = "localhost";
	
	private static final String TEST_PROJECT = "testProject";
	private static final String TEST_SOURCE_DIR = "testDir";
	private static final Boolean TEST_ACTIVE = true;
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void PeerReviewHomeJson_GivenServerConfig_setServerConfig() {
		// given
		DexterServerConfig serverConfig = createTestServerConfig();
		List<PeerReviewHome> homeList = createTestHomeList(serverConfig);
		
		// when
		PeerReviewHomeJson homeJson = new PeerReviewHomeJson(serverConfig, homeList);
		
		// then
		DexterServerConfig result = homeJson.getServerConfig();
		assertEquals(serverConfig, result);
	}
	
	@Test
	public void PeerReviewHomeJson_GivenHomeList_setHomeList() {
		// given
		DexterServerConfig serverConfig = createTestServerConfig();
		List<PeerReviewHome> homeList = createTestHomeList(serverConfig);
		
		// when
		PeerReviewHomeJson homeJson = new PeerReviewHomeJson(serverConfig, homeList);
		
		// then
		assertEquals(homeList, homeJson.getHomeList());
	}
	
	@Test
	public void equals_givenSameContents_returnTrue() {
		// given
		PeerReviewHomeJson homeJson1 = createTestHomeJson();
		PeerReviewHomeJson homeJson2 = createTestHomeJson();
		
		// when & then
		assertTrue(homeJson1.equals(homeJson2));
	}

	private PeerReviewHomeJson createTestHomeJson() {
		DexterServerConfig serverConfig = createTestServerConfig();
		List<PeerReviewHome> homeList = createTestHomeList(serverConfig);
		
		return new PeerReviewHomeJson(serverConfig, homeList);
	}

	private List<PeerReviewHome> createTestHomeList(DexterServerConfig serverConfig) {
		List<PeerReviewHome> homeList = new ArrayList<PeerReviewHome>();
		PeerReviewHome home = new PeerReviewHome(serverConfig, TEST_PROJECT, TEST_SOURCE_DIR, TEST_ACTIVE);
		homeList.add(home);
		
		return homeList;
	}

	private DexterServerConfig createTestServerConfig() {
		return new DexterServerConfig(TEST_ID, TEST_PW, TEST_HOST, TEST_PORT);
		
	}
}
