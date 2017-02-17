package com.samsung.sec.dexter.core.config;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.samsung.sec.dexter.core.analyzer.ResultFileConstant;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.FileService;

public class PeerReviewConfigFileTest {
	PeerReviewConfigFile configFile;
	FileService fileService;
	
	@Before
	public void setUp() throws Exception {
		fileService = mock(FileService.class);
		configFile = new PeerReviewConfigFile(fileService);
	}

	@Test
	public void checkDexterConfigMap_doNotCheckProjectFullPath() {
		try {
			// given
			Map<String, Object> map = createTestConfigMap();
			
			// when
			configFile.checkDexterConfigMap(map);
		} catch (DexterRuntimeException e) {
			// then
			assert(false);
		}
	}

	private Map<String, Object> createTestConfigMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("projectFullPath", "test");
		map.put(ResultFileConstant.PROJECT_NAME, "test");
		map.put("sourceEncoding", "test");
		map.put("type", "test");
		map.put("dexterHome", "test");
		return map;
	}

	@Test
	public void setFields_doNotOccurException() {
		try {
			// when 
			when(fileService.exists(anyString())).thenReturn(true);
			
			// given
			Map<String, Object> map = createTestConfigMap();
			
			// when
			configFile.setFields(map);
		} catch (Exception e) {
			assert(false);
		}
	}
}
