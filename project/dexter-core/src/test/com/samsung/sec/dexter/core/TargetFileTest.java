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
