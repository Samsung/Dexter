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
package com.samsung.sec.dexter.eclipse.action;

import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.exception.DexterException;
import com.samsung.sec.dexter.eclipse.DexterEclipseActivator;
import com.samsung.sec.dexter.eclipse.EclipseAnalysis;
import com.samsung.sec.dexter.eclipse.ui.DexterUIActivator;
import com.samsung.sec.dexter.eclipse.ui.util.EclipseUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

@SuppressWarnings("restriction")
public class CreateSnapshotActionDelegate implements IObjectActionDelegate {
    ISelection selection;
    private IWorkbenchPart targetPart;
    private List<IFile> targetFiles = new ArrayList<IFile>();
    private long defectGroupId = 1L;

    public CreateSnapshotActionDelegate() {}

    @Override
    public void run(IAction action) {
        if ((selection instanceof StructuredSelection) == false) {
            return;
        }

        if (isLogout())
            return;
        if (isNotAdmin())
            return;

        final StructuredSelection selection = (StructuredSelection) this.selection;
        if (selection == null)
            return;

        final Object selectedObject = selection.getFirstElement();
        if (isNotProjectSelection(selectedObject))
            return;

        createTargetFiles(selection);

        analysis();
    }

    private void analysis() {
        Job analysisJob = new Job("Static Analysis Job") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                monitor.beginTask("Static Analyzing...", targetFiles.size());

                final long snapshotId = System.currentTimeMillis();
                for (int i = targetFiles.size(); i > 0; --i) {
                    final IFile targetFile = targetFiles.get(i - 1);
                    analysisFile(monitor, snapshotId, targetFile);

                    if (monitor.isCanceled())
                        break;
                }

                monitor.done();
                return Status.OK_STATUS;
            }

            private void analysisFile(final IProgressMonitor monitor, final long snapshotId, final IFile targetFile) {
                monitor.subTask("analyzing : " + targetFile.getName());

                Display.getDefault().syncExec(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            EclipseAnalysis.analysis(targetFile, snapshotId, defectGroupId,
                                    DexterConfig.AnalysisType.SNAPSHOT);
                            monitor.worked(1);
                        } catch (DexterException e) {
                            DexterEclipseActivator.LOG.error(e.getMessage(), e);
                        }
                    }
                });
            }
        };

        analysisJob.setPriority(Job.DECORATE);
        analysisJob.schedule();
    }

    private void createTargetFiles(final StructuredSelection selection) {
        @SuppressWarnings("unchecked")
        final Iterator<Object> iter = selection.iterator();
        while (iter.hasNext()) {
            final Object object = iter.next();

            if (object instanceof IResource) {
                final IResource resource = (IResource) object;
                addResourceAsTargetFile(resource);
            }
        }
    }

    private boolean isNotProjectSelection(Object selectedObject) {
        boolean isNotProject = (selectedObject == null || (selectedObject instanceof Project) == false);

        if (isNotProject) {
            showErrorMessage("Snapshot Creation Error", "You can select only a Project element.");
            return true;
        }

        return false;
    }

    private boolean isNotAdmin() {
        if (DexterUIActivator.getDefault().getDexterClient().isCurrentUserAdmin() == false) {
            showErrorMessage("Snapshot Creation Error", "Only administrator can create a snapshot.");

            return true;
        }

        return false;
    }

    private boolean isLogout() {
        if (DexterUIActivator.getDefault().getDexterClient().isLogin() == false) {
            DexterEclipseActivator.LOG.error("Can not create snapshot due to no loggin");

            showErrorMessage("Snapshot Creation Error", "Can not create snapshot due to no loggin");
            // if(targetPart != null && targetPart.getSite() != null &&
            // targetPart.getSite().getShell() != null){
            // }
            return true;
        }

        return false;
    }

    private void showErrorMessage(final String title, final String message) {
        if (targetPart != null && targetPart.getSite() != null && targetPart.getSite().getShell() != null) {
            MessageDialog.openError(targetPart.getSite().getShell(), title, message);
        }
    }

    private void addResourceAsTargetFile(final IResource resource) {
        try {
            if (resource instanceof IFile) {
                final IFile targetFile = (IFile) resource;

                if (EclipseUtil.isValidJavaResource(resource) == false
                        && EclipseUtil.isValidCAndCppResource(resource) == false) {
                    return;
                }
                if (!targetFiles.contains(targetFile)) {
                    targetFiles.add(targetFile);
                }

            } else if (resource instanceof IFolder) {
                final IFolder folder = (IFolder) resource;
                if (folder.members() == null || folder.members().length == 0) {
                    return;
                }
                for (IResource child : folder.members()) {
                    addResourceAsTargetFile(child);
                }
            } else if (resource instanceof IProject) {
                final IProject project = (IProject) resource;
                if (project.members() == null || project.members().length == 0) {
                    return;
                }
                for (IResource child : project.members()) {
                    addResourceAsTargetFile(child);
                }
            }
        } catch (CoreException e) {
            DexterEclipseActivator.LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        this.selection = selection;
    }

    @Override
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        this.targetPart = targetPart;
    }

}
