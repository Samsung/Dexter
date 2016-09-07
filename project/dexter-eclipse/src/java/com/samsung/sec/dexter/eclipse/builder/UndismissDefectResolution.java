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
package com.samsung.sec.dexter.eclipse.builder;

import com.samsung.sec.dexter.core.config.DexterConfig.AnalysisType;
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.exception.DexterException;
import com.samsung.sec.dexter.core.filter.AnalysisFilterHandler;
import com.samsung.sec.dexter.eclipse.DexterEclipseActivator;
import com.samsung.sec.dexter.eclipse.EclipseAnalysis;
import com.samsung.sec.dexter.eclipse.ui.DexterUIActivator;
import com.samsung.sec.dexter.eclipse.ui.util.EclipseUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;

public class UndismissDefectResolution implements IMarkerResolution2 {
    // static Image image =
    // DexterEclipseActivator.getImageDescriptor("/icons/dismissedQuickFix.gif").createImage();
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IMarkerResolution#getLabel()
     */
    @Override
    public String getLabel() {
        return Messages.UndismissDefectResolution_UNDISMISS_LABEL;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.IMarkerResolution#run(org.eclipse.core.resources.IMarker)
     */
    @Override
    public void run(IMarker marker) {
        if (marker == null || marker.exists() == false) {
            return;
        }

        final Defect incompleteDefect = DexterMarker.markerToIncompleteDefect(marker);
        if (incompleteDefect == null) {
            DexterEclipseActivator.LOG.error("Cannot create defect filter because the incompleteDefect is null");
            return;
        }

        AnalysisFilterHandler.getInstance().removeDefectFilter(incompleteDefect,
                DexterUIActivator.getDefault().getDexterClient());
        final IFile file = (IFile) marker.getResource();
        try {
            EclipseAnalysis.analysis(file, -1, -1, AnalysisType.FILE);
        } catch (DexterException e) {
            DexterEclipseActivator.LOG.error(e.getMessage(), e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IMarkerResolution2#getDescription()
     */
    @Override
    public String getDescription() {
        return Messages.UndismissDefectResolution_UNDISMISS_DESC;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IMarkerResolution2#getImage()
     */
    @Override
    public Image getImage() {
        return EclipseUtil.getImage(DexterEclipseActivator.PLUGIN_ID, "/icons/dismissedQuickFix.gif"); //$NON-NLS-1$
    }
}
