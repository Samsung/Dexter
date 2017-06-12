package com.samsung.sec.dexter.core.util;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.samsung.sec.dexter.core.exception.DexterRuntimeException;

public class DexterUtilTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test(expected = DexterRuntimeException.class)
	public void checkNullOrEmptyOfMap_throwExceptionIFNullMap() {
		// when
		DexterUtil.checkNullOrEmptyOfMap(null);
	}
	
	@Test(expected = DexterRuntimeException.class)
	public void checkNullOrEmptyOfMap_throwExceptionIFEmptyMap() {
		// when
		DexterUtil.checkNullOrEmptyOfMap(null);
	}
	
	@Test
	public void checkNullOrEmptyOfMap_doNotThrowExceptionIFNonEmptyMap() {
		// given
		Map<String, Object> testMap = new HashMap<String, Object>();
		testMap.put("test", "test");
		
		try {
			// when
			DexterUtil.checkNullOrEmptyOfMap(testMap);
		} catch (Exception e) {
			fail();
		}
	}

}
