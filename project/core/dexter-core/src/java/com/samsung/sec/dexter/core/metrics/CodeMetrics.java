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
package com.samsung.sec.dexter.core.metrics;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.TargetFile;

public class CodeMetrics extends TargetFile{
	private long createdDateTime = System.currentTimeMillis();
	private Map<String, Object> metrics = new HashMap<String, Object>(10);
	final static Logger logger = Logger.getLogger(CodeMetrics.class);
	
	/**
	 * @return the createdDateTime
	 */
	public long getCreatedDateTime() {
		return createdDateTime;
	}
	/**
	 * @return the metrics
	 */
	public Map<String, Object> getMetrics() {
		return metrics;
	}
	/**
	 * @param createdDateTime the createdDateTime to set
	 */
	public void setCreatedDateTime(final long createdDateTime) {
		this.createdDateTime = createdDateTime;
	}
	/**
	 * @param metrics the metrics to set
	 */
	public void setMetrics(final Map<String, Object> metrics) {
		this.metrics = metrics;
	}
	
	public void addMetric(final String key, final String value){
		if(Strings.isNullOrEmpty(key) || Strings.isNullOrEmpty(value)){
			logger.error("Invalid Parameter : key or value is null or empty");
			return;
		}
		
		if(!this.metrics.containsKey(key)){
			this.metrics.put(key, value);
		}
	}
	
	public void addMetric(final String key, final int value){
		if(Strings.isNullOrEmpty(key)){
			logger.error("Invalid Parameter : key is null or empty");
			return;
		}
		
		if(!this.metrics.containsKey(key)){
			this.metrics.put(key, value);
		}
	}
	
	public void addMetric(final String key, final float value){
		if(Strings.isNullOrEmpty(key)){
			logger.error("Invalid Parameter : key is null or empty");
			return;
		}
		
		if(!this.metrics.containsKey(key)){
			this.metrics.put(key, value);
		}
	}
	
	public void setMetric(final String key, final String value){
		if(Strings.isNullOrEmpty(key) || Strings.isNullOrEmpty(value)){
			logger.error("Invalid Parameter : key or value is null or empty");
			return;
		}
		
		this.metrics.put(key, value);
	}
	
	public void setMetric(final String key, final int value){
		if(Strings.isNullOrEmpty(key)){
			logger.error("Invalid Parameter : key is null or empty");
			return;
		}
		
		this.metrics.put(key, value);
	}
	
	public void setMetric(final String key, final float value){
		if(Strings.isNullOrEmpty(key)){
			logger.error("Invalid Parameter : key is null or empty");
			return;
		}
		
		this.metrics.put(key, value);
	}
	
	public Object getMetric(final String key){
		if(Strings.isNullOrEmpty(key)){
			logger.error("Invalid Parameter : key is null or empty");
			return "";
		}
		
		return this.metrics.get(key);
	}
	
	public String getMetricString(final String key){
		if(Strings.isNullOrEmpty(key)){
			logger.error("Invalid Parameter : key is null or empty");
			return "";
		}
		
		return (String) this.metrics.get(key);
	}
	
	public float getMetricFloat(final String key){
		if(Strings.isNullOrEmpty(key)){
			logger.error("Invalid Parameter : key is null or empty");
			return -1;
		}
		
		return (float) this.metrics.get(key);
	}
}
