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
import static org.junit.Assert.assertNotNull;

import java.util.Random;

import org.codehaus.jackson.map.deser.ValueInstantiators.Base;
import org.junit.Assert;
import org.junit.Test;

public class BaseAnalysisEntityTest {
	private int NumberOfTests=10000;

	@Test
	public void setProjectName_And_getProjectNameTest() {
		BaseAnalysisEntity tester = new BaseAnalysisEntity();
		String projectName = "MyProject";

		tester.setProjectName(projectName);

		Assert.assertEquals(projectName, tester.getProjectName());
	}

	@Test
	public void setProjectFullPath_And_getProjectFullPathTest() {
		BaseAnalysisEntity tester = new BaseAnalysisEntity();
		String fullPath = "C:\\Program Files (x86)\\Eclipse\\workspace\\MyProject";

		tester.setProjectFullPath(fullPath);

		// setting fullPath should change all "\\" to "/"
		fullPath = fullPath.replace("\\", "/");
		
		Assert.assertEquals(fullPath, tester.getProjectFullPath());
	}

	@Test
	public void setSnapshotId_And_getSnapshotIdTest() {
		BaseAnalysisEntity tester = new BaseAnalysisEntity();
		long snapshotId = 0;
		Random random = new Random();

		for (int i = 0; i < NumberOfTests; i++) {
			snapshotId = random.nextLong();
			tester.setSnapshotId(snapshotId);
			Assert.assertEquals(snapshotId, tester.getSnapshotId());
		}
	}

	@Test
	public void setSourceFileFullPath_And_getSourceFileFullPathTest_FirstTest_SetsSourceFileFullPathWithoutSlashesAndBackslashesAtTheEnd() {
		BaseAnalysisEntity tester = new BaseAnalysisEntity();
		String SourceFileFullPath = "C:\\Program Files (x86)\\Eclipse\\workspace\\MyProject\\src\\MyClass.java";
		String ProccessedSourceFileFullPath = null;

		tester.setSourceFileFullPath(SourceFileFullPath);
		ProccessedSourceFileFullPath = tester.getSourceFileFullPath();

		Assert.assertFalse(ProccessedSourceFileFullPath.endsWith("\\"));
		Assert.assertFalse(ProccessedSourceFileFullPath.endsWith("/"));
	}
}
