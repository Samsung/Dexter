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
package com.samsung.sec.dexter.core.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.junit.Test;

import com.samsung.sec.dexter.core.exception.DexterRuntimeException;

public class DexterConfigFileTest {

	@Test
	public void it_should_return_map_object_from_file() {
		DexterConfigFile dcf = new DexterConfigFile();
		File cfgFile = new File("./src/test/dexter_cfg.json");
		Map<String, Object> cfgMap = dcf.getConfigurationMap(cfgFile);
		
		assertNotNull(cfgMap);
		assertEquals("C:/DEV/temp/dexter-cli_2.2.13_64", (String) cfgMap.get("dexterHome"));
		assertEquals("localhost", (String) cfgMap.get("dexterServerIp"));
		assertEquals("4982", (String) cfgMap.get("dexterServerPort"));
		assertEquals("epg_ug", (String) cfgMap.get("projectName"));
		assertEquals("E:/prjs/epg/ug-program-detail", (String) cfgMap.get("projectFullPath"));
		assertEquals("UTF-8", (String) cfgMap.get("sourceEncoding"));
		assertTrue(cfgMap.get("libDir") instanceof ArrayList);
		assertTrue("".equals(cfgMap.get("binDir")));
		assertEquals("PROJECT", (String) cfgMap.get("type"));
		assertTrue(cfgMap.get("sourceDir") instanceof ArrayList);
		assertTrue(cfgMap.get("headerDir") instanceof ArrayList);
	}
	
	@Test
	public void it_should_throw_Exception_if_file_no_exist() {
		DexterConfigFile dcf = new DexterConfigFile();
		File cfgFile = new File("./not_exist_file_cfg.json");
		try {
			Map<String, Object> cfgMap = dcf.getConfigurationMap(cfgFile);
			fail();
		} catch (DexterRuntimeException e){
			
		}
	}
}
