package com.samsung.sec.dexter.core.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class DexterServerConfigTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void equals_givenNull_returnFalse() {
		DexterServerConfig srcConfig = createTestConfig();
		
		// when
		boolean result = srcConfig.equals(null);
		
		// then
		assertEquals(false, result);
	}

	@Test
	public void equals_givenSameObject_returnTrue() {
		// given
		DexterServerConfig srcConfig = createTestConfig();
		
		// when
		boolean result = srcConfig.equals(srcConfig);
		
		// then
		assertEquals(true, result);
	}

	@Test
	public void equals_givenSameContentsObject_returnTrue() {
		// given
		DexterServerConfig srcConfig = createTestConfig();
		DexterServerConfig dstConfig = createTestConfig();
		
		// when
		boolean result = srcConfig.equals(dstConfig);
		boolean result2 = dstConfig.equals(srcConfig);
		
		// then
		assertEquals(true, result);
		assertEquals(true, result2);
	}

	@Test
	public void equals_givenSameContentsObjectWithNull_returnTrue() {
		// given
		DexterServerConfig srcConfig = createTestConfigWithNull();
		DexterServerConfig dstConfig = createTestConfigWithNull();
		
		// when
		boolean result = srcConfig.equals(dstConfig);
		boolean result2 = dstConfig.equals(srcConfig);
		
		// then
		assertEquals(true, result);
		assertEquals(true, result2);
	}

	private DexterServerConfig createTestConfig() {
		return new DexterServerConfig("testId", "testPw", "localhost", 8080);
	}
	
	private DexterServerConfig createTestConfigWithNull() {
		return new DexterServerConfig("testId", null, "localhost", 8080);
	}

}
