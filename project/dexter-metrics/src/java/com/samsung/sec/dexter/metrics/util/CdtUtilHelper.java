/**
 * Copyright (c) 2014 Samsung Electronics, Inc.,
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
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
package com.samsung.sec.dexter.metrics.util;

import com.samsung.sec.dexter.core.analyzer.ResultFileConstant;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.cdt.core.dom.ast.IASTCaseStatement;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTDoStatement;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTSwitchStatement;
import org.eclipse.cdt.core.dom.ast.IASTWhileStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTCatchHandler;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTTryBlockStatement;

public class CdtUtilHelper {
    static int count = 0;
    static int sloc = 0;
    static int methodCount = 0;
    static int classCount = 0;
    final static Map<String, Integer> mapSourceMatrices = new HashMap<String, Integer>();
    final static Map<String, Integer> mapFunctionLocMetrices = new HashMap<String, Integer>();
    public final static String LINE_SEPARATOR = System.getProperty("line.separator");
    final private static Map<String, String> mapModuleName = new HashMap<String, String>();
    static Logger logger = Logger.getLogger(CdtUtilHelper.class);

    static Map<String, String> getMapData() {
        if (mapModuleName.isEmpty()) {
            mapModuleName.put(ResultFileConstant.CLASS_NAME, "");
            mapModuleName.put(ResultFileConstant.METHOD_NAME, "");
        }
        return mapModuleName;
    }

    private CdtUtilHelper() {

    }

    /**
     * visitFunction(IASTDeclaration declaration , int lineNo, String
     * fileExtension) method is responsible for visit functional statement
     * 
     * @param [in]
     * IASTDeclaration declaration , int lineNo, String
     * fileExtension
     * @return [out] boolean
     * @warning [None]
     * @exception IO
     * exception
     */
    static boolean visitFunction(IASTDeclaration declaration, int lineNo,
            String fileExtension) {
        boolean visitStatus = false;

        if ((declaration instanceof IASTSimpleDeclaration)) {
            final IASTSimpleDeclaration simple_declaration = (IASTSimpleDeclaration) declaration;
            String className = "";
            String MethodName = "";

            final IASTDeclarator[] declaratorList = simple_declaration
                    .getDeclarators();
            for (IASTDeclarator declarator : declaratorList) {
                if (declarator instanceof IASTFunctionDeclarator) {

                    if (simple_declaration.getDeclSpecifier().getStorageClass() == IASTDeclSpecifier.sc_typedef) {
                        return visitStatus;
                    }

                    if (simple_declaration.getFileLocation().getFileName()
                            .contains(".h")) {
                        if (fileExtension.equals(".cpp")
                                || fileExtension.equals(".c")) {
                            return visitStatus;
                        }
                    }

                    final int FunctionStartingLineNumber = declarator
                            .getFileLocation().getStartingLineNumber();
                    final int FunctionEndLineNumber = declaration
                            .getFileLocation().getEndingLineNumber();

                    if (FunctionStartingLineNumber > lineNo) {
                        visitStatus = true;
                        putDataInToMap(className, "");
                    }
                    IASTNode node1 = declarator.getParent().getParent();

                    if (node1 instanceof CPPASTCompositeTypeSpecifier) {
                        className = ((CPPASTCompositeTypeSpecifier) node1)
                                .getName().toString();
                    }

                    MethodName = declarator.getName().toString();

                    if (fileExtension.equals(".c")) {
                        className = "";
                    }
                    if (FunctionStartingLineNumber <= lineNo
                            && lineNo <= FunctionEndLineNumber) {
                        visitStatus = true;
                        putDataInToMap(className, MethodName);
                    }
                }
            }
        } else if (declaration instanceof IASTFunctionDefinition) {
            final IASTFunctionDefinition Func_Definition = (IASTFunctionDefinition) declaration;
            final IASTFunctionDeclarator Func_Declarator = Func_Definition
                    .getDeclarator();

            final int FunctionStartingLineNumber = declaration
                    .getFileLocation().getStartingLineNumber();
            final int FunctionEndLineNumber = declaration.getFileLocation()
                    .getEndingLineNumber();
            String className = "";
            String MethodName = "";

            IASTNode node = Func_Definition.getParent();

            if (node instanceof CPPASTCompositeTypeSpecifier) {
                className = ((CPPASTCompositeTypeSpecifier) node).getName()
                        .toString();
            }

            if (Func_Definition.getDeclSpecifier().getStorageClass() == IASTDeclSpecifier.sc_typedef) {
                return visitStatus;
            }

            if (Func_Definition.getFileLocation().getFileName().contains(".h")) {
                if (fileExtension.equals(".cpp") || fileExtension.equals(".c")) {
                    return visitStatus;
                }
            }

            MethodName = Func_Declarator.getName().toString();

            if (FunctionStartingLineNumber > lineNo) {
                visitStatus = true;
                putDataInToMap(className, "");
            }

            if (MethodName.contains("::")) {
                int indexofColon = MethodName.indexOf("::");
                className = MethodName.substring(0, indexofColon);
                MethodName = MethodName.substring(indexofColon + 2);
            }

            if (FunctionStartingLineNumber <= lineNo
                    && lineNo <= FunctionEndLineNumber) {
                visitStatus = true;
                putDataInToMap(className, MethodName);

            }

        }

        return visitStatus;

    }

    private static void putDataInToMap(String className, String MethodName) {

        mapModuleName.clear();
        mapModuleName.put(ResultFileConstant.CLASS_NAME, className);
        mapModuleName.put(ResultFileConstant.METHOD_NAME, MethodName);
    }

    /**
     * visitSourceCodeforCalMethodAndClassCount(IASTDeclaration declaration )
     * method is responsible for visit functional statement
     * 
     * @param [in]
     * IASTDeclaration declaration
     * @return [out] boolean
     * @warning [None]
     * @exception IO
     * exception
     */
    static boolean visitSourceCodeforCalMethodAndClassCount(
            IASTDeclaration declaration) {
        // String declarationString ="";
        if ((declaration instanceof IASTSimpleDeclaration)) {
            final IASTSimpleDeclaration simple_declaration = (IASTSimpleDeclaration) declaration;
            IASTDeclSpecifier decSpecifier = simple_declaration
                    .getDeclSpecifier();
            if ((decSpecifier instanceof CPPASTCompositeTypeSpecifier)) {
                classCount++;
                CPPASTCompositeTypeSpecifier cppComptypeSpecifier = ((CPPASTCompositeTypeSpecifier) decSpecifier);
                IASTDeclaration[] astDeclarationList = cppComptypeSpecifier
                        .getMembers();

                for (IASTDeclaration declaration1 : astDeclarationList) {
                    if (declaration1 instanceof IASTSimpleDeclaration) {
                        final IASTDeclarator[] declaratorList = ((IASTSimpleDeclaration) declaration1)
                                .getDeclarators();
                        for (IASTDeclarator declarator : declaratorList) {
                            if (declarator instanceof IASTFunctionDeclarator) {
                                methodCount++;

                            }
                        }
                    }
                }

            }
        } else if ((declaration instanceof IASTFunctionDefinition)) {
            methodCount++;
        }
        return false;
    }

    /**
     * visitSourceCodeforCalFileComplexity(IASTDeclaration declaration) method
     * is responsible for visit functional statement
     * 
     * @param [in]
     * IASTDeclaration declaration
     * @return [out] boolean
     * @warning [None]
     * @exception IO
     * exception
     */
    static boolean visitSourceCodeforCalFileComplexity(
            IASTDeclaration declaration) {
        String MethodName = "";
        count = 0;
        if (declaration instanceof IASTFunctionDefinition) {
            final IASTFunctionDefinition Func_Definition = (IASTFunctionDefinition) declaration;
            final IASTFunctionDeclarator Func_Declarator = Func_Definition
                    .getDeclarator();
            IASTStatement iastStatement = ((IASTFunctionDefinition) declaration)
                    .getBody();
            MethodName = Func_Declarator.getName().toString();
            visitCompoundStatement(iastStatement);
            int complexity = count + 1;
            mapSourceMatrices.put(MethodName, complexity);

        }

        return false;
    }

    /**
     * visitCompoundStatement(IASTStatement iastStatement) method is responsible
     * for visit functional statement
     * 
     * @param [in]
     * IASTStatement iastStatement
     * @return [out] void
     * @warning [None]
     * @exception IO
     * exception
     */
    private static void visitCompoundStatement(IASTStatement iastStatement) {
        if (iastStatement instanceof IASTCompoundStatement) {
            IASTStatement[] arrayStatement = ((IASTCompoundStatement) iastStatement)
                    .getStatements();
            for (IASTStatement statement : arrayStatement) {

                if ((statement instanceof IASTIfStatement)
                        || (statement instanceof IASTForStatement)
                        || (statement instanceof IASTSwitchStatement)
                        || (statement instanceof IASTWhileStatement)
                        || (statement instanceof IASTCaseStatement)
                        || (statement instanceof IASTDoStatement)
                        || (statement instanceof IASTCompoundStatement)) {

                    count++;
                    if (statement instanceof IASTIfStatement) {
                        final IASTIfStatement if_statement = (IASTIfStatement) statement;
                        visitElseIfStatement(if_statement);
                    } else if (statement instanceof IASTForStatement) {
                        final IASTForStatement for_statement = (IASTForStatement) statement;
                        final IASTStatement for_body = for_statement.getBody();
                        if (for_body instanceof IASTCompoundStatement) {
                            visitCompoundStatement(for_body);
                        }

                    } else if (statement instanceof IASTWhileStatement) {
                        final IASTWhileStatement while_statement = (IASTWhileStatement) statement;
                        final IASTStatement while_body = while_statement
                                .getBody();
                        if (while_body instanceof IASTCompoundStatement) {
                            visitCompoundStatement(while_body);
                        }

                    } else if (statement instanceof IASTDoStatement) {
                        final IASTDoStatement do_statement = (IASTDoStatement) statement;
                        final IASTStatement do_body = do_statement.getBody();
                        if (do_body instanceof IASTCompoundStatement) {
                            visitCompoundStatement(do_body);
                        }
                    } else if (statement instanceof IASTSwitchStatement) {
                        final IASTSwitchStatement switch_statement = (IASTSwitchStatement) statement;

                        final IASTStatement switch_body = switch_statement
                                .getBody();

                        if ((switch_body instanceof IASTCompoundStatement)) {
                            visitCompoundStatement(switch_body);
                        }
                    }

                }

            }

        } else if (iastStatement instanceof CPPASTTryBlockStatement) {
            count++;
            final CPPASTTryBlockStatement try_statement = (CPPASTTryBlockStatement) iastStatement;
            IASTStatement astStatement = try_statement.getTryBody();
            if (astStatement instanceof IASTCompoundStatement) {
                visitCompoundStatement(astStatement);
            }

        } else if (iastStatement instanceof CPPASTCatchHandler) {
            count++;
            final CPPASTCatchHandler try_statement = (CPPASTCatchHandler) iastStatement;
            IASTStatement astStatement = try_statement.getCatchBody();
            if (astStatement instanceof IASTCompoundStatement) {
                visitCompoundStatement(astStatement);
            }

        }
    }

    /**
     * @param if_statement
     */
    private static void visitElseIfStatement(final IASTIfStatement if_statement) {
        final IASTStatement else_clause = if_statement.getElseClause();
        final IASTStatement than_clause = if_statement.getThenClause();
        if (than_clause != null) {
            // count++;
            if (than_clause instanceof IASTCompoundStatement) {

                visitCompoundStatement(than_clause);
            } else if (than_clause instanceof IASTIfStatement) {
                count++;
                visitElseIfStatement((IASTIfStatement) than_clause);
            }
        }

        if (else_clause != null) {
            // count++;
            if (else_clause instanceof IASTCompoundStatement) {

                visitCompoundStatement(else_clause);
            } else if (else_clause instanceof IASTIfStatement) {
                count++;
                visitElseIfStatement((IASTIfStatement) else_clause);
            }
        }
    }

    public static boolean visitSourceCodeforSloc(IASTDeclaration declaration, final String sourcePath) {
        String MethodName = "";
        int functionSloc = 0;
        if (declaration instanceof IASTFunctionDefinition) {
            final IASTFunctionDefinition Func_Definition = (IASTFunctionDefinition) declaration;
            final IASTFunctionDeclarator Func_Declarator = Func_Definition.getDeclarator();
            MethodName = Func_Declarator.getName().toString();

            IASTStatement iastStatement = ((IASTFunctionDefinition) declaration).getBody();
            functionSloc = visitSloc(iastStatement, sourcePath);

            mapFunctionLocMetrices.put(MethodName, functionSloc);
        }
        return false;
    }

    private static int visitSloc(IASTStatement iastStatement, final String sourcePath) {
        final int startLineNumber = iastStatement.getFileLocation().getStartingLineNumber();
        final int endLineNumber = iastStatement.getFileLocation().getEndingLineNumber();

        sloc = MetricUtil.getFunctionLOCArray(sourcePath, startLineNumber, endLineNumber);
        return sloc;
    }
}
