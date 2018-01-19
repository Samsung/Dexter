package com.samsung.sec.dexter.executor.peerreview.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.samsung.sec.dexter.core.config.PeerReviewHome;
import com.samsung.sec.dexter.core.config.PeerReviewHomeJson;
import com.samsung.sec.dexter.core.util.DexterClient;
import com.samsung.sec.dexter.core.util.DexterServerConfig;
import com.samsung.sec.dexter.core.util.FileUtil;
import com.samsung.sec.dexter.core.util.IDexterClient;
import com.samsung.sec.dexter.core.util.JerseyDexterWebResource;
import com.samsung.sec.dexter.executor.cli.IDexterCLIOption;
import com.samsung.sec.dexter.executor.peerreview.IPeerReviewHomeJsonScanner;

public class PeerReviewHomeJsonCLI implements IPeerReviewHomeJsonScanner{
	IDexterCLIOption cliOption;
	IHomeJsonCLIState homeJsonCLIState;
	PeerReviewHomeJson homeJson;
	Scanner scanner;
	FileUtil fileUtil;
	
	public PeerReviewHomeJsonCLI(IDexterCLIOption cliOption, FileUtil fileUtil) {
		this.cliOption = cliOption;
		homeJsonCLIState = new HomeJsonCLIStartState();
		homeJson = createEmptyHomeJson();
		this.scanner = new Scanner(System.in);
		this.fileUtil = fileUtil;
	}

	public Scanner getScanner() {
		return scanner;
	}

	public void setScanner(Scanner scanner) {
		this.scanner = scanner;
	}

	private PeerReviewHomeJson createEmptyHomeJson() {
		DexterServerConfig config = new DexterServerConfig("", "", "", 0);
		List<PeerReviewHome> homeList = new ArrayList<PeerReviewHome>();
		
		return new PeerReviewHomeJson(config, homeList);
	}

	public IDexterCLIOption getCLIOption() {
		return cliOption;
	}

	public void setState(IHomeJsonCLIState state) {
		homeJsonCLIState = state;
	}

	public IHomeJsonCLIState getState() {
		return homeJsonCLIState;
	}

	@Override
	public PeerReviewHomeJson getPeerReviewHomeJsonFromUser() {
		
		while (!(homeJsonCLIState instanceof HomeJsonCLICompleteState)) {
			homeJsonCLIState.doAction(this);
		}
		
		return homeJson;
	}

	public PeerReviewHomeJson getPeerReviewHomeJson() {
		return homeJson;
	}

	public FileUtil getFileUtil() {
		return fileUtil;
	}

	public IDexterClient createDexterClient() {
		return new DexterClient(
				new JerseyDexterWebResource(homeJson.getServerConfig()));
	}

}
