package com.samsung.sec.dexter.eclipse.action;

import java.util.Iterator;
import com.samsung.sec.dexter.eclipse.DexterEclipseActivator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.DexterClient;
import com.samsung.sec.dexter.eclipse.ui.util.EclipseUtil;
import com.samsung.sec.dexter.eclipse.ui.view.CodeMetricsView;

public class OpenCodeMetricsActionDelegate implements IObjectActionDelegate {

	ISelection selection;
	//String modulePath = "";
	//String fileName = "";
	
	@Override
	public void run(IAction action) {
		if (selection instanceof StructuredSelection) {

			final StructuredSelection sel = (StructuredSelection) this.selection;

			@SuppressWarnings("unchecked")
			Iterator<Object> iter = sel.iterator();
			while (iter.hasNext()) {
				final Object object = iter.next();
				if (object instanceof IResource) {
					final IResource resource = (IResource) object;
					codeMetricsResource(resource);
				}
			}

		}
	}

	private void codeMetricsResource(final IResource resource) {
		if (resource instanceof IFile) {
			final IFile targetFile = (IFile) resource;
			if (targetFile.getName().endsWith(".java")) {

				//setModulePath(getModulePath(targetFile));
				//setFileName(targetFile.getName());
				StringBuilder makeCodeMetricsUrl = new StringBuilder();
				try {
					IViewPart view = EclipseUtil.findView(CodeMetricsView.ID);
					final CodeMetricsView codeMetricsView = (CodeMetricsView) view;

					
					makeCodeMetricsUrl.append("http://").append(DexterClient.getInstance().getServerHost()).append(":") //$NON-NLS-1$ //$NON-NLS-2$
							.append(DexterClient.getInstance().getServerPort()).append(DexterConfig.CODE_METRICS_BASE)//$NON-NLS-1$
							.append("?").append(DexterConfig.CODE_METRICS_FILE_NAME).append("=").append(targetFile.getName())//$NON-NLS-1$
							.append("&").append(DexterConfig.CODE_METRICS_MODULE_PATH).append("=").append(EclipseUtil.getModulePath(targetFile));//$NON-NLS-1$
					
					codeMetricsView.setUrl(makeCodeMetricsUrl.toString());

					EclipseUtil.showView(CodeMetricsView.ID);
				} catch (DexterRuntimeException e) {
					DexterEclipseActivator.LOG.error("Cannot open the Code Metrics Description View");
					DexterEclipseActivator.LOG.error(e.getMessage(), e);
				}
			}
		}
	}

	/*private void setModulePath(String modulePath) {
		if (modulePath.startsWith("/")) {
			modulePath = modulePath.substring(1, modulePath.length());
		}
		this.modulePath = modulePath;
	}*/

	/*private void setFileName(String fileName) {
		this.fileName = fileName;
	}*/

	/*final private String getFileName() {
		return this.fileName;
	}*/

	/*public static IJavaProject getJavaProject(final IFile file) {
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
*/
	/*public String getModulePath(IFile file) {
		String fileFullPath = DexterUtil.refinePath(file.getLocation().toFile()
				.getAbsolutePath());

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
				if (entry.getPath().getDevice() == null) {
					final String projectName = file.getProject().getName();
					folder = file.getProject().getFolder(
							entry.getPath().toString()
									.replace("/" + projectName, ""));
				} else {
					folder = file.getProject().getFolder(entry.getPath());
				}

				final String srcDir = DexterUtil.refinePath(folder
						.getLocation().toFile().getAbsolutePath());
				if (fileFullPath.indexOf(srcDir) != -1) {
					return fileFullPath.replace(srcDir, "").replace(
							"/" + file.getName(), "");
				}
			}
		}

		throw new DexterRuntimeException(
				"Cannot extract module path from IFile object : "
						+ fileFullPath);
	}*/

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub

	}

}
