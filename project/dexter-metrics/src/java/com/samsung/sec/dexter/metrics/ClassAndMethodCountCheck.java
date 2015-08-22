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

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class ClassAndMethodCountCheck extends Check {
	private int classCount = 0;
	private int methodCount = 0;
	
	@Override
	public int[] getDefaultTokens() {
		return new int[]{
				TokenTypes.CLASS_DEF, TokenTypes.METHOD_DEF
			};
	}
	
	@Override
	public int[] getRequiredTokens() {
		return new int[] { TokenTypes.CLASS_DEF, TokenTypes.METHOD_DEF };
	}
	
	@Override
	public void visitToken(DetailAST aAST) {
		switch (aAST.getType()) {
		case TokenTypes.CLASS_DEF:
			classCount ++;
			break;
		case TokenTypes.METHOD_DEF:
			methodCount ++;
			break;
		default:
			break;
		}
	}
	
	@Override
	public void finishTree(DetailAST aRootAST) {
		log(1,1, "classCount:" + classCount + ";methodCount:" + methodCount);
		super.finishTree(aRootAST);
	}
}
