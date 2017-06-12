package com.samsung.sec.dexter.vdcpp.plugin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisEntityFactory;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.analyzer.AnalysisResultChangeHandlerForUT;
import com.samsung.sec.dexter.core.analyzer.IAnalysisEntityFactory;
import com.samsung.sec.dexter.core.analyzer.ITestHandlerAtTheEndOfHandleAnalysisResult;
import com.samsung.sec.dexter.core.checker.IChecker;
import com.samsung.sec.dexter.core.checker.ICheckerConfig;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.defect.Occurence;
import com.samsung.sec.dexter.core.plugin.PluginDescription;
import com.samsung.sec.dexter.core.util.DexterUtil;
import com.samsung.sec.dexter.vdcpp.checkerlogic.ICheckerLogic;

import java.io.File;

import org.junit.Test;

public class DexterVdCppPluginTest {
    private final IAnalysisEntityFactory factory = new AnalysisEntityFactory();

    @Test
    public void test_creating_Plugin_instance() {
        DexterVdCppPlugin plugin = new DexterVdCppPlugin();
        plugin.init();

        assertNotNull(plugin.getCheckerConfig());
    }

    @Test
    public void test_usleep_checker_config() {
        DexterVdCppPlugin plugin = new DexterVdCppPlugin();
        plugin.init();

        ICheckerConfig config = plugin.getCheckerConfig();
        //assertEquals(5, config.getCheckerList().size());
        assertEquals("dexter-vd-cpp", config.getToolName());

        IChecker usleepChecker = config.getChecker("USLEEP");
        assertEquals("Market Issue", usleepChecker.getCategoryName());
        assertEquals("USLEEP", usleepChecker.getCode());
        assertEquals(0, usleepChecker.getCwe());
        //assertTrue(firstChecker.isActive());
        assertEquals("CRI", usleepChecker.getSeverityCode());
        assertEquals("BOTH", usleepChecker.getType());
        assertEquals("0.10.6", usleepChecker.getVersion().toString());
        assertEquals("usleep(useconds_t usec) function can affect system performance, if usec parameter set too shortly. VD recommend that you use more than 10000 ms of usleep function.",
                usleepChecker.getDescription());
    }

    @Test
    public void test_Plugin_Description() {
        DexterVdCppPlugin plugin = new DexterVdCppPlugin();
        plugin.init();

        PluginDescription desc = plugin.getDexterPluginDescription();

        assertEquals("Samsung Electroincs", desc.get3rdPartyName());
        assertEquals(DexterConfig.LANGUAGE.CPP, desc.getLanguage());
        assertEquals("dexter-vd-cpp", desc.getPluginName());
        assertEquals("0.10.6", desc.getVersion().toString());
    }

    @Test
    public void checkerlogic_should_be_initialized_when_called_init() {
        DexterVdCppPlugin plugin = new DexterVdCppPlugin();
        plugin.init();

        ICheckerLogic checkerLogic = plugin.getCheckerLogic("USLEEP");
        assertEquals("com.samsung.sec.dexter.vdcpp.checkerlogic.USleepCheckerLogic",
                checkerLogic.getClass().getName());
    }

    @Test
    public void analyze_method_should_work_for_CONSTANT_NAMING_CHECKER() {
        AnalysisResult result = analyze("src/crc/const_naming.cpp");

        for (Defect defect : result.getDefectList()) {
            if (defect.getCheckerCode().equals("CONST_NAMING")) {
                assertEquals(1, defect.getOccurences().size());
                assertEquals("", defect.getClassName());
                assertEquals("", defect.getMethodName());
                assertEquals(
                        "[#1@5] const variable can be Upper Alphabet, underline, or number. your input is BADNaming1 ",
                        defect.getMessage());

                Occurence occ = defect.getOccurences().get(0);
                assertEquals("BADNaming1", occ.getStringValue());
                assertEquals(5, occ.getStartLine());
                assertEquals(5, occ.getEndLine());
                assertEquals(139, occ.getCharStart());
                assertEquals(153, occ.getCharEnd());
                assertEquals("const variable can be Upper Alphabet, underline, or number. your input is BADNaming1",
                        occ.getMessage());
            }
        }
    }

    @Test
    public void analyze_method_should_work_for_USleep() {
        AnalysisResult result = analyze("src/crc/UsleepCheckerLogic.cpp");

        for (Defect defect : result.getDefectList()) {
            if (defect.getCheckerCode().equals("USLEEP")) {
                assertEquals(1, defect.getOccurences().size());
                assertEquals(
                        "[#1@31] checkUsleep function argument 1000  should be greater than 10000 to avoid performance issue; ",
                        defect.getMessage());

                Occurence occ = defect.getOccurences().get(0);
                assertEquals(31, occ.getStartLine());
                assertEquals(31, occ.getEndLine());

            }
        }

    }

    @Test
    public void analyze_method_for_VECTOR_ERASE_FUNCTION_MISUSE() {
        AnalysisResult result = analyze("src/crc/VectorEraseFunctionMisuse.cpp");

        //assertEquals(5, result.getDefectList().size());	
        for (Defect defect : result.getDefectList()) {
            if (defect.getCheckerCode().equals("VECTOR_ERASE_FUNCTION_MISUSE")) {
                assertEquals(4, defect.getOccurences().size());
                assertEquals(
                        "[#1@44] Avoid vector erase function inside iterative block; [#2@49] Avoid vector erase function inside iterative block; [#3@54] Avoid vector erase function inside iterative block; [#4@59] Avoid vector erase function inside iterative block; ",
                        defect.getMessage());

                Occurence occ = defect.getOccurences().get(0);
                assertEquals(44, occ.getStartLine());
                assertEquals(44, occ.getEndLine());

            }
        }

    }

    @Test
    public void analyze_method_for_SIGNED_UNSIGNED_ASSIGNMENT_ERROR() {
        AnalysisResult result = analyze("src/crc/SignedUnsignedAssignmentError.cpp");

        //assertEquals(5, result.getDefectList().size());	
        for (Defect defect : result.getDefectList()) {
            if (defect.getCheckerCode().equals("SIGNED_UNSIGNED_ASSIGNMENT_ERROR")) {
                assertEquals(3, defect.getOccurences().size());
                assertEquals(
                        "[#1@28] A Signed data type is transformed into a larger Unsigned data type. This can produce unexpected values. [#2@29] A Signed data type is transformed into a larger Unsigned data type. "
                                +
                                "This can produce unexpected values. [#3@30] A Signed data type is transformed into a larger Unsigned data type. This can produce unexpected values. ",
                        defect.getMessage());

                Occurence occ = defect.getOccurences().get(0);
                assertEquals(28, occ.getStartLine());
                assertEquals(28, occ.getEndLine());

            }
        }

    }

    @Test
    public void analyze_method_for_FREE_ON_RETURN_VALUE() {
        AnalysisResult result = analyze("src/crc/NoFreeOfReturnValue.cpp");

        //assertEquals(5, result.getDefectList().size());	
        for (Defect defect : result.getDefectList()) {
            if (defect.getCheckerCode().equals("CHECK_FREE_STMT")) {
                assertEquals(1, defect.getOccurences().size());
                Occurence occ = defect.getOccurences().get(0);
                assertEquals("[#1@28] You should have free statement for a returned object by calling "
                        + occ.getVariableName() + " ", defect.getMessage());

                assertEquals(28, occ.getStartLine());
                assertEquals(28, occ.getEndLine());

            }
        }

    }

    @Test
    public void analyze_method_for_FREE_PARAMETER_STMT() {
        AnalysisResult result = analyze("src/crc/NoFreeOfParameterValue.cpp");

        //assertEquals(5, result.getDefectList().size());	
        for (Defect defect : result.getDefectList()) {
            if (defect.getCheckerCode().equals("CHECK_FREE_STMT_PARAM")) {
                assertEquals(1, defect.getOccurences().size());
                Occurence occ = defect.getOccurences().get(0);
                assertEquals("[#1@21] You should have free statement for a parameter by calling "
                        + occ.getVariableName() + " ", defect.getMessage());

                assertEquals(21, occ.getStartLine());
                assertEquals(21, occ.getEndLine());

            }
        }

    }

    @Test
    public void analyze_method_for_THREAD_UNSAFE_FUNCTION() {
        AnalysisResult result = analyze("src/crc/ThreadUnsafeFunction.cpp");

        //assertEquals(5, result.getDefectList().size());	
        for (Defect defect : result.getDefectList()) {
            if (defect.getCheckerCode().equals("CHECK_THREAD_UNSAFE_FUNCTION_DBUS_GLIB")) {
                assertEquals(1, defect.getOccurences().size());
                Occurence occ = defect.getOccurences().get(0);
                assertEquals(
                        "[#1@62] " + occ.getVariableName()
                                + ": dbus-glib is thread unsafe function. so VD recommends NOT to use dbus-glib in the multi-thread environment. ",
                        defect.getMessage());

                assertEquals(62, occ.getStartLine());
                assertEquals(65, occ.getEndLine());

            }
        }

    }

    @Test
    public void analyze_method_for_CHECK_DUID() {
        AnalysisResult result = analyze("src/crc/CheckDUID.cpp");

        //assertEquals(5, result.getDefectList().size());	
        for (Defect defect : result.getDefectList()) {
            if (defect.getCheckerCode().equals("CHECK_USAGE_DUID")) {
                assertEquals(9, defect.getOccurences().size());
                Occurence occ = defect.getOccurences().get(0);
                assertEquals(
                        occ.getVariableName()
                                + " is used. You need to check usage and the purpose of ID system. please refer to Samsung Smart TV Service Device Identifier Guideline",
                        occ.getStringValue());
                assertEquals(9, occ.getStartLine());
                assertEquals(9, occ.getEndLine());

            }
        }

    }

    @Test
    public void analyze_method_for_AtoiReturnValueAsArrayIndex() {
        AnalysisResult result = analyze("src/crc/AtoiReturnValueAsArrayIndex.cpp");

        //assertEquals(5, result.getDefectList().size());	
        for (Defect defect : result.getDefectList()) {
            if (defect.getCheckerCode().equals("CHECK_USAGE_ATOI_AS_ARRAY_INDEX")) {
                assertEquals(2, defect.getOccurences().size());
                Occurence occ = defect.getOccurences().get(0);
                assertEquals(
                        "[#1@25] atoi() couldreturned return negative value(return type is integer value). do Not use returned value of atoi() "
                                +
                                "as array index without checking its range [#2@32] atoi() couldreturned return negative value(return type is integer value). "
                                +
                                "do Not use returned value of atoi() as array index without checking its range ",
                        defect.getMessage());

                assertEquals(25, occ.getStartLine());
                assertEquals(25, occ.getEndLine());

            }
        }

    }

    @Test
    public void analyze_method_for_NoFreeForThirdParameter() {
        AnalysisResult result = analyze("src/crc/NoFreeForThirdParameter.cpp");

        //assertEquals(5, result.getDefectList().size());	
        for (Defect defect : result.getDefectList()) {
            if (defect.getCheckerCode().equals("CHECK_FREE_STMT_THIRD_PARAM")) {
                assertEquals(3, defect.getOccurences().size());
                Occurence occ = defect.getOccurences().get(0);
                assertEquals(
                        "[#1@20] You should have free statement for a parameter by calling app_control_get_extra_data "
                                +
                                "[#2@25] You should have free statement for a parameter by calling app_control_get_extra_data_array "
                                +
                                "[#3@31] You should have free statement for a parameter by calling app_control_get_extra_data_array ",
                        defect.getMessage());

                assertEquals(20, occ.getStartLine());
                assertEquals(20, occ.getEndLine());

            }
        }

    }

    @Test
    public void analyze_method_for_ArrayIndexOutOfBounds() {
        AnalysisResult result = analyze("src/crc/IndexOutOfBoundForOneDArray.cpp");

        for (Defect defect : result.getDefectList()) {
            if (defect.getCheckerCode().equals("CHECK_ARRAY_INDEX_OUT_OF_BOUNDS")) {
                assertEquals(5, defect.getOccurences().size());
                Occurence occ = defect.getOccurences().get(0);
                assertEquals("[#1@22] Array index out of bounds [#2@27] Array index out of bounds [#3@35]" +
                        " Array index out of bounds [#4@36] Array index out of bounds" +
                        " [#5@37] Array index out of bounds ", defect.getMessage());

                assertEquals(22, occ.getStartLine());
                assertEquals(22, occ.getEndLine());

                break;
            }
        }

    }

    @Test
    public void analyze_method_for_TwoDArrayIndexOutOfBoundCheckerLogic() {
        AnalysisResult result = analyze("src/crc/2DArrayIndexOutOfBound.cpp");

        for (Defect defect : result.getDefectList()) {
            if (defect.getCheckerCode().equals("CHECK_ARRAY_INDEX_OUT_OF_BOUNDS_TWO_DIMESIONAL")) {
                assertEquals(2, defect.getOccurences().size());
                Occurence occ = defect.getOccurences().get(0);
                assertEquals("[#1@26] Array index out of bounds [#2@34] Array index out of bounds ",
                        defect.getMessage());

                assertEquals(26, occ.getStartLine());
                assertEquals(26, occ.getEndLine());

                break;
            }
        }
    }

    @Test
    public void analyze_method_for_ForWhileControlBracket() {
        AnalysisResult result = analyze("src/crc/ForWhileControlBracket.cpp");

        for (Defect defect : result.getDefectList()) {
            if (defect.getCheckerCode().equals("FOR_WHILE_CONTROL_BRACKET")) {
                assertEquals(4, defect.getOccurences().size());
                Occurence occ = defect.getOccurences().get(0);
                assertEquals(
                        "[#1@22] Although conditional or control statement (if/for/while) is single line, '{' and '}' shall be existed "
                                +
                                "[#2@28] Although conditional or control statement (if/for/while) is single line, '{' and '}' shall be existed "
                                +
                                "[#3@30] Although conditional or control statement (if/for/while) is single line, '{' and '}' shall be existed "
                                +
                                "[#4@35] Although conditional or control statement (if/for/while) is single line, '{' and '}' shall be existed ",
                        defect.getMessage());

                assertEquals(22, occ.getStartLine());
                assertEquals(22, occ.getEndLine());

                break;
            }
        }

    }

    @Test
    public void analyze_method_for_AddFallThroughCommentOnSwitchCase() {
        AnalysisResult result = analyze("src/crc/AddFallThroughCommentOnSwitchCase.cpp");

        for (Defect defect : result.getDefectList()) {
            if (defect.getCheckerCode().equals("ADD_FALLTHROUGH_IN_SWITCHCASE")) {
                assertEquals(2, defect.getOccurences().size());
                Occurence occ = defect.getOccurences().get(0);
                assertEquals(
                        "[#1@25] In switch statement, every case, except default, shall contain break/return statement. Omitting break/return intentionally, /* FALL THROUGH */ comment shall be used "
                                +
                                "[#2@31] In switch statement, every case, except default, shall contain break/return statement. Omitting break/return intentionally, /* FALL THROUGH */ comment shall be used ",
                        defect.getMessage());
                assertEquals(25, occ.getStartLine());
                assertEquals(25, occ.getEndLine());

                break;
            }
        }

    }

    @Test
    public void analyze_method_for_IfElseControl() {
        AnalysisResult result = analyze("src/crc/IfElseControl.cpp");

        for (Defect defect : result.getDefectList()) {
            if (defect.getCheckerCode().equals("CHECK_IFELSE_CONTROL")) {
                assertEquals(3, defect.getOccurences().size());
                Occurence occ = defect.getOccurences().get(0);
                assertEquals(
                        "[#1@27] 'else' and 'else if' in if-else or if-else if-else statement shall be started in the new line, "
                                +
                                "[#2@34] 'else' and 'else if' in if-else or if-else if-else statement shall be started in the new line, "
                                +
                                "[#3@37] 'else' and 'else if' in if-else or if-else if-else statement shall be started in the new line, ",
                        defect.getMessage());
                assertEquals(27, occ.getStartLine());
                assertEquals(27, occ.getEndLine());

                break;
            }
        }

    }

    @Test
    public void analyze_method_for_ForWhileNullBody() {
        AnalysisResult result = analyze("src/crc/ForWhileNullBody.cpp");

        for (Defect defect : result.getDefectList()) {
            if (defect.getCheckerCode().equals("FOR_WHILE_NULL_BODY")) {
                assertEquals(8, defect.getOccurences().size());
                Occurence occ = defect.getOccurences().get(0);
                assertEquals(
                        "[#1@21] When body is omitted intentionally in 'for/while' statement, it shall contain null statement(';') and comment, /* NULL */ "
                                +
                                "[#2@24] When body is omitted intentionally in 'for/while' statement, it shall contain null statement(';') and comment, /* NULL */ "
                                +
                                "[#3@27] When body is omitted intentionally in 'for/while' statement, it shall contain null statement(';') and comment, /* NULL */ "
                                +
                                "[#4@32] When body is omitted intentionally in 'for/while' statement, it shall contain null statement(';') and comment, /* NULL */ "
                                +
                                "[#5@37] When body is omitted intentionally in 'for/while' statement, it shall contain null statement(';') and comment, /* NULL */ "
                                +
                                "[#6@43] When body is omitted intentionally in 'for/while' statement, it shall contain null statement(';') and comment, /* NULL */ "
                                +
                                "[#7@49] When body is omitted intentionally in 'for/while' statement, it shall contain null statement(';') and comment, /* NULL */ "
                                +
                                "[#8@55] When body is omitted intentionally in 'for/while' statement, it shall contain null statement(';') and comment, /* NULL */ ",
                        defect.getMessage());
                assertEquals(21, occ.getStartLine());
                assertEquals(21, occ.getEndLine());

                break;
            }
        }

    }

    @Test
    public void analyze_method_for_DoWhileBracket() {
        AnalysisResult result = analyze("src/crc/DoWhileBracket.cpp");

        for (Defect defect : result.getDefectList()) {
            if (defect.getCheckerCode().equals("DO_WHILE_BRACKET")) {
                assertEquals(1, defect.getOccurences().size());
                Occurence occ = defect.getOccurences().get(0);
                assertEquals("[#1@23] In the do-while statement, {} shall be always used ",
                        defect.getMessage());
                assertEquals(23, occ.getStartLine());
                assertEquals(25, occ.getEndLine());

                break;
            }
        }

    }

    @Test
    public void analyze_method_for_FunctionReturnValue() {
        AnalysisResult result = analyze("src/crc/FunctionReturnValue.c");

        for (Defect defect : result.getDefectList()) {
            if (defect.getCheckerCode().equals("FUNCTION_RETURN_VALUE")) {
                assertEquals(1, defect.getOccurences().size());
                Occurence occ = defect.getOccurences().get(0);
                assertEquals(
                        "[#1@20] Return type shall be explicitly used. Even if there is no return value, 'void' shall be used ",
                        defect.getMessage());
                assertEquals(20, occ.getStartLine());
                assertEquals(20, occ.getEndLine());

                break;
            }
        }

    }

    @Test
    public void analyze_method_for_MacroParenthesis() {
        AnalysisResult result = analyze("src/crc/MacroParenthesis.cpp");

        for (Defect defect : result.getDefectList()) {
            if (defect.getCheckerCode().equals("MACRO_PARENTHESIS")) {
                assertEquals(7, defect.getOccurences().size());
                Occurence occ = defect.getOccurences().get(0);
                assertEquals(
                        "[#1@17] In macro definition, every expression and the arguments in the expressions shall be enclosed by '(' and ')' for each "
                                +
                                "[#2@18] In macro definition, every expression and the arguments in the expressions shall be enclosed by '(' and ')' for each "
                                +
                                "[#3@21] In macro definition, every expression and the arguments in the expressions shall be enclosed by '(' and ')' for each "
                                +
                                "[#4@22] In macro definition, every expression and the arguments in the expressions shall be enclosed by '(' and ')' for each "
                                +
                                "[#5@25] In macro definition, every expression and the arguments in the expressions shall be enclosed by '(' and ')' for each "
                                +
                                "[#6@26] In macro definition, every expression and the arguments in the expressions shall be enclosed by '(' and ')' for each "
                                +
                                "[#7@27] In macro definition, every expression and the arguments in the expressions shall be enclosed by '(' and ')' for each ",
                        defect.getMessage());
                assertEquals(17, occ.getStartLine());
                assertEquals(17, occ.getEndLine());

                break;
            }
        }

    }

    private AnalysisResult analyze(final String testFilePath) {
        DexterVdCppPlugin plugin = new DexterVdCppPlugin();
        plugin.init();

        final AnalysisConfig config = createAnalysisConfigTestData(testFilePath);
        return plugin.analyze(config);
    }

    private AnalysisConfig createAnalysisConfigTestData(final String testFilePath) {
        final AnalysisConfig config = factory.createAnalysisConfig();

        final String projectFullPath = new File("testdata/DefectiveProject").getAbsolutePath();
        final String sourceFileFullPath = DexterUtil.addPaths(projectFullPath, testFilePath);
        final String sourceDir = DexterUtil.addPaths(projectFullPath, "src");

        config.setProjectName("DefectiveProject");
        config.setSourceFileFullPath(sourceFileFullPath);
        config.setProjectFullPath(projectFullPath);
        config.setAnalysisType(DexterConfig.AnalysisType.PROJECT);
        config.addSourceBaseDirList(sourceDir);
        config.generateFileNameWithSourceFileFullPath();
        config.generateModulePath();

        AnalysisResultChangeHandlerForUT handler = new AnalysisResultChangeHandlerForUT(
                new ITestHandlerAtTheEndOfHandleAnalysisResult() {
                    @Override
                    public void testAfterHandlingAnalysisResult(final AnalysisResult result) {}
                });

        config.setResultHandler(handler);
        return config;
    }

}
