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
package com.samsung.sec.dexter.core.checker;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.plugin.PluginVersion;
import com.samsung.sec.dexter.core.util.DexterUtil;

public class Checker {
	private String code;
	private String name;
	private String type;	//{BOTH | DEV | REVIEW | NONE}
	private String categoryName;
	private String severityCode;
	private String version;
	private StringBuilder description = new StringBuilder();
	private boolean isActive;
	private Map<String, String> properties = new HashMap<String, String>();
	private int cwe;
	
	static Logger logger = Logger.getLogger(Checker.class);
	
	public Checker(String code, String name, String version, boolean isActive){
		this.name = name;
		this.version = version;
		this.code = code;
		this.isActive = isActive;
	}
	
	public Checker(String code, String name, String version, String description, boolean isActive){
		this.name = name;
		this.version = version;
		this.code = code;
		this.isActive = isActive;
		this.description = new StringBuilder(description);
	}
	
	
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
		
		final Checker other = (Checker) obj;
		
		return Objects.equal(this.name, other.name)
			&& Objects.equal(this.code, other.code)
			&& Objects.equal(this.severityCode, other.severityCode)
			&& Objects.equal(this.version, other.version)
			&& Objects.equal(this.description, other.description)
			&& Objects.equal(this.isActive, other.isActive)
			&& Objects.equal(this.properties, other.properties);
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
	    return Objects.hashCode(this.name, this.code, this.severityCode, this.version);
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder msg = new StringBuilder();
		msg.append(name).append(" ").append(this.version).append(" ").append(isActive)
			.append(" ").append(description);
		
		
	    return msg.toString();
	}
	
	public String getProperty(final String key){
		if(Strings.isNullOrEmpty(key)){
			logger.error("Invalide Paramether: key");
			return "";
		}
		
		return this.properties.get(key);
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the version
	 */
	public PluginVersion getVersion() {
		return new PluginVersion(version);
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description.toString();
	}
	/**
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(final PluginVersion version) {
		this.version = version.getVersion();
	}
	
	/**
	 * @param description the description to set
	 */
	public void setDescription(final String description) {
		this.description = new StringBuilder(description);
		
		if(this.properties.size() > 0 && description.indexOf("${") > 0){
			replaceDescriptionWithProperties();
		}
	}
	
	private void replaceDescriptionWithProperties() {
		for(String key : this.properties.keySet()){
			final String value = this.properties.get(key);
			replaceDescriptionWithProperty(key, value);
		}
    }

	private void replaceDescriptionWithProperty(String key, final String value) {
	    String $key = "${" + key + "}";
	    
	    int begin = this.description.indexOf($key);
	    while (begin != -1){
	    	this.description.replace(begin, begin+$key.length(), value);
	    	
	    	begin = this.description.indexOf($key);
	    }
    }
	
	public void addProperty(String key, String value){
		this.properties.put(key, value);
		
		replaceDescriptionWithProperty(key, value);
	}

	/**
	 * @return the isActive
	 */
	public boolean isActive() {
		return isActive;
	}
	/**
	 * @param isActive the isActive to set
	 */
	public void setActive(final boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}


	/**
	 * @return the severity
	 */
	public String getSeverityCode() {
		return severityCode;
	}

	/**
	 * @return the properties
	 */
	public Map<String, String> getProperties() {
		return properties;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(final String code) {
		this.code = code;
	}

	/**
	 * @param severity the severity to set
	 */
	public void setSeverityCode(final String severityCode) {
		this.severityCode = severityCode;
	}

//	/**
//	 * @param properties the properties to set
//	 */
//	public void setProperties(final Map<String, String> properties) {
//		this.properties = properties;
//	}


	/**
	 * [Mandatory] Method의 핵심 기능 및 사용된 알고리즘을 완전한 문장으로 기술한다.
	 * 
	 * @param string void
	 */
    public void addDescriptionWithNewLine(final String string) {
    	description.append(string).append(DexterUtil.LINE_SEPARATOR);
    }

	/**
	 * @param cwEid void
	 */
    public void setCWE(final int cwe) {
    	this.cwe = cwe;
    }

	/**
	 * @return the cwe
	 */
	public int getCwe() {
		return cwe;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(final StringBuilder description) {
		this.description = description;
	}

	/**
	 * @param cwe the cwe to set
	 */
	public void setCwe(final int cwe) {
		this.cwe = cwe;
	}

	public String getCategoryName() {
		return this.categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public void setType(String type) {
		this.type = type;
    }
	
	public String getType(){
		return this.type;
	}
}
