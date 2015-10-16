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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IBinding;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.checker.Checker;
import com.samsung.sec.dexter.core.defect.PreOccurence;
import com.samsung.sec.dexter.vdcpp.plugin.DexterVdCppPlugin;
import com.samsung.sec.dexter.vdcpp.util.CppUtil;


public class SignedUnsignedAssignmentError implements ICheckerLogic{

	private IASTTranslationUnit translationUnit;	
	private Map<String, IASTDeclSpecifier> dictionary = new HashMap<String, IASTDeclSpecifier>();  
	private boolean isOK =false;
	private boolean IsValeOK =false;

	@Override
	public void analyze(final AnalysisConfig config, final AnalysisResult result, 
			final Checker checker, IASTTranslationUnit unit) {
		dictionary.clear();
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
				if(ast instanceof IASTFunctionDefinition)
				{									
					visitFunction(config, result, checker, ast);
				}		
				else if(ast instanceof IASTSimpleDeclaration)
				{												
					//visitOtherCompoundDeclaration(config, result, checker, ast);				
				}				
				return super.visit(ast);
			}

			private void visitFunction(final AnalysisConfig config,
					final AnalysisResult result, final Checker checker,
					final IASTDeclaration ast) {

				ASTVisitor visitor1 = new ASTVisitor() {
					public int visit(IASTStatement statement ) {					

						if (statement instanceof IASTDeclarationStatement)
						{
							IASTDeclaration declaration =((IASTDeclarationStatement) statement).getDeclaration();
							if(declaration instanceof IASTSimpleDeclaration)
							{
								IASTDeclarator[] declarators =((IASTSimpleDeclaration) declaration).getDeclarators();
								IASTDeclSpecifier decspecifer =((IASTSimpleDeclaration) declaration).getDeclSpecifier();

								for (IASTDeclarator iastDeclarator : declarators) {
									String declarator =iastDeclarator.getName().toString();

									if(!dictionary.containsKey(declarator))
									{
										dictionary.put(declarator, decspecifer);
									}
								}								
							}														
						}

						return ASTVisitor.PROCESS_CONTINUE;
					}

				};
				visitor1.shouldVisitStatements = true; 					
				ast.accept(visitor1);


				ASTVisitor visitor = new ASTVisitor() {
					public int visit(IASTExpression expression ) {							
						if (expression instanceof IASTBinaryExpression)
						{
							IASTExpression exp1 =((IASTBinaryExpression) expression).getOperand1();
							IASTExpression exp2 =((IASTBinaryExpression) expression).getOperand2();
							int operator =((IASTBinaryExpression) expression).getOperator();							

							if ((exp1 instanceof IASTIdExpression) && (exp2 instanceof IASTIdExpression) && (operator == IASTBinaryExpression.op_assign) )
							{
								IASTName name1 =((IASTIdExpression) exp1).getName();
								IASTName name2 =((IASTIdExpression) exp2).getName();								

								IASTSimpleDeclSpecifier decspeciferFirst =getDeclaratorSpecifer(name1.toString());
								IASTSimpleDeclSpecifier decspeciferSecond =getDeclaratorSpecifer(name2.toString());								

								if(decspeciferFirst !=null && decspeciferSecond !=null )
								{									
									if(decspeciferFirst.isUnsigned() && !decspeciferSecond.isUnsigned())
									{
										int lineNo =expression.getFileLocation().getStartingLineNumber();
										if(getInitializeExpression(name2,lineNo))
										{
											isOK=false;
												fillDefectData( config,
													result,  checker,
													expression.getFileLocation(),  checker.getDescription(), name1.toString());
										}
									}

								}							

							}
							else if ((exp1 instanceof IASTIdExpression) && (exp2 instanceof IASTLiteralExpression) ) 
							{
								if((operator >=8)&& (operator <=11 ))
								{
									IASTName name1 =((IASTIdExpression) exp1).getName();									
									IASTSimpleDeclSpecifier decspeciferFirst =getDeclaratorSpecifer(name1.toString());
									if(decspeciferFirst !=null )
									{
										if(decspeciferFirst.toString().equals("char"))
										{
											fillDefectData( config,
													result,  checker,
													expression.getFileLocation(),   checker.getDescription(), name1.toString());
										}

									}	

								}
							}

						}
						return ASTVisitor.PROCESS_CONTINUE;
					}
				
					private boolean getInitializeExpression(IASTName name, int lineNo) {						

						
						final IBinding binding = name.resolveBinding();
						if ((binding != null) )
						{								
							final IASTName[] references2 = translationUnit.getReferences(binding);										
							for (IASTName reference : references2)
							{
								IASTNode node =reference.getParent();
								if((node.getFileLocation().getStartingLineNumber() <=lineNo))
								{
									while(true)
									{
										if(node== null || node instanceof IASTBinaryExpression )
										{
											break;
										}
										node =node.getParent();
									}
									if((node instanceof IASTBinaryExpression) )
									{
										isOK =getLiteralExpression(node);	
										
									}
								}

							}

						}

						return isOK;						
					}

					
					private boolean getLiteralExpression(IASTNode node) {
						
						IASTExpression exp2 =((IASTBinaryExpression) node).getOperand2();
						if(exp2 instanceof IASTUnaryExpression)
						{
							int Operator =((IASTUnaryExpression) exp2).getOperator();
							if(Operator == IASTUnaryExpression.op_minus)
							{
								IsValeOK = true;									
							}
							else
							{
								IsValeOK = false;
							}
						 
						}
						else if(exp2 instanceof IASTLiteralExpression)
						{
							int operator =((IASTBinaryExpression) node).getOperator();		
							if(operator == IASTBinaryExpression.op_assign )
							{
								String name =exp2.toString();								
								Integer value = 0 ;
								try
								{
									int index = 0 ;
									if(name.startsWith("0x"))
									{
										index =name.indexOf("0x");
										name =name.substring(index+2) ;
										value = Integer.valueOf(name, 16);
									}
									else if(name.startsWith("0X"))
									{
										index =name.indexOf("0X");
										name =name.substring(index+2) ;
										value = Integer.valueOf(name, 16);
									}
									else
									{
										value = Integer.valueOf(name);
									}

								}								
								catch(Exception ex)
								{

								}
								if(value >127)
								{
									IsValeOK = true;									
								}
								else
								{
									IsValeOK = false;
								}
							}

						}
						else if(exp2 instanceof IASTBinaryExpression)
						{
							getLiteralExpression(exp2);

						}
						return IsValeOK;		
					}
				};
				visitor.shouldVisitExpressions = true; 					
				ast.accept(visitor);
				dictionary.clear();

			}

			private IASTSimpleDeclSpecifier getDeclaratorSpecifer(String name)
			{
				if(dictionary.containsKey(name))
				{					
					IASTDeclSpecifier decSpec= (IASTDeclSpecifier) dictionary.get(name) ;					
					if(decSpec instanceof IASTSimpleDeclSpecifier)
					{
						return (IASTSimpleDeclSpecifier)decSpec;
					}
				}
				return null;
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
				preOcc.setStringValue(msg);
				preOcc.setMessage(msg);

				return preOcc;

			}

		};

		return visitor;
	}


}
