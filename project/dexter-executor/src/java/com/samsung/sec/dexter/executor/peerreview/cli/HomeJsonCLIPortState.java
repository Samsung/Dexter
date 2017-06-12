package com.samsung.sec.dexter.executor.peerreview.cli;

import com.samsung.sec.dexter.core.util.DexterServerConfig;

public class HomeJsonCLIPortState implements IHomeJsonCLIState {

	@Override
	public void doAction(PeerReviewHomeJsonCLI homeJsonCLI) {
		DexterServerConfig serverConfig = homeJsonCLI.getPeerReviewHomeJson().getServerConfig();
		
		System.out.print("Input server port(ex: '8080') << ");
		int port = homeJsonCLI.getScanner().nextInt();
		
		serverConfig.setPort(port);
		homeJsonCLI.setState(new HomeJsonCLIServerCheckState());
	}

}
