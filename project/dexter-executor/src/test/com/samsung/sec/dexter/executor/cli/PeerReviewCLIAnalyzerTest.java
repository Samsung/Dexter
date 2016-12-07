package com.samsung.sec.dexter.executor.cli;

import static org.mockito.Mockito.*;

import org.junit.Before;
import com.samsung.sec.dexter.executor.DexterAnalyzer;

public class PeerReviewCLIAnalyzerTest {
	ICLILog cliLog;
	IDexterCLIOption cliOption;
	DexterAnalyzer dexterAnalyzer;
	PeerReviewCLIAnalyzer cliAnalyzer;
	
	@Before
	public void setUp() throws Exception {
		cliLog = mock(ICLILog.class);
		cliOption = mock(IDexterCLIOption.class);
		dexterAnalyzer = mock(DexterAnalyzer.class);
		
		cliAnalyzer = new PeerReviewCLIAnalyzer(cliOption, cliLog, dexterAnalyzer);
	}
	

}
