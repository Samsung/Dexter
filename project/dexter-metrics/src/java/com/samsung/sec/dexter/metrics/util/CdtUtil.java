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
package com.samsung.sec.dexter.metrics.util;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.parser.ParserLanguage;

import com.google.common.base.Charsets;
import com.samsung.sec.dexter.util.TranslationUnitFactory;

public class CdtUtil {
	static Charset sourceEncoding = Charsets.UTF_8;
	static Logger logger = Logger.getLogger(CdtUtil.class);

	/**
	 * ExtractModuleName(String sourceFilePath, final int lineNumber) method is
	 * responsible for getting ModuleInformation
	 * 
	 * @param [in] String sourceFilePath, final int lineNumber
	 * @return [out] Map<String, String>
	 * @warning [None]
	 * @exception IO
	 *                exception
	 */
	public static synchronized Map<String, String> extractModuleName( String sourceFilePath, final int lineNumber) {
		Map<String, String> mapModuleName = null;

		String code = CdtUtilHelper.getContentsFromFile(sourceFilePath, sourceEncoding);
		IASTTranslationUnit translationUnit = TranslationUnitFactory.getASTTranslationUnit(code, ParserLanguage.CPP, 
				sourceFilePath);

		final String fileExtension = sourceFilePath.substring(sourceFilePath.indexOf('.'));

		ASTVisitor visitor = new ASTVisitor() {
			public int visit(IASTDeclaration declaration) {

				boolean visitStatus = CdtUtilHelper.visitFunction(declaration, lineNumber, fileExtension);

				if (visitStatus) {
					return ASTVisitor.PROCESS_ABORT;
				}

				return ASTVisitor.PROCESS_CONTINUE;

			}
		};
		visitor.shouldVisitDeclarations = true;
		translationUnit.accept(visitor);
		mapModuleName = CdtUtilHelper.getMapData();

		return mapModuleName;
	}

	/**
	 * GeneratorCodeMetrics(String sourceFilePath) method is responsible for
	 * generating code Metrices
	 * 
	 * @param [in] String sourceFilePath
	 * @return [out] Map<String, String>
	 * @warning [None]
	 * @exception IO
	 *                exception
	 */
	public static synchronized List<Map<String, Object>> generatorCodeMetrics(
			final String sourceFilePath, List<String> functionList) {
		
		CdtUtilHelper.mapSourceMatrices.clear();
		CdtUtilHelper.mapFunctionLocMetrices.clear();
		CdtUtilHelper.count = 0;
		CdtUtilHelper.methodCount = 0;
		CdtUtilHelper.classCount = 0;

		String code = CdtUtilHelper.getContentsFromFile(sourceFilePath, sourceEncoding);
		IASTTranslationUnit translationUnit = TranslationUnitFactory
				.getASTTranslationUnit(code, ParserLanguage.CPP, sourceFilePath);

		ASTVisitor visitor = new ASTVisitor() {
			public int visit(IASTDeclaration declaration) {
				boolean visitStatus = CdtUtilHelper.visitSourceCodeforCalMethodAndClassCount(declaration);
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

				boolean visitStatus1 = CdtUtilHelper.visitSourceCodeforCalFileComplexity(declaration);
				if (visitStatus1) {
					return ASTVisitor.PROCESS_ABORT;
				}
				return ASTVisitor.PROCESS_CONTINUE;
			}
		};
		
		visitor1.shouldVisitDeclarations = true;
		translationUnit.accept(visitor1);

		ASTVisitor visitor2 = new ASTVisitor() {
			public int visit(IASTDeclaration declaration) {
				boolean visitStatus2 = CdtUtilHelper.visitSourceCodeforSloc(declaration, sourceFilePath);
				if (visitStatus2) {
					return ASTVisitor.PROCESS_ABORT;
				}
				return ASTVisitor.PROCESS_CONTINUE;
			}
		};
		
		visitor2.shouldVisitDeclarations = true;
		translationUnit.accept(visitor2);
		
		final Map<String, Integer> mapSourceMatrices = CdtUtilHelper.mapSourceMatrices;
		final Map<String, Integer> mapLocSourceMetrices = CdtUtilHelper.mapFunctionLocMetrices;

		final List<Map<String, Object>> functionMetricsMap = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < functionList.size(); i++) {
			if (mapSourceMatrices.containsKey(functionList.get(i))) {
				Map<String, Object> mapFunctionMetrics = new HashMap<String, Object>();
				mapFunctionMetrics.put("functionName", functionList.get(i));
				mapFunctionMetrics.put("cc", mapSourceMatrices.get(functionList.get(i)));
				mapFunctionMetrics.put("sloc", mapLocSourceMetrices.get(functionList.get(i)));
				mapFunctionMetrics.put("callDepth", 0);

				functionMetricsMap.add(mapFunctionMetrics);
			}
		}
		return functionMetricsMap;
	}
}
