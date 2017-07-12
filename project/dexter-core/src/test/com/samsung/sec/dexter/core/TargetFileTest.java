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
package com.samsung.sec.dexter.core;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;

import org.junit.Test;

import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.DexterConfig.LANGUAGE;

public class TargetFileTest {
	String TestFilePath = ".\\src\\java\\com\\samsung\\sec\\dexter\\core\\TestJavaFile_For_TargetFileTest.java";

	@Test
	public void setModulePath_And_getModulePathTest_SetsEmptyString_GivenEmpty() {
		TargetFile tester = new TargetFile();

		tester.setModulePath("");
		assertEquals(tester.getModulePath(), "");
	}

	@Test
	public void setModulePath_And_getModulePathTest_SetsModulePathWithSlashes_GivenModulePathWithBackslashes() {
		TargetFile tester = new TargetFile();

		tester.setModulePath("\\This\\is\\a\\path\\with\\backslashes\\");
		assertEquals(tester.getModulePath(), "This/is/a/path/with/backslashes");
	}

	@Test
	public void setModulePathTest_And_getModulePathTest_SetsModulePathWithSlashes_GivenJavaPathWithDots() {
		TargetFile tester = new TargetFile();
		
		tester.setFileName("I am a Java file.java");
		tester.setModulePath("This.is.A.Java.path.with.dots");

		assertEquals(tester.getModulePath(), "This/is/A/Java/path/with/dots");
	}
	
	@Test
	public void setFileStatus_And_getFileStatusTest_SetsProperFileStatus_GivenFileStatus() {
		TargetFile tester = new TargetFile();
		String FileStatus = "Fine";
		
		tester.setFileStatus(FileStatus);
		
		assertEquals(tester.getFileStatus(), FileStatus);
	}
	
	@Test
	public void getLanguageEnum_DeterminesProperLanguage_GivenFileWithExtension() {
		TargetFile tester = new TargetFile();
		
		tester.setFileName("I am Java file.JAVA");
		assertEquals(tester.getLanguageEnum(), DexterConfig.LANGUAGE.JAVA);
		tester.setFileName("I am C++ file.cpp");
		assertEquals(tester.getLanguageEnum(), DexterConfig.LANGUAGE.CPP);
		tester.setFileName("I am C++ file.hpp");
		assertEquals(tester.getLanguageEnum(), DexterConfig.LANGUAGE.CPP);
		tester.setFileName("I am C++ file.c");
		assertEquals(tester.getLanguageEnum(), DexterConfig.LANGUAGE.CPP);
		tester.setFileName("I am C++ file.h");
		assertEquals(tester.getLanguageEnum(), DexterConfig.LANGUAGE.CPP);
		tester.setFileName("I am JavaScript file.js");
		assertEquals(tester.getLanguageEnum(), DexterConfig.LANGUAGE.JAVASCRIPT);
		tester.setFileName("I am C# file.cs");
		assertEquals(tester.getLanguageEnum(), DexterConfig.LANGUAGE.C_SHARP);
		tester.setFileName("And I am a text file You do not know me.txt");
		assertEquals(tester.getLanguageEnum(), DexterConfig.LANGUAGE.UNKNOWN);
	}
}
