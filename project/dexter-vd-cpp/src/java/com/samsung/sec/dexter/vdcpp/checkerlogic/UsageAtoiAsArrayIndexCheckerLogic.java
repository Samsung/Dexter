/**
 *  @file   CheckUsageAtoiAsArrayIndex.java
 *  @brief  CheckUsageAtoiAsArrayIndex class source file
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
import org.eclipse.cdt.core.dom.ast.IASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTEqualsInitializer;

import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.checker.IChecker;
import com.samsung.sec.dexter.core.defect.PreOccurence;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.vdcpp.plugin.DexterVdCppPlugin;
import com.samsung.sec.dexter.vdcpp.util.CppUtil;


public class UsageAtoiAsArrayIndexCheckerLogic implements ICheckerLogic{
	
	private IASTTranslationUnit translationUnit;

	@Override
	public void analyze(final AnalysisConfig config, final AnalysisResult result, 
			final IChecker checker, IASTTranslationUnit unit) {
		translationUnit =unit;		
		ASTVisitor visitor = createVisitor(config, result, checker);
		visitor.shouldVisitDeclarations = true;
		unit.accept(visitor);
	}

	private ASTVisitor createVisitor(final AnalysisConfig config,
			final AnalysisResult result, final IChecker checker) {
		ASTVisitor visitor = new ASTVisitor() {
			@Override
			public int visit(IASTDeclaration ast ) {

				if(ast instanceof IASTFunctionDefinition)
				{									
					visitFunction(config, result, checker, ast);
				}		

				return super.visit(ast);
			}

			private void visitFunction(final AnalysisConfig config,
					final AnalysisResult result, final IChecker checker,
					IASTDeclaration ast) {
				ASTVisitor visitor = new ASTVisitor() {
					public int visit(IASTExpression astExpression ) {							

						if(astExpression instanceof IASTFunctionCallExpression)
						{				
							IASTExpression functionCallExpression =   ((IASTFunctionCallExpression) astExpression).getFunctionNameExpression();	
							int functionLineNO =functionCallExpression.getFileLocation().getStartingLineNumber();
							IASTNode parentnode =astExpression.getParent();
							String functionName =functionCallExpression.getRawSignature();

							if(functionCallExpression instanceof IASTIdExpression)
							{
								functionName =((IASTIdExpression) functionCallExpression).getName().toString();
							}
							
							if(functionName.equals("atoi"))
							{
								//test cass1:
								if(parentnode instanceof IASTArraySubscriptExpression)
								{
									
									visitAtoiFunction(config, result, checker,
											astExpression, functionLineNO, parentnode);

								}
								//Test case2:
								else if(parentnode instanceof IASTEqualsInitializer  )
								{
									
									visitEqualsInitializerStatements(config,
											result, checker, astExpression,
											parentnode);
								}
								else
								{
									//Do Nothing
								}
							}
						}

						return ASTVisitor.PROCESS_CONTINUE;

					}

					private void visitEqualsInitializerStatements(
							final AnalysisConfig config,
							final AnalysisResult result, final IChecker checker,
							IASTExpression astExpression, IASTNode parentnode) {
						IASTNode parent =parentnode.getParent();
						while(!(parent instanceof IASTDeclarator ))
						{
							parent =parentnode.getParent();
						}									
						if(parent instanceof IASTDeclarator)
						{
							IASTName name =((IASTDeclarator) parent).getName();										
							final IBinding binding = name.resolveBinding();
							if ((binding != null) )
							{
								final IASTName[] references = translationUnit.getReferences(binding);
								for (IASTName reference : references)
								{
								
									int lineNo =reference.getFileLocation().getStartingLineNumber();
									IASTNode parentNode =reference.getParent().getParent();
									if(parentNode instanceof IASTArraySubscriptExpression)
									{
										visitAtoiFunction(config, result, checker,
												astExpression, lineNo,parentNode);
										
									}												
									
								}
							}
							
						}
					}

					private void visitAtoiFunction(final AnalysisConfig config,
							final AnalysisResult result, final IChecker checker,
							IASTExpression astExpression, int functionLineNO, IASTNode parentnode) {
						IASTInitializerClause[] expParameters =((IASTFunctionCallExpression) astExpression).getArguments();
						for (IASTInitializerClause expParameter : expParameters)
						{								
							if(expParameter instanceof IASTLiteralExpression)
							{

								String expValue =expParameter.toString();
								if(expValue.contains("-"))										
								{																	
									fillDefectData( config, result,checker,parentnode.getFileLocation(),checker.getDescription(),expParameter.toString());
								}

							}
							else if(expParameter instanceof IASTIdExpression)
							{
								 visitIdExpressions(config,
										result, checker, functionLineNO,
										parentnode, expParameter);
							}
						}
					}

					private void visitIdExpressions(
							final AnalysisConfig config,
							final AnalysisResult result, final IChecker checker,
							int functionLineNO, IASTNode parentnode,
							IASTInitializerClause expParameter) {
						final IBinding binding = ((IASTIdExpression) expParameter).getName().resolveBinding();
						if ((binding != null) )
						{
							final IASTName[] references = translationUnit.getDeclarationsInAST(binding);	

							for (IASTName reference : references)
							{
								IASTNode node =reference.getParent();

								if(node instanceof IASTDeclarator) 
								{
									IASTInitializer initialize =((IASTDeclarator)node).getInitializer();
									int initLineNo =initialize.getFileLocation().getStartingLineNumber();

									if(initLineNo <=functionLineNO)
									{
										if(initialize instanceof  CPPASTEqualsInitializer)													
										{
											expParameter = ((CPPASTEqualsInitializer)initialize).getInitializerClause();
											if(expParameter instanceof IASTLiteralExpression)
											{
												try
												{

													String expValue =expParameter.toString();
													if(expValue.contains("-"))										
													{																	
														fillDefectData( config, result,checker,parentnode.getFileLocation(),checker.getDescription(),expParameter.toString());
													}
												}
												catch(Exception ex)
												{
													throw new DexterRuntimeException(ex.getMessage());
												}

											}
										}
									}

								}
							}

						}
						//return expParameter;
					}						

				};
				visitor.shouldVisitExpressions = true; 					
				ast.accept(visitor);
			}

			private void fillDefectData(AnalysisConfig config,
					AnalysisResult result, IChecker checker,
					IASTFileLocation fileLocation, String message, String declaratorName) {
				PreOccurence preOcc = createPreOccurence(config, checker, fileLocation, message,declaratorName);
				result.addDefectWithPreOccurence(preOcc);

			}
			private PreOccurence createPreOccurence(AnalysisConfig config,
					IChecker checker, IASTFileLocation fileLocation, String msg,String declaratorName) {
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

		};

		return visitor;
	}


}
