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

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;

public class CommentLineCheckTest {
	
	@Test
	public void getDefaultTokensTest_ReturnsProperIntArrayWithTokens() {
		CommentLineCheck tester = new CommentLineCheck();
		int[] defaultTokens;
		defaultTokens = tester.getDefaultTokens();
		
		assertEquals(defaultTokens[0], TokenTypes.PACKAGE_DEF);
		assertEquals(defaultTokens[1], TokenTypes.IMPORT);
		assertEquals(defaultTokens[2], TokenTypes.CLASS_DEF);
		assertEquals(defaultTokens[3], TokenTypes.ENUM);
		assertEquals(defaultTokens[4], TokenTypes.METHOD_DEF);
		assertEquals(defaultTokens[5], TokenTypes.CTOR_CALL);
		assertEquals(defaultTokens[6], TokenTypes.ANNOTATION_FIELD_DEF);
	}
}
