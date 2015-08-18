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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.IDexterHomeListener;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;

public class PersistenceProperty implements IDexterHomeListener{
	final private static Logger LOG = Logger.getLogger(PersistenceProperty.class);
	final private String propertyFileName = "dexter-core-config.properties";
	private String propertyFilePath;
	private Properties properties = new Properties();
	
	private PersistenceProperty(){
		DexterConfig.getInstance().addDexterHomeListener(this);
		LOG.debug("PersistenceProperty");
	}
	
	private static class LazyHolder {
		private static final PersistenceProperty INSTANCE = new PersistenceProperty();
	}

	public static PersistenceProperty getInstance() {
		return LazyHolder.INSTANCE;
	}
	
	private void initPropertyFilePath(){
		final String dexterHome = DexterConfig.getInstance().getDexterHome();
		
		this.propertyFilePath = Strings.isNullOrEmpty(dexterHome) ? "./" + this.propertyFileName 
				: dexterHome + "/" + this.propertyFileName;
	}
	
	public String getPropertyFileName() {
	    return this.propertyFileName;
    }
	
	private void loadProperties(){
		FileInputStream fis = null;
		
		try {
	        fis = new FileInputStream(this.propertyFilePath);
	        properties.load(fis);
	        
        } catch (FileNotFoundException e) {
	        throw new DexterRuntimeException(e.getMessage(), e);
        } catch (IOException e) {
        	throw new DexterRuntimeException(e.getMessage(), e);
        } finally {
        	DexterUtil.closeInputStream(fis);
        }
	}
	
	private void writeProperties(){
		OutputStream output = null;
		
		try {
			if(Strings.isNullOrEmpty(this.propertyFilePath)) initPropertyFilePath();
			
	        output = new FileOutputStream(this.propertyFilePath);
	        properties.store(output, DexterUtil.currentDateTime());
        } catch (FileNotFoundException e) {
        	throw new DexterRuntimeException(e.getMessage(), e);
        } catch (IOException e) {
        	throw new DexterRuntimeException(e.getMessage(), e);
        } finally {
        	DexterUtil.closeOutputStream(output);
        }
	}

	@Override
    public void handleDexterHomeChanged() {
		initProperty();
    }

	private void initProperty() {
		boolean isFirstLoading = Strings.isNullOrEmpty(propertyFilePath);
		
		initPropertyFilePath();
		DexterUtil.createEmptyFileIfNotExist(propertyFilePath);
		
		if(isFirstLoading){
			loadProperties();
		} else {
			writeProperties();
		}
    }

	public void write(String key, String value) {
		properties.put(key, value);
		writeProperties();
    }
	
	public String read(String key){
		try{
			return (String) properties.get(key);
		} catch (Exception e){
			LOG.error(e.getMessage(), e);
			return "";
		}
	}
}
