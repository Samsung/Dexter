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
package com.samsung.sec.dexter.eclipse;

import com.google.common.base.Stopwatch;
import com.google.common.cache.CacheLoader.InvalidCacheLoadException;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisEntityFactory;
import com.samsung.sec.dexter.core.analyzer.IAnalysisEntityFactory;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.DexterConfig.LANGUAGE;
import com.samsung.sec.dexter.core.exception.DexterException;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.plugin.IDexterPluginManager;
import com.samsung.sec.dexter.core.util.DexterUtil;
import com.samsung.sec.dexter.core.util.IDexterClient;
import com.samsung.sec.dexter.eclipse.ui.DexterUIActivator;
import com.samsung.sec.dexter.eclipse.ui.util.EclipseUtil;
import com.samsung.sec.dexter.executor.DexterAnalyzer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;

public class EclipseAnalysis {
    /**
     * @param file
     * @param snapshotId
     * time-miliseconds - If you want to make a snapshot, the value
     * should not be -1,
     * @throws DexterException
     */
    public static void analysis(final IFile file, final long snapshotId, final long defectGroupId,
            final DexterConfig.AnalysisType analysisType)
                    throws DexterException {
        if (DexterConfig.getInstance().isAnalysisAllowedFile(file.getName()) == false) {
            throw new DexterRuntimeException("file is not supporting to analyze : " + file.getName());
        }

        try {
            final AnalysisConfig config = getAnalysisConfig(file);

            config.setSnapshotId(snapshotId);
            config.setResultHandler(new AnalysisResultHandler(file));
            config.setProjectName(config.getProjectName());
            config.setSnapshotId(snapshotId);
            config.setDefectGroupId(defectGroupId);
            config.setAnalysisType(analysisType);

            execute(config);
        } catch (DexterRuntimeException e) {
            DexterEclipseActivator.LOG.error(e.getMessage(), e);
        }

    }

    public static void analysis(final IResource resource, final DexterConfig.AnalysisType analysisType) {
        final Stopwatch s = Stopwatch.createStarted();

        final IFile file = (IFile) resource;
        try {
            EclipseAnalysis.analysis(file, -1, -1, analysisType);
            DexterEclipseActivator.LOG.info("Analysis Elapsed : " + s.elapsed(TimeUnit.MILLISECONDS) + " ms >> "
                    + file.getFullPath().toOSString());
        } catch (DexterException e) {
            DexterEclipseActivator.LOG
                    .error("Analysis Failed: " + file.getFullPath().toOSString() + " : " + e.getMessage(), e);
        }
    }

    public static void deleteDefect(final IResource resource) {
        final IDexterClient client = DexterUIActivator.getDefault().getDexterClient();
        if (client.isServerAlive() == false)
            return;

        final IFile file = (IFile) resource;

        try {
            // TODO 다형성 적용할 것
            if (EclipseUtil.isValidJavaResource(resource)) {
                client.deleteDefects(DexterEclipseActivator.getJDTUtil().getModulePath(file), file.getName());
            } else if (EclipseUtil.isValidCAndCppResource(resource)) {
                client.deleteDefects(DexterEclipseActivator.getCDTUtil().getModulePath(file), file.getName());
            }
        } catch (DexterRuntimeException e) {
            DexterEclipseActivator.LOG.error(e.getMessage(), e);
        }
    }

    /**
     * @param file
     * @return null if failing to load dexter-eclipse-jdt plugin
     * @throws DexterException
     */
    private static AnalysisConfig getAnalysisConfig(final IFile file) {
        final String key = file.getLocation().toFile().getAbsolutePath();
        try {
            return DexterEclipseActivator.getDefault().getConfigCache().get(key);
        } catch (InvalidCacheLoadException e) {
            IAnalysisEntityFactory analysisFactory = new AnalysisEntityFactory();

            final AnalysisConfig config = createAnalysisConfig(file, analysisFactory);
            DexterEclipseActivator.getDefault().getConfigCache().put(key, config);
            return config;
        } catch (ExecutionException e) {
            DexterEclipseActivator.LOG.error(e.getMessage(), e);
        }

        return null;
    }

    private static AnalysisConfig createAnalysisConfig(final IFile file, final IAnalysisEntityFactory configFactory) {
        LANGUAGE language = DexterUtil.getLanguage(file.getFileExtension());

        if (language == LANGUAGE.JAVA) {
            return DexterEclipseActivator.getJDTUtil().createAnalysisConfigForJava(file, configFactory);
        } else if (language == LANGUAGE.C || language == LANGUAGE.CPP) {
            return DexterEclipseActivator.getCDTUtil().createAnalysisConfigForCpp(file, configFactory);
        } else {
            throw new DexterRuntimeException("cannot analyze the file: " + file.getName());
        }
    }

    private static void execute(final AnalysisConfig config) {
        final IDexterClient client = DexterUIActivator.getDefault().getDexterClient();
        final IDexterPluginManager pluginManager = DexterUIActivator.getDefault().getPluginManager();
        DexterAnalyzer.runSync(config, pluginManager, client);
    }
}
