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
import org.eclipse.cdt.core.dom.ast.IASTArrayDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTArrayModifier;
import org.eclipse.cdt.core.dom.ast.IASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTDoStatement;
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
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTWhileStatement;
import org.eclipse.cdt.core.dom.ast.IBinding;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.checker.Checker;
import com.samsung.sec.dexter.core.defect.PreOccurence;
import com.samsung.sec.dexter.vdcpp.plugin.DexterVdCppPlugin;
import com.samsung.sec.dexter.vdcpp.util.CppUtil;


public class ArrayIndexOutOfBoundsCheckerLogic implements ICheckerLogic{

	private IASTTranslationUnit translationUnit;
	private final static Logger LOG = Logger.getLogger(DexterVdCppPlugin.class);
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

				//int linNO =ast.getFileLocation().getStartingLineNumber();
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

					public int visit(IASTDeclarator declarator ) 					
					{
						//int lineNo =declarator.getFileLocation().getStartingLineNumber();
						if(declarator instanceof IASTArrayDeclarator)
						{

							IASTArrayModifier[] arrayModifier =((IASTArrayDeclarator) declarator).getArrayModifiers();
							IASTName name =declarator.getName();							

							if(arrayModifier.length ==1) //Added Check for One D array
							{
								IASTExpression constExpression =arrayModifier[0].getConstantExpression();							

								IASTInitializer initializer =declarator.getInitializer();
								int initcount =0;
								IASTInitializerClause  initclause;
								if(initializer instanceof IASTEqualsInitializer)
								{
									initclause =((IASTEqualsInitializer) initializer).getInitializerClause();														
									initcount =initclause.getChildren().length;
								}

								if((constExpression !=null  && (constExpression instanceof IASTLiteralExpression))|| initcount >0)
								{
									visitConstantLiteralExpression(config,
											result, checker, name,
											constExpression, initcount);
								}
							}

						}

						return ASTVisitor.PROCESS_CONTINUE;
					}

				};
				visitor.shouldVisitDeclarators = true; 					
				ast.accept(visitor);

			}

			private void visitConstantLiteralExpression(
					final AnalysisConfig config,
					final AnalysisResult result, final Checker checker,
					IASTName name, IASTExpression constExpression,
					int initcount) {
				int constExpValue=0;
				if(constExpression !=null  && (constExpression instanceof IASTLiteralExpression))
				{
					constExpValue =convertIntoDecimalNumber(constExpression.toString());
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
							visitArraySubscriptionExpression(config, result,
									checker, constExpValue, nodes);

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
								int operandValue =convertIntoDecimalNumber(operand2.toString());																	
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

			private void visitArraySubscriptionExpression(
					final AnalysisConfig config, final AnalysisResult result,
					final Checker checker, int constExpValue, IASTNode nodes) {
				IASTInitializerClause initClause =((IASTArraySubscriptExpression) nodes).getArgument();
				if(initClause instanceof IASTIdExpression)
				{
					visitIdExpression(config, result, checker,
							constExpValue, nodes, initClause);

				}
				else if(initClause instanceof  IASTLiteralExpression)
				{
					int operandValue =convertIntoDecimalNumber(initClause.toString());												
					if(operandValue >=constExpValue)
					{
						fillDefectData( config, result,checker,nodes.getFileLocation(),checker.getDescription(),initClause.toString());

					}
				}				
				else if(initClause instanceof IASTUnaryExpression)
				{
					IASTExpression operand =((IASTUnaryExpression) initClause).getOperand();					
					if(operand instanceof IASTIdExpression)
					{
						visitIdExpression(config, result, checker,
								constExpValue, nodes, operand);

					}
				}
				else
				{
					//Extra Test case not able to cover
				}
			}

			private void visitIdExpression(final AnalysisConfig config,
					final AnalysisResult result, final Checker checker,
					int constExpValue, IASTNode nodes,
					IASTInitializerClause initClause) {
				IASTName expName =((IASTIdExpression) initClause).getName();
				final IBinding bindingIDExp = expName.resolveBinding();

				if ((bindingIDExp != null) )
				{
					final IASTName[] idExpReferences = translationUnit.getReferences(bindingIDExp);
					final IASTName[]  expDefinitions= translationUnit.getDefinitionsInAST(bindingIDExp);

					visitIdExpressionReference(config, result, checker,
							constExpValue, nodes, expName, idExpReferences,
							expDefinitions);
				}
			}

			private void visitIdExpressionReference(
					final AnalysisConfig config, final AnalysisResult result,
					final Checker checker, int constExpValue, IASTNode nodes,
					IASTName expName, final IASTName[] idExpReferences,
					final IASTName[] expDefinitions) {
				for (IASTName idExpReferenc : idExpReferences)
				{
					//int line=idExpReferenc.getFileLocation().getStartingLineNumber();
					IASTNode parentNode = searchForStatementNode(idExpReferenc);

					if(parentNode !=null && ((parentNode instanceof IASTForStatement) ||(parentNode instanceof IASTWhileStatement)
							||(parentNode instanceof IASTDoStatement)))
					{
						IASTNode[] iastnodes =parentNode.getChildren();
						for (IASTNode iastNode : iastnodes) 
						{
							if(iastNode instanceof IASTBinaryExpression)
							{																
								IASTInitializerClause operand2 =((IASTBinaryExpression) iastNode).getInitOperand2();
								IASTExpression  expOperand1 =((IASTBinaryExpression) iastNode).getOperand1();
								int operator =((IASTBinaryExpression) iastNode).getOperator();
								if(expOperand1.toString().equals(expName.toString()))
								{										
									if(operand2 instanceof IASTFieldReference )
									{
										String operand =((IASTFieldReference) operand2).getFieldName().toString();
										if(operand.equals("length") && (operator == 10 ||operator == 11 || operator == 28) )
										{
											fillDefectData( config, result,checker,nodes.getFileLocation(),checker.getDescription(),operand2.toString());
											return;
										}
									}																
									else if(operand2 instanceof IASTLiteralExpression)
									{
										int operandValue =convertIntoDecimalNumber(operand2.toString());																	
										if((operator == 8 ||operator == 9 ))
										{
											operandValue =operandValue-1;
										}

										if(operandValue >=constExpValue)
										{
											fillDefectData( config, result,checker,nodes.getFileLocation(),checker.getDescription(),operand2.toString());
											return;
										}
									}
								}

							}

						}

					}
					else
					{

						IASTInitializerClause initclause = getInitializerExpression(expDefinitions);
						if(initclause instanceof IASTLiteralExpression)
						{
							int operandValue =convertIntoDecimalNumber(initclause.toString());							
							if(operandValue >=constExpValue)
							{
								fillDefectData( config, result,checker,nodes.getFileLocation(),checker.getDescription(),initclause.toString());
								return;
							}
						}

					}
				}
			}

			private IASTInitializerClause getInitializerExpression(
					final IASTName[] expDefinitions) {
				IASTInitializerClause initclause =null;
				IASTInitializer  init =null;
				for (IASTName iastName : expDefinitions) 
				{
					IASTNode parent =iastName.getParent();
					if(parent instanceof IASTDeclarator)
					{
						init =((IASTDeclarator) parent).getInitializer();
						if(init instanceof IASTEqualsInitializer)
						{
							initclause =((IASTEqualsInitializer) init).getInitializerClause();										
						}
					}
				}
				return initclause;
			}

			private IASTNode searchForStatementNode(IASTName idExpReferenc) {
				IASTNode parentNode =idExpReferenc.getParent();
				while(parentNode !=null )
				{
					parentNode =parentNode.getParent();
					if((parentNode instanceof IASTForStatement)||(parentNode instanceof IASTFunctionDefinition)
							||(parentNode instanceof IASTWhileStatement)
							||(parentNode instanceof IASTDoStatement)
							||(parentNode instanceof IASTTranslationUnit))
					{
						break;
					}
				}
				return parentNode;
			}

			private Integer convertIntoDecimalNumber(String string) 
			{				
				Integer outputDecimal=0;				
				try
				{
					if(string.contains("x") ||string.contains("X") )
					{
						int index =-1;
						if(string.contains("x"))
						{
							index =string.indexOf("x")+1;
						}
						else if(string.contains("X"))
						{
							index =string.indexOf("X")+1;
						}
						string =string.substring(index);
						outputDecimal = Integer.parseInt(string, 16);
					}
					else if(string.contains("o") ||string.contains("O"))
					{
						int index =-1;
						if(string.contains("o"))
						{
							index =string.indexOf("o")+1;
						}
						else if(string.contains("O"))
						{
							index =string.indexOf("O")+1;
						}
						string =string.substring(index);
						outputDecimal = Integer.parseInt(string, 8);
					}
					else if(string.contains( "'0'") ||string.contains("'\0'")||string.contains( "\"0\"") ||string.contains("\"\0\""))
					{
						outputDecimal =0;
					}
					else 
					{
						outputDecimal =Integer.valueOf(string);	
					}
				}
				catch(NumberFormatException ex)
				{
					if(string.matches("-?[0-9a-fA-F]+"))
					{
						outputDecimal = Integer.parseInt(string, 16);
					}
					if(LOG !=null)
					{
						LOG.error("Invalid Input Number"); 
					}
				}
				catch(Exception ex)
				{
					if(LOG !=null)
					{
						LOG.error(ex.getMessage()); 
					}					
				}

				return outputDecimal;						
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
