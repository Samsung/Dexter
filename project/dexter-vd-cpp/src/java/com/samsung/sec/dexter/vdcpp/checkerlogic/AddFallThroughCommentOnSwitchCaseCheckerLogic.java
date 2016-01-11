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
import org.eclipse.cdt.core.dom.ast.IASTBreakStatement;
import org.eclipse.cdt.core.dom.ast.IASTCaseStatement;
import org.eclipse.cdt.core.dom.ast.IASTComment;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDefaultStatement;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTSwitchStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.checker.Checker;
import com.samsung.sec.dexter.core.defect.PreOccurence;
import com.samsung.sec.dexter.vdcpp.plugin.DexterVdCppPlugin;
import com.samsung.sec.dexter.vdcpp.util.CppUtil;


public class AddFallThroughCommentOnSwitchCaseCheckerLogic implements ICheckerLogic
{
	private IASTTranslationUnit translationUnit;	
	private transient IASTComment[] commentList;
	private String[] regexStrings ={"/\\*\\s*.*\\s+[fF][aA][lL][lL]\\s*[tT][hH][rR][oO][uU][gG][hH]\\s+.*\\s*\\*/",
								"/\\*\\s*[fF][aA][lL][lL]\\s*[tT][hH][rR][oO][uU][gG][hH]\\s*\\*/",
								"/\\*\\s*[fF][aA][lL][lL]\\s*[tT][hH][rR][oO][uU][gG][hH]\\s+.*\\s*\\*/",
								"/\\*\\s*.*\\s+[fF][aA][lL][lL]\\s*[tT][hH][rR][oO][uU][gG][hH]\\s*\\*/",
								"//\\s*.*\\s+[fF][aA][lL][lL]\\s*[tT][hH][rR][oO][uU][gG][hH]\\s+.*\\s*",
								"//\\s*.*\\s+[fF][aA][lL][lL]\\s*[tT][hH][rR][oO][uU][gG][hH]\\s*",
								"//\\s*[fF][aA][lL][lL]\\s*[tT][hH][rR][oO][uU][gG][hH]\\s+.*\\s*",
								"//\\s*[fF][aA][lL][lL]\\s*[tT][hH][rR][oO][uU][gG][hH]\\s*"	
								};
	@Override
	public void analyze(final AnalysisConfig config, final AnalysisResult result, 
			final Checker checker, IASTTranslationUnit unit) {
		translationUnit =unit;	
		commentList =translationUnit.getComments();
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
					commentList =ast.getTranslationUnit().getComments();
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
						if (statement instanceof IASTSwitchStatement)
						{
							final IASTSwitchStatement switchStatement = (IASTSwitchStatement) statement;
							final IASTCompoundStatement switchBody = (IASTCompoundStatement) switchStatement.getBody();
							final IASTStatement[] lstStatement = switchBody.getStatements();
							IASTStatement currentCaseStatement = null;
							boolean breakOccured  = false;
							boolean caseHasStatements = false;
														
							for (int i=0; i<lstStatement.length; i++)
							{
								if (lstStatement[i] instanceof IASTCaseStatement)
								{
									if (caseHasStatements && !breakOccured)
									{
										final int prevLoc = lstStatement[i-1].getFileLocation().getEndingLineNumber();
										final int curLoc = lstStatement[i].getFileLocation().getStartingLineNumber();
										checkFallThroughComment(prevLoc, curLoc, currentCaseStatement);
									}
									
									currentCaseStatement = lstStatement[i];
									breakOccured = false;
									caseHasStatements = false;
								}
								else if (lstStatement[i] instanceof IASTDefaultStatement)
								{	
									if (caseHasStatements && !breakOccured && (i != 0))
									{
										
										final int prevLoc = lstStatement[i-1].getFileLocation().getEndingLineNumber();
										final int curLoc = lstStatement[i].getFileLocation().getStartingLineNumber();
										checkFallThroughComment(prevLoc, curLoc, currentCaseStatement);
									}
									
									currentCaseStatement = null;	
									caseHasStatements = false;	
								}
								else if ( (lstStatement[i] instanceof IASTBreakStatement) ||(lstStatement[i] instanceof IASTReturnStatement) )
								{
									breakOccured = true;
								}
								else if (lstStatement[i] instanceof IASTCompoundStatement)
								{
									caseHasStatements = true;
									final IASTCompoundStatement compoundStatement = (IASTCompoundStatement)lstStatement[i];
									final IASTStatement[] stmtList = compoundStatement.getStatements();
									for (IASTStatement stmt : stmtList)
									{
										if ( (stmt instanceof IASTBreakStatement) ||(stmt instanceof IASTReturnStatement) )
										{
											breakOccured = true;											
										}
									}
								}			                  
			                    else
			                    {
			                    	caseHasStatements = true;	
			                    }

							}
							
							
							if ((currentCaseStatement != null)
									&& caseHasStatements
									&& !breakOccured)
							{
								final int prevLoc = lstStatement[lstStatement.length-1]
								                         .getFileLocation().getEndingLineNumber();
								final int curLoc = switchBody.getFileLocation().getEndingLineNumber();
								checkFallThroughComment(prevLoc, curLoc, currentCaseStatement);
								
								currentCaseStatement = null;	
								caseHasStatements = false;	
							}
							
							return PROCESS_SKIP;
						}
						
						return PROCESS_CONTINUE;
					}
					

					private void checkFallThroughComment(int previousLocation,
							int currentLocation, IASTStatement currentCaseStatement) 
					{

						if (currentCaseStatement == null)
			            {
			                return;
			            }

			            boolean fallThroughComment = false;
			            for (IASTComment comment : commentList)
			            {
			                    String commentString = comment.toString();
			                    commentString = commentString.replaceAll("(\\r|\\n)", " ");	
			                    boolean isMatching =false;
			                    for(int i=0;i<regexStrings.length;i++)
			                    {
			                    	if(commentString.matches(regexStrings[i]))
			                    	{
			                    		isMatching =true;
			                    		break;
			                    	}
			                    }
			                    
			                    if ((comment.getFileLocation().getStartingLineNumber() >= previousLocation) && (comment.getFileLocation().getEndingLineNumber() <= currentLocation) && (isMatching)) 
			                    {
			                        fallThroughComment = true;
			                        break;
			                    }

			            }
			           									
						if (!fallThroughComment)
						{		
							fillDefectData( config,
									result,  checker,
									currentCaseStatement.getFileLocation(),  checker.getDescription(), "");
						}					
						
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
