package com.samsung.sec.dexter.executor.peerreview.cli;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.samsung.sec.dexter.core.config.PeerReviewHomeJson;

public class HomeJsonCLIStartStateTest extends HomeJsonCLIStateTest {
	HomeJsonCLIStartState state;
	
	@Before
	public void setUp() throws Exception {
		state = new HomeJsonCLIStartState();
	}

	@Test
	public void doAction_setIdAndPassword_FromCliOption() {
		// given
		when(cliOption.getUserId()).thenReturn("testId");
		when(cliOption.getUserPassword()).thenReturn("testPw");
		
		// when
		state.doAction(homeJsonCLI);
		
		// then
		PeerReviewHomeJson homeJson = homeJsonCLI.getPeerReviewHomeJson();
		assertEquals("testId", homeJson.getServerConfig().getUserId());
		assertEquals("testPw", homeJson.getServerConfig().getUserPwd());
	}
	
	@Test
	public void doAction_setStateToHostnameState() {
		// when
		state.doAction(homeJsonCLI);
		
		// then
		assert(homeJsonCLI.getState() instanceof HomeJsonCLIHostnameState);
	}

}
