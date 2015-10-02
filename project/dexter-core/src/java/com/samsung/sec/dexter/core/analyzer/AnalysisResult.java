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
package com.samsung.sec.dexter.core.analyzer;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.BaseAnalysisEntity;
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.defect.Occurence;
import com.samsung.sec.dexter.core.defect.PreOccurence;

public class AnalysisResult extends BaseAnalysisEntity {
	/** list of defects */
	private List<Defect> defectList = new ArrayList<Defect>();
	
	static Logger logger = Logger.getLogger(AnalysisResult.class);
	
	protected AnalysisResult(){
		
	}
	
    /**
	 * @param preOccr void
	 */
    public Defect addDefectWithPreOccurence(final PreOccurence preOccr) {
    	boolean isNewDefect = true;
    	Defect returnDefect = null;
    	
    	for(Defect defect : defectList){
    		if(defect.equalsWithPreOccurence(preOccr)){
    			boolean isDifferentOccr = true;
    			for(Occurence occr : defect.getOccurences()){
    				if(occr.equalsWithPreOccurence(preOccr)){
    					isDifferentOccr = false;
    				}
    			}
    			
    			if(isDifferentOccr){
    				defect.addOccurence(preOccr.toOccurence());
    			}
    			
    			isNewDefect = false;
    			returnDefect = defect;
    			break;
    		}
    	}
    	
    	if(isNewDefect){
    		Defect defect = preOccr.toDefect();
    		defect.addOccurence(preOccr.toOccurence());
    		defectList.add(defect);
    		returnDefect = defect;
    	}
    	
    	return returnDefect;
    }
    
    public void addDefect(Defect defect){
    	assert defect != null;
    	
    	if(defectList.contains(defect)) return;
    	
    	defectList.add(defect);
    }
    

	/**
	 * @return the defectList
	 */
	public List<Defect> getDefectList() {
	    return defectList;
	}

	/**
	 * @param defectList the defectList to set
	 */
	public void setDefectList(final List<Defect> defectList) {
		this.defectList = defectList;
	}


	/**
	 * @return 
	 */
    public String getKey() {
    	if(Strings.isNullOrEmpty(getModulePath())){
			return getFileName();
		} else {
			return getModulePath() + "/" + getFileName();
		}
    }
}
