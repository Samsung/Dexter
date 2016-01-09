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
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
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


public class TwoDArrayIndexOutOfBoundCheckerLogic implements ICheckerLogic{

	private IASTTranslationUnit translationUnit;
	private final static Logger LOG = Logger.getLogger(DexterVdCppPlugin.class);
	//Dictionary for storing array size initialization value.
	private Dictionary<String, int[]> dictionary = new Hashtable<String, int[]>();
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
					dictionary.remove(dictionary.keys());
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
							IASTArrayModifier[] arrayModifier =((IASTArrayDeclarator) declarator).getArrayModifiers();
							IASTName arrayDeclaratorName =declarator.getName();
							int[] arrayLength =new int[arrayModifier.length];

							int initCount =0;//initCount store initialize element count.
							int constExpValue=0;

							if(arrayModifier.length <=1) //Check for 1D pointer array that point to 2D array.
							{
								check1DPointerArrayThatPointTo2DArray(config,
										result, checker, declarator, arrayDeclaratorName,
										arrayLength, initCount, constExpValue);

							}							
							else if(arrayModifier.length>1)//Check other 2d array statements
							{
								checkOther2DArrayStatements(config, result,
										checker, declarator, arrayModifier,
										arrayDeclaratorName, arrayLength,
										initCount, constExpValue);


							}

						}

						return ASTVisitor.PROCESS_CONTINUE;
					}
				};

				visitor.shouldVisitDeclarators = true; 					
				ast.accept(visitor);

			}
			/**
			 * checkOther2DArrayStatements()
			 * checking all 2D array initialization block 
			 * 
			 * @param     	[in] final AnalysisConfig config,
							final AnalysisResult result, final Checker checker,
							IASTDeclarator declarator,
							IASTArrayModifier[] arrayModifier,
							IASTName arrayDeclaratorName, int[] arrayLength,
							int initCount, int constExpValue
			 * @return		[None]
			 * @warning		[None]
			 * @exception	[None]
			 */					
			private void checkOther2DArrayStatements(
					final AnalysisConfig config,
					final AnalysisResult result, final Checker checker,
					IASTDeclarator declarator,
					IASTArrayModifier[] arrayModifier,
					IASTName arrayDeclaratorName, int[] arrayLength,
					int initCount, int constExpValue) {
				for(int i=0;i<arrayModifier.length;i++)							
				{
					IASTExpression constExpression =arrayModifier[i].getConstantExpression();
					if(constExpression !=null && (constExpression instanceof IASTLiteralExpression))
					{
						arrayLength[i]=convertIntoDecimalNumber(constExpression.toString());
					}
					else if(constExpression !=null && (constExpression instanceof IASTIdExpression))
					{
						//Need to add test case if possible
					}
					else
					{
						//Do Nothing
					}

				}

				IASTInitializer initializer =declarator.getInitializer();								
				IASTInitializerClause initClause =null; 
				if(initializer instanceof IASTEqualsInitializer)
				{
					initClause = ((IASTEqualsInitializer) initializer).getInitializerClause();
					initCount =((IASTEqualsInitializer) initializer).getInitializerClause().getChildren().length;

				}

				if(arrayDeclaratorName.toString().equals("") )
				{
					boolean isDictonaryContainsInitClause =(initClause !=null && dictionary.get(initClause.toString()) !=null);
					if(isDictonaryContainsInitClause)
					{
						arrayLength =(int[]) dictionary.get(initClause.toString());									
					}
				}
				else
				{
					dictionary.put(arrayDeclaratorName.toString(), arrayLength);
				}

				check2DArrayStatement(config, result, checker,
						arrayDeclaratorName, arrayLength, initCount,
						constExpValue);
			}

			/**
			 * check1DPointerArrayThatPointTo2DArray()
			 * Check 1D pointer array that point to 2D array initialization block 
			 * 
			 * @param     	[in] final AnalysisConfig config,
							final AnalysisResult result, final Checker checker,
							IASTDeclarator declarator, IASTName name,
							int[] arrayLength, int initCount, int constExpValue
			 * @return		[None]
			 * @warning		[None]
			 * @exception	[None]
			 */	
			private void check1DPointerArrayThatPointTo2DArray(
					final AnalysisConfig config,
					final AnalysisResult result, final Checker checker,
					IASTDeclarator declarator, IASTName name,
					int[] arrayLength, int initCount, int constExpValue) {
				IASTInitializer initializer =declarator.getInitializer();								
				IASTInitializerClause  initClause;

				if(initializer instanceof IASTEqualsInitializer)
				{
					initClause =((IASTEqualsInitializer) initializer).getInitializerClause();
					String initClauseName =initClause.getRawSignature();
					boolean IsDictonaryContainsInitCaluse =(dictionary.get(initClauseName) != null);
					if(IsDictonaryContainsInitCaluse)
					{
						arrayLength =dictionary.get(initClause.getRawSignature());
						initCount =initClause.getChildren().length;
					}


				}
				check2DArrayStatement(config, result, checker,
						name, arrayLength, initCount,
						constExpValue);


			}

			/**
			 * Check2DArrayStatement()
			 * check all array statement used inside function 
			 * 
			 * @param     	[in] final AnalysisConfig config,
							final AnalysisResult result, final Checker checker,
							IASTName name, int[] arrayLength, int initCount,
							int constExpValue
			 * @return		[None]
			 * @warning		[None]
			 * @exception	[None]
			 */					
			private void check2DArrayStatement(
					final AnalysisConfig config,
					final AnalysisResult result, final Checker checker,
					IASTName name, int[] arrayLength, int initCount,
					int constExpValue) {

				if(arrayLength.length >1  || initCount >0)
				{
					final IBinding binding = name.resolveBinding();
					if ((binding != null) )
					{
						final IASTName[] references = translationUnit.getReferences(binding);
						for (IASTName reference : references)
						{
							IASTNode parentNode =reference.getParent().getParent();
							List<IASTInitializerClause> initClauseList =new ArrayList<IASTInitializerClause>();									
							if(parentNode instanceof IASTArraySubscriptExpression)										
							{
								constExpValue = visitArraySubscriptExpression(
										config, result, checker,
										arrayLength, constExpValue,
										parentNode, initClauseList);
							}
							else if(parentNode instanceof IASTBinaryExpression)
							{
								visitBinaryArrayExpression(config,
										result, checker, constExpValue,
										parentNode);


							}
							else
							{
								//Do Nothing
							}

						}
					}
				}


			}

			private int visitArraySubscriptExpression(
					final AnalysisConfig config,
					final AnalysisResult result, final Checker checker,
					int[] arrayLength, int constExpValue,
					IASTNode parentnode,
					List<IASTInitializerClause> initClauseList) {

				//int lineNo =parentnode.getFileLocation().getStartingLineNumber();
				parentnode = visitAllArrayInitializerClause(parentnode,
						initClauseList);


				Object[] initArray =initClauseList.toArray();											
				for(int i=0;i<initArray.length;i++)											
				{
					IASTInitializerClause initClause =(IASTInitializerClause)initArray[i];

					if(initClause instanceof IASTIdExpression)
					{
						constExpValue =arrayLength[i];	
						if(visitInitClauseThatIsTypeOfIDExpression(
								config, result,
								checker, constExpValue,
								parentnode, initClause))
							break;

					}
					else if(initClause instanceof  IASTLiteralExpression)
					{
						constExpValue =arrayLength[i];	
						if(visitInitClauseThatIsTypeOfLiteralExpression(
								config, result, checker, constExpValue,
								parentnode, initClause))
							break;
					}
					else
					{
						//Do Nothing
					}


				}

				return constExpValue;
			}

			private boolean visitInitClauseThatIsTypeOfLiteralExpression(
					final AnalysisConfig config,
					final AnalysisResult result, final Checker checker,
					int constExpValue, IASTNode node,
					IASTInitializerClause initClause) {
				int operandValue =convertIntoDecimalNumber(initClause.toString());												
				if(operandValue >=constExpValue)
				{
					fillDefectData( config, result,checker,node.getFileLocation(),checker.getDescription(),initClause.toString());
					return true;
				}
				return false;

			}

			private IASTNode visitAllArrayInitializerClause(
					IASTNode parentNode,
					List<IASTInitializerClause> lstInitClause) {

				if(parentNode instanceof IASTArraySubscriptExpression)
				{
					IASTInitializerClause initClause =((IASTArraySubscriptExpression) parentNode).getArgument();
					lstInitClause.add(initClause);
					parentNode =parentNode.getParent();
					if(parentNode instanceof IASTArraySubscriptExpression)
					{
						IASTInitializerClause initSubClause =((IASTArraySubscriptExpression) parentNode).getArgument();									
						lstInitClause.add(initSubClause);
					}

				}

				return parentNode;
			}

			private boolean visitBinaryArrayExpression(
					final AnalysisConfig config,
					final AnalysisResult result, final Checker checker,
					int constExpValue, IASTNode node) {
				IASTInitializerClause operand2 =((IASTBinaryExpression) node).getInitOperand2();	
				int operator =((IASTBinaryExpression) node).getOperator();

				if(operand2 instanceof IASTFieldReference )
				{
					if(visitFieldRefExpression(config, result, checker,
							node, operand2, operator))
					{
						return true;
					}
				}																
				else if(operand2 instanceof IASTLiteralExpression)
				{
					if(visitLitealExpression(config, result, checker,
							constExpValue, node, operand2, operator))
					{
						return true;
					}

				}
				else
				{
					//Do Nothing
				}
				return false;

			}

			private boolean visitFieldRefExpression(
					final AnalysisConfig config,
					final AnalysisResult result, final Checker checker,
					IASTNode node, IASTInitializerClause operand2,
					int operator) {
				String operand =((IASTFieldReference) operand2).getFieldName().toString();
				if(operand.equals("length") && (operator == 10 ||operator == 11 || operator == 28) )
				{
					fillDefectData( config, result,checker,node.getFileLocation(),checker.getDescription(),operand2.toString());
					return true;
				}

				return false;
			}

			private boolean visitLitealExpression(
					final AnalysisConfig config,
					final AnalysisResult result, final Checker checker,
					int constExpValue, IASTNode node,
					IASTInitializerClause operand2, int operator) {
				int operandValue =convertIntoDecimalNumber(operand2.toString());																	
				if((operator == 8 ||operator == 9 ))
				{
					operandValue =operandValue-1;
				}

				if(operandValue >=constExpValue)
				{
					fillDefectData( config, result,checker,node.getFileLocation(),checker.getDescription(),operand2.toString());
					return true;
				}
				return false;
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
			private boolean visitInitClauseThatIsTypeOfIDExpression(
					final AnalysisConfig config,
					final AnalysisResult result, final Checker checker,
					int constExpValue, IASTNode node,
					IASTInitializerClause initClause) {
				IASTName initClauseName =((IASTIdExpression) initClause).getName();
				final IBinding binding = initClauseName.resolveBinding();
				if ((binding != null) )
				{
					final IASTName[] references = translationUnit.getReferences(binding);
					for (IASTName reference : references)
					{
						IASTNode parentNode = searchForStatementNode(reference);
						if(parentNode !=null && (parentNode instanceof IASTForStatement) )
						{
							IASTNode[] childNodes =parentNode.getChildren();
							for (IASTNode childNode : childNodes) 
							{
								if(visitBinayExpressionNodes(
										config, result, checker,
										constExpValue, node, childNode))
									return true;

							}


						}

					}
				}
				return false;
			}

			private IASTNode searchForStatementNode(IASTName idExpReferenc) {
				IASTNode parentNode =idExpReferenc.getParent();
				while(parentNode !=null )
				{
					parentNode =parentNode.getParent();
					if((parentNode instanceof IASTForStatement)||(parentNode instanceof IASTFunctionDefinition)							
							||(parentNode instanceof IASTTranslationUnit))
					{
						break;
					}
				}
				return parentNode;
			}

			private boolean visitBinayExpressionNodes(
					final AnalysisConfig config,
					final AnalysisResult result, final Checker checker,
					int constExpValue, IASTNode parentNode, IASTNode childNode) {
				if(childNode instanceof IASTBinaryExpression)
				{																	

					IASTInitializerClause operand2 =((IASTBinaryExpression) childNode).getInitOperand2();
					int operator =((IASTBinaryExpression) childNode).getOperator();
					if(operand2 instanceof IASTFieldReference )
					{
						if(visitFieldRefExpression(config, result,
								checker, parentNode, operand2, operator))
						{
							return true;
						}
					}																
					else if(operand2 instanceof IASTLiteralExpression)
					{
						if(visitLitealExpression(config, result, checker,
								constExpValue, parentNode, operand2,
								operator))
						{
							return true;
						}
					}
					else
					{
						//Do Nothing
					}

				}
				return false;
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
