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

import org.apache.log4j.Logger;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
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
import com.samsung.sec.dexter.core.checker.Checker;
import com.samsung.sec.dexter.core.defect.PreOccurence;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.vdcpp.plugin.DexterVdCppPlugin;
import com.samsung.sec.dexter.vdcpp.util.CppUtil;
import java.math.BigDecimal;

public class USleepCheckerLogic implements ICheckerLogic{

	private int sleepTime;
	private IASTTranslationUnit translationUnit;
	private final static Logger LOG = Logger.getLogger(DexterVdCppPlugin.class);


	@Override
	public void analyze(final AnalysisConfig config, final AnalysisResult result, 
			final Checker checker, IASTTranslationUnit unit) {
		translationUnit =unit;		
		sleepTime =Integer.valueOf(checker.getProperty("value"));
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
				
				return super.visit(ast);
			}

			private void visitFunction(final AnalysisConfig config,
					final AnalysisResult result, final Checker checker,
					IASTDeclaration ast) {
				ASTVisitor visitor = new ASTVisitor() {
					public int visit(IASTExpression astExpression ) {							

						if(astExpression instanceof IASTFunctionCallExpression)
						{				
							visitFunctioncallExpressions(config, result,
									checker, astExpression);

						}

						return ASTVisitor.PROCESS_CONTINUE;

					}

					private void visitFunctioncallExpressions(
							final AnalysisConfig config,
							final AnalysisResult result, final Checker checker,
							IASTExpression astExpression) {
						IASTExpression functionCallExpression =   ((IASTFunctionCallExpression) astExpression).getFunctionNameExpression();		

						String functionName =functionCallExpression.getRawSignature();

						if(functionCallExpression instanceof IASTIdExpression)
						{
							functionName =((IASTIdExpression) functionCallExpression).getName().toString();
						}

						if(functionName.equals("usleep"))
						{
							IASTInitializerClause[] expParameters =((IASTFunctionCallExpression) astExpression).getArguments();
							for (IASTInitializerClause expParameter : expParameters)
							{								
								if(expParameter instanceof IASTLiteralExpression)
								{
									try
									{
										int value =convertIntoDecimalNumber(expParameter.toString());
										if(value <sleepTime)
										{
	
											String msg ="checkUsleep function argument "+expParameter.toString()+ "  should be greater than "+sleepTime+ " to avoid performance issue;";
											fillDefectData( config, result,checker,expParameter.getFileLocation(),msg,expParameter.toString());
										}
									}
									catch (DexterRuntimeException e) {
										LOG.error(e.getMessage(), e);
									}
								}
								else if(expParameter instanceof IASTIdExpression)
								{
									 visitIdExpressions(
											config, result, checker,
											astExpression, expParameter);
								}
								else
								{
									//Do Nothing
								}
							}

						}
					}

					private void visitIdExpressions(
							final AnalysisConfig config,
							final AnalysisResult result, final Checker checker,
							IASTExpression astExpression,
							IASTInitializerClause expParameter) {
						final IBinding binding = ((IASTIdExpression) expParameter).getName().resolveBinding();
						if ((binding != null) )
						{
							final IASTName[] references = translationUnit.getDeclarationsInAST(binding);	

							for (IASTName reference : references)
							{
								IASTNode parentNode =reference.getParent();

								if(parentNode instanceof IASTDeclarator) 
								{
									IASTInitializer initializer =((IASTDeclarator)parentNode).getInitializer();

									if(initializer instanceof  CPPASTEqualsInitializer)													
									{
										expParameter = ((CPPASTEqualsInitializer)initializer).getInitializerClause();
										if(expParameter instanceof IASTLiteralExpression)
										{
											try
											{
												int value =convertIntoDecimalNumber(expParameter.toString());
												if(value <sleepTime)
												{											
													String msg ="argument"+ expParameter.toString()+"  in checkUsleep function should be greater than "+sleepTime+" to avoid performance issue;";
													fillDefectData( config, result,checker,astExpression.getFileLocation(),msg,expParameter.toString());
												}
											}
											catch (DexterRuntimeException e) {
												LOG.error(e.getMessage(), e);
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
			
			private Integer convertIntoDecimalNumber(String string) 
			{				
				Integer outputDecimal=0;	
				
				try
				{	
					string = string.replaceAll("(U|u|LL|ll|L|l)", "");		
					
					if(string.contains("x") ||string.contains("X") )		//ex) 0xff : hexa-decimal
					{
						string = string.replaceFirst("(0x|0X)", "");	
						outputDecimal = Integer.parseInt(string, 16);
					}
					else if(string.contains("e") || string.contains("E"))	//ex) 1e5
					{
						outputDecimal = new BigDecimal(string).intValue();
					}
					else if(string.startsWith("0") && string.length()>1 )							//ex) 033 : octa-decimal
					{
						string = string.replaceFirst("[0]", "");	
						outputDecimal = Integer.parseInt(string, 8);
					}
					else 
					{
						outputDecimal =Integer.valueOf(string);	
					}
					
					return outputDecimal;
				}
				catch(Exception e)
				{
					throw new DexterRuntimeException("Invalid input number " + e.getMessage(), e);	
				}						
			}	

		};

		return visitor;
	}


}
