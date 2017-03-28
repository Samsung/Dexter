package com.samsung.sec.dexter.executor.peerreview.cli;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.concurrent.ExecutionException;

import org.junit.Before;
import org.junit.Test;

import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.IDexterConfigFile;
import com.samsung.sec.dexter.core.config.DexterConfig.RunMode;
import com.samsung.sec.dexter.core.plugin.IDexterPluginManager;
import com.samsung.sec.dexter.executor.cli.AccountService;
import com.samsung.sec.dexter.executor.cli.IDexterCLIOption;
import com.samsung.sec.dexter.executor.cli.IDexterCLIOption.CommandMode;
import com.samsung.sec.dexter.executor.peerreview.PeerReviewConfigJob;
import com.samsung.sec.dexter.executor.peerreview.cli.PeerReviewMain;

public class PeerReviewMainTest {
	DexterConfig dexterConfig;
	IDexterCLIOption cliOption;
	IDexterConfigFile dexterConfigFile;
	PeerReviewConfigJob configJob;
	PeerReviewMain peerReviewMain;
	IDexterPluginManager pluginManager;
	AccountService accountService;
	
	@Before
	public void setUp() {
		dexterConfig = mock(DexterConfig.class);
		cliOption = mock(IDexterCLIOption.class);
		configJob = mock(PeerReviewConfigJob.class);
		dexterConfigFile = mock(IDexterConfigFile.class);
		pluginManager = mock(IDexterPluginManager.class);
		accountService = mock(AccountService.class);
		
		when(cliOption.getConfigFilePath()).thenReturn("./");
		
		peerReviewMain = new PeerReviewMain(dexterConfig, cliOption, dexterConfigFile, 
			configJob, pluginManager, accountService);
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
	public void testStart_startConfigJob() throws InterruptedException, ExecutionException {
		peerReviewMain.startConfigJob();
		
		verify(configJob).start();
	}
	
	@Test
	public void init_setAccountService() {
		// then
		assertEquals(accountService, peerReviewMain.getAccountService());
	}
	
	@Test
	public void doMain_callCreateAccount_IfCreateAccountMode() throws InterruptedException, ExecutionException {
		// given
		when(cliOption.getCommandMode()).thenReturn(CommandMode.CREATE_ACCOUNT);
		
		// when
		peerReviewMain.doMain();
		
		// then
		verify(accountService).createAccount(eq(cliOption));
	}
	
	@Test
	public void doMain_doNotCallCreateAccount_IfNotCreateAccountMode() throws InterruptedException, ExecutionException {
		// given
		when(cliOption.getCommandMode()).thenReturn(CommandMode.STATIC_ANALYSIS);
		
		// when
		peerReviewMain.doMain();
		
		// then
		verify(accountService, never()).createAccount(eq(cliOption));
	}
}
