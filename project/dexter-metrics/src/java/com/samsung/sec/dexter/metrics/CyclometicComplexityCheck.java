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
import com.puppycrawl.tools.checkstyle.api.FastStack;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class CyclometicComplexityCheck extends Check {
	private static final int INITIAL_VALUE = 0;
	private final FastStack<Integer> mValueStack = FastStack.newInstance();
	private int mCurrentValue = 0;
	private int mMax = 0;
	private int mMin = 0;
	private int mAverage = 0;
	private int statementBlockCount = 0;
	private int totalConditionStatementCount = 0;

	@Override
	public int[] getDefaultTokens() {
		return new int[]{
			TokenTypes.CTOR_DEF, TokenTypes.METHOD_DEF, TokenTypes.INSTANCE_INIT,
			TokenTypes.STATIC_INIT, TokenTypes.LITERAL_WHILE, TokenTypes.LITERAL_DO,
			TokenTypes.LITERAL_FOR, TokenTypes.LITERAL_IF, TokenTypes.LITERAL_CASE,
			TokenTypes.LITERAL_CATCH, TokenTypes.QUESTION, TokenTypes.LAND, TokenTypes.LOR
		};
	}

	@Override
	public int[] getRequiredTokens() {
		return new int[] { TokenTypes.CTOR_DEF, TokenTypes.METHOD_DEF, TokenTypes.INSTANCE_INIT, TokenTypes.STATIC_INIT };
	}

	@Override
	public void visitToken(DetailAST aAST) {
		switch (aAST.getType()) {
		case TokenTypes.CTOR_DEF:
		case TokenTypes.METHOD_DEF:
		case TokenTypes.INSTANCE_INIT:
		case TokenTypes.STATIC_INIT:
			visitMethodDef();
			break;
		default:
			visitTokenHook(aAST);
		}
	}
	
	private void visitTokenHook(final DetailAST aAST) {
		mCurrentValue ++;
	}

	private void visitMethodDef() {
		mValueStack.push(mCurrentValue);
		mCurrentValue = INITIAL_VALUE;
	}

	@Override
	public void leaveToken(DetailAST aAST) {
		switch (aAST.getType()) {
		case TokenTypes.CTOR_DEF:
		case TokenTypes.METHOD_DEF:
		case TokenTypes.INSTANCE_INIT:
		case TokenTypes.STATIC_INIT:
			leaveMethodDef(aAST);
			break;
		default:
			leaveTokenHook(aAST);
		}
	}

	private void leaveTokenHook(final DetailAST aAST) {
	}

	private void leaveMethodDef(final DetailAST aAST) {
		if(mCurrentValue > mMax) mMax = mCurrentValue;
		if(mCurrentValue < mMin) mMin = mCurrentValue;
		statementBlockCount ++;
		totalConditionStatementCount += mCurrentValue;
		mCurrentValue = mValueStack.pop();
	}
	
	@Override
	public void finishTree(final DetailAST aRootAST) {
		mAverage = totalConditionStatementCount == 0 ? 0 : totalConditionStatementCount / statementBlockCount;
		log(1,1, "maxComplexity:" + mMax + ";minComplexity:" + mMin + ";averageComplexity:" + mAverage + ";stmtBlockCount:" + statementBlockCount + ";conditionStmtCount:" + totalConditionStatementCount);
		super.finishTree(aRootAST);
	}
}
