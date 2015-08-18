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
package com.samsung.sec.dexter.core.filter;

import com.google.gson.Gson;
import com.samsung.sec.dexter.core.BaseDefect;
import com.samsung.sec.dexter.core.defect.Defect;

public class DefectFilter extends BaseDefect{
	private long fid;
	private boolean isActive;
	private transient int line = -1;
	private long createdDateTime;
	
	public String toJson(){
		final Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	public Defect toDefect() {
		final Defect defect = new Defect();
		
		defect.setToolName(toolName);
		defect.setLanguage(language);
		defect.setCheckerCode(checkerCode);
		defect.setFileName(getFileName());
		defect.setModulePath(getModulePath());
		defect.setClassName(className);
		defect.setMethodName(methodName);
		defect.setLanguage(this.language);
		defect.setToolName(this.toolName);
		
		return defect;
	}
	
	/* (non-Javadoc)
	 * @see com.samsung.sec.dexter.core.BaseDefect#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
	    return super.equals(obj);
	}
	
	/* (non-Javadoc)
	 * @see com.samsung.sec.dexter.core.BaseDefect#hashCode()
	 */
	@Override
	public int hashCode() {
	    return super.hashCode();
	}
	
	/**
	 * @return the isActive
	 */
	public boolean isActive() {
		return isActive;
	}
	/**
	 * @return the createdDateTime
	 */
	public long getCreatedDateTime() {
		return createdDateTime;
	}
	/**
	 * @param isActive the isActive to set
	 */
	public void setActive(final boolean isActive) {
		this.isActive = isActive;
	}
	/**
	 * @param createdDateTime the createdDateTime to set
	 */
	public void setCreatedDateTime(final long createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	/**
	 * @return the fid
	 */
	public long getFid() {
		return fid;
	}

	/**
	 * @param fid the fid to set
	 */
	public void setFid(final long fid) {
		this.fid = fid;
	}

	/**
	 * @return the line
	 */
	public int getLine() {
		return line;
	}

	/**
	 * @param line the line to set
	 */
	public void setLine(final int line) {
		this.line = line;
	}
}
