package com.samsung.sec.dexter.executor.peerreview;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
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
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.DexterServerConfig;
import com.samsung.sec.dexter.executor.peerreview.PeerReviewController;
import com.samsung.sec.dexter.executor.peerreview.PeerReviewHomeMonitor;

public class PeerReviewControllerTest {
	PeerReviewHomeMonitor peerReviewHomeMonitor;
	PeerReviewController peerReviewController;
	
	@Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	@Before
	public void setUp() throws Exception {
		peerReviewHomeMonitor = mock(PeerReviewHomeMonitor.class);
		peerReviewController = new PeerReviewController(peerReviewHomeMonitor);
	}

	@Test
	public void testInit() {
		assertEquals(0, peerReviewController.getPeerReviewHomeList().size());
	}
	
	@Test
	public void testUpdate_verifyHomeSize() throws IOException {
		File jsonFile = makeTestPeerReviewJson();
		
		peerReviewController.update(jsonFile);
		
		assertEquals(2, peerReviewController.getPeerReviewHomeList().size());
	}
	
	@Test
	public void testUpdate_verifyFirstHome() throws IOException {
		File jsonFile = makeTestPeerReviewJson();
		
		peerReviewController.update(jsonFile);
		
		PeerReviewHome home = peerReviewController.getPeerReviewHomeList().get(0);
		assertEquals("testProject", home.getProjectName());
		assertEquals("/test", home.getSourceDir());
		assertEquals(true, home.isActive());
		assertNotNull(home.getDexterServerConfig());
	}
	
	@Test
	public void testUpdate_verifySecondHome() throws IOException {
		File jsonFile = makeTestPeerReviewJson();
		
		peerReviewController.update(jsonFile);
		
		PeerReviewHome home = peerReviewController.getPeerReviewHomeList().get(1);
		assertEquals("testProject2", home.getProjectName());
		assertEquals("/test2", home.getSourceDir());
		assertEquals(false, home.isActive());
		assertNotNull(home.getDexterServerConfig());
	}
	
	@Test
	public void testUpdate_verifyDexterServerConfig() throws IOException {
		File jsonFile = makeTestPeerReviewJson();
		
		peerReviewController.update(jsonFile);
		
		DexterServerConfig config = peerReviewController.getPeerReviewHomeList().get(0).getDexterServerConfig();
		assertEquals("127.0.0.1", config.getHostname());
		assertEquals(8080, config.getPort());
		assertEquals("testId", config.getUserId());
		assertEquals("testPw", config.getUserPwd());
	}
	
	@Test(expected = DexterRuntimeException.class)
	public void testUpdate_throwExceptionIfEmptyServerInfo() throws IOException {
		File jsonFile = makeTestPeerReviewJsonWithEmptyServer();
		
		peerReviewController.update(jsonFile);
	}
	
	@Test(expected = DexterRuntimeException.class)
	public void testUpdate_throwExceptionIfEmptyHome() throws IOException {
		File jsonFile = makeTestPeerReviewJsonWithEmptyHome();
		
		peerReviewController.update(jsonFile);
	}
	
	@Test
	public void testUpdate_callUpdateToHomeMonitor() throws IOException {
		File jsonFile = makeTestPeerReviewJson();
		
		peerReviewController.update(jsonFile);
		
		verify(peerReviewHomeMonitor).update(eq(peerReviewController.getPeerReviewHomeList()));
	}
	
	private File makeTestPeerReviewJson() throws IOException {
		File jsonFile = temporaryFolder.newFile("peerReview.json");
		List<String> contents = Lists.newArrayList(
				"{",
				"  \"server\" : {",
				"  \"ip\" : \"127.0.0.1\",",
				"  \"port\" : \"8080\",",
				"  \"id\" : \"testId\",",
				"  \"pw\" : \"testPw\"",
				"  },",
				"  \"home\" : [{",
				"    \"projectName\" : \"testProject\",",
				"    \"sourceDir\": \"/test\",",
				"    \"active\" : \"on\"",
				"  }, {",
				"    \"projectName\" : \"testProject2\",",
				"    \"sourceDir\": \"/test2\",",
				"    \"active\" : \"off\"",
				"  }]",
				"}");
		
		CharSink sink = Files.asCharSink(jsonFile, Charsets.UTF_8);
	    sink.writeLines(contents, "\n");
		
	    return jsonFile;
	}
	
	private File makeTestPeerReviewJsonWithEmptyServer() throws IOException {
		File jsonFile = temporaryFolder.newFile("peerReview.json");
		List<String> contents = Lists.newArrayList(
				"{",
				"  \"server\" : {",
				"  },",
				"  \"home\" : [{",
				"    \"projectName\" : \"testProject\",",
				"    \"sourceDir\": \"/test\",",
				"    \"active\" : \"on\"",
				"  }, {",
				"    \"projectName\" : \"testProject2\",",
				"    \"sourceDir\": \"/test2\",",
				"    \"active\" : \"off\"",
				"  }]",
				"}");
		
		CharSink sink = Files.asCharSink(jsonFile, Charsets.UTF_8);
	    sink.writeLines(contents, "\n");
		
	    return jsonFile;
	}
	
	private File makeTestPeerReviewJsonWithEmptyHome() throws IOException {
		File jsonFile = temporaryFolder.newFile("peerReview.json");
		List<String> contents = Lists.newArrayList(
				"{",
				"  \"server\" : {",
				"  \"ip\" : \"127.0.0.1\",",
				"  \"port\" : \"8080\",",
				"  \"id\" : \"testId\",",
				"  \"pw\" : \"testPw\"",
				"  },",
				"  \"home\" : []",
				"}");
		
		CharSink sink = Files.asCharSink(jsonFile, Charsets.UTF_8);
	    sink.writeLines(contents, "\n");
		
	    return jsonFile;
	}

}
