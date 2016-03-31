package com.samsung.sec.dexter.core.metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.TargetFile;

public class FunctionMetrics extends TargetFile {
	private long createdDateTime = System.currentTimeMillis();
	private Map<String, Object> metrics = new HashMap<String, Object>(10);
	private List<Map<String, Object>> functionMetrics = new ArrayList<Map<String, Object>>();
	final static Logger logger = Logger.getLogger(FunctionMetrics.class);
	
	public long getCreatedDateTime(){
		return createdDateTime;
	}
	
	public Map<String, Object> getMetrics(){
		return metrics;
		
	}
	
	public List<Map<String, Object>> getFunctionMetrics(){
		return functionMetrics;
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
	
	
	
	public void setFunctionMetrics(final List<Map<String, Object>> functionMetrics){
		this.functionMetrics = functionMetrics;
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
}
