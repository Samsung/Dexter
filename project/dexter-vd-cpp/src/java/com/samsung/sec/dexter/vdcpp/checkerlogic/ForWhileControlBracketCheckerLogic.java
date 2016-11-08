/**
 *  @file   ForWhileControlBracket.java
 *  @brief  ForWhileControlBracket class source file
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
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDoStatement;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTSwitchStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTWhileStatement;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.checker.IChecker;
import com.samsung.sec.dexter.core.defect.PreOccurence;
import com.samsung.sec.dexter.vdcpp.plugin.DexterVdCppPlugin;
import com.samsung.sec.dexter.vdcpp.util.CppUtil;


public class ForWhileControlBracketCheckerLogic implements ICheckerLogic
{
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
					visitOtherCompoundDeclaration(config, result, checker, ast);				
				}
				return super.visit(ast);
			}

			private void visitOtherCompoundDeclaration(
					final AnalysisConfig config, final AnalysisResult result,
					final IChecker checker, final IASTDeclaration ast) {
				ASTVisitor visitor = new ASTVisitor() {
					public int visit(IASTStatement statement ) {						
						return ASTVisitor.PROCESS_CONTINUE;					

					}	
				};
				visitor.shouldVisitStatements = true; 					
				ast.accept(visitor);
			}

			private void visitFunction(final AnalysisConfig config,
					final AnalysisResult result, final IChecker checker,
					final IASTDeclaration ast) {
				ASTVisitor visitor = new ASTVisitor() {
					public int visit(IASTStatement statement ) {						

						if (statement instanceof IASTIfStatement)
						{
							visitIfStatements(config, result, checker,
									statement);
						}
						else if (statement instanceof IASTForStatement)
						{
							visitForStatements(config, result, checker,
									statement);
							
							
						}
						else if (statement instanceof IASTWhileStatement)
						{
							visitWhileStatements(config, result, checker,
									statement);				
							
						}
						else if (statement instanceof IASTDoStatement)
						{
							visitDoStatements(config, result, checker,
									statement);	
							
						}
						else if (statement instanceof IASTSwitchStatement)
						{
							visitSwitchStatements(config, result, checker,
									statement);	
							
						}
					
											
						return ASTVisitor.PROCESS_CONTINUE;
					}

					private void visitSwitchStatements(
							final AnalysisConfig config,
							final AnalysisResult result, final IChecker checker,
							IASTStatement statement) {
						final IASTSwitchStatement switch_statement = (IASTSwitchStatement) statement;
						final IASTStatement switch_body = switch_statement.getBody();
						if (!(switch_body instanceof IASTCompoundStatement))
						{													
							String ruleData ="switch Statement";									
							fillDefectData( config,
									result,  checker,
									switch_body.getFileLocation(),  checker.getDescription(), ruleData);
						}
					}

					private void visitDoStatements(final AnalysisConfig config,
							final AnalysisResult result, final IChecker checker,
							IASTStatement statement) {
						final IASTDoStatement do_statement = (IASTDoStatement) statement;
						final IASTStatement do_body = do_statement.getBody();
						if (!(do_body instanceof IASTCompoundStatement))
						{															
							String ruleData ="Do Statement";									
							fillDefectData( config,
									result,  checker,
									do_body.getFileLocation(),  checker.getDescription(), ruleData);
						}
					}

					private void visitWhileStatements(
							final AnalysisConfig config,
							final AnalysisResult result, final IChecker checker,
							IASTStatement statement) {
						final IASTWhileStatement while_statement = (IASTWhileStatement) statement;
						final IASTStatement while_body = while_statement.getBody();
						if (!(while_body instanceof IASTCompoundStatement))
						{																
							String ruleData ="while Statement";									
							fillDefectData( config,
									result,  checker,
									while_body.getFileLocation(),  checker.getDescription(), ruleData);
						}
					}

					private void visitForStatements(
							final AnalysisConfig config,
							final AnalysisResult result, final IChecker checker,
							IASTStatement statement) {
						final IASTForStatement for_statement = (IASTForStatement) statement;
						final IASTStatement for_body = for_statement.getBody();
						if (!(for_body instanceof IASTCompoundStatement))
						{
							String ruleData ="for Statement";	
							fillDefectData( config,
									result,  checker,
									for_body.getFileLocation(),  checker.getDescription(), ruleData);
							
						}
					}

					private void visitIfStatements(final AnalysisConfig config,
							final AnalysisResult result, final IChecker checker,
							IASTStatement statement) {
						final IASTIfStatement if_statement = (IASTIfStatement) statement;							
						final IASTStatement then_clause = if_statement.getThenClause();								
						if (!(then_clause instanceof IASTCompoundStatement))
						{								
							String ruleData ="";
							if(if_statement.getThenClause() !=null)
							{
								ruleData="if Statement";
							}
							else if(if_statement.getElseClause() !=null)
							{
								ruleData="else if Statement";
							}
							
							fillDefectData( config,
									result,  checker,
									then_clause.getFileLocation(),  checker.getDescription(), ruleData);
							
						}
						final IASTStatement else_clause = if_statement.getElseClause();
						if (else_clause != null)
						{
							if (!(else_clause instanceof IASTIfStatement) &&
								!(else_clause instanceof IASTCompoundStatement))
							{
								String ruleData ="else Statement";									
								fillDefectData( config,
										result,  checker,
										else_clause.getFileLocation(),  checker.getDescription(), ruleData);
								
							}		
						}
					}

					

				};
				visitor.shouldVisitStatements = true; 			
				ast.accept(visitor);
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
