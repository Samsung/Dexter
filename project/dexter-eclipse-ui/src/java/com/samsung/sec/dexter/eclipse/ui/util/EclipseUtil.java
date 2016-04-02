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

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.ITextEditor;

import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.DexterUtil;
import com.samsung.sec.dexter.eclipse.ui.DexterUIActivator;

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
	
	public static boolean isValidJavaResource(IResource resource){
		if((resource.getType() != IResource.FILE)) return false;
		
		String fileExtension = resource.getFileExtension(); 
		if(Strings.isNullOrEmpty(fileExtension)) return false;
		fileExtension = fileExtension.toLowerCase();
		
		return "java".equals(fileExtension);
	}

	public static boolean isValidCAndCppResource(IResource resource){
		if((resource.getType() != IResource.FILE)) return false;
		
		// c h cpp hpp => 1 or 3
		String fileExtension = resource.getFileExtension(); 
		if(Strings.isNullOrEmpty(fileExtension)) return false;
		fileExtension = fileExtension.toLowerCase();
		
		return "c".equals(fileExtension) || "cpp".equals(fileExtension) 
				|| "h".equals(fileExtension) || "hpp".equals(fileExtension);
	}
	
	public static String getDefaultDexterHomePath() {
		String curDir = EclipseUtil.getRcpHomePath();
		String defaultDexterHome;
		if(Strings.isNullOrEmpty(curDir)){
			defaultDexterHome = DexterConfig.getInstance().getDefaultDexterHome();
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
		IWorkspace workspace = null; 
				
		try {
			workspace = ResourcesPlugin.getWorkspace();
		} catch (IllegalStateException e){
			throw new DexterRuntimeException("cannot create IFile from File: " + e.getMessage(), e);
		}
		
		final IPath location = Path.fromOSString(file.getAbsolutePath());
		
		if(workspace == null || location == null){
			return null;
		}
		
		return workspace.getRoot().getFileForLocation(location);
	}

	

	public static Image getImage(String pluginId, String imagePath){
		return AbstractUIPlugin.imageDescriptorFromPlugin(pluginId, imagePath).createImage();
	}

	public static IViewPart findView(String viewId) {
		IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(viewId);

		if (view == null) {
			try {
				view = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage().showView(viewId);
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
	
	public static Object loadSingleExtensionObject(final String pluginId, final String extensionId, @SuppressWarnings("rawtypes") final Class EmptyReplaceClass) {
		try {
			IExtension[] extensions = Platform.getExtensionRegistry()
					.getExtensionPoint(pluginId, extensionId).getExtensions();
			
			final String errorMessage = "can't load dexter-eclipse-jdt plugin which extended " + pluginId + "." + extensionId;
			
			if(extensions.length == 0){
				DexterUIActivator.LOG.error(errorMessage);
				return EmptyReplaceClass.newInstance();
			}
			
			final IConfigurationElement[] configs = extensions[0].getConfigurationElements();
			
			if(configs.length != 1){
				DexterUIActivator.LOG.warn(errorMessage);
				return EmptyReplaceClass.newInstance();
			}
			
			return configs[0].createExecutableExtension("class");
		} catch (Exception e) {
			throw new DexterRuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * @param sourceFileFullPath
	 * @param line		if editor is eclipse editor(extends IEditorPart), it will move to the line
	 */
	public static void openEditor(final String sourceFileFullPath, final int line) {
		final File file = DexterUtil.getFile(sourceFileFullPath);
		final IFileStore fileStore = EFS.getLocalFileSystem().getStore(file.toURI());
		final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		try{
			IEditorPart editor = IDE.openEditorOnFileStore(page, fileStore);
			
			if(editor instanceof ITextEditor){
				ITextEditor textEditor = (ITextEditor) editor;
            	IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
            	textEditor.selectAndReveal(document.getLineOffset(line -1), document.getLineLength(line-1)-1);
			}
		} catch (PartInitException | BadLocationException e){
			DexterUIActivator.LOG.error(e.getMessage(), e);
		}
	}
	
	/*
	public static voi
		
		final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		int line = log.getOccurence().getStartLine();
        try {
            IEditorPart openEditor = IDE.openEditor(page, file);
            
            if(openEditor instanceof ITextEditor) {
            	ITextEditor textEditor = (ITextEditor) openEditor;
            	IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
            	textEditor.selectAndReveal(document.getLineOffset(line -1), document.getLineLength(line-1)-1);
            }
        } catch (CoreException e) {
        	DexterUIActivator.LOG.error(e.getMessage(), e);
        } catch (BadLocationException e) {
        	DexterUIActivator.LOG.error(e.getMessage(), e);
        }
	} */
}
