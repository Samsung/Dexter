package com.samsung.sec.dexter.metrics.util;

//import static org.junit.Assert.*;

import org.junit.Test;

import com.samsung.sec.dexter.metrics.util.MetricUtil;

public class MetricUtilTest {

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

}
