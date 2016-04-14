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

package com.samsung.sec.dexter.eclipse.cdt.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.CProjectNature;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ILibraryReference;
import org.eclipse.cdt.core.model.ISourceRoot;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.IAnalysisEntityFactory;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.DexterUtil;

public class EclipseCppUtil {
	public static AnalysisConfig createAnalysisConfigForCpp(final IFile file, final IAnalysisEntityFactory configFactory){
		final AnalysisConfig config = configFactory.createAnalysisConfig();
		config.setProjectName(file.getProject().getName());

		final String projectFullPath = DexterUtil.refinePath(file.getProject().getLocation().toFile().getAbsolutePath());
		config.setProjectFullPath(projectFullPath);
		config.setFileName(file.getName());
		EclipseCppUtil.addReferencePaths(file, config);
		config.setSourceFileFullPath(file.getLocation().toFile().getAbsolutePath());
		
		return config;
	}
	
	/**
	 * add source, include, library, module paths
	 * @param file
	 * @param config
	 */
	private static void addReferencePaths(IFile file, AnalysisConfig config){
		if(Strings.isNullOrEmpty(config.getProjectName())){
			throw new DexterRuntimeException("config.projectName should be set before call this method");
		}
		
		ICProject cProject = getCProject(file);
		
		try {
			ISourceRoot[] sourceRoots = cProject.getSourceRoots();
			if(sourceRoots != null){
				for(ISourceRoot sourceRoot : sourceRoots){
					config.addSourceBaseDirList(DexterUtil.refinePath(
							sourceRoot.getResource().getLocation().toFile().getAbsolutePath()));
				}
			}
		} catch (CModelException e) {
			throw new DexterRuntimeException("cannot get Source Root Paths", e);
		}
		
		String filePath = DexterUtil.refinePath(file.getLocation().toFile().getAbsolutePath()).replace("/"+file.getName(), "");
		for(String path : config.getSourceBaseDirList()){
			if(filePath.startsWith(path)){
				String modulePath = filePath.replace(filePath, "");
				config.setModulePath(modulePath);
				break;
			}
		}
		
		// TODO test for libraries
		try {
			ILibraryReference[] libReferences = cProject.getLibraryReferences();
			
			if(libReferences != null){
				for(ILibraryReference ref : libReferences){
					config.addLibDirList(DexterUtil.refinePath(
							ref.getLibraryEntry().getFullLibraryPath().toFile().getAbsolutePath()));
				}
			}
		} catch (CModelException e) {
			throw new DexterRuntimeException("cannot get Source Root Paths", e);
		}
		
		/* TODO too slow when we use this
		try {
			IIncludeReference[] refs = cProject.getIncludeReferences();
			
			if(refs != null){
				for(IIncludeReference ref : refs){
					config.addHeaderBaseDirList(DexterUtil.refinePath(
							ref.getIncludeEntry().getFullIncludePath().toOSString()));
				}
			}
		} catch (CModelException e) {
			throw new DexterRuntimeException("cannot get include Paths", e);
		}
		*/
	}
	
	public static ICProject getCProject(final IFile file) {
		final IProject project = file.getProject();
		if (project == null) {
			throw new DexterRuntimeException("Project is null");
		}
		
	    ICProject cProject;
		try {
			if (project.hasNature(CProjectNature.C_NATURE_ID)) {
				cProject = CoreModel.getDefault().create(project);
			} else {
				throw new DexterRuntimeException("this is not C or Cpp project");
			}
		} catch (CoreException e1) {
			throw new DexterRuntimeException(e1.getMessage(), e1);
		}
		
	    return cProject;
    }

	public static String getModulePath(IFile file) {
		ICProject cProject = getCProject(file);
		List<String> sourceDirList = new ArrayList<String>(1);
		
		try {
			ISourceRoot[] sourceRoots = cProject.getSourceRoots();
			if(sourceRoots != null){
				for(ISourceRoot sourceRoot : sourceRoots){
					sourceDirList.add(DexterUtil.refinePath(
							sourceRoot.getResource().getLocation().toFile().getAbsolutePath()));
				}
			}
		} catch (CModelException e) {
			throw new DexterRuntimeException("cannot get Source Root Paths", e);
		}
		
		String filePath = DexterUtil.refinePath(file.getLocation().toFile().getAbsolutePath()).replace("/"+file.getName(), "");
		for(String path : sourceDirList){
			if(filePath.startsWith(path)){
				String modulePath = filePath.replace(filePath, "");
				return modulePath;
			}
		}
		
		return "";
	}
}
