package com.samsung.sec.dexter.vdcpp.util;

import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.checker.Checker;
import com.samsung.sec.dexter.core.defect.PreOccurence;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.vdcpp.plugin.DexterVdCppPlugin;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.cdt.core.dom.ast.ASTTypeUtil;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTCastExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IBasicType;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IPointerType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.IVariable;

public class CDTASTUtil {
    private final static Logger LOG = Logger.getLogger(DexterVdCppPlugin.class);

    public static void fillDefectData(final AnalysisConfig config,
            final AnalysisResult result, final Checker checker, final IASTTranslationUnit translationUnit,
            final IASTFileLocation fileLocation, final String message, final String declaratorName) {

        PreOccurence preOcc = createPreOccurence(config, checker, translationUnit, fileLocation, message,
                declaratorName);
        result.addDefectWithPreOccurence(preOcc);

    }

    public static boolean isLongLong(IASTNode node) {
        IASTName name = null;
        if (node instanceof IASTIdExpression) {
            name = ((IASTIdExpression) node).getName();
        } else if (node instanceof IASTDeclarator) {
            name = ((IASTDeclarator) node).getName();
        }

        if (name == null)
            return false;
        final IBinding binding = name.resolveBinding();
        if (binding == null)
            return false;

        if (!(binding instanceof IVariable))
            return false;

        IType type = ((IVariable) binding).getType();
        return (type instanceof IBasicType && ((IBasicType) type).isLongLong());
    }

    public static boolean isLong(IASTNode node) {
        IASTName name = null;
        if (node instanceof IASTIdExpression) {
            name = ((IASTIdExpression) node).getName();
        } else if (node instanceof IASTDeclarator) {
            name = ((IASTDeclarator) node).getName();
        }

        if (name == null)
            return false;
        final IBinding binding = name.resolveBinding();
        if (binding == null)
            return false;

        if (!(binding instanceof IVariable))
            return false;

        IType type = ((IVariable) binding).getType();
        return (type instanceof IBasicType && ((IBasicType) type).isLong());
    }

    public static boolean isSigned(IASTNode node) {
        IASTName name = null;
        if (node instanceof IASTIdExpression) {
            name = ((IASTIdExpression) node).getName();
        } else if (node instanceof IASTDeclarator) {
            name = ((IASTDeclarator) node).getName();
        }

        if (name == null)
            return false;
        final IBinding binding = name.resolveBinding();
        if (binding == null)
            return false;

        if (!(binding instanceof IVariable))
            return false;

        IType type = ((IVariable) binding).getType();

        return !ASTTypeUtil.getType(type).contains("unsigned");
    }

    public static boolean isUnsigned(IASTNode node) {
        IASTName name = null;
        if (node instanceof IASTIdExpression) {
            name = ((IASTIdExpression) node).getName();
        } else if (node instanceof IASTDeclarator) {
            name = ((IASTDeclarator) node).getName();
        }

        if (name == null)
            return false;
        final IBinding binding = name.resolveBinding();
        if (binding == null)
            return false;

        if (!(binding instanceof IVariable))
            return false;

        IType type = ((IVariable) binding).getType();

        return ASTTypeUtil.getType(type).contains("unsigned");
    }

    public static boolean checkVariableType(IASTNode node, String regex) {
        IASTName name = null;
        if (node instanceof IASTIdExpression) {
            name = ((IASTIdExpression) node).getName();
        } else if (node instanceof IASTDeclarator) {
            name = ((IASTDeclarator) node).getName();
        }

        if (name == null)
            return false;
        final IBinding binding = name.resolveBinding();
        if (binding == null)
            return false;

        if (!(binding instanceof IVariable))
            return false;

        IType type = ((IVariable) binding).getType();
        return getBasicType(type).matches(regex);
    }

    public static String getBasicType(IType type) {
        String typeString = "";
        if (isCharType(type)) {
            typeString = "char";
        } else if (isShortType(type)) {
            typeString = "short";
        } else if (isIntegerType(type)) {
            typeString = "int";
        }

        return typeString;
    }

    public static boolean isCharType(IType type) {
        if (!(type instanceof IBasicType))
            return false;

        IBasicType.Kind kind = ((IBasicType) type).getKind();
        return (kind == IBasicType.Kind.eChar || kind == IBasicType.Kind.eChar16 || kind == IBasicType.Kind.eChar32);
    }

    public static boolean isShortType(IType type) {
        return (type instanceof IBasicType && ((IBasicType) type).isShort());
    }

    public static boolean isIntegerType(IType type) {
        if (!(type instanceof IBasicType))
            return false;

        IBasicType.Kind kind = ((IBasicType) type).getKind();
        return (kind == IBasicType.Kind.eInt && !((IBasicType) type).isShort() && !((IBasicType) type).isLong()
                && !((IBasicType) type).isLongLong());
    }

    public static boolean checkReturnType(IASTFunctionDefinition functionDefinition, String returnType) {
        if (!(functionDefinition instanceof IASTFunctionDefinition))
            return false;

        IASTDeclSpecifier declSpecifier = functionDefinition.getDeclSpecifier();
        if (!(declSpecifier instanceof IASTNamedTypeSpecifier))
            return false;

        String declSpecifierName = ((IASTNamedTypeSpecifier) declSpecifier).getName().toString();
        return (declSpecifierName.compareTo(returnType) == 0);
    }

    public static boolean isFunctionCallExpression(IASTExpression expression) {
        return (expression instanceof IASTFunctionCallExpression);
    }

    public static boolean isFunctionDefinition(IASTDeclaration declaration) {
        return (declaration instanceof IASTFunctionDefinition);
    }

    public static boolean isReturnStatement(IASTStatement statement) {
        return (statement instanceof IASTReturnStatement);
    }

    public static boolean isLeftBitShiftBinaryExpression(IASTExpression expression) {
        return ((expression instanceof IASTBinaryExpression) &&
                (((IASTBinaryExpression) expression).getOperator() == IASTBinaryExpression.op_shiftLeft));
    }

    public static boolean isPlusMinusBinaryExpression(IASTExpression expression) {
        return ((expression instanceof IASTBinaryExpression) &&
                ((((IASTBinaryExpression) expression).getOperator() == IASTBinaryExpression.op_plus) ||
                        (((IASTBinaryExpression) expression).getOperator() == IASTBinaryExpression.op_minus)));
    }

    public static boolean isMultiplyBinaryExpression(IASTExpression expression) {
        return ((expression instanceof IASTBinaryExpression) &&
                (((IASTBinaryExpression) expression).getOperator() == IASTBinaryExpression.op_multiply));
    }

    public static boolean isAssignBinaryExpression(IASTExpression expression) {
        return ((expression instanceof IASTBinaryExpression) &&
                (((IASTBinaryExpression) expression).getOperator() == IASTBinaryExpression.op_assign));
    }

    public static boolean isContainsLongLongSuffix(String value) {
        return (value.contains("LL") || value.contains("ll") || value.contains("ULL") || value.contains("ull"));
    }

    public static boolean checkFunctionName(IASTFunctionCallExpression expression, String functionName) {
        IASTExpression functionNameExpression = (expression).getFunctionNameExpression();
        String name = functionNameExpression.toString();

        return (name.compareTo(functionName) == 0);
    }

    public static boolean checkDimensionOfPointer(IASTNode node, int dimension) {
        IASTName name = null;
        if (node instanceof IASTIdExpression) {
            name = ((IASTIdExpression) node).getName();
        } else if (node instanceof IASTDeclarator) {
            name = ((IASTDeclarator) node).getName();
        }

        if (name == null)
            return false;
        final IBinding binding = name.resolveBinding();
        if (binding == null)
            return false;

        if (!(binding instanceof IVariable))
            return false;

        IType type = ((IVariable) binding).getType();
        if (!(type instanceof IPointerType))
            return false;

        String typeString = ((IPointerType) type).toString();

        return (getCountOfCharacter(typeString, '*') == dimension);
    }

    public static IASTNode getAssignedTo(IASTNode assignedFrom) {
        IASTNode assignedTo = null;

        IASTNode parent = ((IASTNode) assignedFrom).getParent();
        while (isBracketedPrimaryUnaryExpression(parent))
            parent = ((IASTNode) parent).getParent();

        if (parent instanceof IASTCastExpression)
            parent = ((IASTNode) parent).getParent();

        if (parent instanceof IASTExpression) {
            if (isAssignBinaryExpression((IASTExpression) parent))
                assignedTo = ((IASTBinaryExpression) parent).getOperand1();
        } else if (parent instanceof IASTEqualsInitializer) {
            parent = ((IASTNode) parent).getParent();

            if (parent instanceof IASTDeclarator)
                assignedTo = parent;
        }

        return assignedTo;
    }

    private static boolean isBracketedPrimaryUnaryExpression(IASTNode node) {
        return ((node instanceof IASTUnaryExpression) &&
                (((IASTUnaryExpression) node).getOperator() == IASTUnaryExpression.op_bracketedPrimary));
    }

    private static int getCountOfCharacter(String string, char character) {
        int count = 0;
        for (int i = 0; i < string.length(); i++)
            if (string.charAt(i) == character)
                count++;

        return count;
    }

    public static String getValueOfExpression(IASTExpression operandExpression) {
        String value = "";

        if (operandExpression instanceof IASTLiteralExpression) {
            value = getValueOfLiteralExpression(operandExpression);
        }
        /*
         * else if(operandExpression instanceof IASTIdExpression){
         * value=getValueOfIdExpression(operandExpression);
         * }
         */

        return value;
    }

    private static String getValueOfLiteralExpression(IASTExpression operandExpression) {
        return operandExpression.toString();
    }

    public static Integer convertIntoDecimalNumber(String string) {
        Integer outputDecimal = 0;

        try {
            string = string.replaceAll("(U|u|LL|ll|L|l)", "");

            if (string.contains("x") || string.contains("X")) //ex) 0xff : hexa-decimal
            {
                string = string.replaceFirst("(0x|0X)", "");
                outputDecimal = Integer.parseInt(string, 16);
            } else if (string.contains("e") || string.contains("E")) //ex) 1e5
            {
                outputDecimal = new BigDecimal(string).intValue();
            } else if (string.startsWith("0") && string.length() > 1) //ex) 033 : octa-decimal
            {
                string = string.replaceFirst("[0]", "");
                outputDecimal = Integer.parseInt(string, 8);
            } else {
                outputDecimal = Integer.valueOf(string);
            }

            return outputDecimal;
        } catch (Exception e) {
            throw new DexterRuntimeException("Invalid input number " + e.getMessage(), e);
        }
    }

    private static PreOccurence createPreOccurence(final AnalysisConfig config,
            final Checker checker, final IASTTranslationUnit translationUnit,
            final IASTFileLocation fileLocation, final String message, final String declarationName) {
        final int startLine = fileLocation.getStartingLineNumber();
        final int endLine = fileLocation.getEndingLineNumber();
        final int startOffset = fileLocation.getNodeOffset();
        final int endOffset = startOffset + fileLocation.getNodeLength();

        Map<String, String> tempmap = CppUtil.extractModuleName(translationUnit, startLine);
        String className = tempmap.get("className");
        String methodName = tempmap.get("methodName");
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
        preOcc.setVariableName(declarationName);
        preOcc.setStringValue(message);
        preOcc.setMessage(message);

        return preOcc;
    }
}
