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
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBasicType;
import org.eclipse.cdt.core.dom.ast.IBasicType.Kind;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IFunctionType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.internal.core.dom.parser.c.CFunction;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.checker.Checker;
import com.samsung.sec.dexter.core.defect.PreOccurence;
import com.samsung.sec.dexter.vdcpp.plugin.DexterVdCppPlugin;
import com.samsung.sec.dexter.vdcpp.util.CppUtil;


public class FunctionReturnValueCheckerLogic implements ICheckerLogic{


	private IASTTranslationUnit translationUnit;		


	@Override
	public void analyze(final AnalysisConfig config, final AnalysisResult result, 
			final Checker checker, IASTTranslationUnit unit) {
		translationUnit =unit;	
		ASTVisitor visitor = createVisitor(config, result, checker);
		visitor.shouldVisitDeclarators = true;		
		unit.accept(visitor);		
	}

	private ASTVisitor createVisitor(final AnalysisConfig config,
			final AnalysisResult result, final Checker checker) {
		ASTVisitor visitor = new ASTVisitor() {
			@Override
			public int visit(IASTDeclarator ast ) {
				//int lineNo =ast.getFileLocation().getStartingLineNumber();
				if (ast instanceof IASTFunctionDeclarator )
				{									
					visitFunctionDeclarator(config, result, checker, ast);
				}
				else if(ast instanceof IASTFunctionDefinition)
				{
					visitFunctionDefinition(config, result, checker, ast); 
				}

				return super.visit(ast);
			}


		};

		return visitor;

	}


	private void visitFunctionDefinition(AnalysisConfig config,
			AnalysisResult result, Checker checker, IASTDeclarator ast) {
		// TODO Auto-generated method stub
	}

	private void visitFunctionDeclarator(final AnalysisConfig config,
			final AnalysisResult result, final Checker checker,
			IASTDeclarator ast) {
		final IASTDeclarator CFuncDecl = (IASTDeclarator) ast;
		final String CFPStringRaw = CFuncDecl.getParent().getRawSignature();

		IASTName CStrFuncDecl = CFuncDecl.getName();
		if(CStrFuncDecl !=null)
		{
			final IBinding CStrFuncDeclFbinding = CStrFuncDecl.resolveBinding();
			if ((CStrFuncDeclFbinding != null) && (CStrFuncDeclFbinding instanceof CFunction))
			{
				CStrFuncDecl = CStrFuncDecl.getLastName();
				final int IndexOfCStrFuncDecl = CFPStringRaw.indexOf(CStrFuncDecl.toString());
				boolean violation = false;
				for (int k = 0; k < IndexOfCStrFuncDecl; k++)
				{
					if ((CFPStringRaw.charAt(k) == '\r') || (CFPStringRaw.charAt(k) == '\n') || 
							(CFPStringRaw.charAt(k) == '\t') || (CFPStringRaw.charAt(k) == ' '))
					{
						violation = true;
					} else
					{
						violation = false;
						break;
					}
				}
				if ((CFPStringRaw.indexOf(CStrFuncDecl.toString()) == 0) || (violation))
				{

					final CFunction CFunc = (CFunction) CStrFuncDeclFbinding;
					final IFunctionType CFtype = CFunc.getType();

					final IType CFRtype = CFtype.getReturnType();
					if(CFRtype instanceof IBasicType)
					{
						final IBasicType ReturnType = (IBasicType) CFRtype;
						final Kind number = ReturnType.getKind();
						if (!number.toString().equals("void"))
						{
							fillDefectData( config, result,checker,CStrFuncDecl.getFileLocation(),checker.getDescription(),CStrFuncDecl.toString());

						}
					}

				}
			}
		}
	}



	private void fillDefectData(AnalysisConfig config,
			AnalysisResult result, Checker checker,
			IASTFileLocation fileLocation, String message, String declaratorName) {
		PreOccurence preOcc = createPreOccurence(config, checker, fileLocation, message,declaratorName);
		result.addDefectWithPreOccurence(preOcc);

	}
	private PreOccurence createPreOccurence(AnalysisConfig config,
			Checker checker, IASTFileLocation fileLocation, String msg,String declaratorName) {
		final int startLine = fileLocation.getStartingLineNumber();
		final int endLine = fileLocation.getEndingLineNumber();
		final int startOffset = fileLocation.getNodeOffset();
		final int endOffset = startOffset + fileLocation.getNodeLength();

		Map<String,String> tempmap =CppUtil.extractModuleName(translationUnit, startLine);
		String className =tempmap.get("className");
		String methodName =tempmap.get("methodName");

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
		preOcc.setVariableName(declaratorName);
		preOcc.setStringValue(msg);
		preOcc.setMessage(msg);

		return preOcc;

	}


}
