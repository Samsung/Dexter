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

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class AnalysisLogContentProvider implements ITreeContentProvider {
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang.Object)
	 */
    @Override
    public Object[] getElements(Object inputElement) {
    	return getChildren(inputElement);
    }

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
    @Override
    public Object[] getChildren(Object inputElement) {
    	if (inputElement instanceof RootAnalysisLog){
    		final RootAnalysisLog log = (RootAnalysisLog) inputElement;
   			return log.getChildren();
    	} else if(inputElement instanceof AnalysisLog){
    		final AnalysisLog log = (AnalysisLog) inputElement;
   			return log.getDefectLogList().toArray(new DefectLog[log.getDefectLogList().size()]);
    	} else if(inputElement instanceof DefectLog){
    		final DefectLog defect = (DefectLog) inputElement; 
    		return defect.getChildren().toArray(new OccurenceLog[defect.getChildren().size()]);
    	} else {
    		return null;
    	}
    }

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
    @Override
    public Object getParent(Object inputElement) {
    	if(inputElement instanceof AnalysisLog){
    		final AnalysisLog log = (AnalysisLog) inputElement;
   			return log.getRootLog();
    	} else if(inputElement instanceof DefectLog){
    		final DefectLog defectLog = (DefectLog) inputElement; 
    		return defectLog.getParent();
    	} else if(inputElement instanceof OccurenceLog){
    		final OccurenceLog occLog = (OccurenceLog) inputElement;
    		return occLog.getParent();
    	} else {
    		return null;
    	}
    }

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
    @Override
    public boolean hasChildren(Object inputElement) {
    	if(inputElement instanceof AnalysisLog){
    		final AnalysisLog log = (AnalysisLog) inputElement;
    		return log.getDefectLogList().size() > 0;
//    		if(log.getCreatedTime() == null){
//    		} else {
//    			return true;
//    		}
    	} else if(inputElement instanceof DefectLog){
    		final DefectLog defect = (DefectLog) inputElement; 
    		return defect.getChildren().size() > 0;
    	} else {
    		return false;
    	}
    }
    
    /* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
