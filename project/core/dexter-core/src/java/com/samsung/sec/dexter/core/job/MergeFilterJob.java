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

import org.apache.log4j.Logger;

import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.filter.AnalysisFilterHandler;
import com.samsung.sec.dexter.core.util.DexterClient;
import com.samsung.sec.dexter.core.util.IDexterClient;

public class MergeFilterJob implements Runnable {
	private final static Logger logger = Logger.getLogger(MergeFilterJob.class);
	
	private final AnalysisFilterHandler filter = AnalysisFilterHandler.getInstance();
	private static int COUNT = DexterConfig.MAX_JOB_DELAY_COUNT;
	
    @Override
    public void run() {
    	long freeMemSize = Runtime.getRuntime().freeMemory();
    	
    	if(freeMemSize > DexterConfig.ALLOWED_FREE_MEMORY_SIZE_FOR_JOBS || COUNT > DexterConfig.MAX_JOB_DELAY_COUNT){
    		COUNT = 0;

    		IDexterClient client = DexterClient.getInstance();
    		if(client.isServerAlive() == false || client.isLogin() == false){
    			return;
    		}
    		
    		if(filter.hasFilterToUpload()){
    			filter.uploadFalseAlarmFilter();
    		}

    		if(filter.hasFilterToDownload()){
    			filter.downloadFalseAlarmFilter();
    		}
    	}
    	
    	COUNT ++;
    }
}
