package com.samsung.sec.dexter.executor.peerreview.cli;

import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.util.DexterServerConfig;

public class HomeJsonCLIHostnameState implements IHomeJsonCLIState {

	@Override
	public void doAction(PeerReviewHomeJsonCLI homeJsonCLI) {
		DexterServerConfig serverConfig = homeJsonCLI.getPeerReviewHomeJson().getServerConfig();
		
		System.out.print("Input server address(ex: '10.23.45.67') << ");
		String hostname = homeJsonCLI.getScanner().next();
		
		if (Strings.isNullOrEmpty(hostname)) {
			System.out.println("Invalid server address!!");
		} else {
			serverConfig.setHostname(hostname);
			homeJsonCLI.setState(new HomeJsonCLIPortState());
		}
		
	}

}
