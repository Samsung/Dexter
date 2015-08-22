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
package com.samsung.sec.dexter.core.checker;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CheckerTest {

	@Test
	public void description_field_should_replace_keys_with_properties_type1() {
		Checker checker = new Checker("test-code", "test-name", "1.1.1", true);
		
		// property first
		checker.addProperty("key1", "key1-value");
		checker.addProperty("key2", "key2-value");
		checker.setDescription("test-description: aa ${key1} bb ${key2} cc ${key2} dd $key1 ee $key2");
		
		assertEquals("test-description: aa key1-value bb key2-value cc key2-value dd $key1 ee $key2",
				checker.getDescription());
	}
	
	@Test
	public void description_field_should_replace_keys_with_properties_type2() {
		Checker checker = new Checker("test-code", "test-name", "1.1.1", true);
		
		// description first
		checker.setDescription("test-description: aa ${key1} bb ${key2} cc ${key2} dd $key1 ee $key2");
		checker.addProperty("key1", "key1-value");
		checker.addProperty("key2", "key2-value");
		
		assertEquals("test-description: aa key1-value bb key2-value cc key2-value dd $key1 ee $key2",
				checker.getDescription());
	}
}
