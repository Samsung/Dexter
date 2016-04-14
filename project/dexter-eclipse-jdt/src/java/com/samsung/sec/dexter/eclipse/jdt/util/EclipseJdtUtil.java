package com.samsung.sec.dexter.eclipse.jdt.util;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.IAnalysisEntityFactory;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.DexterUtil;

public class EclipseJdtUtil {
	public static AnalysisConfig createAnalysisConfigForJava(final IFile file, final IAnalysisEntityFactory configFactory){
		final AnalysisConfig config = configFactory.createAnalysisConfig();
		config.setProjectName(file.getProject().getName());

		final String projectFullPath = DexterUtil.refinePath(file.getProject().getLocation().toFile().getAbsolutePath());
		config.setProjectFullPath(projectFullPath);
		
		addSourceFoldersAndLibFiles(file, config);
		config.setFileName(file.getName());
		
		final String outputDir = getOutputDir(file);
		config.setOutputDir(outputDir); 
		config.setModulePath(getModulePath(file));
		config.setSourceFileFullPath(file.getLocation().toFile().getAbsolutePath());
		
		return config;
	}
	
	private static void addSourceFoldersAndLibFiles(IFile file, AnalysisConfig config){
		if(Strings.isNullOrEmpty(config.getProjectName())){
			throw new DexterRuntimeException("config.projectName should be set before call this method");
		}
		
		IClasspathEntry[] entries;
		try{
			IJavaProject javaProject = getJavaProject(file);
			entries = javaProject.getResolvedClasspath(false);
			
			if(entries == null || entries.length == 0){
				throw new DexterRuntimeException("There is no source folder or library folders/files");
			}
		} catch (JavaModelException e) {
			throw new DexterRuntimeException(e.getMessage() + " thus, it failed to add source folder and library files", e);
		}
		
		
		for (int i = 0; i < entries.length; i++) {
			final IClasspathEntry entry = entries[i];

			if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
				if(entry.getPath().getDevice() == null){
					IFolder folder = file.getProject().getFolder(entry.getPath().toString().replace("/" + config.getProjectName(), ""));
					config.addSourceBaseDirList(folder.getLocation().toFile().getAbsolutePath());
				} else {
					IFolder folder = file.getProject().getFolder(entry.getPath());
					config.addSourceBaseDirList(folder.getLocation().toFile().getAbsolutePath());
				}
			} else if (entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
				final IPath path = entry.getPath();
				if(path.getDevice() != null){
					continue;
				}
				
				IFolder folder = file.getProject().getFolder(entry.getPath());
				
				File fileOrFolder = new File(folder.getLocation().toFile().getAbsolutePath());
				
				if(!fileOrFolder.exists()){
					continue;
				}
				
				if(fileOrFolder.isDirectory()){
					config.addLibDirList(folder.getLocation().toFile().getAbsolutePath());
				} else {
					config.addLibFile(folder.getLocation().toFile().getAbsolutePath());
				}
			}
			// else kind : IClasspathEntry.CPE_CONTAINER, IClasspathEntry.CPE_PROJECT, IClasspathEntry.CPE_VARIABLE
		}
	}
	
	public static IJavaProject getJavaProject(final IFile file) {
		final IProject project = file.getProject();
		if (project == null) {
			throw new DexterRuntimeException("Project is null");
		}
		
	    IJavaProject javaProject;
		try {
			if (project.hasNature(JavaCore.NATURE_ID)) {
				javaProject = JavaCore.create(project);
			} else {
				throw new DexterRuntimeException("this is not java project");
			}
		} catch (CoreException e1) {
			throw new DexterRuntimeException(e1.getMessage(), e1);
		}
		
	    return javaProject;
    }

	public static String getOutputDir(IFile file) {
		IJavaProject javaProject = getJavaProject(file);
		
		try {
			final String outputName = javaProject.getOutputLocation().toString().replace(file.getProject().getName(), "");
			final String outputDir = file.getProject().getLocation().toFile().getAbsolutePath() + outputName;
			return DexterUtil.refinePath(outputDir);
		} catch (JavaModelException e) {
			throw new DexterRuntimeException(e.getMessage(), e);
		}
	}
	
	public static String getModulePath(IFile file) {
		String fileFullPath = DexterUtil.refinePath(file.getLocation().toFile().getAbsolutePath());
		
		IJavaProject javaProject = getJavaProject(file);
		
		IClasspathEntry[] entries;
		try {
			entries = javaProject.getResolvedClasspath(false);
		} catch (JavaModelException e) {
			throw new DexterRuntimeException(e.getMessage(), e);
		}
		
		for (int i = 0; i < entries.length; i++) {
			IClasspathEntry entry = entries[i];

			if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
				IFolder folder = null;
				if(entry.getPath().getDevice() == null){
					final String projectName = file.getProject().getName();
					folder = file.getProject().getFolder(entry.getPath().toString().replace("/" + projectName, ""));
				} else {
					folder = file.getProject().getFolder(entry.getPath());
				}

				final String srcDir = DexterUtil.refinePath(folder.getLocation().toFile().getAbsolutePath());
				if(fileFullPath.indexOf(srcDir) != -1){
					return fileFullPath.replace(srcDir, "").replace("/" + file.getName(), "");
				}
			}
		}
		
		throw new DexterRuntimeException("Cannot extract module path from IFile object : " + fileFullPath);
	}
}
