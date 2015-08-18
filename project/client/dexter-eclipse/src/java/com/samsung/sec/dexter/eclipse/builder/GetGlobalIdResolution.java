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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;

import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.util.DexterClient;
import com.samsung.sec.dexter.eclipse.DexterEclipseActivator;
import com.samsung.sec.dexter.eclipse.ui.util.EclipseUtil;

public class GetGlobalIdResolution implements IMarkerResolution2 {

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IMarkerResolution#getLabel()
	 */
	@Override
	public String getLabel() {
		return Messages.GetGlobalIdResolution_GET_GLOBAL_ID_LABEL;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IMarkerResolution#run(org.eclipse.core.resources.IMarker)
	 */
	@Override
	public void run(final IMarker markerOriginal) {
		
		final IResource resource = markerOriginal.getResource();
		if(!(resource instanceof IFile)){
			return;
		}
		
		
		final IFile file = (IFile) resource;
		
        try {
        	final IMarker[] markers = file.findMarkers(DexterMarker.DEFECT_MARKER_TYPE, true, IResource.DEPTH_INFINITE);
	        if(markers != null && markers.length > 0){
	        	for(final IMarker marker : markers){
	        		Defect defect = new Defect();
	        		defect.setToolName((String) marker.getAttribute("toolName", "")); //$NON-NLS-1$ //$NON-NLS-2$
	        		defect.setLanguage((String) marker.getAttribute("language", "")); //$NON-NLS-1$ //$NON-NLS-2$
	        		defect.setCheckerCode((String) marker.getAttribute("checkerCode", "")); //$NON-NLS-1$ //$NON-NLS-2$
	        		defect.setClassName((String) marker.getAttribute("className", "")); //$NON-NLS-1$ //$NON-NLS-2$
	        		defect.setFileName((String) marker.getAttribute("fileName", "")); //$NON-NLS-1$ //$NON-NLS-2$
	        		defect.setMethodName((String) marker.getAttribute("methodName", "")); //$NON-NLS-1$ //$NON-NLS-2$
	        		defect.setModulePath((String) marker.getAttribute("modulePath", "")); //$NON-NLS-1$ //$NON-NLS-2$
	        		
	        		final long gdid = DexterClient.getInstance().getGlobalDid(defect);
	        		
	        		if(gdid >= 0){
	        			marker.setAttribute(IMarker.MESSAGE,
	        					((String) marker.getAttribute(IMarker.MESSAGE)).replace("To-Be-Defined", "" + gdid)); //$NON-NLS-1$ //$NON-NLS-2$
	        		}
	        		marker.setAttribute(IMarker.MESSAGE,
        					((String) marker.getAttribute(IMarker.MESSAGE)).replace("(-1)", "" + gdid)); //$NON-NLS-1$ //$NON-NLS-2$
	        	}
	        }
        } catch (CoreException e) {
	        DexterEclipseActivator.LOG.error(e.getMessage(), e);
        }
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IMarkerResolution2#getDescription()
	 */
	@Override
	public String getDescription() {
		return Messages.GetGlobalIdResolution_GET_GLOBAL_ID_DESC;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IMarkerResolution2#getImage()
	 */
	@Override
	public Image getImage() {
		return EclipseUtil.getImage(DexterEclipseActivator.PLUGIN_ID, "/icons/getGdid.gif"); //$NON-NLS-1$
	}

}
