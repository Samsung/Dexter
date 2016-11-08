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
import com.samsung.sec.dexter.core.checker.IChecker;
import com.samsung.sec.dexter.core.defect.PreOccurence;
import com.samsung.sec.dexter.vdcpp.plugin.DexterVdCppPlugin;
import com.samsung.sec.dexter.vdcpp.util.CppUtil;


public class CheckFreeStatementForThirdParam implements ICheckerLogic
{
	private IASTTranslationUnit translationUnit;	
	private String[] lstMethods=null;
	@Override
	public void analyze(final AnalysisConfig config, final AnalysisResult result, 
			final IChecker checker, IASTTranslationUnit unit) {
		translationUnit =unit;
		lstMethods= checker.getProperty("method-list").split(",");
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
				else if(ast instanceof IASTSimpleDeclaration)
				{												
					visitOtherCompoundDeclaration(config, result, checker, ast);				
				}
				return super.visit(ast);
			}

			private void visitOtherCompoundDeclaration(
					final AnalysisConfig config, final AnalysisResult result,
					final IChecker checker, final IASTDeclaration ast) {
				ASTVisitor visitor = new ASTVisitor() {
					public int visit(IASTExpression astExpression ) {							

						if(astExpression instanceof IASTFunctionCallExpression)
						{				
							IASTExpression exp =   ((IASTFunctionCallExpression) astExpression).getFunctionNameExpression();	
							String functionName =exp.getRawSignature();

							if(exp instanceof IASTIdExpression)
							{
								functionName =((IASTIdExpression) exp).getName().toString();
							}

							for (String  methodName : lstMethods)
							{					
								if(functionName.equals(methodName))
								{
									
								   IASTInitializerClause[] params =	((IASTFunctionCallExpression) astExpression).getArguments();
								   if(params.length >2 && (params[2] instanceof IASTUnaryExpression))
									{
										IASTExpression  exp1 = ((IASTUnaryExpression)params[2]).getOperand();
										if(exp1 instanceof IASTExpression)
										{
											IASTName  name =((IASTIdExpression) exp1).getName();
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
									
																		
								}
							}
						}
						return ASTVisitor.PROCESS_CONTINUE;
					}

				};
				visitor.shouldVisitExpressions = true; 					
				ast.accept(visitor);
			}

			private void visitFunction(final AnalysisConfig config,
					final AnalysisResult result, final IChecker checker,
					final IASTDeclaration ast) {
				ASTVisitor visitor = new ASTVisitor() {
					public int visit(IASTExpression astExpression ) {							

						if(astExpression instanceof IASTFunctionCallExpression)
						{				
							IASTExpression exp =   ((IASTFunctionCallExpression) astExpression).getFunctionNameExpression();	
							String functionName =exp.getRawSignature();

							if(exp instanceof IASTIdExpression)
							{
								functionName =((IASTIdExpression) exp).getName().toString();
							}

							for (String  methodName : lstMethods)
							{					
								if(functionName.equals(methodName))
								{
									
								   IASTInitializerClause[] params =	((IASTFunctionCallExpression) astExpression).getArguments();
									if(params.length >2 && (params[2] instanceof IASTUnaryExpression))
									{
										IASTExpression  exp1 = ((IASTUnaryExpression)params[2]).getOperand();
										if(exp1 instanceof IASTExpression)
										{
											IASTName  name =((IASTIdExpression) exp1).getName();
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
									
																		
								}
							}

						}
						return ASTVisitor.PROCESS_CONTINUE;
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
						IASTExpression exp1 =  ((IASTArraySubscriptExpression) parent).getArrayExpression();					
						
						IASTNode  parent1 =parent.getParent();
						if(parent1 instanceof IASTFunctionCallExpression)
						{
							IASTExpression exp =   ((IASTFunctionCallExpression) parent1).getFunctionNameExpression();							
							String functionName =exp.getRawSignature();

							if((exp instanceof IASTIdExpression)  && (exp1 instanceof IASTIdExpression))
							{
								functionName =((IASTIdExpression) exp).getName().toString();
								String functArgument =exp1.toString();
								if(functionName.equals("free") && functArgument.contains(expName))
								{
									IsFreeArrayInternalObjects =true;
								}
							}
						}						
						
					}
					
					if(parent instanceof IASTFunctionCallExpression)
					{
						IASTExpression exp =   ((IASTFunctionCallExpression) parent).getFunctionNameExpression();
						IASTInitializerClause[] expParameter =((IASTFunctionCallExpression) parent).getArguments();
						
						List<String> parameter =new ArrayList<String>();
						for (IASTInitializerClause string : expParameter) {
							parameter.add(string.toString());
						}
						String functionName =exp.getRawSignature();

						if(exp instanceof IASTIdExpression)
						{
							functionName =((IASTIdExpression) exp).getName().toString();
							if(functionName.equals("free") && parameter.contains(expName))
							{
								IsFreeArrayObjects =true;
							}
						}
					}
				}
				if(IsFreeArrayInternalObjects && IsFreeArrayObjects)
				{
					status= true;
				}
				return status;
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
						IASTExpression exp =   ((IASTFunctionCallExpression) parent).getFunctionNameExpression();
						IASTInitializerClause[] expParameter =((IASTFunctionCallExpression) parent).getArguments();
						
						List<String> parameter =new ArrayList<String>();
						for (IASTInitializerClause string : expParameter) {
							parameter.add(string.toString());
						}
						String functionName =exp.getRawSignature();

						if(exp instanceof IASTIdExpression)
						{
							functionName =((IASTIdExpression) exp).getName().toString();
							if(functionName.equals("free") && parameter.contains(ExpName))
							{
								status =true;
							}
						}
					}
				}
				return status;
			}		
			private void fillDefectData(AnalysisConfig config,
					AnalysisResult result, IChecker checker,
					IASTFileLocation fileLocation, String message, String declaratorName) {

				PreOccurence preOcc = createPreOccurence(config, checker, fileLocation, message,declaratorName);
				result.addDefectWithPreOccurence(preOcc);
			}

			private PreOccurence createPreOccurence(AnalysisConfig config,
					IChecker checker, IASTFileLocation fileLocation, String msg,String decName) {
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
