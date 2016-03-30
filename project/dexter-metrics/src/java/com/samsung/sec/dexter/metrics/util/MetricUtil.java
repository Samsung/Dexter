package com.samsung.sec.dexter.metrics.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;

public class MetricUtil {
	public static int getFunctionLOCArray(final String source, final int start, final int end) {
		if(Strings.isNullOrEmpty(source) || start == end){
			return 0;
		}
		
		if(start > end){
			throw new DexterRuntimeException("cannot count line because end line is smaller than start line number. start:" + start + " end:" + end);
		}
		
		final Scanner scanner;
		
		try {
			scanner = new Scanner(new File(source));
			 
		} catch (FileNotFoundException e) {
			throw new DexterRuntimeException("can't read source File");
		}

		//final Scanner scanner = new Scanner(source);
		
		int currentLine = 0;
		while(currentLine < start){
			scanner.nextLine();
			currentLine++;
		}
		
		int length = end - start;
		int loc = 1;
		boolean isComment = false;
		for(int i=0; i < length; i++){
			String line = scanner.nextLine().trim();
			
			if(line.length() == 0)
				continue;
			
			int startOffSet = line.indexOf("/*");
			int endOffSet = line.indexOf("*/");
			if(startOffSet >= 0 && endOffSet >= startOffSet){
				if(startOffSet > 0)	loc++;
				continue;
			}
			
			int offset = line.indexOf("/*"); 
			if(offset >= 0){
				if(offset > 0) loc++;
				isComment = true;
				continue;
			}
			
			if(line.indexOf("*/") >= 0){
				isComment = false;
				continue;
			}
			
			if(isComment)
				continue;
			
			if(line.startsWith("//"))
				continue;
			
			loc++;
		}
		
		scanner.close();
		return loc;
	}
}
