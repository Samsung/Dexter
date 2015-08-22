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
package com.samsung.sec.dexter.core.plugin;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.primitives.Ints;

public class PluginVersion {
	private int major = 0;
	private int minor = 0;
	private int patch = 0;
	
	final static Logger logger = Logger.getLogger(PluginVersion.class);
	
	public PluginVersion(final int major, final int minor, final int patch){
		this.major = major;
		this.minor = minor;
		this.patch = patch;
	}
	
	public PluginVersion(final int ... versions){
		if(versions == null){
			logger.error("Invalid Parameter : versions is null");
			return;
		}
		
		if(versions[0] > -1){
			this.major = versions[0];
		}
		
		if(versions[1] > -1){
			this.minor = versions[1];
		}
		
		if(versions[2] > -1){
			this.patch = versions[2];
		}
	}
	
	/**
	 * [Mandatory] Method의 핵심 기능 및 사용된 알고리즘을 완전한 문장으로 기술한다.
	 *
	 * @param version
	 */
    public PluginVersion(final String version) {
    	if(Strings.isNullOrEmpty(version)){
    		logger.error("Invalid Parameter : version is null or empty");
    		return;
    	}
    	
    	final Iterator<String> iter = Splitter.on('.').trimResults().omitEmptyStrings().split(version).iterator();
    	
    	if(iter.hasNext()){
    		Integer value = Ints.tryParse(iter.next()); 
    		this.major = value == null ? 0 : value;
    	}
    	
    	if(iter.hasNext()){
    		Integer value = Ints.tryParse(iter.next());
    		this.minor = value == null ? 0 : value;
    	}
    	
    	if(iter.hasNext()){
    		Integer value = Ints.tryParse(iter.next());
    		this.patch = value == null ? 0 : value;
    	}
    }

	public String getVersion(){
		return major + "." + minor + "." + patch;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (getClass() != obj.getClass()){
			return false;
		}
		
		final PluginVersion other = (PluginVersion) obj;
		
		return Objects.equal(this.major, other.major)
				&& Objects.equal(this.minor, other.minor)
				&& Objects.equal(this.patch, other.patch);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(this.major, this.minor, this.patch);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getVersion();
//		return Objects.toStringHelper(this).addValue(this.major).addValue(".")
//				.addValue(this.minor).addValue(".").addValue(this.patch).toString();
	}
	
	
	/**
	 * @return the major
	 */
	public int getMajor() {
		return major;
	}
	/**
	 * @return the minor
	 */
	public int getMinor() {
		return minor;
	}
	/**
	 * @return the patch
	 */
	public int getPatch() {
		return patch;
	}
	/**
	 * @param major the major to set
	 */
	public void setMajor(final int major) {
		this.major = major;
	}
	/**
	 * @param minor the minor to set
	 */
	public void setMinor(final int minor) {
		this.minor = minor;
	}
	/**
	 * @param patch the patch to set
	 */
	public void setPatch(final int patch) {
		this.patch = patch;
	}

	/**
	 * @param version
	 * @return 
	 */
    public int compare(final PluginVersion version) {
    	if(this.major > version.major){
    		return 1;
    	} else if(this.major < version.major){
    		return -1;
    	}
    	
    	if(this.minor > version.minor){
    		return 1;
    	} else if(this.minor < version.minor){
    		return -1;
    	}
    	
    	if(this.patch > version.patch){
    		return 1;
    	} else if(this.patch < version.patch){
    		return -1;
    	}
    	
	    return 0;
    }
}
