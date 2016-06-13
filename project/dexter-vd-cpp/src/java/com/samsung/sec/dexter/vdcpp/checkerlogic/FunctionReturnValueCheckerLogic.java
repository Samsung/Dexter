/**
 *  @file   FunctionReturnValue.java
 *  @brief  FunctionReturnValue class source file
 *  @author adarsh.t
 *
 * Copyright 2015 by Samsung Electronics, Inc.
 * All rights reserved.
 * 
 * Project Description :
 * This software is the confidential and proprietary information
 * of Samsung Electronics, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Samsung Electronics.
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
