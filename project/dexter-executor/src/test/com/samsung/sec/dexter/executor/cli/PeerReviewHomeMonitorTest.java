package com.samsung.sec.dexter.executor.cli;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.InOrder;

import com.samsung.sec.dexter.core.config.PeerReviewHome;
import com.samsung.sec.dexter.core.util.DexterServerConfig;

public class PeerReviewHomeMonitorTest {
	ExecutorService excutorService;
	PeerReviewHomeMonitor homeMonitor;
	PeerReviewCLIAnalyzer cliAnalyzer;
	
	@Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	@Before
	public void setUp() throws Exception {
		excutorService = mock(ExecutorService.class);
		cliAnalyzer = mock(PeerReviewCLIAnalyzer.class);
		homeMonitor = new PeerReviewHomeMonitor(excutorService, FileSystems.getDefault().newWatchService(), cliAnalyzer);
	}

	@Test
	public void testUpdate_submitRunnableTask() throws InterruptedException, IOException {
		List<PeerReviewHome> homeList = createTestPeerReviewHomeListForMapTest();
		
		homeMonitor.update(homeList);
		
		verify(excutorService).submit(any(Runnable.class));
	}
	
	@Test
	public void testUpdate_makePeerReviewWatchMapWithRightSize() throws IOException {
		List<PeerReviewHome> homeList = createTestPeerReviewHomeListForMapTest();
		
		homeMonitor.update(homeList);
		
		assertEquals(3, homeMonitor.getPeerReviewWatchMap().size());
	}
	
	private List<PeerReviewHome> createTestPeerReviewHomeListForMapTest() throws IOException {
		File test1Dir = temporaryFolder.newFolder("test1");
		File test2Dir = temporaryFolder.newFolder("test2");
		temporaryFolder.newFolder("test2", "subTest");
		
		List<PeerReviewHome> homeList = new ArrayList<PeerReviewHome>();
		
		homeList.add(new PeerReviewHome(
				new DexterServerConfig("testId", "testPw", "127.0.0.1", 8080),
				"testProject", test1Dir.getPath(), true));
		homeList.add(new PeerReviewHome(
				new DexterServerConfig("testId", "testPw", "127.0.0.1", 8080),
				"test2Project", test2Dir.getPath(), true));
		
		return homeList;
	}

}
