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
package com.samsung.sec.dexter.core.job;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.DexterClient;
import com.samsung.sec.dexter.core.util.DexterUtil;

public class SendResultJob implements Runnable {
	private final static Logger logger = Logger.getLogger(SendResultJob.class);
	private static AtomicInteger COUNT = new AtomicInteger(DexterJobFacade.MAX_JOB_DELAY_COUNT);
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
    @Override
    public void run() {
    	long freeMemSize = Runtime.getRuntime().freeMemory();
    	
    	if(freeMemSize > DexterJobFacade.ALLOWED_FREE_MEMORY_SIZE_FOR_JOBS || COUNT.get() > DexterJobFacade.MAX_JOB_DELAY_COUNT){
    		COUNT.set(0);
    		try {
	            SendResultJob.send();
	            logger.debug("SendResultJob Executed");
            } catch (DexterRuntimeException e) {
            	logger.debug(e.getMessage(), e);
            }
    	}
    	
    	COUNT.addAndGet(1);
    }
    
    public static void send() { 
    	final File resultFolder = new File(DexterConfig.getInstance().getResultPath());
    	
		for(final File file : DexterUtil.getSubFiles(resultFolder)){
			if(file.isFile()){
				sendResult(file);
			}
		}
    }

	private static void sendResult(final File resultFile) {
		final String oldResultPath = DexterConfig.getInstance().getOldResultPath();
		final String errorResultPath = DexterConfig.getInstance().getErrorResultPath();
		
		if (resultFile.isDirectory() || resultFile.exists() == false || resultFile.canRead() == false) {
			throw new DexterRuntimeException("Invalid resultFile parameter: " + resultFile.toString());
		}
		
		if (!"json".equals(Files.getFileExtension(resultFile.toString())) || resultFile.toString().indexOf("result_") == -1) {
			handleInvalidResultFile(resultFile, errorResultPath);
		}
		
		try {
			final List<String> contents = Files.readLines(resultFile, Charsets.UTF_8);
			final StringBuilder result = new StringBuilder(1024);
			for (final String content : contents) {
				result.append(content);
			}
			contents.clear();

			DexterClient.getInstance().sendAnalsysisResult(result.toString());
			Files.move(resultFile, new File(oldResultPath + "/" + resultFile.getName()));
		} catch (IOException e) {
			throw new DexterRuntimeException(e.getMessage(), e);
		} catch (Exception e){
			throw new DexterRuntimeException(e.getMessage(), e);
		}
	}

	private static void handleInvalidResultFile(final File resultFile, final String errorResultPath) {
	    try {
	    	final File errorPath = new File(errorResultPath);
	    	if(errorPath.exists() == false){
	    		if(errorPath.mkdir()){
	    			Files.move(resultFile, new File(errorResultPath + DexterUtil.FILE_SEPARATOR + resultFile.getName()));
	    		} else {
	    			logger.warn("can't make error folder for result: " + errorResultPath + " for file name: " + resultFile.toString());
	    		}
	    	} else {
	    		Files.move(resultFile, new File(errorResultPath + DexterUtil.FILE_SEPARATOR + resultFile.getName()));
	    	}
	    } catch (IOException e) {
	        logger.warn(e.getMessage(), e);
	    }
	    
	    throw new DexterRuntimeException("Invalid resultFile Name: " + resultFile.toString());
    }
}
