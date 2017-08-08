package com.samsung.sec.dexter.executor.peerreview.cli;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.samsung.sec.dexter.core.util.DexterServerConfig;
import com.samsung.sec.dexter.core.util.IDexterClient;

public class HomeJsonCLIServerCheckStateTest {
	HomeJsonCLIServerCheckState state;
	PeerReviewHomeJsonCLI homeJsonCLI;
	IDexterClient dexterClient;

	@Before
	public void setUp() throws Exception {
		state = new HomeJsonCLIServerCheckState();
		homeJsonCLI = mock(PeerReviewHomeJsonCLI.class);
		dexterClient = mock(IDexterClient.class);
		when(homeJsonCLI.createDexterClient()).thenReturn(dexterClient);
	}

	@Test
	public void doAction_setStateToHomeState_IfServerIsAlive() {
		// given
		when(dexterClient.isServerAlive(anyString())).thenReturn(true);
		
		// when
		state.doAction(homeJsonCLI);
		
		// then
		verify(homeJsonCLI).setState(any(HomeJsonCLIHomeState.class));
	}

	@Test
	public void doAction_setStateToHostnameState_IfServerIsNotAlive() {
		// given
		when(dexterClient.isServerAlive(anyString())).thenReturn(false);
		
		// when
		state.doAction(homeJsonCLI);
		
		// then
		verify(homeJsonCLI).setState(any(HomeJsonCLIHostnameState.class));
	}
}
