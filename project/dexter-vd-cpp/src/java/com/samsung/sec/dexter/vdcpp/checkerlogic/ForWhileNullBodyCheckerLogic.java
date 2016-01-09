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
import org.eclipse.cdt.core.dom.ast.IASTComment;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNullStatement;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTWhileStatement;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.checker.Checker;
import com.samsung.sec.dexter.core.defect.PreOccurence;
import com.samsung.sec.dexter.vdcpp.plugin.DexterVdCppPlugin;
import com.samsung.sec.dexter.vdcpp.util.CppUtil;


public class ForWhileNullBodyCheckerLogic implements ICheckerLogic
{
	private IASTTranslationUnit translationUnit;	
	private transient IASTComment[] commentList;
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
					commentList = ast.getTranslationUnit().getComments();
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
					public int visit(IASTStatement statement ) {						
						return ASTVisitor.PROCESS_CONTINUE;					

					}	
				};
				visitor.shouldVisitStatements = true; 					
				ast.accept(visitor);
			}

			private void visitFunction(final AnalysisConfig config,
					final AnalysisResult result, final Checker checker,
					final IASTDeclaration ast) {
				ASTVisitor visitor = new ASTVisitor() {
					public int visit(IASTStatement statement ) {
						if (statement instanceof IASTForStatement)
						{
							visitForStatements(config, result, checker,
									statement);
						}
						else if (statement instanceof IASTWhileStatement)
						{
							visitWhileStatements(config, result, checker,
									statement);
						}
						
						return PROCESS_CONTINUE;
					}

					private void visitForStatements(
							final AnalysisConfig config,
							final AnalysisResult result, final Checker checker,
							IASTStatement statement) {
						final IASTForStatement For_Statement = (IASTForStatement)statement;
						final IASTStatement statementBody = For_Statement.getBody();
						
						if (statementBody instanceof IASTCompoundStatement)
						{
							visitCompoundStatementInForControlBlock(config, result,
									checker, For_Statement, statementBody);
							
						}
						else
						{
							if ((statementBody instanceof IASTNullStatement) &&
									(!isNullCommentExists(statementBody)))
							{
								
								fillDefectData( config,
										result,  checker,
										For_Statement.getFileLocation(),  checker.getDescription(), "for");
							}
						}
					}

					private void visitWhileStatements(
							final AnalysisConfig config,
							final AnalysisResult result, final Checker checker,
							IASTStatement statement) {
						final IASTWhileStatement While_Statement = (IASTWhileStatement)statement;
						final IASTStatement statementBody = While_Statement.getBody();
						
						if (statementBody instanceof IASTCompoundStatement)
						{
							visitCompoundStatementInWhileControlBlock(config,
									result, checker, While_Statement,
									statementBody);
							
						}
						else
						{
							if ((statementBody instanceof IASTNullStatement) &&
									(!isNullCommentExists(statementBody)))
							{
								
								fillDefectData( config,
										result,  checker,
										While_Statement.getFileLocation(),  checker.getDescription(), "while");
								
							}
						}
					}

					private void visitCompoundStatementInWhileControlBlock(
							final AnalysisConfig config,
							final AnalysisResult result, final Checker checker,
							final IASTWhileStatement While_Statement,
							final IASTStatement statementBody) {
						final IASTCompoundStatement compound_stmt = (IASTCompoundStatement) statementBody;
						final IASTStatement[] lstStmt = compound_stmt.getStatements();
						
						if (lstStmt.length == 0)	
						{
							fillDefectData( config,
									result,  checker,
									While_Statement.getFileLocation(),  checker.getDescription(), "while");
						}
						else if ((lstStmt.length == 1) &&
								(lstStmt[0] instanceof IASTNullStatement) &&
								(!isNullCommentExists(statementBody)))
						{
							fillDefectData( config,
									result,  checker,
									While_Statement.getFileLocation(),  checker.getDescription(), "while");
							
						}
					}

					private void visitCompoundStatementInForControlBlock(
							final AnalysisConfig config,
							final AnalysisResult result, final Checker checker,
							final IASTForStatement For_Statement,
							final IASTStatement statementBody) {
						final IASTCompoundStatement compound_stmt = (IASTCompoundStatement) statementBody;
						final IASTStatement[] lstStmt = compound_stmt.getStatements();
						if (lstStmt.length == 0)		
						{
							fillDefectData( config,
									result,  checker,
									For_Statement.getFileLocation(),  checker.getDescription(), "for");
						}
						else if ((lstStmt.length == 1) &&
								(lstStmt[0] instanceof IASTNullStatement) &&
								(!isNullCommentExists(statementBody)))		
						{
							
							fillDefectData( config,
									result,  checker,
									For_Statement.getFileLocation(),  checker.getDescription(), "for");
						}
					}

					private boolean isNullCommentExists(IASTStatement statement) {
						boolean result = false;
						int statementStartLineNo =statement.getFileLocation().getStartingLineNumber();
						int statementEndLineNo =statement.getFileLocation().getEndingLineNumber();
						for (IASTComment comment : commentList)
						{
							int commentStartLineNo =comment.getFileLocation().getStartingLineNumber();							
							if ( ( commentStartLineNo<= statementEndLineNo) &&
									(commentStartLineNo >= statementStartLineNo))  
							{
								if (comment.toString().matches("/\\*\\s*[nN][uU][lL][lL]\\s*\\*/"))
								{
									result = true;
								}	
								break;
							}
						}
						
						return result;
					}

					

				};
				visitor.shouldVisitStatements = true; 			
				ast.accept(visitor);
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
