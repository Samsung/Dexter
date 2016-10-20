/**
 *  @file   VectorEraseFunctionMisuse.java
 *  @brief  VectorEraseFunctionMisuse class source file
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
import com.samsung.sec.dexter.core.checker.IChecker;
import com.samsung.sec.dexter.core.defect.PreOccurence;
import com.samsung.sec.dexter.vdcpp.plugin.DexterVdCppPlugin;
import com.samsung.sec.dexter.vdcpp.util.CppUtil;


public class VectorEraseFunctionMisuseCheckerLogic implements ICheckerLogic{

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
				else if(ast instanceof IASTSimpleDeclaration)
				{												
					//visitOtherCompoundDeclaration(config, result, checker, ast);				
				}

				return super.visit(ast);
			}

			
			private void visitFunction(final AnalysisConfig config,
					final AnalysisResult result, final IChecker checker,
					IASTDeclaration ast) {
				ASTVisitor visitor = new ASTVisitor() {
					public int visit(IASTStatement astStatement ) {							

						if(astStatement instanceof IASTForStatement)
						{
							visitForControlStatements(config, result, checker,
									astStatement);								
						}						

						return ASTVisitor.PROCESS_CONTINUE;
					}

					private void visitForControlStatements(
							final AnalysisConfig config,
							final AnalysisResult result, final IChecker checker,
							IASTStatement astStatement) {
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
							IASTDeclaration decaration =((IASTDeclarationStatement) initStatment).getDeclaration();
							if(decaration instanceof IASTSimpleDeclaration)
							{
								visitdeclarationNode(config, result,
										checker, iastNodes, operand, decaration);
							}							

						}
						else if(initStatment instanceof IASTExpressionStatement)
						{						
							visitExpressionStatements(config, result, checker,
									iastNodes, initStatment, operand);		

						}
					}

					private void visitExpressionStatements(
							final AnalysisConfig config,
							final AnalysisResult result, final IChecker checker,
							IASTNode[] iastNodes, IASTStatement initStatment,
							String operand) {
						IASTExpression expressionStatement =((IASTExpressionStatement) initStatment).getExpression();								
						if(expressionStatement instanceof IASTBinaryExpression)
						{
							IASTExpression Operand1Expression =((IASTBinaryExpression) expressionStatement).getOperand1();							

							if(Operand1Expression instanceof IASTIdExpression)
							{
								final IBinding binding = ((IASTIdExpression) Operand1Expression).getName().resolveBinding();
								if ((binding != null) )
								{
									final IASTName[] references = translationUnit.getDeclarationsInAST(binding);
									for (IASTName reference : references)
									{
										IASTNode node =reference.getParent();

										if(node instanceof IASTDeclarator) 
										{
											IASTNode parent =((IASTDeclarator)node).getParent();
											if(parent instanceof  IASTSimpleDeclaration)													
											{
												visitDecarationNode(
														config, result,
														checker,
														iastNodes,
														operand, parent);
											}

										}
									}
								}

							}
						}
					}

				};
				visitor.shouldVisitStatements = true; 					
				ast.accept(visitor);
			}

			private void visitDecarationNode(
					final AnalysisConfig config,
					final AnalysisResult result, final IChecker checker,
					IASTNode[] iastNodes, String operand, IASTNode inst) {
				String declSpecifier= ((IASTSimpleDeclaration)inst).getDeclSpecifier().getRawSignature();
				if(declSpecifier.contains("vector")&& declSpecifier.contains("::iterator"))
				{
					for (IASTNode iastNode : iastNodes) {

						if(iastNode instanceof IASTExpressionStatement)
						{
							IASTExpression expressionStatement =((IASTExpressionStatement) iastNode).getExpression();
							if(expressionStatement instanceof IASTFunctionCallExpression)
							{															  
								visitFunctionCallExpression(config, result,
										checker, operand, expressionStatement);
								
							}

						}
					}

				}
			}


			private void visitFunctionCallExpression(
					final AnalysisConfig config, final AnalysisResult result,
					final IChecker checker, String operand,
					IASTExpression expressionStatement) {
				String functionName  = ((IASTFunctionCallExpression) expressionStatement).getFunctionNameExpression().getRawSignature();								
				IASTInitializerClause[] initClaus	= ((IASTFunctionCallExpression) expressionStatement).getArguments();
				for (IASTInitializerClause iastInitializerClause : initClaus) 
				{
					String argument =iastInitializerClause.getRawSignature();
					if(functionName.contains("erase") && argument.contains(operand))
					{
						fillDefectData( config, result,checker,expressionStatement.getFileLocation(),checker.getDescription(),argument);
					}
				}
			}

			private void visitdeclarationNode(
					final AnalysisConfig config,
					final AnalysisResult result, final IChecker checker,
					IASTNode[] iastNodes, String operand,
					IASTDeclaration dec) {
				String decSpecifier =((IASTSimpleDeclaration) dec).getDeclSpecifier().getRawSignature();
				if(decSpecifier.contains("vector")&& decSpecifier.contains("::iterator"))
				{
					for (IASTNode iastNode : iastNodes) {

						if(iastNode instanceof IASTExpressionStatement)
						{
							IASTExpression expressionStatement =((IASTExpressionStatement) iastNode).getExpression();
							if(expressionStatement instanceof IASTFunctionCallExpression)
							{															    
								visitFunctionCallExpression(config, result,
										checker, operand, expressionStatement);
							}

						}
					}

				}
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
				preOcc.setStringValue(msg);
				preOcc.setMessage(msg);

				return preOcc;

			}

		};

		return visitor;
	}


}
