package com.samsung.sec.dexter.core.util;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FileServiceTest {
	FileUtil fileService;
	
	@Rule
	public final TemporaryFolder tempFolder = new TemporaryFolder();

	@Before
	public void setUp() throws Exception {
		fileService = new FileUtil();
	}

	@Test
	public void exists_returnTrueIfFileExistsInThePath() throws IOException {
		// given
		File homeFolder = tempFolder.newFolder("dexterHome");
		
		// when
		boolean result = fileService.exists(homeFolder.getPath());
		
		// then
		assertEquals(true, result);
	}
	
	@Test
	public void exists_returnFalseIfFileNotExistsInThePath() throws IOException {
		// when
		boolean result = fileService.exists("./t/e/s/t/nonExistFolder");
		
		// then
		assertEquals(false, result);
	}

}
