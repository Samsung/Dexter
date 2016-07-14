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
package com.samsung.sec.dexter.plugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisEntityFactory;
import com.samsung.sec.dexter.core.plugin.BaseDexterPluginManager;
import com.samsung.sec.dexter.core.plugin.EmptyDexterPluginInitializer;
import com.samsung.sec.dexter.core.plugin.IDexterPluginManager;

public class DexterPluginManagerTest {
	private IDexterPluginManager pluginManager = new BaseDexterPluginManager(new EmptyDexterPluginInitializer());
	
	@Test
	public void shouldWorkWhenSrcFolderHasFile() throws IOException {
		File tempDir = Files.createTempDir();
		File srcFile = new File(tempDir.getAbsolutePath() + "/" + "TestSource.java");
		StringBuilder contents = new StringBuilder();
		contents.append("public class TestSource { ");
		contents.append("\tpublic void main(String[] args){ }\t}");
	    Files.write(contents.toString(), srcFile, Charsets.UTF_8);
	    
		AnalysisConfig config = (new AnalysisEntityFactory()).createAnalysisConfig();
		// set source dir that has no source file
		config.setSourceBaseDirList(Arrays.asList(tempDir.getAbsolutePath()));
		
		pluginManager.analyze(config);

		deleteDir(tempDir);
	}
	
	@Test
	public void shouldWorkWhenSrcFolderHasNoFile() {
		File tempDir = Files.createTempDir();
		
		AnalysisConfig config = (new AnalysisEntityFactory()).createAnalysisConfig();
		// set source dir that has no source file
		config.setSourceBaseDirList(Arrays.asList(tempDir.getAbsolutePath()));
		
		pluginManager.analyze(config);

		deleteDir(tempDir);
	}

	private void deleteDir(File tempDir) {
		try {
			FileUtils.deleteDirectory(tempDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
