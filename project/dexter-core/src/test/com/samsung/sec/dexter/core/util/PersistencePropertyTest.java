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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.junit.Test;

import com.samsung.sec.dexter.core.config.DexterConfig;

public class PersistencePropertyTest {
	
	@Test
	public void test_writing() throws Exception {
		DexterConfig.getInstance().setDexterHome(".");
		
		PersistenceProperty p = PersistenceProperty.getInstance();
		
		String key = "test-key";
		String value = "test-value";
		p.write(key, value);
		
		Properties prop = new Properties();
		FileInputStream fis = new FileInputStream("./" + p.getPropertyFileName());
		prop.load(fis);
		
		assertEquals(value, prop.get(key));
		
		fis.close();
		
		deletePropertyFile(p);
	}
	
	@Test
	public void test_reading() throws Exception {
		String key = "test-key";
		String value = "test-value";
		
		Properties prop = new Properties();
		prop.put(key, value);
		
		FileOutputStream fos = new FileOutputStream("./dexter-core-config.properties");
		prop.store(fos, null);
		fos.close();
		
		DexterConfig.getInstance().setDexterHome(".");		
		PersistenceProperty p = PersistenceProperty.getInstance();
		
		assertEquals(value, p.read(key));
		
		deletePropertyFile(p);
	}

	private void deletePropertyFile(PersistenceProperty p) {
	    File propFile = new File("./" + p.getPropertyFileName());
		propFile.delete();
    }
}
