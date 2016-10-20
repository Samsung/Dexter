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
package com.samsung.sec.dexter.findbugs.plugin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisEntityFactory;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.analyzer.AnalysisResultChangeHandlerForUT;
import com.samsung.sec.dexter.core.analyzer.IAnalysisEntityFactory;
import com.samsung.sec.dexter.core.checker.CheckerConfig;
import com.samsung.sec.dexter.core.checker.IChecker;
import com.samsung.sec.dexter.core.util.DexterUtil;

import java.io.File;

import org.junit.Test;

public class FindBugsWrapperTest {
    @Test
    public void test_creating_and_initializing_findbugswrapper() {
        FindBugsWrapper findbugs = new FindBugsWrapper();

        findbugs.initCheckerConfig();
        CheckerConfig checkerConfig = findbugs.getCheckerConfig();
        assertEquals(FindbugsDexterPlugin.PLUGIN_NAME, checkerConfig.getToolName());
        IChecker firstChecker = checkerConfig.getCheckerList().iterator().next();
        assertEquals("AppendingToAnObjectOutputStream", firstChecker.getCategoryName());
        assertEquals("IO_APPENDING_TO_OBJECT_OUTPUT_STREAM", firstChecker.getCode());
        assertEquals(0, firstChecker.getCwe());
        assertEquals("Doomed attempt to append to an object output stream", firstChecker.getDescription());
        assertFalse(firstChecker.isActive());
        assertEquals("IO_APPENDING_TO_OBJECT_OUTPUT_STREAM", firstChecker.getName());
    }

    @Test
    public void execute_method_should_analyze_and_create_result() {
        FindBugsWrapper findbugs = new FindBugsWrapper();
        findbugs.initCheckerConfig();

        final IAnalysisEntityFactory factory = new AnalysisEntityFactory();
        final AnalysisConfig config = factory.createAnalysisConfig();

        // setup
        final String projectFullPath = new File("testdata/DefectiveProject").getAbsolutePath();
        final String sourceFileFullPath = DexterUtil.addPaths(projectFullPath, "src/defect/example/LockInversion.java");
        final String sourceDir = DexterUtil.addPaths(projectFullPath, "src");
        final String binDir = DexterUtil.addPaths(projectFullPath, "bin");

        config.setProjectName("DefectiveProject");
        config.setSourceFileFullPath(sourceFileFullPath);
        config.setProjectFullPath(projectFullPath);
        config.addSourceBaseDirList(sourceDir);
        config.setOutputDir(binDir);
        config.generateFileNameWithSourceFileFullPath();
        config.generateModulePath();
        config.setResultHandler(new AnalysisResultChangeHandlerForUT());

        AnalysisResult result = findbugs.execute(config);
        assertNotNull(result);
    }
}
