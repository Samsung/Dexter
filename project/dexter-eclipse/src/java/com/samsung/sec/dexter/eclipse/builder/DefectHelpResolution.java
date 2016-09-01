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

import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.eclipse.DexterEclipseActivator;
import com.samsung.sec.dexter.eclipse.ui.util.CheckerDescriptionUtil;
import com.samsung.sec.dexter.eclipse.ui.util.EclipseUtil;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.PlatformUI;

public class DefectHelpResolution implements IMarkerResolution2 {
    /*
     * @see org.eclipse.ui.IMarkerResolution#getLabel()
     */
    @Override
    public String getLabel() {
        return Messages.DefectHelpResolution_SHOW_DEFECT_DESC;
    }

    /*
     * @see
     * org.eclipse.ui.IMarkerResolution#run(org.eclipse.core.resources.IMarker)
     */
    @Override
    public void run(final IMarker marker) {
        try {
            final Defect defect = DexterMarker.markerToIncompleteDefect(marker);
            CheckerDescriptionUtil.openCheckerDescriptionView(defect);
        } catch (DexterRuntimeException e) {
            DexterEclipseActivator.LOG.error(Messages.DefectHelpResolution_DEFECT_HELP_ERROR, e);
            MessageDialog.openConfirm(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                    Messages.DefectHelpResolution_DEFECT_HELP_ERROR,
                    Messages.DefectHelpResolution_DEFECT_HELP_ERROR_DESC);
        }
    }

    /*
     * @see org.eclipse.ui.IMarkerResolution2#getDescription()
     */
    @Override
    public String getDescription() {
        return Messages.DefectHelpResolution_DEFECT_HELP_DESC;
    }

    /*
     * @see org.eclipse.ui.IMarkerResolution2#getImage()
     */
    @Override
    public Image getImage() {
        return EclipseUtil.getImage(DexterEclipseActivator.PLUGIN_ID, "/icons/defectHelp.gif"); //$NON-NLS-1$
    }
}
