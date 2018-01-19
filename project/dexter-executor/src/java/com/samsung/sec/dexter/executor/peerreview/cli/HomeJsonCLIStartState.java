package com.samsung.sec.dexter.executor.peerreview.cli;

import com.samsung.sec.dexter.core.config.PeerReviewHomeJson;
import com.samsung.sec.dexter.core.util.DexterServerConfig;
import com.samsung.sec.dexter.executor.cli.IDexterCLIOption;

public class HomeJsonCLIStartState implements IHomeJsonCLIState {

	@Override
	public void doAction(PeerReviewHomeJsonCLI homeJsonCLI) {
		IDexterCLIOption cliOption = homeJsonCLI.getCLIOption();
		DexterServerConfig serverConfig = homeJsonCLI.getPeerReviewHomeJson().getServerConfig();
		
		serverConfig.setUserId(cliOption.getUserId());
		serverConfig.setUserPwd(cliOption.getUserPassword());
		
		homeJsonCLI.setState(new HomeJsonCLIHostnameState());
	}

}
