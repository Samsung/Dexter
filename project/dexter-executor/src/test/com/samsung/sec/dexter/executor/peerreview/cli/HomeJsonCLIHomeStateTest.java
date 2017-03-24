package com.samsung.sec.dexter.executor.peerreview.cli;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

public class HomeJsonCLIHomeStateTest extends HomeJsonCLIStateTest{
	HomeJsonCLIHomeState state;

	@Before
	public void setUp() throws Exception {
		state = new HomeJsonCLIHomeState();
		when(fileUtil.exists(anyString())).thenReturn(true);
	}

	@Test
	public void doAction_getHomeFolder_FromCLI() {
		// given
		String homeFolder = "/test";
		homeJsonCLI.setScanner(new Scanner(homeFolder));
		
		// when
		state.doAction(homeJsonCLI);
		
		// then
		assertEquals(homeFolder, homeJsonCLI.getPeerReviewHomeJson().getHomeList().get(0).getSourceDir());
	}
	
	@Test
	public void doAction_setStateToCompleteState_IfSuccess() {
		// given
		String homeFolder = "/test";
		homeJsonCLI.setScanner(new Scanner(homeFolder));
		
		// when
		state.doAction(homeJsonCLI);
		
		// then
		assert(homeJsonCLI.getState() instanceof HomeJsonCLICompleteState);
	}
	
	@Test
	public void doAction_remainHomeState_IfHomeFolderIsNotExist() {
		// given
		when(fileUtil.exists(anyString())).thenReturn(false);
		String homeFolder = "/test";
		homeJsonCLI.setScanner(new Scanner(homeFolder));
		homeJsonCLI.setState(state);
		
		// when
		state.doAction(homeJsonCLI);
		
		// then
		assert(homeJsonCLI.getState() instanceof HomeJsonCLIHomeState);
	}

}
