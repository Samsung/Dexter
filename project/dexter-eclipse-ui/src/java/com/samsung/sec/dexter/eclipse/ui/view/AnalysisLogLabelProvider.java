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
package com.samsung.sec.dexter.eclipse.ui.view;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.samsung.sec.dexter.eclipse.ui.DexterUIActivator;
import com.samsung.sec.dexter.eclipse.ui.util.EclipseUtil;

public class AnalysisLogLabelProvider extends LabelProvider implements ITableLabelProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
    @Override
    public Image getColumnImage(Object element, int columnIndex) {
    	if(columnIndex != 0){
    		return null;
    	}
    	if(element instanceof AnalysisLog){
    		return EclipseUtil.getImage(DexterUIActivator.PLUGIN_ID, "/icons/analysislog.gif");
    	} else if(element instanceof DefectLog){
    		return EclipseUtil.getImage(DexterUIActivator.PLUGIN_ID, "/icons/defectlog.gif");
    	} else if(element instanceof OccurenceLog){
    		return EclipseUtil.getImage(DexterUIActivator.PLUGIN_ID, "/icons/occurencelog.gif");
    	} else {
    		return null;
    	}
    }

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
    @Override
    public String getColumnText(Object element, int columnIndex) {
    	
    	if(element instanceof AnalysisLog){
    		AnalysisLog log = (AnalysisLog) element;
    		return log.getLabel(columnIndex);
    	} else if(element instanceof DefectLog){
    		DefectLog defectLog = (DefectLog) element; 
    		return defectLog.getLabel(columnIndex);
    	} else if(element instanceof OccurenceLog){
    		OccurenceLog occLog = (OccurenceLog) element;
    		return occLog.getLabel(columnIndex);
    	} else {
    		return "";
    	}
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.BaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
     */
    @Override
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }
}
