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
package com.samsung.sec.dexter.eclipse.builder;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.google.common.base.Stopwatch;
import com.samsung.sec.dexter.core.exception.DexterException;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.plugin.DexterPluginManager;
import com.samsung.sec.dexter.core.util.DexterClient;
import com.samsung.sec.dexter.eclipse.DexterEclipseActivator;
import com.samsung.sec.dexter.eclipse.EclipseAnalysis;
import com.samsung.sec.dexter.eclipse.ui.util.EclipseUtil;

public class DexterBuilder extends IncrementalProjectBuilder{
	
	class DexterDeltaVisitor implements IResourceDeltaVisitor {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse
		 * .core.resources.IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) throws CoreException {
			final IResource resource = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.ADDED: // handle added resource
			case IResourceDelta.CHANGED: // handle changed resource
				checkJava(resource);
				break;
			case IResourceDelta.REMOVED: // handle removed resource
				deleteDefect(resource);
				break;
//			case IResourceDelta.OPEN:
//				break;
			}
			// return true to continue visiting children.
			return true;
		}
	}

	class JavaResourceVisitor implements IResourceVisitor {
		public boolean visit(final IResource resource) {
			checkJava(resource);

			return true;
		}
	}

	public static final String BUILDER_ID = "dexter-eclipse.dexterBuilder";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 * java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, @SuppressWarnings("rawtypes") Map args, IProgressMonitor monitor)
	        throws CoreException {
		if (kind == FULL_BUILD) {
			fullBuild(monitor);
		} else {
			final IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		
		return new IProject[0];
	}

	void checkJava(final IResource resource) {
		// can analyze without login because there can be network problem.
		if (DexterPluginManager.getInstance().getPluginList().size() < 1) {
			return;
		}

		if (resource instanceof IFile && resource.getName().endsWith(".java")) {
			final Stopwatch s = Stopwatch.createStarted();
			
			final IFile file = (IFile) resource;
			try {
				EclipseAnalysis.analysis(file, -1, -1);
				DexterEclipseActivator.LOG.info("Analysis Elapsed : " + s.elapsed(TimeUnit.MILLISECONDS) + " ms >> "
						+ file.getFullPath().toOSString());
			} catch (DexterException e) {
				DexterEclipseActivator.LOG.error("Analysis Failed: " + file.getFullPath().toOSString() + " : " + e.getMessage(), e);
			}
		}
	}
	
	private void deleteDefect(final IResource resource){
		if (DexterPluginManager.getInstance().getPluginList().size() < 1) {
			return;
		}

		if (!(resource instanceof IFile) || !resource.getName().endsWith(".java")) {
			return;
		}
		
		final IFile file = (IFile) resource;
		
		try {
			DexterClient.getInstance().deleteDefects(EclipseUtil.getModulePath(file), file.getName());
		} catch (DexterRuntimeException e) {
			DexterEclipseActivator.LOG.error(e.getMessage(), e);
		}
	}
	

	protected void fullBuild(final IProgressMonitor monitor) throws CoreException {
	}

	protected void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) throws CoreException {
		// the visitor does the work.
		delta.accept(new DexterDeltaVisitor());
	}
}
