package com.samsung.sec.dexter.executor.peerreview.cli;

public class HomeJsonCLITestState implements IHomeJsonCLIState {
	boolean isCalled = false;
	
	@Override
	public void doAction(PeerReviewHomeJsonCLI homeJsonCLI) {
		isCalled = true;
		homeJsonCLI.setState(new HomeJsonCLICompleteState());
	}
	
	public boolean isCalled() {
		return isCalled;
	}

}
