/**
 * Copyright (c) 2014 Samsung Electronics, Inc.,
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package com.samsung.sec.dexter.executor.cli;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.samsung.sec.dexter.core.analyzer.AnalysisEntityTestUtil;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.analyzer.EndOfAnalysisHandler;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.IDexterClient;

public class MainTest {

	@Test
	public void createResultChangeHandler_should_print_result_of_static_analysis_on_cli() {
		Main cliMain = new Main();
		EndOfAnalysisHandler handler = cliMain.createResultChanageHandler();
		
		AnalysisResult result = AnalysisEntityTestUtil.createSampleAnalysisResult();
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		cliMain.setLog(new CliLogger(new PrintStream(out)));

		List<AnalysisResult> resultList = new ArrayList<AnalysisResult>();
		resultList.add(result);
		handler.handleAnalysisResult(resultList);

		String expectStr = " ● C:/test-project/src/test-module/test-filename\r\n"
				+ "    ▶ Total Defects: 1\r\n"
				+ "    ▶ test-checker-code / CRI / 1\r\n"
				+ "       └ 10 test-occurence-message\r\n";
		assertEquals(expectStr, out.toString());
	}

	@Test
	public void should_create_account_when_using_c_option(){
		IDexterClient client = mock(IDexterClient.class);

		when(client.hasAccount(anyString())).thenReturn(false);
		//when(client.login()).thenThrow(new DexterRuntimeException(""));
		//doThrow(new DexterRuntimeException("")).when(client).login();
		
		System.out.println(client.hasAccount("abc"));
		
		//verify(client).hasAccount("abc");
	}
}
