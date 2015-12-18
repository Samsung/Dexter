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
package com.samsung.sec.dexter.util;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.parser.ParserLanguage;

import com.google.common.base.Charsets;

public class CppUtil {

	static Charset sourceEncoding = Charsets.UTF_8;
	static Logger logger = Logger.getLogger(CppUtil.class);

	private CppUtil() {
	}

	/**
	 * ExtractModuleName(String sourceFilePath, final int lineNumber) method is
	 * responsible for getting ModuleInformation
	 * 
	 * @param [in]
	 *            String sourceFilePath, final int lineNumber
	 * @return [out] Map<String, String>
	 * @warning [None]
	 * @exception IO
	 *                exception
	 */
	public static synchronized Map<String, String> extractModuleName(String sourceFilePath, final int lineNumber) {
		Map<String, String> mapModuleName = null;

		String code = DexterUtilHelper.getContentsFromFile(sourceFilePath, sourceEncoding);
		IASTTranslationUnit translationUnit = TranslationUnitFactory.getASTTranslationUnit(code, ParserLanguage.CPP,
				sourceFilePath);

		final String fileExtension = sourceFilePath.substring(sourceFilePath.indexOf('.'));

		ASTVisitor visitor = new ASTVisitor() {
			public int visit(IASTDeclaration declaration) {

				boolean visitStatus = DexterUtilHelper.visitFunction(declaration, lineNumber, fileExtension);

				if (visitStatus) {
					return ASTVisitor.PROCESS_ABORT;
				}

				return ASTVisitor.PROCESS_CONTINUE;

			}
		};
		visitor.shouldVisitDeclarations = true;
		translationUnit.accept(visitor);
		mapModuleName = DexterUtilHelper.getMapData();

		return mapModuleName;
	}

	/**
	 * GeneratorCodeMetrics(String sourceFilePath) method is responsible for
	 * generating code Metrices
	 * 
	 * @param [in]
	 *            String sourceFilePath
	 * @return [out] Map<String, String>
	 * @warning [None]
	 * @exception IO
	 *                exception
	 */
	public static synchronized Map<String, Object> generatorCodeMetrics(String sourceFilePath) {
		Map<String, Object> mapCodeMetrics = new HashMap<String, Object>();
		DexterUtilHelper.mapSourceMatrices.clear();
		DexterUtilHelper.count = 0;
		DexterUtilHelper.methodCount = 0;
		DexterUtilHelper.classCount = 0;

		String code = DexterUtilHelper.getContentsFromFile(sourceFilePath, sourceEncoding);
		IASTTranslationUnit translationUnit = TranslationUnitFactory.getASTTranslationUnit(code, ParserLanguage.CPP,
				sourceFilePath);

		ASTVisitor visitor = new ASTVisitor() {
			public int visit(IASTDeclaration declaration) {
				boolean visitStatus = DexterUtilHelper.visitSourceCodeforCalMethodAndClassCount(declaration);
				if (visitStatus) {
					return ASTVisitor.PROCESS_ABORT;
				}
				return ASTVisitor.PROCESS_CONTINUE;
			}
		};
		visitor.shouldVisitDeclarations = true;
		translationUnit.accept(visitor);

		ASTVisitor visitor1 = new ASTVisitor() {
			public int visit(IASTDeclaration declaration) {

				boolean visitStatus1 = DexterUtilHelper.visitSourceCodeforCalFileComplexity(declaration);

				if (visitStatus1) {
					return ASTVisitor.PROCESS_ABORT;
				}

				return ASTVisitor.PROCESS_CONTINUE;

			}
		};
		visitor1.shouldVisitDeclarations = true;
		translationUnit.accept(visitor1);

		Map<String, Integer> mapSourceMatrices = DexterUtilHelper.mapSourceMatrices;
		Collection<Integer> values = mapSourceMatrices.values();
		Object[] arrayValues = values.toArray();
		int MaxComplexity = 0;
		int MinComplexity = 0;
		int SumOfComplexity = 0;
		int AverageComplexity = 0;
		if (arrayValues.length > 0) {
			int ArrayFirstValue = (int) arrayValues[0];
			MaxComplexity = ArrayFirstValue;
			MinComplexity = ArrayFirstValue;
			SumOfComplexity = 0;

			for (Object object : arrayValues) {
				int value = (int) object;
				SumOfComplexity += value;
				if (value > MaxComplexity) {
					MaxComplexity = value;
				}
				if (value < MinComplexity) {
					MinComplexity = value;
				}

			}
			AverageComplexity = (int) SumOfComplexity / arrayValues.length;
		}

		int methodCount = DexterUtilHelper.methodCount;
		int classCount = DexterUtilHelper.classCount;
		int[] locArray = SourceCodeMatricsHelper.getSourceLOCArray(sourceFilePath);
		int SLOC = locArray[0];
		int FileLOC = locArray[1];
		int CodeCommentLOC = locArray[3];
		// int EmptyLineLOC =locArray[2];
		float commentRatio = 0.0f;
		int baseTotalLoc = CodeCommentLOC + SLOC;
		if (baseTotalLoc != 0) {
			commentRatio = CodeCommentLOC / (float) baseTotalLoc;
		}
		mapCodeMetrics.put("loc", FileLOC);
		mapCodeMetrics.put("sloc", SLOC);
		mapCodeMetrics.put("cloc", CodeCommentLOC);
		mapCodeMetrics.put("commentRatio", commentRatio);
		mapCodeMetrics.put("maxComplexity", MaxComplexity);
		mapCodeMetrics.put("minComplexity", MinComplexity);
		mapCodeMetrics.put("avgComplexity", AverageComplexity);
		mapCodeMetrics.put("classCount", classCount);
		mapCodeMetrics.put("methodCount", methodCount);

		return mapCodeMetrics;
	}
}
