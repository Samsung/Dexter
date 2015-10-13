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
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
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


public class VectorEraseFunctionMisuse implements ICheckerLogic{

	private IASTTranslationUnit translationUnit;	

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

			/*
			private void visitOtherCompoundDeclaration(
					final AnalysisConfig config, final AnalysisResult result,
					final Checker checker, IASTDeclaration ast) {
							ASTVisitor visitor = new ASTVisitor() {
					public int visit(IASTStatement astStatement ) {							

						if(astStatement instanceof IASTForStatement)
						{
							//test case not able to cover							
						}
						return ASTVisitor.PROCESS_CONTINUE;
					}

				};
				visitor.shouldVisitStatements = true; 					
				ast.accept(visitor);
			}
			*/
			
			
			private void visitFunction(final AnalysisConfig config,
					final AnalysisResult result, final Checker checker,
					IASTDeclaration ast) {
				ASTVisitor visitor = new ASTVisitor() {
					public int visit(IASTStatement astStatement ) {							

						if(astStatement instanceof IASTForStatement)
						{
							IASTForStatement  forStatement =  ((IASTForStatement) astStatement);
							IASTNode[] iastNodes= forStatement.getBody().getChildren();
							IASTStatement initStatment= forStatement.getInitializerStatement();
							IASTExpression itrExp= forStatement.getIterationExpression();
							String operand="";
							if(itrExp instanceof IASTUnaryExpression)
							{
								operand =((IASTUnaryExpression) itrExp).getOperand().getRawSignature();								
							}
							else if(itrExp instanceof IASTIdExpression)
							{
								operand =itrExp.getRawSignature();
							}
								

							if(initStatment instanceof IASTDeclarationStatement)
							{
								IASTDeclaration dec =((IASTDeclarationStatement) initStatment).getDeclaration();
								if(dec instanceof IASTSimpleDeclaration)
								{
									visitdeclarationNode(config, result,
											checker, iastNodes, operand, dec);
								}							

							}
							else if(initStatment instanceof IASTExpressionStatement)
							{						
								IASTExpression astExp =((IASTExpressionStatement) initStatment).getExpression();								
								if(astExp instanceof IASTBinaryExpression)
								{
									IASTExpression t1 =((IASTBinaryExpression) astExp).getOperand1();							

									if(t1 instanceof IASTIdExpression)
									{
										final IBinding binding = ((IASTIdExpression) t1).getName().resolveBinding();
										if ((binding != null) )
										{

											final IASTName[] references2 = translationUnit.getDeclarationsInAST(binding);
											for (IASTName reference : references2)
											{
												IASTNode node =reference.getParent();

												if(node instanceof IASTDeclarator) 
												{
													IASTNode inst =((IASTDeclarator)node).getParent();

													if(inst instanceof  IASTSimpleDeclaration)													
													{
														visitDecarationNode(
																config, result,
																checker,
																iastNodes,
																operand, inst);
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
				visitor.shouldVisitStatements = true; 					
				ast.accept(visitor);
			}

			private void visitDecarationNode(
					final AnalysisConfig config,
					final AnalysisResult result, final Checker checker,
					IASTNode[] iastNodes, String operand, IASTNode inst) {
				String decspec= ((IASTSimpleDeclaration)inst).getDeclSpecifier().getRawSignature();
				if(decspec.contains("vector")&& decspec.contains("::iterator"))
				{
					for (IASTNode iastNode : iastNodes) {

						if(iastNode instanceof IASTExpressionStatement)
						{
							IASTExpression exp =((IASTExpressionStatement) iastNode).getExpression();
							if(exp instanceof IASTFunctionCallExpression)
							{															  
								String funname  = ((IASTFunctionCallExpression) exp).getFunctionNameExpression().getRawSignature();								
								IASTInitializerClause[] initClaus	= ((IASTFunctionCallExpression) exp).getArguments();
								for (IASTInitializerClause iastInitializerClause : initClaus) 
								{
									String argument =iastInitializerClause.getRawSignature();
									if(funname.contains("erase") && argument.contains(operand))
									{			  

										fillDefectData( config, result,checker,exp.getFileLocation(),checker.getDescription(),argument);
									}
								}

								
							}

						}
					}

				}
			}

			private void visitdeclarationNode(
					final AnalysisConfig config,
					final AnalysisResult result, final Checker checker,
					IASTNode[] iastNodes, String operand,
					IASTDeclaration dec) {
				String decspec =((IASTSimpleDeclaration) dec).getDeclSpecifier().getRawSignature();
				if(decspec.contains("vector")&& decspec.contains("::iterator"))
				{
					for (IASTNode iastNode : iastNodes) {

						if(iastNode instanceof IASTExpressionStatement)
						{
							IASTExpression exp =((IASTExpressionStatement) iastNode).getExpression();
							if(exp instanceof IASTFunctionCallExpression)
							{															    
								String funname  = ((IASTFunctionCallExpression) exp).getFunctionNameExpression().getRawSignature();
							
								IASTInitializerClause[] initClaus	= ((IASTFunctionCallExpression) exp).getArguments();
								for (IASTInitializerClause iastInitializerClause : initClaus) 
								{
									String argument =iastInitializerClause.getRawSignature();
									if(funname.contains("erase") && argument.contains(operand))
									{			  

										fillDefectData( config, result,checker,exp.getFileLocation(),checker.getDescription(),argument);
									}
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
