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
package com.samsung.sec.dexter.vdcpp.checkerlogic;

import java.util.Map;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclaration;

import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.checker.Checker;
import com.samsung.sec.dexter.core.defect.PreOccurence;
import com.samsung.sec.dexter.vdcpp.plugin.DexterVdCppPlugin;
import com.samsung.sec.dexter.vdcpp.util.CppUtil;

public class ConstNamingCheckerLogic implements ICheckerLogic {
	private IASTTranslationUnit unit;
	@Override
	public void analyze(final AnalysisConfig config, final AnalysisResult result, final Checker checker,
	        final IASTTranslationUnit unit) {
		this.unit = unit;
		
		ASTVisitor visitor = createVisitor(config, result, checker);
		visitor.shouldVisitDeclarators = true;
		unit.accept(visitor);
	}

	private ASTVisitor createVisitor(final AnalysisConfig config, final AnalysisResult result, final Checker checker) {
		ASTVisitor visitor = new ASTVisitor() {
			@Override
			public int visit(IASTDeclarator declarator) {
				Object object = declarator.getParent();
				
				if (object instanceof CPPASTSimpleDeclaration) {
					CPPASTSimpleDeclaration parent = (CPPASTSimpleDeclaration) object;

					if (parent.getDeclSpecifier().isConst()) {
						addDefect(config, result, checker, declarator);
					}
				}

				return super.visit(declarator);
			}

			private void addDefect(final AnalysisConfig config, final AnalysisResult result, final Checker checker,
			        IASTDeclarator declarator) {
				String declaratorName = declarator.getName().toString();
				String regExp = checker.getProperty("RegExp");

				if (declaratorName.matches(regExp))
					return;

				PreOccurence preOcc = createPreOccurence(config, checker, declarator, declaratorName);
				result.addDefectWithPreOccurence(preOcc);
			}

			private PreOccurence createPreOccurence(final AnalysisConfig config, final Checker checker,
			        IASTDeclarator declarator, String declaratorName) {
				IASTFileLocation fileLocation = declarator.getFileLocation();
				final int startLine = fileLocation.getStartingLineNumber();
				final int endLine = fileLocation.getEndingLineNumber();
				final int startOffset = fileLocation.getNodeOffset();
				final int endOffset = startOffset + fileLocation.getNodeLength();

				Map<String, String> map = CppUtil.extractModuleName(unit, startLine); 
				final String className = map.get("className");
				final String methodName = map.get("methodName");

				PreOccurence preOcc = new PreOccurence();
				preOcc.setCheckerCode(checker.getCode());
				preOcc.setFileName(config.getFileName());
				preOcc.setModulePath(config.getModulePath());
				preOcc.setClassName(className);
				preOcc.setMethodName(methodName);
				preOcc.setLanguage(config.getLanguageEnum().toString());
				preOcc.setSeverityCode(checker.getSeverityCode());
				preOcc.setMessage(checker.getDescription());
				preOcc.setToolName(DexterVdCppPlugin.PLUGIN_NAME);

				preOcc.setStartLine(startLine);
				preOcc.setEndLine(endLine);
				preOcc.setCharStart(startOffset);
				preOcc.setCharEnd(endOffset);
				preOcc.setStringValue(declaratorName);
				preOcc.setMessage("const variable can be Upper Alphabet, underline, or number. your input is "
				        + declaratorName);

				return preOcc;
			}
		};
		
		return visitor;
	}
}
