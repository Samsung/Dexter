package com.samsung.sec.dexter.executor.peerreview.cli;

import com.samsung.sec.dexter.core.util.DexterServerConfig;
import com.samsung.sec.dexter.core.util.IDexterClient;

public class HomeJsonCLIServerCheckState implements IHomeJsonCLIState {

	@Override
	public void doAction(PeerReviewHomeJsonCLI homeJsonCLI) {
		IDexterClient dexterClient = homeJsonCLI.createDexterClient();

		System.out.print("Connecting server...");
		
		if (dexterClient.isServerAlive("test")) {
			System.out.println("\rCompleted to connect server.");
			homeJsonCLI.setState(new HomeJsonCLIHomeState());
		} else {
			System.out.println("\rCan't connect server (" + 
					dexterClient.getServerHost() + ":" + dexterClient.getServerPort() + ")");
			homeJsonCLI.setState(new HomeJsonCLIHostnameState());
		}
	}

}
