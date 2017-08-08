package com.samsung.sec.dexter.executor.peerreview.cli;

import java.util.List;

import com.samsung.sec.dexter.core.config.PeerReviewHome;
import com.samsung.sec.dexter.core.util.DexterServerConfig;
import com.samsung.sec.dexter.core.util.FileUtil;

public class HomeJsonCLIHomeState implements IHomeJsonCLIState {
	private static final String PROJECT_NAME = "project1";

	@Override
	public void doAction(PeerReviewHomeJsonCLI homeJsonCLI) {
		List<PeerReviewHome> homeList = homeJsonCLI.getPeerReviewHomeJson().getHomeList();
		FileUtil fileUtil = homeJsonCLI.getFileUtil();
		
		System.out.print("Input source folder path(ex: 'c:/work/project') << ");
		String homeFolder = homeJsonCLI.getScanner().next();
		
		if (fileUtil.exists(homeFolder)) {
			homeList.add(createHomeFolder(homeFolder, homeJsonCLI.getPeerReviewHomeJson().getServerConfig()));
			homeJsonCLI.getPeerReviewHomeJson().setHomeList(homeList);
			
			homeJsonCLI.setState(new HomeJsonCLICompleteState());
		} else {
			System.out.println("Source folder does not exist : " + homeFolder);
		}
	}

	private PeerReviewHome createHomeFolder(String homeFolder, DexterServerConfig serverConfig) {
		return new PeerReviewHome(serverConfig, PROJECT_NAME, homeFolder, true);
	}

}
