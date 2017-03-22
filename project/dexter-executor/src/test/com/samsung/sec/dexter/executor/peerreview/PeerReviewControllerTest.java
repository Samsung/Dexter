package com.samsung.sec.dexter.executor.peerreview;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.CharSink;
import com.google.common.io.Files;
import com.samsung.sec.dexter.core.config.PeerReviewHome;
import com.samsung.sec.dexter.core.config.PeerReviewHomeJson;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.DexterServerConfig;
import com.samsung.sec.dexter.core.util.PeerReviewHomeUtil;
import com.samsung.sec.dexter.executor.peerreview.PeerReviewController;
import com.samsung.sec.dexter.executor.peerreview.PeerReviewHomeMonitor;

public class PeerReviewControllerTest {
	PeerReviewHomeMonitor peerReviewHomeMonitor;
	PeerReviewController peerReviewController;
	PeerReviewHomeUtil peerReivewHomeUtil;
	
	@Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	@Before
	public void setUp() throws Exception {
		peerReviewHomeMonitor = mock(PeerReviewHomeMonitor.class);
		peerReivewHomeUtil = mock(PeerReviewHomeUtil.class);
		peerReviewController = new PeerReviewController(peerReviewHomeMonitor, peerReivewHomeUtil);
	}

	@Test
	public void testInit() {
		assertEquals(0, peerReviewController.getPeerReviewHomeList().size());
	}
	
	@Test
	public void testUpdate_verifyHomeSize() throws IOException {
		// given
		File jsonFile = makeTestPeerReviewJsonFile();
		when(peerReivewHomeUtil.loadJson(any(Reader.class))).thenReturn(makeTestHomeJson());
		
		// when
		peerReviewController.update(jsonFile);
		
		// then
		assertEquals(2, peerReviewController.getPeerReviewHomeList().size());
	}
	
	@Test
	public void testUpdate_verifyFirstHome() throws IOException {
		// given
		File jsonFile = makeTestPeerReviewJsonFile();
		when(peerReivewHomeUtil.loadJson(any(Reader.class))).thenReturn(makeTestHomeJson());
		
		// when
		peerReviewController.update(jsonFile);
		
		// then
		PeerReviewHome home = peerReviewController.getPeerReviewHomeList().get(0);
		assertEquals("testProject", home.getProjectName());
		assertEquals("/test", home.getSourceDir());
		assertEquals(true, home.isActive());
		assertNotNull(home.getDexterServerConfig());
	}
	
	@Test
	public void testUpdate_verifySecondHome() throws IOException {
		// given
		File jsonFile = makeTestPeerReviewJsonFile();
		when(peerReivewHomeUtil.loadJson(any(Reader.class))).thenReturn(makeTestHomeJson());
		
		// when
		peerReviewController.update(jsonFile);
		
		// then
		PeerReviewHome home = peerReviewController.getPeerReviewHomeList().get(1);
		assertEquals("testProject2", home.getProjectName());
		assertEquals("/test2", home.getSourceDir());
		assertEquals(false, home.isActive());
		assertNotNull(home.getDexterServerConfig());
	}
	
	@Test
	public void testUpdate_verifyDexterServerConfig() throws IOException {
		// given
		File jsonFile = makeTestPeerReviewJsonFile();
		when(peerReivewHomeUtil.loadJson(any(Reader.class))).thenReturn(makeTestHomeJson());
		
		// when
		peerReviewController.update(jsonFile);
		
		// then
		DexterServerConfig config = peerReviewController.getPeerReviewHomeList().get(0).getDexterServerConfig();
		assertEquals("127.0.0.1", config.getHostname());
		assertEquals(8080, config.getPort());
		assertEquals("testId", config.getUserId());
		assertEquals("testPw", config.getUserPwd());
	}
	
	@Test
	public void testUpdate_restartHomeMonitor() throws IOException {
		// given
		File jsonFile = makeTestPeerReviewJsonFile();
		when(peerReivewHomeUtil.loadJson(any(Reader.class))).thenReturn(makeTestHomeJson());
		
		// when
		peerReviewController.update(jsonFile);
		
		// then
		verify(peerReviewHomeMonitor).restart(eq(peerReviewController.getPeerReviewHomeList()));
	}
	
	private PeerReviewHomeJson makeTestHomeJson() {
		DexterServerConfig serverConfig = new DexterServerConfig("testId", "testPw", "127.0.0.1", 8080);
		List<PeerReviewHome> homeList = new ArrayList<>();
		homeList.add(new PeerReviewHome(serverConfig, "testProject", "/test", true));
		homeList.add(new PeerReviewHome(serverConfig, "testProject2", "/test2", false));
		
		return new PeerReviewHomeJson(serverConfig, homeList);
	}
	
	private File makeTestPeerReviewJsonFile() throws IOException {
		return temporaryFolder.newFile("peerReview.json");
	}
}
