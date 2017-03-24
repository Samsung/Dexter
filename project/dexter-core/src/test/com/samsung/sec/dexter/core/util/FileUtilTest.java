package com.samsung.sec.dexter.core.util;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FileUtilTest {
	FileUtil fileUtil;
	
	@Rule
	public final TemporaryFolder tempFolder = new TemporaryFolder();

	@Before
	public void setUp() throws Exception {
		fileUtil = new FileUtil();
	}

	@Test
	public void exists_returnTrueIfFileExistsInThePath() throws IOException {
		// given
		File homeFolder = tempFolder.newFolder("dexterHome");
		
		// when
		boolean result = fileUtil.exists(homeFolder.getPath());
		
		// then
		assertEquals(true, result);
	}
	
	@Test
	public void exists_returnFalseIfFileNotExistsInThePath() throws IOException {
		// when
		boolean result = fileUtil.exists("./t/e/s/t/nonExistFolder");
		
		// then
		assertEquals(false, result);
	}
	
	@Test
	public void writeToFile_writeFile() throws IOException {
		// given
		StringWriter strWriter = new StringWriter();
		String msg = "This is a test file.";
		
		// when
		fileUtil.write(strWriter, msg);
		
		// then
		assertEquals(msg, strWriter.toString());
	}

}
