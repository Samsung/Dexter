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

import com.google.common.base.Objects;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.DexterConfig.LANGUAGE;

public class PluginDescription {
	protected String pluginName;
	protected DexterConfig.LANGUAGE language;
	protected String thirdPartyName;
	protected PluginVersion version;
	protected boolean isActive;
	protected String description; 
	
    public PluginDescription(final String thirdPartyName, final String pluginName, final PluginVersion version, final LANGUAGE language, final String descrition) {
    	this.thirdPartyName = thirdPartyName;
    	this.pluginName = pluginName;
    	this.version = version;
    	this.language = language;
    	this.description = descrition;
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
    	
    	final PluginDescription other = (PluginDescription) obj;
    	
    	return
    			Objects.equal(this.pluginName, other.pluginName)
    			&& Objects.equal(this.thirdPartyName, other.thirdPartyName)
    			&& this.version.equals(other.version)
    			&& this.language.equals(other.language);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
    	return Objects.hashCode(this.pluginName, this.version, this.language, this.thirdPartyName, this.isActive);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	return Objects.toStringHelper(this).addValue(this.pluginName).addValue(this.version).addValue(" by ").addValue(this.thirdPartyName).toString();
    }

	public String getPluginName(){
		return this.pluginName;	}
	
	public String get3rdPartyName(){
		return this.thirdPartyName;
	}
	
	public PluginVersion getVersion(){
		return this.version;
	}
	
	public String getDescription(){
		return description;
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

	public DexterConfig.LANGUAGE getLanguage() {
	    return this.language;
    }
	
	public void setLanguage(DexterConfig.LANGUAGE language) {
	    this.language = language;
    }
}
