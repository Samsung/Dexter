package com.samsung.sec.dexter.executor.peerreview;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.InOrder;

import com.samsung.sec.dexter.core.config.PeerReviewHome;
import com.samsung.sec.dexter.core.util.DexterServerConfig;
import com.samsung.sec.dexter.executor.peerreview.PeerReviewHomeMonitor;
import com.samsung.sec.dexter.executor.peerreview.PeerReviewHomeMonitor.MonitoringState;
import com.samsung.sec.dexter.executor.peerreview.cli.PeerReviewCLIAnalyzer;

public class PeerReviewHomeMonitorTest {
	ExecutorService excutorService;
	PeerReviewHomeMonitor homeMonitor;
	PeerReviewCLIAnalyzer cliAnalyzer;
	Future<?> future;
	
	@Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	@Before
	public void setUp() throws Exception {
		excutorService = mock(ExecutorService.class);
		future = mock(Future.class);
		doReturn(future).when(excutorService).submit(any(Runnable.class));
		
		cliAnalyzer = mock(PeerReviewCLIAnalyzer.class);
		homeMonitor = new PeerReviewHomeMonitor(excutorService, FileSystems.getDefault().newWatchService(), cliAnalyzer);
	}

	@Test
	public void testRestart_submitRunnableTask() throws InterruptedException, IOException {
		List<PeerReviewHome> homeList = createTestPeerReviewHomeListForMapTest();
		
		homeMonitor.restart(homeList);
		
		verify(excutorService).submit(any(Runnable.class));
	}
	
	@Test
	public void testRestart_makePeerReviewWatchMapWithRightSize() throws IOException {
		List<PeerReviewHome> homeList = createTestPeerReviewHomeListForMapTest();
		
		homeMonitor.restart(homeList);
		
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
	
	@Test
	public void cancel_setMonitoringStateToCancel() throws IOException {
		// given
		List<PeerReviewHome> homeList = createTestPeerReviewHomeListForMapTest();
		homeMonitor.restart(homeList);
		
		// when
		homeMonitor.cancel();
		
		// then
		assertEquals(MonitoringState.CANCEL, homeMonitor.getMonitoringState());
	}

}
