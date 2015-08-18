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
package com.samsung.sec.dexter.eclipse.ui.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;

import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.exception.DexterException;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.DexterUtil;
import com.samsung.sec.dexter.eclipse.ui.DexterUIActivator;
import com.samsung.sec.dexter.eclipse.ui.view.DefectHelpView;

public class EclipseUtil {
	/**
	 * @return ex) C:/dexter/
	 */
	public static String getRcpHomePath(){
		if(Platform.getInstallLocation() == null){
			DexterUIActivator.LOG.error("Platform.getInstallLocation() null");
			return "";
		}
		
		final URL url = Platform.getInstallLocation().getURL();
		
		String path = url.getPath();
		if(path.startsWith("/") && path.indexOf(":") > 0){
			path = path.substring(1);
		}
		
		if(!path.endsWith("/") && !path.endsWith("\\") && !path.endsWith(DexterUtil.PATH_SEPARATOR)){
			path = path + "/";
		}
		
		return path;
	}
	
	public static String getDefaultDexterHomePath() {
		String curDir = EclipseUtil.getRcpHomePath();
		String defaultDexterHome;
		if(Strings.isNullOrEmpty(curDir)){
			defaultDexterHome = System.getProperty("user.home") + "/" + DexterConfig.DEXTER_DEFAULT_FOLDER_NAME; //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			defaultDexterHome = curDir + DexterConfig.DEXTER_DEFAULT_FOLDER_NAME;
		}
		
		return defaultDexterHome;
	}
	
	public static String getProductIniFilePath(){
		URL url;
		
		try {
			if(Platform.getInstallLocation() == null){
				DexterUIActivator.LOG.error("Platform.getInstallLocation() null");
				return "";
			}
			
			if(Platform.getInstallLocation().getURL() == null){
				DexterUIActivator.LOG.error("Platform.getInstallLocation().getURL() null");
				return "";
			}
			
			if(Platform.getProduct() == null){
				DexterUIActivator.LOG.error("Platform.getProduct() null");
				return "";
			}
			
			
			url = new URL(Platform.getInstallLocation().getURL() + Platform.getProduct().getName() + ".ini");
			String path = url.getPath();
			if(path.startsWith("/") && path.indexOf(":") > 0){
				path = path.substring(1);
			}
			
			if(!path.endsWith("/") && !path.endsWith("\\") && !path.endsWith(DexterUtil.PATH_SEPARATOR)){
				path = path + "/";
			}
			
			return path;
        } catch (MalformedURLException e) {
        	DexterUIActivator.LOG.error(e.getMessage(), e);
        	return "";
        }
	}
	
	public static String getProductName(){
		if(Platform.getProduct() == null){
			DexterUIActivator.LOG.error("Platform.getProduct() null");
			return "";
		}
		
		return Platform.getProduct().getName();
	}
	
	public static IFile getIFileFromFile(File file){
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IPath location = Path.fromOSString(file.getAbsolutePath());
		
		if(workspace == null || location == null){
			return null;
		}
		
		return workspace.getRoot().getFileForLocation(location);
	}

	/**
	 * @param file
	 * @param config
	 * @throws DexterException
	 */
	public static void addSourceFoldersAndLibFiles(IFile file, AnalysisConfig config){
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
		/*
		String projectName = file.getProject().getName();
		if(outputDir.startsWith(DexterUtil.PATH_SEPARATOR)){
			outputDir = outputDir.replaceFirst(DexterUtil.PATH_SEPARATOR + projectName, "");
		} else if(outputDir.startsWith("/")) {
			outputDir = outputDir.replaceFirst("/" + projectName, "");
		} else {
			outputDir = outputDir.replace(projectName, "");
		}
		*/
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

	public static Image getImage(String pluginId, String imagePath){
		return AbstractUIPlugin.imageDescriptorFromPlugin(pluginId, imagePath).createImage();
	}

	public static IViewPart findView(String viewId) {
		IViewPart view =  PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(viewId);
		
		if(view == null){
			try {
	            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(DefectHelpView.ID);
	            view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(DefectHelpView.ID);
            } catch (PartInitException e) {
            	throw new DexterRuntimeException(e.getMessage(), e);
            }
		}
		
		return view;
    }

	public static void showView(String viewId) {
		if(PlatformUI.getWorkbench() == null){
			DexterUIActivator.LOG.error("workbench is null");
		}
		
		if(PlatformUI.getWorkbench().getActiveWorkbenchWindow() == null){
			DexterUIActivator.LOG.error("active workbench is null");
		}
		
		if(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage() == null){
			DexterUIActivator.LOG.error("activePage is null");
		}
		
		
		try {
	        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(viewId);
        } catch (PartInitException e) {
	        throw new DexterRuntimeException(e.getMessage(), e);
        } catch (Exception e){
        	throw new DexterRuntimeException(e.getMessage(), e);
        }
    }

	public static void errorMessageBox(final String title, final String message) {
		Shell shell = Display.getDefault().getActiveShell();
		if(shell == null) return;
		
		MessageDialog.openError(shell, title, message);
    }
	
	public static void infoMessageBox(final String title, final String message) {
		Shell shell = Display.getDefault().getActiveShell();
		if(shell == null) return;
		
		MessageDialog.openInformation(shell, title, message);
    }
	
	public static void openPerspective(final String perspectiveId){
		final IWorkbench workbench = PlatformUI.getWorkbench();
		
		new UIJob("Switching perspectives") {
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				try {
					workbench.showPerspective(perspectiveId, workbench.getActiveWorkbenchWindow());
				} catch (WorkbenchException e) {
					return new Status(IStatus.ERROR, DexterUIActivator.PLUGIN_ID,
					        "Error while switching perspectives", e);
				}
				return Status.OK_STATUS;
			}
		}.run(new NullProgressMonitor());
	}
}
