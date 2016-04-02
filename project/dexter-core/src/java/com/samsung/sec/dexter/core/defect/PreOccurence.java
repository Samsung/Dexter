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
package com.samsung.sec.dexter.core.defect;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.samsung.sec.dexter.core.BaseDefect;
import com.samsung.sec.dexter.core.analyzer.ResultFileConstant;

public class PreOccurence extends BaseDefect {
	private String occurenceCode = "";
	private int charStart = -1;
	private int charEnd = -1;
	private int startLine = -1;
	private int endLine = -1;
	private String variableName = "";
	private String stringValue = "";
	private String fieldName = "";
	private String message = "";
	private String severityCode = "";
	private String categoryName = "";

	
	public String getCategoryName() {
		return categoryName;
	}

	/**
	 * @param severityCode the severityCode to set
	 */
	public void setSeverityCode(final String severityCode) {
		this.severityCode = severityCode;
	}

	public void setCategoryName(final String categoryName){
		this.categoryName = categoryName;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		
		if(super.equals(obj) == false){
			return false;
		}
		
		final PreOccurence other = (PreOccurence) obj;
		
		if(!Objects.equal(this.startLine, other.startLine)){
			return false;
		}
		
		if(!Objects.equal(this.endLine, other.endLine)){
			return false;
		}
		
		if(!Objects.equal(this.charStart, other.charStart)){
			return false;
		}
		
		if(!Objects.equal(this.charEnd, other.charEnd)){
			return false;
		}
		
		if(!Strings.isNullOrEmpty(this.fieldName) && !Objects.equal(this.fieldName, other.fieldName)){
			return false;
		}
		
		if(!Strings.isNullOrEmpty(this.variableName) && !Objects.equal(this.variableName, other.variableName)){
			return false;
		}
		
		if(!Strings.isNullOrEmpty(this.stringValue) && !Objects.equal(this.stringValue, other.stringValue)){
			return false;
		}
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
	    return Objects.hashCode(checkerCode, getFieldName(), startLine, endLine);
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
	    return Objects.toStringHelper(this)
	    		.add(ResultFileConstant.TOOL_NAME, toolName)
	    		.add(ResultFileConstant.LANGUAGE, language)
	    		.add(ResultFileConstant.CHECKER_CODE, checkerCode)
	    		.add(ResultFileConstant.FILE_NAME, getFileName())
	    		.add(ResultFileConstant.START_LINE, this.startLine)
	    		.add(ResultFileConstant.END_LINE, endLine)
	    		.add(ResultFileConstant.CHAR_START, this.charStart)
	    		.add(ResultFileConstant.CHAR_END, charEnd)
	    		.add(ResultFileConstant.MODULE_PATH, getModulePath())
	    		.add(ResultFileConstant.CLASS_NAME, this.className)
	    		.add(ResultFileConstant.VARIABLE_NAME, this.variableName)
	    		.add(ResultFileConstant.STRING_VALUE, this.stringValue).toString();
	}
	
	public Occurence toOccurence(){
		final Occurence occurence = new Occurence();
		
		occurence.setStartLine(startLine);
		occurence.setEndLine(endLine);
		occurence.setVariableName(variableName);
		occurence.setStringValue(stringValue);
		occurence.setFieldName(fieldName);
		occurence.setCharStart(this.charStart);
		occurence.setCharEnd(this.charEnd);
		occurence.setMessage(message);
		occurence.setCode(this.occurenceCode);
		
		return occurence;
	}
	
	public Defect toDefect(){
		final Defect defect = new Defect();
		
		defect.setCheckerCode(checkerCode);
		defect.setClassName(className);
		defect.setFileName(getFileName());
		defect.setModulePath(getModulePath());
		defect.setMethodName(methodName);
//		defect.setMessage(message);		// ==> needs to summarize sub occurrences' messages
		defect.setSeverityCode(severityCode);
		defect.setCategoryName(categoryName);
		defect.setLanguage(language);
		defect.setToolName(toolName);
		
		return defect;
	}
	
	/**
	 * @param startLine void
	 */
    public void setStartLine(final int startLine) {
    	this.startLine = startLine;
    }

	/**
	 * @param endLine void
	 */
    public void setEndLine(final int endLine) {
    	this.endLine = endLine;
    }

	/**
	 * @param name void
	 */
    public void setVariableName(final String name) {
    	this.variableName = name;
    }

	/**
	 * @param value void
	 */
    public void setStringValue(final String value) {
    	this.stringValue = value;
    }

	/**
	 * @param fieldName void
	 */
    public void setFieldName(final String fieldName) {
    	this.fieldName = fieldName;
    }

	/**
	 * @return the startLine
	 */
	public int getStartLine() {
		return startLine;
	}

	/**
	 * @return the endLine
	 */
	public int getEndLine() {
		return endLine;
	}

	/**
	 * @return the variableName
	 */
	public String getVariableName() {
		return variableName;
	}

	/**
	 * @return the stringValue
	 */
	public String getStringValue() {
		return stringValue;
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}


	/**
	 * @return the charStart
	 */
	public int getCharStart() {
		return charStart;
	}

	/**
	 * @param charStart the charStart to set
	 */
	public void setCharStart(final int charStart) {
		this.charStart = charStart;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(final String message) {
		this.message = message;
	}

	/**
	 * @return the charEnd
	 */
	public int getCharEnd() {
		return charEnd;
	}

	/**
	 * @param charEnd the charEnd to set
	 */
	public void setCharEnd(int charEnd) {
		this.charEnd = charEnd;
	}

	/**
	 * @return the occurenceCode
	 */
	public String getOccurenceCode() {
		return occurenceCode;
	}

	/**
	 * @param occurenceCode the occurenceCode to set
	 */
	public void setOccurenceCode(final String occurenceCode) {
		this.occurenceCode = occurenceCode;
	}

	/**
	 * @return 
	 */
    public String toJson() {
	    return new Gson().toJson(this);
    }
}
