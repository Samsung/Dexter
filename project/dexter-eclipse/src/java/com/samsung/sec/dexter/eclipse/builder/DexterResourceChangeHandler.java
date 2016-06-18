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

import org.eclipse.core.internal.resources.ResourceException;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import com.samsung.sec.dexter.eclipse.DexterEclipseActivator;
import com.samsung.sec.dexter.eclipse.EclipseAnalysis;
import com.samsung.sec.dexter.eclipse.ui.util.EclipseUtil;

public class DexterResourceChangeHandler implements IResourceChangeListener{
	private static DexterResourceChangeHandler INSTANCE;
	private static SourceFileChangeDeltaVisitor VISITOR;
	
	private DexterResourceChangeHandler() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.PRE_BUILD);
	}
	
	public synchronized static DexterResourceChangeHandler start() {
		if(INSTANCE == null){
			INSTANCE = new DexterResourceChangeHandler();
			VISITOR = new SourceFileChangeDeltaVisitor();
		}
		
		return INSTANCE;
	}
	
	public synchronized static void shutdown(){
		if(INSTANCE != null && ResourcesPlugin.getWorkspace() != null){
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(INSTANCE);
			INSTANCE = null;
		}
	}
	
	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		try {
			event.getDelta().accept(VISITOR);
		} catch (CoreException e) {
			DexterEclipseActivator.LOG.error("Cannot analyze Source file because : " + e.getMessage(), e);
		}
	}
	
	static class SourceFileChangeDeltaVisitor implements IResourceDeltaVisitor{
		@Override
		public boolean visit(IResourceDelta delta) throws CoreException {
			// TODO : A static analysis plug-in should provide running environmental information either post-build or no-need-build
			if (EclipseUtil.isValidCAndCppResource(delta.getResource()) == false) {
				return true;
			}
			
			switch(delta.getKind()){
				case IResourceDelta.REMOVED:
					EclipseAnalysis.deleteDefect(delta.getResource());
					break;
				case IResourceDelta.CHANGED:
					analysis(delta);
					break;
				default:
			}
			
			return true;
		}

		private void analysis(IResourceDelta delta) {
			boolean isDexterMarkerChanged = false;
			
			
			if(delta.getMarkerDeltas() != null){
				for(IMarkerDelta d : delta.getMarkerDeltas()){
					if(d.getType().contains("dexter")){
						try {
							boolean hasDexter = (boolean) d.getMarker().getAttribute("dexter");
							if(hasDexter){
								isDexterMarkerChanged = true;
								d.getMarker().setAttribute(DexterMarker.KEY_USED_ONCE, false);
							}
						} catch (ResourceException e) {
							// do nothing
						} catch (CoreException e){
							DexterEclipseActivator.LOG.warn(e.getMessage(), e);
						}
					}
				}
			}
			
			if(isDexterMarkerChanged == false)
				EclipseAnalysis.analysis(delta.getResource());
		}
	}
}
