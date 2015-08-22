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
package com.samsung.sec.dexter.eclipse;

import java.util.concurrent.ExecutionException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import com.google.common.cache.CacheLoader.InvalidCacheLoadException;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisEntityFactory;
import com.samsung.sec.dexter.core.analyzer.IAnalysisEntityAbstractFactory;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.exception.DexterException;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.DexterUtil;
import com.samsung.sec.dexter.eclipse.ui.util.EclipseUtil;
import com.samsung.sec.dexter.executor.DexterAnalyzer;

public class EclipseAnalysis {
	/**
	 * @param file
	 * @param snapshotId time-miliseconds - If you want to make a snapshot, the value should not be -1, 
	 * @throws DexterException 
	 */
	public static void analysis(final IFile file, final long snapshotId, final long defectGroupId) throws DexterException {
		if(DexterConfig.getInstance().isAnalysisAllowedFile(file.getName()) == false){
			throw new DexterRuntimeException("file is not supporting to analyze : " + file.getName());
		}
		
		final AnalysisConfig config = getAnalysisConfig(file);
		config.setSnapshotId(snapshotId);
		config.setResultHandler(new ARHandler(file));
		config.setProjectName(config.getProjectName());
		config.setSnapshotId(snapshotId);
		config.setDefectGroupId(defectGroupId);
		
		execute(config);
	}
	
	private static AnalysisConfig getAnalysisConfig(final IFile file) throws DexterException{
		final String key = file.getLocation().toFile().getAbsolutePath();
		try {
        	return DexterEclipseActivator.getDefault().getConfigCache().get(key);
		} catch (InvalidCacheLoadException e){
			IAnalysisEntityAbstractFactory analysisFactory = new AnalysisEntityFactory();
			
			final AnalysisConfig config = createAnalysisConfig(file, analysisFactory);
			DexterEclipseActivator.getDefault().getConfigCache().put(key, config);
			return config;
		} catch (ExecutionException e) {
	        DexterEclipseActivator.LOG.error(e.getMessage(), e);
        }
		
		return null;
	}

	private static AnalysisConfig createAnalysisConfig(final IFile file, final IAnalysisEntityAbstractFactory configFactory){
		final AnalysisConfig config = configFactory.createAnalysisConfig();
		config.setProjectName(file.getProject().getName());

		final String projectFullPath = DexterUtil.refinePath(file.getProject().getLocation().toFile().getAbsolutePath());
		config.setProjectFullPath(projectFullPath);
		EclipseUtil.addSourceFoldersAndLibFiles(file, config);
		config.setFileName(file.getName());
		final String outputDir = EclipseUtil.getOutputDir(file);
		config.setOutputDir(outputDir);
		config.setModulePath(EclipseUtil.getModulePath(file));
		config.setSourceFileFullPath(file.getLocation().toFile().getAbsolutePath());
		
		return config;
	}
	
	private static void execute(final AnalysisConfig config) {
		DexterAnalyzer.getInstance().runSync(config);
	}
	
	public static IJavaProject getJavaProject(final IProject project) {
	    IJavaProject javaProject;
		try {
			if (project.hasNature(JavaCore.NATURE_ID)) {
				javaProject = JavaCore.create(project);
			} else {
				DexterEclipseActivator.LOG.error("this is not java project");
				return null;
			}
		} catch (CoreException e1) {
			DexterEclipseActivator.LOG.error(e1.getMessage(), e1);
			return null;
		}
	    return javaProject;
    }
}
