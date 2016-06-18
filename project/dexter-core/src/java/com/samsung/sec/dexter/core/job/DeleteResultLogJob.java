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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.util.DexterUtil;

public class DeleteResultLogJob implements Runnable {
	private final static Logger logger = Logger.getLogger(DeleteResultLogJob.class);
	private static AtomicInteger COUNT = new AtomicInteger(DexterJobFacade.MAX_JOB_DELAY_COUNT);
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
    @Override
    public void run() {
    	long freeMemSize = Runtime.getRuntime().freeMemory();
    	
    	if(freeMemSize > DexterJobFacade.ALLOWED_FREE_MEMORY_SIZE_FOR_JOBS || COUNT.get() > DexterJobFacade.MAX_JOB_DELAY_COUNT){
    		COUNT.set(0);
    		DeleteResultLogJob.deleteOldLog();
    		
    		if (DexterConfig.getInstance().isStandalone()) {
    			DeleteResultLogJob.deleteResultLog();
    		}
    	}
    	
       	COUNT.addAndGet(1);
    }

    public static void deleteResultLog(){
    	final StringBuilder path = new StringBuilder(512);
    	path.append(DexterConfig.getInstance().getDexterHome()).append("/").append(DexterConfig.RESULT_FOLDER_NAME);
    	
    	deleteLogFile(path.toString(), "json");
    }
    
    public static void deleteOldLog(){
    	final StringBuilder path = new StringBuilder(512);
    	path.append(DexterConfig.getInstance().getDexterHome()).append("/").append(DexterConfig.RESULT_FOLDER_NAME)
    		.append("/").append(DexterConfig.OLD_FOLDER_NAME);
    	
    	deleteLogFile(path.toString(), "json");
    }

	private static void deleteLogFile(final String resultOldPath, final String ext) {
		final File resultOldFolder = new File(resultOldPath);
		
		if (resultOldFolder.exists() == false && resultOldFolder.mkdir() == false) {
			return;
		}
		
		final List<File> oldResultFileList = new ArrayList<File>(150);
		
		final File[] resultOldFolders = DexterUtil.getSubFiles(resultOldFolder);
		for(File file : resultOldFolders){
			if(file.isFile() && file.getName().toLowerCase().endsWith(ext)){
				oldResultFileList.add(file);
			}
		}
		
		Collections.sort(oldResultFileList, new OrderingByDate());
		
		for(int index = DexterConfig.MAX_LOG_COUNT; index < oldResultFileList.size(); index ++){
			File file = oldResultFileList.get(index);
			if(file.delete()){
				logger.debug(file.getName() + " was deleted");
			} else {
				logger.warn("deleting " + file.getName() + " file was failed");
			}
		}
    }
    
    static class OrderingByDate implements Comparator<File> {
    	@Override
    	public int compare(final File f1, final File f2) {
    		if(f1.lastModified() > f2.lastModified()){
    			return -1;
    		} else if(f1.lastModified() < f2.lastModified()){
    			return 1;
    		} else {
    			return 0;
    		}
    	}
    }
}
