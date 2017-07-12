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
package com.samsung.sec.dexter.metrics.util;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;

//import static org.junit.Assert.*;

import org.junit.Test;

import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.metrics.util.MetricUtil;

public class MetricUtilTest {
	String TestFilePath = ".\\src\\test\\com\\samsung\\sec\\dexter\\metrics\\util\\TestFile_For_MetricUtilTest.java";
	
	@Test
	public void test() {
		final String source = "void main()\n" 
	+ "{\n" + 
	"//test4\n" + 
	"/*test6\n" + 
	"THIS IS COMMENT 1\n" + 
	"THIS IS COMMENT 2\n" + 
	"THIS IS COMMENT 3\n" + 
	"*/\n" + 
	"print(\"%d\",10);\n" + 
	"THIS IS NOT A COMMENT1 //comment \n"+
	"THIS IS NOT A COMMENT2 /*comment*/ \n" +
	"void main3(){ } \n" +
	"void main5(){ \n" +
	"} \n"  +
    "}";
		
	//MetricUtil.getFunctionLOCArray(source, 0, 14);
	}
	
	@Test
	public void MetricUtilTest_ThrowsDexterRuntimeException_GivenNoneExistanceFile () {
		int start = 0;
		int end = 10;
		try {
			MetricUtil.getFunctionLOCArray(".\\ThereIsNoSuchFile.no", start, end);
			Assert.fail();
		}
		catch(DexterRuntimeException e) {

		}
	}
	
	@Test
	public void MetricUtilTest_ReturnsZero_GivenEmptyFileName () {
		int start = 0;
		int end = 10;
		int loc;
		try {
			loc=MetricUtil.getFunctionLOCArray("", start, end);
			assertEquals(0, loc);
		}
		catch(DexterRuntimeException e) {
			Assert.fail();
		}
	}
	
	@Test
	public void MetricUtilTest_EmptyFilePath() {
		int start = 0;
		int end = 10;
		MetricUtil.getFunctionLOCArray("", start, end);
	}
	
	@Test
	public void MetricUtilTest_ThrowsDexterRuntimeException_GivenStartGreaterThanEnd() {
		int start = 4;
		int end = 2;
		try {
			MetricUtil.getFunctionLOCArray(TestFilePath, start, end);
			Assert.fail();
		}
		catch(DexterRuntimeException e) {
			assertEquals("cannot count line because end line "
					+ "is smaller than start line number. "
					+ "start:" + start + " end:" + end, e.getMessage());
		}
	}
	
	@Test
	public void MetricUtilTest_ReturnsProperLoc_GivenCorrectExample() {
		int start = 0;
		int end = 23;
		int loc=0;
		loc=MetricUtil.getFunctionLOCArray(TestFilePath, start, end);
		
		assertEquals(loc, 12);
	}
}
