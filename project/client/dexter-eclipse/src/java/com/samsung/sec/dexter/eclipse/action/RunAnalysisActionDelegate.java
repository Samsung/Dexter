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
package com.samsung.sec.dexter.eclipse.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.samsung.sec.dexter.core.exception.DexterException;
import com.samsung.sec.dexter.eclipse.DexterEclipseActivator;
import com.samsung.sec.dexter.eclipse.EclipseAnalysis;

public class RunAnalysisActionDelegate implements IObjectActionDelegate {
	ISelection selection;
	//private IWorkbenchPart targetPart;
	private List<IFile> targetFiles = new ArrayList<IFile>();

	@Override
	public void run(IAction action) {
		if(selection instanceof StructuredSelection){
			targetFiles = new ArrayList<IFile>();
			
			final StructuredSelection sel = (StructuredSelection) this.selection;
			
			@SuppressWarnings("unchecked")
            Iterator<Object> iter = sel.iterator();
			while(iter.hasNext()){
				final Object object = iter.next();
				
				if(object instanceof IResource){
					final IResource resource = (IResource) object;
					analysisResource(resource);
				}
			}
			
			
			Job analysisJob = new Job("Static Analysis Job"){
				@Override
                protected IStatus run(IProgressMonitor monitor) {
					monitor.beginTask("Static Analyzing...", targetFiles.size());
					
					for(int i=targetFiles.size(); i > 0; --i) {
						final IFile targetFile = targetFiles.get(i-1);
						monitor.subTask("analyzing : " + targetFile.getName());
						
						//Thread.sleep(50);
						
						Display.getDefault().syncExec(new Runnable() {
							@Override
							public void run() {
								try {
									EclipseAnalysis.analysis(targetFile, -1, -1);
								} catch (DexterException e) {
									DexterEclipseActivator.LOG.error(e.getMessage(), e);
								}
							}
						});
						
						monitor.worked(1);
					}
					
					monitor.done();
					return Status.OK_STATUS;
                }
				
			};
			
			analysisJob.setPriority(Job.DECORATE);
			analysisJob.schedule();
		}
	}
	
	private void analysisResource(final IResource resource){
		try {
        	if(resource instanceof IFile){
        		final IFile targetFile = (IFile) resource;
				if(targetFile.getName().endsWith(".java")){
					if(!targetFiles.contains(targetFile)){
						targetFiles.add(targetFile);
					}
				}
        	} else if(resource instanceof IFolder){
        		final IFolder folder = (IFolder) resource;
        		if(folder.members() == null || folder.members().length == 0){
    	        	return;
    	        }
        		for(final IResource child : folder.members()){
        			analysisResource(child);
        		}
        	} else if(resource instanceof IProject){
        		final IProject project = (IProject) resource;
        		if(project.members() == null || project.members().length == 0){
    	        	return;
    	        }
        		for(final IResource child : project.members()){
        			analysisResource(child);
        		}
        	}
        } catch (CoreException e) {
	        DexterEclipseActivator.LOG.error(e.getMessage(), e);
        }
	}

	@Override
	public void selectionChanged(final IAction action, final ISelection selection) {
		this.selection = selection;
	}

	@Override
	public void setActivePart(final IAction action, final IWorkbenchPart targetPart) {
		//this.targetPart = targetPart;
	}

}
