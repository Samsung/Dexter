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

public class Occurence {
	/** if occurence is different with Defect's checker code, you can use this field(option) */
	private String code = "";
	
	/** the beginning line for the defect (mandatory) */
	private int startLine = -1;
	
	/** the end line for the defect (mandatory) */
	private int endLine = -1;
	
	/** the beginning offset for the defect (option). if not applicable, use -1 */
	private int charStart = -1;
	
	/** the end offset for the defect (option). if not applicable, use -1 */
	private int charEnd = -1;
	
	/** if the defect is related to a variable, you can use this field to inform (option) */
	private String variableName = "";
	
	/** if the defect is related to a String object, you can use this field to inform (option) */
	private String stringValue = "";
	
	/** if the defect is related to a field, you can use this field to inform (option) */
	private String fieldName = "";
	
	/** the message to describe the defect (mandatory) */
	private String message = "";
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if(obj == null){
			return false;
		}
		
		if(this.getClass() != obj.getClass()){
			return false;
		}
		
		final Occurence other = (Occurence) obj;
		
		if(!Objects.equal(this.startLine, other.startLine)){
			return false;
		}
		
		if(!Objects.equal(this.endLine, other.endLine)){
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
		
		if(!Strings.isNullOrEmpty(this.message) && !Objects.equal(this.message, other.message)){
			return false;
		}
		
		return true;
	}
	
	public boolean equalsWithPreOccurence(final Object obj) {
		if(obj == null){
			return false;
		}
		
		if(PreOccurence.class != obj.getClass()){
			return false;
		}
		
		final PreOccurence other = (PreOccurence) obj;
		
		if(!Objects.equal(this.startLine, other.getStartLine())){
			return false;
		}
		
		if(!Objects.equal(this.endLine, other.getEndLine())){
			return false;
		}
		
		if(!Strings.isNullOrEmpty(this.fieldName) && !Objects.equal(this.fieldName, other.getFieldName())){
			return false;
		}
		
		if(!Strings.isNullOrEmpty(this.variableName) && !Objects.equal(this.variableName, other.getVariableName())){
			return false;
		}
		
		if(!Strings.isNullOrEmpty(this.stringValue) && !Objects.equal(this.stringValue, other.getStringValue())){
			return false;
		}
		
		if(!Strings.isNullOrEmpty(this.message) && !Objects.equal(this.message, other.getMessage())){
			return false;
		}
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
	    return Objects.hashCode(startLine, endLine);
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
	    return Objects.toStringHelper(this).add("startLine", this.startLine)
	    		.add("endLine", endLine).add("variableName", this.variableName)
	    		.add("stringValue", this.stringValue).toString();
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
	 * @return int
	 */
    public int getCharEnd() {
	    return this.charEnd;
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
	 * @param charEnd the charEnd to set
	 */
	public void setCharEnd(final int charEnd) {
		this.charEnd = charEnd;
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
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(final String code) {
		this.code = code;
	}
}
