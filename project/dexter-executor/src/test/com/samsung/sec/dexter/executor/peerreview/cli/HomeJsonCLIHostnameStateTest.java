package com.samsung.sec.dexter.executor.peerreview.cli;

import static org.junit.Assert.*;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

public class HomeJsonCLIHostnameStateTest extends HomeJsonCLIStateTest {
	HomeJsonCLIHostnameState state;
	
	@Before
	public void setUp() throws Exception {
		state = new HomeJsonCLIHostnameState();
	}

	@Test
	public void doAction_getHostname_FromCLI() {
		// given
		String hostName = "10.10.10.10";
		homeJsonCLI.setScanner(new Scanner(hostName));
		
		// when
		state.doAction(homeJsonCLI);
		
		// then
		assertEquals(hostName, homeJsonCLI.getPeerReviewHomeJson().getServerConfig().getHostname());
	}
	
	@Test
	public void doAction_setStateToPortState_IfSuccess() {
		// given
		String hostName = "10.10.10.10";
		homeJsonCLI.setScanner(new Scanner(hostName));
		
		// when
		state.doAction(homeJsonCLI);
		
		// then
		assert(homeJsonCLI.getState() instanceof HomeJsonCLIPortState);
	}
	
}
