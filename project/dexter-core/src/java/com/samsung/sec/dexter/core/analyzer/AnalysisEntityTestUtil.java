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

import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.defect.Occurence;

public class AnalysisEntityTestUtil {
	public static AnalysisResult createSampleAnalysisResult(){
		IAnalysisEntityFactory factory = new AnalysisEntityFactory();
		AnalysisResult result = factory.createAnalysisResult(factory.createAnalysisConfig());
		
		Defect defect = createSampleDefect();
		result.addDefect(defect);
		result.setSourceFileFullPath("C:/test-project/src/test-module/test-filename");
		
		return result;
	}
	
	public static Defect createSampleDefect() {
	    Defect defect = new Defect();
		defect.setCheckerCode("test-checker-code");
		defect.setClassName("test-class");
		defect.setCreatedDateTime(System.currentTimeMillis());
		defect.setFileName("test-filename");
		defect.setLanguage("JAVA");
		defect.setMessage("test-message");
		defect.setMethodName("test-method");
		defect.setModulePath("test-module");
		defect.setSeverityCode("CRI");
		defect.setCategoryName("security");
		defect.setToolName("test-tool");
		
		Occurence occ = new Occurence();
		occ.setStartLine(10);
		occ.setEndLine(11);
		occ.setMessage("test-occurence-message");
		
		defect.addOccurence(occ);
		
		return defect;
    }
}
