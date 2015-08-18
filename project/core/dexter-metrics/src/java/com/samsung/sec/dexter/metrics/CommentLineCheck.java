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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.TextBlock;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.AbstractTypeAwareCheck;

public class CommentLineCheck extends AbstractTypeAwareCheck {
	private int cloc = 0;
	private int commentCount = 0;
	private int loc = 0;
	private int sloc = 0;

	@Override
	protected void logLoadError(Token arg0) {
	}

	@Override
	protected void processAST(DetailAST ast) {
	}

	@Override
	public int[] getDefaultTokens() {
		return new int[] { TokenTypes.PACKAGE_DEF, TokenTypes.IMPORT, TokenTypes.CLASS_DEF, TokenTypes.ENUM,
				TokenTypes.METHOD_DEF, TokenTypes.CTOR_CALL, TokenTypes.ANNOTATION_FIELD_DEF };
	}

	@Override
	public void beginTree(final DetailAST aRootAST) {
		final FileContents contents = getFileContents();

		calculateCommentLine(contents.getCComments());
		calculateCppCommentLine(contents.getCppComments());
		
		loc = getLines().length;
		
		for(final String s : getLines()){
			for(byte b : s.getBytes()){
				if(';' == b){
					sloc++;
				}
			}
		}

		super.beginTree(aRootAST);
	}

	private void calculateCommentLine(final Map<Integer, List<TextBlock>> map) {
		if (map == null) return;
		final Iterator<Integer> i = map.keySet().iterator();
		if(i == null) return;
		while (i.hasNext()) {
			final List<TextBlock> cmtList = (List<TextBlock>) map.get(i.next());
			if(cmtList == null || cmtList.size() == 0) continue;
			for (final TextBlock cmt : cmtList) {
				countCommentLine(cmt);
			}
		}
	}

	private void calculateCppCommentLine(final Map<Integer, TextBlock> map) {
		if (map == null) return;
		final Iterator<Integer> i = map.keySet().iterator();
		while (i.hasNext()) {
			TextBlock cmt = map.get(i.next());
			countCommentLine(cmt);
		}
	}

	private void countCommentLine(final TextBlock cmt) {
		if(cmt == null) return;
		
		commentCount ++;
		cloc += cmt.getText() != null ? cmt.getText().length : 0;
	}

	@Override
	public void finishTree(final DetailAST aRootAST) {
		float commentRatio = loc == 0 ? 0.0f : (float) cloc / (float) loc;
		log(1, 1, "commentCount:" + commentCount + ";cloc:" + cloc + ";loc:" + loc 
				+ ";sloc:" + sloc + ";commentRatio:" + commentRatio);
		super.finishTree(aRootAST);
	}

	@Override
	public void destroy() {
		super.destroy();
	}

}
