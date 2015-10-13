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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.util.DexterClient;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.analyzer.IDexterPluginInitializer;
import com.samsung.sec.dexter.core.checker.CheckerConfig;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.IDexterHomeListener;
import com.samsung.sec.dexter.core.exception.DexterException;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;

public class DexterPluginManager implements IDexterHomeListener{
	private final static Logger LOG = Logger.getLogger(DexterPluginManager.class);

	private List<IDexterPlugin> pluginList = new ArrayList<IDexterPlugin>(0);
	private boolean isInitialized = false;
	IDexterPluginInitializer initializer;
	
	private static class DexterPluginManagerHolder {
		private final static DexterPluginManager INSTANCE = new DexterPluginManager();
	}

	public static DexterPluginManager getInstance() {
		return DexterPluginManagerHolder.INSTANCE;
	}
	
	/**
	 * @return the isInitialized
	 */
	public boolean isInitialized() {
		return isInitialized;
	}

	private DexterPluginManager() {
		DexterConfig.getInstance().addDexterHomeListener(this);
		LOG.debug("DexterPluginManager");
	}

	public void initDexterPlugins() throws DexterRuntimeException{
		assert initializer != null;
		
		this.isInitialized = false;
		pluginList = new ArrayList<IDexterPlugin>(0);
		
		initializer.init(pluginList);
		initSupportingFileExetensions();
		initCheckerConfigJsonFile();
		this.isInitialized = true;
		LOG.info("Dexter plug-ins initialized successfully");
	}
	
	private void initSupportingFileExetensions() {
		DexterConfig.getInstance().removeAllSupportingFileExtensions();
		for(IDexterPlugin plugin : pluginList){
			DexterConfig.getInstance().addSupprotingFileExtensions(plugin.getSupportingFileExtensions());
		}
    }

	private void initCheckerConfigJsonFile() {
		Thread updateCheckerThread = new Thread(){
			@Override
			public void run() {
				Gson gson = new Gson();
				for (int i=0; i< pluginList.size(); i++) {
					IDexterPlugin plugin = pluginList.get(i);
					String pluginName = plugin.getDexterPluginDescription().getPluginName();
					
					final String configResult = DexterClient.getInstance().getDexterPluginCheckerJsonFile(pluginName);
					if(configResult.equals("failed")){
						LOG.info("There is no update "+ pluginName + " checker config");
					}
					else{
						CheckerConfig checkerConfig = gson.fromJson(configResult, CheckerConfig.class);
						plugin.setCheckerConfig(checkerConfig);
						LOG.info("Dexter "+ pluginName + " plug-ins Update successfully");
					}
				}
			}
		};
		
		if(DexterConfig.getInstance().getRunMode() == DexterConfig.RunMode.CLI){
			updateCheckerThread.run();
		}
		else {
			updateCheckerThread.start();
		}
	}
	
	public void destroy() throws DexterException {
		DexterConfig.getInstance().removeDexterHomeListener(this);
		for(IDexterPlugin plugin : this.pluginList){
			plugin.destroy();
		}
		
		pluginList.clear();
    }
	
	public void destroy(final String pluginName) throws DexterException{
		for (int i=0; i< pluginList.size(); i++) {
			final IDexterPlugin plugin = pluginList.get(i);
			
			if (pluginName.equals(plugin.getDexterPluginDescription().getPluginName())) {
				plugin.destroy();
				pluginList.remove(i);
				return;
			}
		}
	}
	
	public List<AnalysisResult> analyze(final AnalysisConfig config){
		List<AnalysisResult> resultList = new ArrayList<AnalysisResult>();
		
	    for (final IDexterPlugin plugin : DexterPluginManager.getInstance().getPluginList()) {
			if (plugin.supportLanguage(config.getLanguageEnum())) {
				try {
					resultList.add(plugin.analyze(config));
				} catch (IllegalStateException | NullPointerException e) {
					LOG.error("Analysis Exception: " + config.getSourceFileFullPath() 
							+ "\n" + e.getMessage(), e);
				}
			}
		}
	    
	    return resultList;
    }
	
	public CheckerConfig getCheckerConfig(final String pluginName){
		assert Strings.isNullOrEmpty(pluginName) == false;
		assert isInitialized != false;
		
		for (final IDexterPlugin plugin : pluginList) {
			if (pluginName.equals(plugin.getDexterPluginDescription().getPluginName())) {
				final CheckerConfig config = plugin.getCheckerConfig();
				return config;
			}
		}

		throw new DexterRuntimeException("there is no proper Checker Config info for " + pluginName);
	}

	public void setCheckerConfig(final String pluginName, final CheckerConfig config) {
		assert !Strings.isNullOrEmpty(pluginName);
		assert config != null;
		
		if(isInitialized == false){
			throw new DexterRuntimeException("Invalid status : isInitialized is false");
		}
		
		for (final IDexterPlugin plugin : this.pluginList) {
			if (plugin.getDexterPluginDescription().getPluginName().equals(pluginName)) {
				plugin.setCheckerConfig(config);
			}
		}
	}

	/**
	 * @return List<IDexterPlugin>
	 */
	public List<IDexterPlugin> getPluginList() {
		if(isInitialized == false){
			LOG.warn("Plugin initiation is not finished or not started yet." 
					 + " There is no static analysis plug-ins to execute."
					 + " check your plug-ins or login status");
			return new ArrayList<IDexterPlugin>(0);
		}
		
		return pluginList;
	}

	public void runDexterHomeChangeHandler(final String oldPath, final String newPath) 
			throws DexterException {
		for(IDexterPlugin plugin : this.pluginList){
			plugin.handleDexterHomeChanged(oldPath, newPath);
		}
    }

	public void setDexterPluginInitializer(final IDexterPluginInitializer initializer) {
		this.initializer = initializer;
    }

	@Override
    public void handleDexterHomeChanged() {
		initDexterPlugins();
    }
}
