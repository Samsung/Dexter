/**
 *  @file   CheckFreeStatementForThirdParam.java
 *  @brief  CheckFreeStatementForThirdParam class source file
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IBinding;

import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.checker.Checker;
import com.samsung.sec.dexter.core.defect.PreOccurence;
import com.samsung.sec.dexter.vdcpp.plugin.DexterVdCppPlugin;
import com.samsung.sec.dexter.vdcpp.util.CppUtil;


public class FreeStatementForThirdParamCheckerLogic implements ICheckerLogic
{
	private IASTTranslationUnit translationUnit;	
	private String[] lstMethods=null;
	@Override
	public void analyze(final AnalysisConfig config, final AnalysisResult result, 
			final Checker checker, IASTTranslationUnit unit) {
		translationUnit =unit;
		lstMethods= checker.getProperty("method-list").split(",");
		ASTVisitor visitor = createVisitor(config, result, checker);
		visitor.shouldVisitDeclarations = true;
		unit.accept(visitor);
	}

	private ASTVisitor createVisitor(final AnalysisConfig config,
			final AnalysisResult result, final Checker checker) {
		ASTVisitor visitor = new ASTVisitor() {
			@Override
			public int visit(IASTDeclaration ast ) {
				if(ast instanceof IASTFunctionDefinition)
				{									
					visitFunction(config, result, checker, ast);
				}		
				else if(ast instanceof IASTSimpleDeclaration)
				{												
					visitOtherCompoundDeclaration(config, result, checker, ast);				
				}
				return super.visit(ast);
			}

			private void visitOtherCompoundDeclaration(
					final AnalysisConfig config, final AnalysisResult result,
					final Checker checker, final IASTDeclaration ast) {
				ASTVisitor visitor = new ASTVisitor() {
					public int visit(IASTExpression astExpression ) {							

						if(astExpression instanceof IASTFunctionCallExpression)
						{				
							visitFunctionCallExpressionForCompoundBlock(config,
									result, checker, ast, astExpression);
						}
						return ASTVisitor.PROCESS_CONTINUE;
					}

					private void visitFunctionCallExpressionForCompoundBlock(
							final AnalysisConfig config,
							final AnalysisResult result, final Checker checker,
							final IASTDeclaration ast,
							IASTExpression astExpression) {
						IASTExpression functExpression =   ((IASTFunctionCallExpression) astExpression).getFunctionNameExpression();	
						String functionName =functExpression.getRawSignature();

						if(functExpression instanceof IASTIdExpression)
						{
							functionName =((IASTIdExpression) functExpression).getName().toString();
						}

						for (String  methodName : lstMethods)
						{					
							if(functionName.equals(methodName))
							{								
							   IASTInitializerClause[] params =	((IASTFunctionCallExpression) astExpression).getArguments();
							   if(params.length >2 && (params[2] instanceof IASTUnaryExpression))
								{
									IASTExpression  unaryExpression = ((IASTUnaryExpression)params[2]).getOperand();
									if(unaryExpression instanceof IASTExpression)
									{
										visitUnaryExpressionForCompoundBlock(
												config, result, checker,
												ast, astExpression,
												functionName, methodName,
												unaryExpression);
										
									}
								}
								
																	
							}
						}
					}

					private void visitUnaryExpressionForCompoundBlock(
							final AnalysisConfig config,
							final AnalysisResult result, final Checker checker,
							final IASTDeclaration ast,
							IASTExpression astExpression, String functionName,
							String methodName, IASTExpression unaryExpression) {
						if(!(unaryExpression instanceof IASTIdExpression))		return;
						
						IASTName  name =((IASTIdExpression) unaryExpression).getName();
						String ExpName =name.toString();
						
						final IBinding binding = name.resolveBinding();
						if ((binding != null) )
						{
							 boolean status =false;
							 if(methodName.contains("array"))
							 {
								 status= checkforFreeFunctionCallForArray(
											ast, ExpName, binding);
							 }
							 else
							 {
								 status=	checkforFreeFunctionCall(
									ast, ExpName, binding);
							 }
							
						    if(!status)
						    {
						    	fillDefectData( config,
										result,  checker,
										astExpression.getFileLocation(),  checker.getDescription(), functionName);
						    }
						}
					}

				};
				visitor.shouldVisitExpressions = true; 					
				ast.accept(visitor);
			}

			private void visitFunction(final AnalysisConfig config,
					final AnalysisResult result, final Checker checker,
					final IASTDeclaration ast) {
				ASTVisitor visitor = new ASTVisitor() {
					public int visit(IASTExpression astExpression ) {							

						if(astExpression instanceof IASTFunctionCallExpression)
						{				
							visitFunctionCallExpression(config, result,
									checker, ast, astExpression);

						}
						return ASTVisitor.PROCESS_CONTINUE;
					}

					private void visitFunctionCallExpression(
							final AnalysisConfig config,
							final AnalysisResult result, final Checker checker,
							final IASTDeclaration ast,
							IASTExpression astExpression) {
						IASTExpression functCallExpression =   ((IASTFunctionCallExpression) astExpression).getFunctionNameExpression();	
						String functionName =functCallExpression.getRawSignature();

						if(functCallExpression instanceof IASTIdExpression)
						{
							functionName =((IASTIdExpression) functCallExpression).getName().toString();
						}

						for (String  methodName : lstMethods)
						{					
							if(functionName.equals(methodName))
							{								
							   IASTInitializerClause[] params =	((IASTFunctionCallExpression) astExpression).getArguments();
								if(params.length >2 && (params[2] instanceof IASTUnaryExpression))
								{
									visitUnaryExpression(config, result,
											checker, ast, astExpression,
											functionName, methodName,
											params);
								}
								
																	
							}
						}
					}

					private void visitUnaryExpression(
							final AnalysisConfig config,
							final AnalysisResult result, final Checker checker,
							final IASTDeclaration ast,
							IASTExpression astExpression, String functionName,
							String methodName, IASTInitializerClause[] params) {
						IASTExpression  unaryExpression = ((IASTUnaryExpression)params[2]).getOperand();
						if(unaryExpression instanceof IASTIdExpression)
						{
							IASTName  name =((IASTIdExpression) unaryExpression).getName();
							String ExpName =name.toString();
							
							final IBinding binding = name.resolveBinding();
							if ((binding != null) )
							{
								
								 boolean status =false;
								 if(methodName.contains("array"))
								 {
									 status= checkforFreeFunctionCallForArray(
												ast, ExpName, binding);
								 }
								 else
								 {
									 status=	checkforFreeFunctionCall(
										ast, ExpName, binding);
								 }
								
							    if(!status)
							    {
							    	fillDefectData( config,
											result,  checker,
											astExpression.getFileLocation(),  checker.getDescription(), functionName);
							    }
							}
							
						}
					}

				};
				visitor.shouldVisitExpressions = true; 					
				ast.accept(visitor);
			}
			
			private boolean checkforFreeFunctionCallForArray(
					IASTDeclaration ast, String expName,
					IBinding binding) {
				boolean status =false;
				final IASTName[] references = ast.getTranslationUnit().getReferences(binding);	
                boolean IsFreeArrayInternalObjects =false;
                boolean IsFreeArrayObjects =false;
				for (IASTName reference : references)
				{					
					IASTNode  parent =reference.getParent().getParent();
					if(parent instanceof IASTArraySubscriptExpression)
					{
						IsFreeArrayInternalObjects = visitArraySubscriptionExpression(
								expName, IsFreeArrayInternalObjects, parent);						
						
					}
					
					if(parent instanceof IASTFunctionCallExpression)
					{
						IsFreeArrayObjects = visitFunctionCallExpressionForArrayObjects(
								expName, IsFreeArrayObjects, parent);
					}
				}
				if(IsFreeArrayInternalObjects && IsFreeArrayObjects)
				{
					status= true;
				}
				return status;
			}

			private boolean visitFunctionCallExpressionForArrayObjects(
					String expName, boolean IsFreeArrayObjects, IASTNode parent) {
				IASTExpression funcNameExpression =   ((IASTFunctionCallExpression) parent).getFunctionNameExpression();
				IASTInitializerClause[] expParameter =((IASTFunctionCallExpression) parent).getArguments();
				
				List<String> parameter =new ArrayList<String>();
				for (IASTInitializerClause string : expParameter) {
					parameter.add(string.toString());
				}				

				if(funcNameExpression instanceof IASTIdExpression)
				{
					String functionName =((IASTIdExpression) funcNameExpression).getName().toString();
					if(functionName.equals("free") && parameter.contains(expName))
					{
						IsFreeArrayObjects =true;
					}
				}
				return IsFreeArrayObjects;
			}

			private boolean visitArraySubscriptionExpression(String expName,
					boolean IsFreeArrayInternalObjects, IASTNode node) {
				IASTExpression arrayExpression=  ((IASTArraySubscriptExpression) node).getArrayExpression();					
				
				IASTNode  parentNode =node.getParent();
				if(parentNode instanceof IASTFunctionCallExpression)
				{
					IASTExpression functionNameExpression =   ((IASTFunctionCallExpression) parentNode).getFunctionNameExpression();							
				
					if((functionNameExpression instanceof IASTIdExpression)  && (arrayExpression instanceof IASTIdExpression))
					{
						String functionName =((IASTIdExpression) functionNameExpression).getName().toString();
						String functArgument =arrayExpression.toString();
						if(functionName.equals("free") && functArgument.contains(expName))
						{
							IsFreeArrayInternalObjects =true;
							
						}
					}
					
				}
				
				return IsFreeArrayInternalObjects;
			}
			
			private boolean checkforFreeFunctionCall(
					final IASTDeclaration ast, String ExpName,
					final IBinding binding) {
				boolean status =false;
				final IASTName[] references = ast.getTranslationUnit().getReferences(binding);	

				for (IASTName reference : references)
				{
					
					IASTNode  parent =reference.getParent().getParent();
					if(parent instanceof IASTFunctionCallExpression)
					{
						status = visitFunctionCallExpressionForArrayObjects(
								ExpName, status, parent);
					}
				}
				return status;
			}		
			private void fillDefectData(AnalysisConfig config,
					AnalysisResult result, Checker checker,
					IASTFileLocation fileLocation, String message, String declaratorName) {

				PreOccurence preOcc = createPreOccurence(config, checker, fileLocation, message,declaratorName);
				result.addDefectWithPreOccurence(preOcc);
			}

			private PreOccurence createPreOccurence(AnalysisConfig config,
					Checker checker, IASTFileLocation fileLocation, String msg,String decName) {
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
				preOcc.setVariableName(decName);				
				msg =msg.replace("${methodName}", decName);
				preOcc.setMessage(msg);
				preOcc.setStringValue(msg);

				return preOcc;

			}
		};

		return visitor;
	}


}
