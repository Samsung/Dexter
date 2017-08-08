package com.samsung.sec.dexter.executor.peerreview.cli;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.samsung.sec.dexter.core.config.PeerReviewHomeJson;
import com.samsung.sec.dexter.core.util.DexterServerConfig;
import com.samsung.sec.dexter.core.util.FileUtil;
import com.samsung.sec.dexter.core.util.IDexterClient;
import com.samsung.sec.dexter.executor.cli.IDexterCLIOption;

public class PeerReviewHomeJsonCLITest {
	PeerReviewHomeJsonCLI jsonCLI;
	IDexterCLIOption cliOption;
	FileUtil fileUtil;

	@Before
	public void setUp() throws Exception {
		cliOption = mock(IDexterCLIOption.class);
		fileUtil = mock(FileUtil.class);
		jsonCLI = new PeerReviewHomeJsonCLI(cliOption, fileUtil);
	}

	@Test
	public void init_setCLIOption() {
		// then
		assertEquals(cliOption, jsonCLI.getCLIOption());
	}
	
	@Test
	public void init_setFileUtil() {
		// then
		assertEquals(fileUtil, jsonCLI.getFileUtil());
	}
	
	@Test
	public void Init_setHomeJsonCLIStartState() {
		assert(jsonCLI.getState() instanceof HomeJsonCLIStartState);
	}
	
	@Test
	public void setState_setHomeJsonCLIState() {
		// given 
		IHomeJsonCLIState state = new HomeJsonCLIStartState();
		
		// when
		jsonCLI.setState(state);
		
		// then
		assertEquals(state, jsonCLI.getState());
	}
	
	@Test
	public void getPeerReviewHomeJsonFromUser_returnHomeJson_ifCompleteState() {
		// given 
		IHomeJsonCLIState state = new HomeJsonCLICompleteState();
		jsonCLI.setState(state);
		
		// when
		PeerReviewHomeJson json = jsonCLI.getPeerReviewHomeJsonFromUser();
		
		// then
		assertNotNull(json);
	}
	
	@Test
	public void getPeerReviewHomeJsonFromUser_callDoAction_ifNotCompleteState() {
		// given
		HomeJsonCLITestState state = new HomeJsonCLITestState();
		jsonCLI.setState(state);
		
		// when
		jsonCLI.getPeerReviewHomeJsonFromUser();
		
		// then
		assertEquals(true, state.isCalled());
	}

	@Test
	public void createDexterClient_createDexterClientWithServerConfig() {
		// when
		IDexterClient dexterClient = jsonCLI.createDexterClient();
		
		// then
		assertNotNull(dexterClient);
	}
}
