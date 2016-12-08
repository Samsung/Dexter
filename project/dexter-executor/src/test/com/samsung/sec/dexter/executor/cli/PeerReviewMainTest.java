package com.samsung.sec.dexter.executor.cli;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.IDexterConfigFile;
import com.samsung.sec.dexter.core.config.DexterConfig.RunMode;
import com.samsung.sec.dexter.core.plugin.IDexterPluginManager;

public class PeerReviewMainTest {
	DexterConfig dexterConfig;
	IDexterCLIOption cliOption;
	IDexterConfigFile dexterConfigFile;
	PeerReviewConfigJob configJob;
	PeerReviewMain peerReviewMain;
	IDexterPluginManager pluginManager;
	
	@Before
	public void setUp() {
		dexterConfig = mock(DexterConfig.class);
		cliOption = mock(IDexterCLIOption.class);
		configJob = mock(PeerReviewConfigJob.class);
		dexterConfigFile = mock(IDexterConfigFile.class);
		pluginManager = mock(IDexterPluginManager.class);
		
		when(cliOption.getConfigFilePath()).thenReturn("./");
		
		peerReviewMain = new PeerReviewMain(dexterConfig, cliOption, dexterConfigFile, configJob, pluginManager);
	}
	  
	@Test
	public void testInitDexterConfig_setRunModeToCLI() {
		peerReviewMain.initDexterConfig();
		
		verify(dexterConfig).setRunMode(RunMode.CLI);
	}
	
	@Test
	public void testInitDexterConfig_loadDexterConfigFile() {
		peerReviewMain.initDexterConfig();
		
		verify(dexterConfigFile).loadFromFile(any(File.class));
	}
	
	@Test
	public void testInitDexterConfig_setDexterHome() {
		when(dexterConfigFile.getDexterHome()).thenReturn("/testHome");
		
		peerReviewMain.initDexterConfig();
		
		verify(dexterConfig).setDexterHome("/testHome");
	}

	@Test
	public void testStart_startConfigJob() {
		peerReviewMain.startConfigJob();
		
		verify(configJob).start();
	}
}
