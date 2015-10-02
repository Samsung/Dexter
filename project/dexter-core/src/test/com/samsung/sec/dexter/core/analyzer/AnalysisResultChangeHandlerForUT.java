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
import com.google.gson.Gson;
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.defect.Occurence;

public class AnalysisResultChangeHandlerForUT implements EndOfAnalysisHandler {
	static Logger logger = Logger.getLogger(AnalysisResultChangeHandlerForUT.class);

	private int totalCnt = 0;
	private int criticalCnt = 0;
	private int majorCnt = 0;
	private int minorCnt = 0;
	private int crcCnt = 0;
	private int etcCnt = 0;

	private int exepectedDefectCount = -1;
	
	private List<Defect> expectedDefectList = new ArrayList<Defect>();

	private String projectName;

	private String fileName;
	
	private ITestHandlerAtTheEndOfHandleAnalysisResult testHandler;
	
	public AnalysisResultChangeHandlerForUT(){
		testHandler = new ITestHandlerAtTheEndOfHandleAnalysisResult(){
			@Override
            public void testAfterHandlingAnalysisResult(final AnalysisResult result) {
				// do nothing
            }
		};
	}
			
	public AnalysisResultChangeHandlerForUT(ITestHandlerAtTheEndOfHandleAnalysisResult handler){
		this.testHandler = handler;
	}
	

	@Override
	public void handleAnalysisResult(final List<AnalysisResult> resultList) {
		for(AnalysisResult result : resultList){
			handleResult(result);
		}
	}
	
	private void handleResult(AnalysisResult result) {
		System.out.println("=================================================================================");
		System.out.println("### START TEST for the result of static analysis");
		
		System.out.println("\t# Json of the AnalysisResult Object : " + AnalysisResultFileManager.getInstance().getJson(result));

		boolean isPassed = true;
		
		for (Defect defect : result.getDefectList()) {
			totalCnt++;
			if ("CRI".equals(defect.getSeverityCode())) {
				criticalCnt++;
			} else if ("MAJ".equals(defect.getSeverityCode())) {
				majorCnt++;
			} else if ("MIN".equals(defect.getSeverityCode())) {
				minorCnt++;
			} else if ("CRC".equals(defect.getSeverityCode())) {
				crcCnt++;
			} else if ("ETC".equals(defect.getSeverityCode())) {
				etcCnt++;
			}
		}
		
		if(this.exepectedDefectCount != -1){
			if(this.exepectedDefectCount == totalCnt){
				System.out.println("\t# Check Defect Count : OK");
			} else {
				isPassed = false;
				System.out.println("\t# Check Defect Count : Fail - expected:" + this.exepectedDefectCount + " real:" + totalCnt);
			}
		}
		
		if(!Strings.isNullOrEmpty(this.projectName)){
			if(this.projectName.equals(result.getProjectName())){
				System.out.println("\t# Check ProjectName : OK");
			} else {
				isPassed = false;
				System.out.println("\t# Check ProjectName : Fail - expected:" + this.projectName + " real:" + result.getProjectName());
			}
		}
		
		if(!Strings.isNullOrEmpty(this.fileName)){
			if(this.fileName.equals(result.getFileName())){
				System.out.println("\t# Check FileName : OK");
			} else {
				isPassed = false;
				System.out.println("\t# Check FileName : Fail - expected:" + this.fileName + " real:" + result.getFileName());
			}
		}
		System.out.println("\t# Total Defect Count : " + this.totalCnt);
		for(Defect defect : result.getDefectList()){
			System.out.println("\t\t" + defect.getCheckerCode() + " occ:" + defect.getOccurences().size());
		}
		
		
		
		System.out.println("\t# Check Defect List...");
		boolean isAllDefectSame = true;
		int i = 1;
		for(Defect d1 : expectedDefectList){
			boolean hasExpectedDefect = false;
			
			for(Defect d2 : result.getDefectList()){
				if(d1.getSeverityCode().equals(d2.getSeverityCode())){
					if(!d1.getCheckerCode().equals(d2.getCheckerCode())){
						continue;
					}
					
					if(!d1.getToolName().equals(d2.getToolName())){
						continue;
					}
					
					if(!d1.getLanguage().equals(d2.getLanguage())){
						continue;
					}
					
					if(!d1.getFileName().equals(d2.getFileName())){
						continue;
					}
					
					boolean isAllOccurenceSame = true;
					for(Occurence o2 : d2.getOccurences()){
						boolean isOccurenceSame = false;
						
						for(Occurence o1 : d1.getOccurences()){
							if(o2.equals(o1)){
								isOccurenceSame = true;
								break;
							}
						}
						
						if(isOccurenceSame == false){
							isAllOccurenceSame = false;
							break;
						}
					}
					
					hasExpectedDefect = true & isAllOccurenceSame;
				}
			}
			
			isAllDefectSame = isAllDefectSame & hasExpectedDefect;
			
			if(hasExpectedDefect){
				System.out.println("\t\t- Defect #" + i++ + " : Ok => defect message - " + d1.getMessage());
			} else {
				isPassed = false;
				Gson gson = new Gson();
				System.out.println("\t\t- Defect #" + i++ + " : Fail => extected Defect - " + gson.toJson(d1));
			}
		}
		
		if(isAllDefectSame){
			System.out.println("\t# Check All Defects : OK");
		} else {
			isPassed = false;
			Gson gson = new Gson();
			System.out.println("\t# Check all the defects : Fail");
			System.out.println("\t\t> texpected defect list : " + gson.toJson(this.expectedDefectList));
			System.out.println("\t\t>     treal defect list : " + gson.toJson(result.getDefectList()));
		}
		
		
		System.out.println("### END TEST for the result of static analysis");
		System.out.println("=================================================================================");
		
		if(isPassed == false){
			throw new RuntimeException("Failed to pass the test. check the messages on the console.");
		}
		
		testHandler.testAfterHandlingAnalysisResult(result);
    }

	/**
	 * @param count
	 */
    public void setExpectedDefectCount(final int count) {
    	this.exepectedDefectCount = count;
    }

	/**
	 * @return the totalCnt
	 */
	public int getTotalCnt() {
		return totalCnt;
	}

	/**
	 * @return the criticalCnt
	 */
	public int getCriticalCnt() {
		return criticalCnt;
	}

	/**
	 * @return the majorCnt
	 */
	public int getMajorCnt() {
		return majorCnt;
	}

	/**
	 * @return the minorCnt
	 */
	public int getMinorCnt() {
		return minorCnt;
	}

	/**
	 * @return the crcCnt
	 */
	public int getCrcCnt() {
		return crcCnt;
	}

	/**
	 * @return the etcCnt
	 */
	public int getEtcCnt() {
		return etcCnt;
	}

	/**
	 * @param d 
	 */
    public void addExpectedDefect(final Defect d) {
    	if(!expectedDefectList.contains(d)){
    		expectedDefectList.add(d);
    	}
    }

	/**
	 * @param projectName 
	 */
    public void setExpectedProjectName(final String projectName) {
    	this.projectName = projectName;
    }

    public void setExpectedFileName(final String fileName) {
    	this.fileName = fileName;
    }
}
