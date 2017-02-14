package com.samsung.sec.dexter.executor.cli.peerreview;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.executor.cli.peerreview.PeerReviewConfigJob;
import com.samsung.sec.dexter.executor.cli.peerreview.PeerReviewController;

public class PeerReviewConfigJobTest {
	DexterConfig dexterConfig;
	PeerReviewController controller;
	PeerReviewConfigJob configJob;
	ScheduledExecutorService scheduler;
	File configFile;
	
	@Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	@Before
	public void setUp() throws Exception {
		dexterConfig = mock(DexterConfig.class);
		controller = mock(PeerReviewController.class);
		scheduler = mock(ScheduledExecutorService.class);
		configJob = new PeerReviewConfigJob(dexterConfig, controller, scheduler);
		
		when(dexterConfig.getDexterHome()).thenReturn(temporaryFolder.getRoot().getPath());
		temporaryFolder.newFolder("cfg");
		configFile = temporaryFolder.newFile("/cfg/peerReview.json");
	}
	
	@Test
	public void testRun_callUpdateIfConfigFileIsChanged() throws IOException {		
		configJob.start();
		configJob.run();
		
		verify(controller).update(any(File.class));
	}
	
	@Test
	public void testRun_doNotCallUpdateIfConfigFileIsNotChanged() throws IOException {		
		configJob.start();
		configJob.run();
		
		configJob.run();
		
		verify(controller, times(1)).update(any(File.class));
	}
	
	@Test 
	public void testStart_setConfigFile() {
		configJob.start();
		
		assertNotNull(configJob.getConfigFile());
	}
	
	@Test 
	public void testStart_startScheduler() {
		configJob.start();
		
		verify(scheduler).scheduleAtFixedRate(eq(configJob), anyLong(), anyLong(), eq(TimeUnit.SECONDS));
	}

	@Test 
	public void testStart_registDexterHomeListener() {
		configJob.start();
		
		verify(dexterConfig).addDexterHomeListener(eq(configJob));
	}
}
