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
package com.samsung.sec.dexter.core.config;

public class DexterCode {
	private String codeKey;
	private String codeValue;
	private String codeName;
	private String description;
	/**
	 * @return the key
	 */
	public String getKey() {
	    return getCodeKey();
	}
	/**
	 * @return the codeKey
	 */
	public String getCodeKey() {
		return codeKey;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
	    return getCodeValue();
	}
	/**
	 * @return the codeValue
	 */
	public String getCodeValue() {
		return codeValue;
	}
	/**
	 * @return the name
	 */
	public String getName() {
	    return getCodeName();
	}
	/**
	 * @return the codeName
	 */
	public String getCodeName() {
		return codeName;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param key the key to set
	 */
	public void setKey(final String key) {
	    setCodeKey(key);
	}
	/**
	 * @param codeKey the codeKey to set
	 */
	public void setCodeKey(final String key) {
		this.codeKey = key;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(final String value) {
	    setCodeValue(value);
	}
	/**
	 * @param codeValue the codeValue to set
	 */
	public void setCodeValue(final String value) {
		this.codeValue = value;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(final String name) {
	    setCodeName(name);
	}
	/**
	 * @param codeName the codeName to set
	 */
	public void setCodeName(final String name) {
		this.codeName = name;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}
}
