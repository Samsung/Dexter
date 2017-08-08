package com.samsung.sec.dexter.executor.cli;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.samsung.sec.dexter.core.util.DexterClient;
import com.samsung.sec.dexter.core.util.EmptyDexterClient;
import com.samsung.sec.dexter.core.util.IDexterClient;

public class AccountServiceTest {
	AccountService service;
	ICLILog cliLog;
	IDexterCLIOption cliOption;

	@Before
	public void setUp() throws Exception {
		cliLog = mock(ICLILog.class);
		service = new AccountService(cliLog);
		cliOption = mock(IDexterCLIOption.class);
	}

	@Test
	public void init_setCliLog() {
		// then
		assertEquals(cliLog, service.getCliLog());
	}
	
	@Test
	public void createDexterClient_returnEmptyDexterClient_IfStandalonMode() {
		// given
		when(cliOption.isStandAloneMode()).thenReturn(true);
		
		// when
		IDexterClient client = service.createDexterClient(cliOption);
		
		// then
		assertTrue(client instanceof EmptyDexterClient);
	}

	@Test
	public void createDexterClient_returnDexterClient_IfNotStandalonMode() {
		// given
		when(cliOption.isStandAloneMode()).thenReturn(false);
		
		// when
		IDexterClient client = service.createDexterClient(cliOption);
		
		// then
		assertTrue(client instanceof DexterClient);
	}
	
	@Test
	public void createAccountHandler_returnEmptyAccountHandler_IfStandaloneMode() {
		// given
		when(cliOption.isStandAloneMode()).thenReturn(true);
		IDexterClient client = mock(IDexterClient.class);
		
		// when
		IAccountHandler handler = service.createAccountHandler(client, cliOption);
		
		// then
		assertTrue(handler instanceof EmptyAccountHandler);
	}
	
	@Test
	public void createAccountHandler_returnAccountHandler_IfNotStandaloneMode() {
		// given
		when(cliOption.isStandAloneMode()).thenReturn(false);
		IDexterClient client = mock(IDexterClient.class);
		
		// when
		IAccountHandler handler = service.createAccountHandler(client, cliOption);
		
		// then
		assertTrue(handler instanceof AccountHandler);
	}
}
