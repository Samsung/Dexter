package com.samsung.sec.dexter.metrics.util;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;

//import static org.junit.Assert.*;

import org.junit.Test;

import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.metrics.util.MetricUtil;

public class MetricUtilTest {
	String TestFilePath1 = ".\\src\\test\\com\\samsung\\sec\\dexter\\metrics\\util\\Test_file_for_MetricUtilTest1.txt";
	String TestFilePath2 = ".\\src\\test\\com\\samsung\\sec\\dexter\\metrics\\util\\Test_file_for_MetricUtilTest2.txt";
	
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
	public void MetricUtilTest_NonexistantFile() {
		MetricUtil tester = new MetricUtil();
		int start = 0;
		int end = 10;
		DexterRuntimeException exception = null;
		try {
			tester.getFunctionLOCArray(".\\ThereIsNoSuchFile.nope", start, end);
		}
		catch(DexterRuntimeException e) {
			exception=e;
			if(exception==null) {
				Assert.fail();
			}
		}
	}
	
	@Test
	public void MetricUtilTest_EmptyFilePath() {
		MetricUtil tester = new MetricUtil();
		int start = 0;
		int end = 10;
		tester.getFunctionLOCArray("", start, end);
	}
	
	@Test
	public void MetricUtilTest_StartGreaterThanEnd() {
		MetricUtil tester = new MetricUtil();
		int start = 4;
		int end = 2;
		tester.getFunctionLOCArray(TestFilePath1, start, end);
	}
	
	@Test
	public void MetricUtilTest_StartNegative() {
		MetricUtil tester = new MetricUtil();
		int start = -10;
		int end = 2;
		tester.getFunctionLOCArray(TestFilePath1, start, end);
	}
	
	@Test
	public void MetricUtilTest_TooLargeEnd() {
		MetricUtil tester = new MetricUtil();
		int start = 0;
		int end = 10000;
		int loc=0;
		loc=tester.getFunctionLOCArray(TestFilePath1, start, end);
		
		assertEquals(loc, 7);
	}
	
	@Test
	public void MetricUtilTest_ProperExample1() {
		MetricUtil tester = new MetricUtil();
		int start = 2;
		int end = 14;
		int loc=0;
		loc=tester.getFunctionLOCArray(TestFilePath1, start, end);
		
		assertEquals(loc, 6);
	}
	
	@Test
	public void MetricUtilTest_ProperExample2() {
		MetricUtil tester = new MetricUtil();
		int start = 0;
		int end = 21;
		int loc=0;
		loc=tester.getFunctionLOCArray(TestFilePath2, start, end);
		
		assertEquals(loc, 12);
	}
	
	
}
