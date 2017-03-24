package com.samsung.sec.dexter.executor.peerreview.cli;

import static org.junit.Assert.*;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

public class HomeJsonCLIPortStateTest extends HomeJsonCLIStateTest {
	HomeJsonCLIPortState state;
	
	@Before
	public void setUp() throws Exception {
		state = new HomeJsonCLIPortState();
	}

	@Test
	public void doAction_getPort_FromCLI() {
		// given
		String port = "8080";
		homeJsonCLI.setScanner(new Scanner(port));
		
		// when
		state.doAction(homeJsonCLI);
		
		// then
		assertEquals(8080, homeJsonCLI.getPeerReviewHomeJson().getServerConfig().getPort());
	}
	
	@Test
	public void doAction_setStateToServerCheckState_IfSuccess() {
		// given
		String port = "8080";
		homeJsonCLI.setScanner(new Scanner(port));
		
		// when
		state.doAction(homeJsonCLI);
		
		// then
		assert(homeJsonCLI.getState() instanceof HomeJsonCLIServerCheckState);
	}

}
