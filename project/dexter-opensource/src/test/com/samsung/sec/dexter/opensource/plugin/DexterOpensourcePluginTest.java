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
package com.samsung.sec.dexter.opensource.plugin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisEntityFactory;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.analyzer.AnalysisResultChangeHandlerForUT;
import com.samsung.sec.dexter.core.analyzer.IAnalysisEntityFactory;
import com.samsung.sec.dexter.core.analyzer.ITestHandlerAtTheEndOfHandleAnalysisResult;
import com.samsung.sec.dexter.core.checker.CheckerConfig;
import com.samsung.sec.dexter.core.checker.IChecker;
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.util.DexterUtil;

import java.io.File;

import org.junit.Test;

public class DexterOpensourcePluginTest {
    private final IAnalysisEntityFactory factory = new AnalysisEntityFactory();

    @Test
    public void test_creating_Plugin_instance() {
        DexterOpensourcePlugin plugin = new DexterOpensourcePlugin();
        plugin.init();

        assertNotNull(plugin.getCheckerConfig());
    }

    @Test
    public void test_correctness_of_CheckerConfig() {
        DexterOpensourcePlugin plugin = new DexterOpensourcePlugin();
        plugin.init();

        CheckerConfig config = plugin.getCheckerConfig();
        assertEquals(13, config.getCheckerList().size());
        assertEquals("dexter-opensource", config.getToolName());

        IChecker firstChecker = config.getCheckerList().iterator().next();
        assertEquals("GPL", firstChecker.getCategoryName());
        assertEquals("GPL_2_0", firstChecker.getCode());
        assertEquals(0, firstChecker.getCwe());
        assertTrue(firstChecker.isActive());
        assertEquals("MAJ", firstChecker.getSeverityCode());
        assertEquals("BOTH", firstChecker.getType());
        assertEquals("0.10.0", firstChecker.getVersion().toString());
    }

    @Test
    public void analyze_method_should_work_for_GPL_2_0() {
        analyze("src/GPL_v2/COPYING", "GPL_2_0", 1);
        analyze("src/GPL_v2/if_bridge.h", "GPL_2_0", 1);
        analyze("src/GPL_v2/libxt_osf.c", "GPL_2_0", 1);
        analyze("src/GPL_v2/mpi-scan.c", "GPL_2_0", 1);
        analyze("src/GPL_v2/posix_mutex.hpp", "GPL_2_0", 0);//  ==> 검출 안됨
    }

    @Test
    public void analyze_method_should_work_for_GPL_3_0() {
        analyze("src/GPL_v3/COPYING.GPLv3", "GPL_3_0", 1);
    }

    @Test
    public void analyze_method_should_work_for_LGPL_2_0() {
        analyze("src/LGPL_v2/pthread.h", "LGPL_2_0", 2);
    }

    @Test
    public void analyze_method_should_work_for_LGPL_2_1() {
        analyze("src/LGPL_v2.1/COPYING", "LGPL_2_1", 3); // 중복검출: LGPL_2_1, GPL_2_0, LGPL_2_0
        //analyze("src/LGPL_v2.1/ffmpeg.c", "LGPL_2_1", 1); // ==> 검출 안됨
        //analyze("src/LGPL_v2.1/nl-list-caches.c", "LGPL_2_1", 1); // ==> 검출 안됨
    }

    @Test
    public void analyze_method_should_work_for_LGPL_3_0() {
        AnalysisResult result = analyze("src/LGPL_v3.0/COPYING.LGPLv3", "LGPL_3_0", 3); // 중복검출: GPL_2_0, LGPL_3_0, GPL_3_0
        testResult("src/LGPL_v3.0/COPYING.LGPLv3", "LGPL_3_0", 3, result);
    }

    private AnalysisResult analyze(final String testFilePath, final String expectedCheckerCode, final int count) {
        DexterOpensourcePlugin plugin = new DexterOpensourcePlugin();
        plugin.init();

        final AnalysisConfig config = createAnalysisConfigTestData(testFilePath, expectedCheckerCode, count);

        return plugin.analyze(config);
    }

    private AnalysisConfig createAnalysisConfigTestData(final String testFilePath,
            final String expectedCheckerCode, final int count) {
        final AnalysisConfig config = factory.createAnalysisConfig();

        final String projectFullPath = new File("testdata/DefectiveProject").getAbsolutePath();
        final String sourceFileFullPath = DexterUtil.addPaths(projectFullPath, testFilePath);
        final String sourceDir = DexterUtil.addPaths(projectFullPath, "src");

        config.setProjectName("DefectiveProject");
        config.setSourceFileFullPath(sourceFileFullPath);
        config.setProjectFullPath(projectFullPath);
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

    private void testResult(final String testFilePath,
            final String expectedCheckerCode, final int count, final AnalysisResult result) {
        assertEquals(count, result.getDefectList().size());

        boolean hasCheckerResult = false;
        for (Defect defect : result.getDefectList()) {
            if (defect.getCheckerCode().equals(expectedCheckerCode)) {
                hasCheckerResult = true;

                assertNotNull(defect.getMessage());
                assertNotNull(defect.getOccurences().get(0).getMessage());
            }
        }

        assertTrue(hasCheckerResult);
    }
}
