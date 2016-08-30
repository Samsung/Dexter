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
package com.samsung.sec.dexter.core.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class DexterUtilTest {
	@Test
	public void test_createDirectoryIfNotExist(){
		final String folderName = "./test-folder";
		assertFalse(new File(folderName).exists());
		
		DexterUtil.createDirectoryIfNotExist(folderName);
		assertTrue(new File(folderName).exists());
		
		assertTrue(new File(folderName).delete());
	}
	
	@Test
	public void test_createEmptyFileIfNotExist(){
		final String filePath = "./test-file.txt";
		assertFalse(new File(filePath).exists());
		
		DexterUtil.createEmptyFileIfNotExist(filePath);
		assertTrue(new File(filePath).exists());
		
		assertTrue(new File(filePath).delete());
	}
	
	@Test
	public void test_getFileNameWithoutExtension() {
		assertEquals("filename", DexterUtil.getFileNameWithoutExtension("filename.txt"));
		assertEquals("file.name", DexterUtil.getFileNameWithoutExtension("file.name.txt"));
		assertEquals("f.ile.name", DexterUtil.getFileNameWithoutExtension("f.ile.name.txt"));
		assertEquals("file_name", DexterUtil.getFileNameWithoutExtension("file_name.txt"));
		assertEquals("f", DexterUtil.getFileNameWithoutExtension("f.txt"));
		assertEquals("f", DexterUtil.getFileNameWithoutExtension("f.t"));
		assertEquals("filename", DexterUtil.getFileNameWithoutExtension("filename"));
		assertEquals("", DexterUtil.getFileNameWithoutExtension(".txt"));
	}
	
	@Test
	public void test_getSubFiles(){
		final String folder = "./result";
		final String fileName = "filename_";
		final String oldFileName1 =  fileName + "1234.json";
		final String oldFileName2 = fileName + "1235.json";
		final String oldFileName3 = fileName + "1236.json";
		final String noOldFileName1 = "file1name" + "0000.json";
		final String noOldFileName2 = "1" + fileName + "1111.json";
		
		DexterUtil.createDirectoryIfNotExist(folder);
		DexterUtil.createEmptyFileIfNotExist(folder + "/" +oldFileName1);
		DexterUtil.createEmptyFileIfNotExist(folder + "/" +oldFileName2);
		DexterUtil.createEmptyFileIfNotExist(folder + "/" +oldFileName3);
		DexterUtil.createEmptyFileIfNotExist(folder + "/" +noOldFileName1);
		DexterUtil.createEmptyFileIfNotExist(folder + "/" +noOldFileName2);
		
		File files[] = DexterUtil.getSubFilesByPrefix(new File(folder), fileName);
		
		for(File file : files){
			final String name = file.getName();
			
			if(!name.equals(oldFileName1) && !name.equals(oldFileName2) && !name.equals(oldFileName3)){
				System.out.println(name);
				fail();
			}
			
			if(name.equals(noOldFileName1) || name.equals(noOldFileName2)){
				fail();
			}
		}
		
		try {
			DexterUtil.deleteDirectory(new File(folder));
		} catch (IOException e) {
			fail();
			e.printStackTrace();
		}
	}
}
