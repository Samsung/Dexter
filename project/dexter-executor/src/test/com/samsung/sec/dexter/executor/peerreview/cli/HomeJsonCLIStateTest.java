package com.samsung.sec.dexter.executor.peerreview.cli;

import static org.mockito.Mockito.*;

import com.samsung.sec.dexter.core.util.FileUtil;
import com.samsung.sec.dexter.executor.cli.IDexterCLIOption;

public class HomeJsonCLIStateTest {

	protected IDexterCLIOption cliOption;
	protected PeerReviewHomeJsonCLI homeJsonCLI;
	protected FileUtil fileUtil;

	public HomeJsonCLIStateTest() {
		cliOption = mock(IDexterCLIOption.class);
		fileUtil = mock(FileUtil.class);
		homeJsonCLI = new PeerReviewHomeJsonCLI(cliOption, fileUtil);
	}

}