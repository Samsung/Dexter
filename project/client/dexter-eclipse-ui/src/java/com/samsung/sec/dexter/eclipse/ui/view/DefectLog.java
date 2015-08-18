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

import java.util.ArrayList;
import java.util.List;

import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.defect.Occurence;
import com.samsung.sec.dexter.core.filter.AnalysisFilterHandler;

public class DefectLog {
	private Defect defect;
	private AnalysisLog parent;
	private boolean isDismissed;
	private List<OccurenceLog> children = new ArrayList<OccurenceLog>();
	
	
	/**
	 * @param defect2
	 */
    public DefectLog(Defect defect) {
    	this.defect = defect;
    	isDismissed = AnalysisFilterHandler.getInstance().isDefectDismissed(defect);
    	
    	for(final Occurence occ : defect.getOccurences()){
    		final OccurenceLog occLog = new OccurenceLog(occ);
    		occLog.setParent(this);
    		this.children.add(occLog);
    	}
    }
	/**
	 * @return the defect
	 */
	public Defect getDefect() {
		return defect;
	}
	/**
	 * @return the parent
	 */
	public AnalysisLog getParent() {
		return parent;
	}
	/**
	 * @return the children
	 */
	public List<OccurenceLog> getChildren() {
		return children;
	}
	/**
	 * @param defect the defect to set
	 */
	public void setDefect(Defect defect) {
		this.defect = defect;
	}
	/**
	 * @param parent the parent to set
	 */
	public void setParent(AnalysisLog parent) {
		this.parent = parent;
	}
	/**
	 * @param children the children to set
	 */
	public void setChildren(List<OccurenceLog> children) {
		this.children = children;
	}
	/**
	 * @param columnIndex
	 * @return 
	 */
    public String getLabel(int columnIndex) {
    	switch(columnIndex){
			case 0:
				return this.defect.getCheckerCode() + " (" + this.defect.getOccurences().size() + ")";
			case 1:
				return AnalysisFilterHandler.getInstance().isDefectDismissed(defect) == true ? "Dismissed" : "NEW";
			case 2:
				return this.defect.getSeverityCode();				
			case 3:
				return this.defect.getModulePath();
			case 4:
				return this.defect.getClassName();
			case 5:
				return this.defect.getMethodName();
			case 6:
				return this.defect.getMessage();
			default:
				return "";
    	}
    }
	/**
	 * @return the isDismissed
	 */
	public boolean isDismissed() {
		return isDismissed;
	}
	/**
	 * @param isDismissed the isDismissed to set
	 */
	public void setDismissed(boolean isDismissed) {
		this.isDismissed = isDismissed;
	}
}
