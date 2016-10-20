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

import org.apache.log4j.Logger;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IVariable;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.IBasicType;
import org.eclipse.cdt.core.dom.ast.IValue;
import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;

import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.checker.IChecker;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.vdcpp.plugin.DexterVdCppPlugin;
import com.samsung.sec.dexter.vdcpp.util.CDTASTUtil;


public class SignExtension2CheckerLogic implements ICheckerLogic{

	private IASTTranslationUnit translationUnit;
	private final static Logger LOG = Logger.getLogger(DexterVdCppPlugin.class);

	@Override
	public void analyze(final AnalysisConfig config, final AnalysisResult result, 
			final IChecker checker, final IASTTranslationUnit unit) {
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
				
				visitFunction(config, result, checker, ast);
				
				return super.visit(ast);
			}

			private void visitFunction(final AnalysisConfig config,
					final AnalysisResult result, final IChecker checker,
					final IASTDeclaration ast) {				
				if(!CDTASTUtil.isFunctionDefinition(ast))	return;
				
				ASTVisitor visitor = new ASTVisitor() {
					public int visit(IASTExpression expression ) {
						
						visitBinaryExpression(config, result, checker, expression);
								
						return ASTVisitor.PROCESS_CONTINUE;
					}

					private void visitBinaryExpression(
							final AnalysisConfig config,
							final AnalysisResult result, final IChecker checker,
							IASTExpression expression) {	
						
						if(!CDTASTUtil.isLeftBitShiftBinaryExpression(expression))
							return;
					
						if(!isSignExtension(expression))
							return;
										
						String msg ="A Signed data type is transformed into a larger Unsigned data type. This can produce unexpected values.";
						CDTASTUtil.fillDefectData( config, result,checker, translationUnit, expression.getFileLocation(),msg,expression.toString());											
					}	
					
					private boolean isSignExtension(IASTExpression expression){			
						if(!isAssignedToLongOrLongLong(expression))
							return false;
						
						IASTExpression operand1Expression =((IASTBinaryExpression) expression).getOperand1();						
						if(!( CDTASTUtil.checkVariableType((IASTNode)operand1Expression, "(char|short)")))
								return false;
							
						//TODO: calculate if actual sign extension occurred
						IASTExpression operand2Expression =((IASTBinaryExpression) expression).getOperand2();
						String valueOfOperand1=CDTASTUtil.getValueOfExpression(operand2Expression);
						if(valueOfOperand1.isEmpty())
							return false;
						
						try
						{
							int value =CDTASTUtil.convertIntoDecimalNumber(valueOfOperand1);
							return (value>30);
						}
						catch (DexterRuntimeException e) {
							LOG.error(e.getMessage(), e);
						}
						
						return false;
					}
					
					private boolean isAssignedToLongOrLongLong(IASTExpression assignedFrom){
						IASTNode assignedTo = CDTASTUtil.getAssignedTo((IASTNode)assignedFrom);
						if(assignedTo==null)	return false;
						
						return CDTASTUtil.isLong(assignedTo) || CDTASTUtil.isLongLong(assignedTo);
					}	
				};
				
				visitor.shouldVisitExpressions = true; 					
				ast.accept(visitor);
			}				
		};

		return visitor;
	}
}
