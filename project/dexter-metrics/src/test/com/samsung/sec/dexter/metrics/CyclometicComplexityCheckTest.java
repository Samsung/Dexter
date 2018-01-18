/**
 * Copyright (c) 2017 Samsung Electronics, Inc.,
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
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
package com.samsung.sec.dexter.metrics;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class CyclometicComplexityCheckTest {

	@Test
	public void getRequiredTokensTest() {
		CyclometicComplexityCheck tester = new CyclometicComplexityCheck();
		int[] requiredTokens;
		requiredTokens = tester.getRequiredTokens();

		assertEquals(requiredTokens[0], TokenTypes.CTOR_DEF);
		assertEquals(requiredTokens[1], TokenTypes.METHOD_DEF);
		assertEquals(requiredTokens[2], TokenTypes.INSTANCE_INIT);
		assertEquals(requiredTokens[3], TokenTypes.STATIC_INIT);
	}

	@Test
	public void getDefaultTokensTest() {
		CyclometicComplexityCheck tester = new CyclometicComplexityCheck();
		int[] defaultTokens;
		defaultTokens = tester.getDefaultTokens();

		assertEquals(defaultTokens[0], TokenTypes.CTOR_DEF);
		assertEquals(defaultTokens[1], TokenTypes.METHOD_DEF);
		assertEquals(defaultTokens[2], TokenTypes.INSTANCE_INIT);
		assertEquals(defaultTokens[3], TokenTypes.STATIC_INIT);
		assertEquals(defaultTokens[4], TokenTypes.LITERAL_WHILE);
		assertEquals(defaultTokens[5], TokenTypes.LITERAL_DO);
		assertEquals(defaultTokens[6], TokenTypes.LITERAL_FOR);
		assertEquals(defaultTokens[7], TokenTypes.LITERAL_IF);
		assertEquals(defaultTokens[8], TokenTypes.LITERAL_CASE);
		assertEquals(defaultTokens[9], TokenTypes.LITERAL_CATCH);
		assertEquals(defaultTokens[10], TokenTypes.QUESTION);
		assertEquals(defaultTokens[11], TokenTypes.LAND);
		assertEquals(defaultTokens[12], TokenTypes.LOR);
	}
}
