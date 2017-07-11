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
package com.samsung.sec.dexter.metrics;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class ClassAndMethodCountCheckTest {
	/*
	@Test
	public void visitTokenTest() {
		ClassAndMethodCountCheck tester = new ClassAndMethodCountCheck();
		DetailAST parent = new DetailAST();
		DetailAST child1 = new DetailAST();
		DetailAST child2 = new DetailAST();

		// Parent is a CLASS_DEF, children are METHOD_DEF
		parent.setType(TokenTypes.CLASS_DEF);
		child1.setType(TokenTypes.METHOD_DEF);
		child1.setType(TokenTypes.METHOD_DEF);

		// Parent has two children (Class has two methods)
		parent.addChild(child1);
		parent.addChild(child2);

		// Visiting the "family" (Class and its methods)
		tester.visitToken(parent);
		tester.visitToken(child1);
		tester.visitToken(child2);
		
		//Here is the problem. I have no idea why "log()" is not even running...
		tester.finishTree(null);
		
		return;
	}
	*/
	@Test
	public void getDefaultTokensTest_ReturnsProperIntArray() {
		ClassAndMethodCountCheck tester = new ClassAndMethodCountCheck();

		int[] tokens = tester.getDefaultTokens();
		
		assertEquals(tokens[0], TokenTypes.CLASS_DEF,TokenTypes.METHOD_DEF);
	}

	@Test
	public void getRequiredTokensTest_ReturnsProperIntArray() {
		ClassAndMethodCountCheck tester = new ClassAndMethodCountCheck();

		int[] tokens = tester.getRequiredTokens();
		
		assertEquals(tokens[0], TokenTypes.CLASS_DEF,TokenTypes.METHOD_DEF);
	}
}
