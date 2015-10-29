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

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTArrayDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTArrayModifier;
import org.eclipse.cdt.core.dom.ast.IASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFieldReference;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.checker.Checker;
import com.samsung.sec.dexter.core.defect.PreOccurence;
import com.samsung.sec.dexter.vdcpp.plugin.DexterVdCppPlugin;
import com.samsung.sec.dexter.vdcpp.util.CppUtil;


public class CheckArrayIndexOutOfBounds implements ICheckerLogic{

	private IASTTranslationUnit translationUnit;
	private Dictionary<String, int[]> dict = new Hashtable<String, int[]>();
	@Override
	public void analyze(final AnalysisConfig config, final AnalysisResult result, 
			final Checker checker, IASTTranslationUnit unit) {
		translationUnit =unit;		
		ASTVisitor visitor = createVisitor(config, result, checker);
		visitor.shouldVisitDeclarations = true;
		unit.accept(visitor);
	}

	private ASTVisitor createVisitor(final AnalysisConfig config,
			final AnalysisResult result, final Checker checker) {
		ASTVisitor visitor = new ASTVisitor() {
			@Override
			public int visit(IASTDeclaration ast ) {

				int linNO = ast.getFileLocation().getStartingLineNumber();
				if(ast instanceof IASTFunctionDefinition)
				{									
					visitFunction(config, result, checker, ast);
					dict.remove(dict.keys());
				}		
				return super.visit(ast);
			}

			private void visitFunction(final AnalysisConfig config,
					final AnalysisResult result, final Checker checker,
					IASTDeclaration ast) {
				ASTVisitor visitor = new ASTVisitor() {
					

					public int visit(IASTDeclarator declarator ) 					
					{
						if(declarator instanceof IASTArrayDeclarator)
						{
							//int lineNo =declarator.getFileLocation().getStartingLineNumber();
							IASTArrayModifier[] arrayModifier =((IASTArrayDeclarator) declarator).getArrayModifiers();
							
							IASTName name =declarator.getName();					
							if(arrayModifier.length >1 && name !=null)
							{
								
								int[] arraylength =new int[arrayModifier.length];		
								for(int i=0;i<arrayModifier.length;i++)							
								{
									IASTExpression exp =arrayModifier[i].getConstantExpression();
									if(exp !=null && (exp instanceof IASTLiteralExpression))
									{
										arraylength[i]=Integer.valueOf(exp.toString());
									}
									else if(exp !=null && exp instanceof IASTIdExpression )
									{
										//Need to add new test case if possible
									}
								}
								dict.put(name.toString(), arraylength);
							}
							
							
							if(arrayModifier.length ==1) //Added Check for One D array
							{
								IASTExpression exp =arrayModifier[0].getConstantExpression();							
							
								IASTInitializer initializer =declarator.getInitializer();
								int initcount =0;
								IASTInitializerClause  initclause;
								if(initializer instanceof IASTEqualsInitializer)
								{
									 initclause =((IASTEqualsInitializer) initializer).getInitializerClause();									
									if(dict.get(initclause.getRawSignature()) != null)
									{
										return ASTVisitor.PROCESS_CONTINUE;
									}									
									initcount =initclause.getChildren().length;
								}
																
								if((exp !=null  && (exp instanceof IASTLiteralExpression))|| initcount >0)
								{
									int constExpValue=0;
									if(exp !=null  && (exp instanceof IASTLiteralExpression))
									{
										constExpValue =Integer.valueOf(exp.toString());
									}
									else
									{
										constExpValue =initcount;
									}

									final IBinding binding = name.resolveBinding();
									if ((binding != null) )
									{
										final IASTName[] references = translationUnit.getReferences(binding);
										for (IASTName reference : references)
										{
											IASTNode nodes =reference.getParent().getParent();									
											if(nodes instanceof IASTArraySubscriptExpression)
											{											
												IASTInitializerClause initClause =((IASTArraySubscriptExpression) nodes).getArgument();
												if(initClause instanceof IASTIdExpression)
												{
													IASTName name1 =((IASTIdExpression) initClause).getName();
													final IBinding binding1 = name1.resolveBinding();
													if ((binding1 != null) )
													{
														final IASTName[] references1 = translationUnit.getReferences(binding1);
														for (IASTName ref1 : references1)
														{
															IASTNode parentNode =ref1.getParent();
															while(parentNode !=null)
															{
																parentNode =parentNode.getParent();
																if(parentNode instanceof IASTForStatement)
																{
																	break;
																}
															}
															if(parentNode !=null)
															{
																IASTNode[] iastnodes =parentNode.getChildren();
																for (IASTNode iastNode : iastnodes) 
																{
																	if(iastNode instanceof IASTBinaryExpression)
																	{																
																		IASTInitializerClause operand2 =((IASTBinaryExpression) iastNode).getInitOperand2();
																		int operator =((IASTBinaryExpression) iastNode).getOperator();
																		if(operand2 instanceof IASTFieldReference )
																		{
																			String operand =((IASTFieldReference) operand2).getFieldName().toString();
																			if(operand.equals("length") && (operator == 10 ||operator == 11 || operator == 28) )
																			{
																				fillDefectData( config, result,checker,nodes.getFileLocation(),checker.getDescription(),operand2.toString());
																				break;
																			}
																		}																
																		else if(operand2 instanceof IASTLiteralExpression)
																		{
																			int operandValue =Integer.valueOf(operand2.toString());																	
																			if((operator == 8 ||operator == 9 ))
																			{
																				operandValue =operandValue-1;
																			}

																			if(operandValue >=constExpValue)
																			{
																				fillDefectData( config, result,checker,nodes.getFileLocation(),checker.getDescription(),operand2.toString());
																				break;
																			}
																		}

																	}

																}

																break;
															}
														}
													}

												}
												else if(initClause instanceof  IASTLiteralExpression)
												{
													int operandValue =Integer.valueOf(initClause.toString());												
													if(operandValue >=constExpValue)
													{
														fillDefectData( config, result,checker,nodes.getFileLocation(),checker.getDescription(),initClause.toString());

													}
												}

											}
											else if(nodes instanceof IASTBinaryExpression)
											{	
												IASTInitializerClause operand2 =((IASTBinaryExpression) nodes).getInitOperand2();
												int operator =((IASTBinaryExpression) nodes).getOperator();
												if(operand2 instanceof IASTFieldReference )
												{
													String operand =((IASTFieldReference) operand2).getFieldName().toString();
													if(operand.equals("length") && (operator == 10 ||operator == 11 || operator == 28) )
													{
														fillDefectData( config, result,checker,nodes.getFileLocation(),checker.getDescription(),operand2.toString());
														break;
													}
												}																
												else if(operand2 instanceof IASTLiteralExpression)
												{
													int operandValue =Integer.valueOf(operand2.toString());																	
													if((operator == 8 ||operator == 9 ))
													{
														operandValue =operandValue-1;
													}

													if(operandValue >=constExpValue)
													{
														fillDefectData( config, result,checker,nodes.getFileLocation(),checker.getDescription(),operand2.toString());
														break;
													}
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
				visitor.shouldVisitDeclarators = true; 					
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

		};

		return visitor;
	}


}
