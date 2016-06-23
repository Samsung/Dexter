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
import com.samsung.sec.dexter.core.checker.Checker;
import com.samsung.sec.dexter.vdcpp.plugin.DexterVdCppPlugin;
import com.samsung.sec.dexter.vdcpp.util.CDTASTUtil;


public class BitShiftCheckerLogic implements ICheckerLogic{

	private IASTTranslationUnit translationUnit;
	private final static Logger LOG = Logger.getLogger(DexterVdCppPlugin.class);

	@Override
	public void analyze(final AnalysisConfig config, final AnalysisResult result, 
			final Checker checker, final IASTTranslationUnit unit) {
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
				
				visitFunction(config, result, checker, ast);
				
				return super.visit(ast);
			}

			private void visitFunction(final AnalysisConfig config,
					final AnalysisResult result, final Checker checker,
					final IASTDeclaration ast) {				
				if(!CDTASTUtil.isFunctionDefinition(ast))	return;
				
				ASTVisitor visitor = new ASTVisitor() {
					public int visit(IASTExpression expression ) {
						
						visitLeftBitShiftBinaryExpression(config, result, checker, expression);
								
						return ASTVisitor.PROCESS_CONTINUE;
					}

					private void visitLeftBitShiftBinaryExpression(
							final AnalysisConfig config,
							final AnalysisResult result, final Checker checker,
							IASTExpression expression) {	
						
						if(!CDTASTUtil.isLeftBitShiftBinaryExpression(expression))
							return;
					
						if(!is64BitShiftOverflowed(expression))
							return;
										
						String msg ="The assignment to long long variable by bit shift constant without a suffix(LL|ULL) might overflow. It needed to specify the constant with a suffix. ex) 1LL|1ULL";
						CDTASTUtil.fillDefectData( config, result,checker, translationUnit, expression.getFileLocation(),msg,expression.toString());											
					}	
					
					private boolean is64BitShiftOverflowed(IASTExpression expression){							
						if(!isAssignedToLongLong(expression))
							return false;
						
						IASTExpression operand1Expression =((IASTBinaryExpression) expression).getOperand1();
						//IASTExpression operand2Expression =((IASTBinaryExpression) expression).getOperand2();
						String valueOfOperand1=CDTASTUtil.getValueOfExpression(operand1Expression);
						//String valueOfOperand2=getValueOfExpression(operand2Expression);
						
						if(valueOfOperand1.isEmpty())
							return false;
						
						//TODO: calculate if actual overflow occurred	
						if(CDTASTUtil.isContainsLongLongSuffix(valueOfOperand1))
							return false;
						
						return true;
					}
					
					private boolean isAssignedToLongLong(IASTExpression assignedFrom){
						IASTNode assignedTo = CDTASTUtil.getAssignedTo((IASTNode)assignedFrom);
						if(assignedTo==null)	return false;
						
						return CDTASTUtil.isLongLong(assignedTo);
					}			
				};
				
				visitor.shouldVisitExpressions = true; 					
				ast.accept(visitor);
			}				
		};

		return visitor;
	}
}
