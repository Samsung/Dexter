/**
 * Copyright (c) 2017 Samsung Electronics, Inc.,
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
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
package com.samsung.sec.dexter.metrics;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.DexterConfig.LANGUAGE;
import com.samsung.sec.dexter.core.metrics.CodeMetrics;
import com.samsung.sec.dexter.core.metrics.FunctionMetrics;

public class CodeMetricsGeneratorTest {
	CodeMetricsGenerator tester = new CodeMetricsGenerator();
	DexterConfig.LANGUAGE language = null;
	String filePath = null;
	CodeMetrics codeMetrics = null;
	FunctionMetrics functionMetrics = null;
	List<String> functionList = null;

	String TestDirectoryPath = ".\\src\\sample\\TestDirectory_For_CodeMetricsGeneratorTest";
	String EmptyJavaTestFilePath = ".\\src\\sample\\TestJavaFile_For_CodeMetricsGeneratorTest_Empty.java";
	String TooLongTestFilePath = ".\\src\\sample\\TestFile_For_CodeMetricsGeneratorTest_TooLong.txt";
	String LogPath = ".\\log\\dexter-core.log";
	
	private boolean isDefault(CodeMetrics codeMetrics) {
		if(!codeMetrics.getMetric("loc").equals(0)) {
			return false;
		}
		if(!codeMetrics.getMetric("sloc").equals(0)) {
			return false;
		}
		if(!codeMetrics.getMetric("maxComplexity").equals(0)) {
			return false;
		}
		if(!codeMetrics.getMetric("classCnt").equals(0)) {
			return false;
		}
		if(!codeMetrics.getMetric("methodCnt").equals(0)) {
			return false;
		}
		if(!codeMetrics.getMetric("minComplexity").equals(0)) {
			return false;
		}
		if(!codeMetrics.getMetric("averageCompexity").equals(0)) {
			return false;
		}
		if(!codeMetrics.getMetric("commentRatio").equals(0.00f)) {
			return false;
		}
		
		return true;
	}
	
	private boolean isDefault(FunctionMetrics codeMetrics) {
		if(!codeMetrics.getMetric("cc").equals(0)) {
			return false;
		}
		if(!codeMetrics.getMetric("sloc").equals(0)) {
			return false;
		}
		if(!codeMetrics.getMetric("callDepth").equals(0)) {
			return false;
		}
		
		return true;
	}
	
	@Test
	public void mainTest_DoesNOTThrowExceptions() {
		CodeMetricsGenerator tester = new CodeMetricsGenerator();
		String[] args = null;

		try {
			CodeMetricsGenerator.main(args);
		} catch (IOException | CheckstyleException e) {
			Assert.fail("main function threw an exception\n" + e.toString());
		}
	}

	@Test
	public void getCodeMetricsTest_LogsAndReturns_GivenNullFilePath() {
		File log = new File(LogPath);
		long fileLength = log.length();
		
		CodeMetricsGenerator.getCodeMetrics(language, filePath, codeMetrics, functionMetrics, functionList);
		
		if(fileLength>=log.length()) {
			Assert.fail("Log was NOT modified by CodeMetricsGenerator");
		}
	}

	@Test
	public void getCodeMetricsTest_LogsAndReturns_GivenDirectoryPath() {
		String filePath = TestDirectoryPath;
		File log = new File(LogPath);
		long fileLength = log.length();
		
		CodeMetricsGenerator.getCodeMetrics(language, filePath, codeMetrics, functionMetrics, functionList);
		
		if(fileLength>=log.length()) {
			Assert.fail("Log was NOT modified by CodeMetricsGenerator");
		}
	}

	@Test
	public void getCodeMetricsTest_LogsAndReturns_GivenTooLongFile() {
		String filePath = TooLongTestFilePath;
		File log = new File(LogPath);
		long fileLength = log.length();
		
		CodeMetricsGenerator.getCodeMetrics(language, filePath, codeMetrics, functionMetrics, functionList);
		
		if(fileLength>=log.length()) {
			Assert.fail("Log was NOT modified by CodeMetricsGenerator");
		}
		
	}
	
	@Test
	public void getCodeMetricsTest_CreatesDefaulMetrics_GivenEmptyJavaFile() {
		DexterConfig.LANGUAGE language = DexterConfig.LANGUAGE.JAVA;
		CodeMetrics codeMetrics = new CodeMetrics();
		FunctionMetrics functionMetrics = new FunctionMetrics();
		String filePath = EmptyJavaTestFilePath;

		CodeMetricsGenerator.getCodeMetrics(language, filePath, codeMetrics, functionMetrics, functionList);
		
		assertTrue(isDefault(codeMetrics));
		assertTrue(isDefault(functionMetrics));
	}
}
