/**
 * Copyright (c) 2016 Samsung Electronics, Inc.,
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

//import com.google.gson.Gson;
import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.checker.CheckerConfig;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.IDexterHomeListener;
import com.samsung.sec.dexter.core.exception.DexterException;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.DexterClient;
import com.samsung.sec.dexter.core.util.IDexterClient;

public class BaseDexterPluginManager implements IDexterPluginManager, IDexterHomeListener{
	private final static Logger LOG = Logger.getLogger(BaseDexterPluginManager.class);

	protected List<IDexterPlugin> pluginList = new ArrayList<IDexterPlugin>(0);
	protected IDexterPluginInitializer initializer;

	public BaseDexterPluginManager(IDexterPluginInitializer pluginInitializer) {
		assert pluginInitializer != null;
		
		DexterConfig.getInstance().addDexterHomeListener(this);
		this.initializer = pluginInitializer;
	}
	
//	private void initDexterPluginsForCLI(final IDexterPluginInitializer pluginInitializer){
//		setDexterPluginInitializer(pluginInitializer);
//		initDexterPlugins();
//
//		if(getPluginList().size() == 0){
//			cliLog.printErrorMessageWhenNoPlugins(config.getDexterPluginFolderPath());
//			exit(1);
//		}
//		
//		for(IDexterPlugin plugin : getPluginList()){
//			PluginDescription desc = plugin.getDexterPluginDescription();
//			cliLog.printMessageWhenPluginLoaded(desc);
//			
//			resetCheckerEnable(desc.getPluginName(), desc.getLanguage().toString(), 
//					plugin.getCheckerConfig().getCheckerList());
//		}
//	}

	@Override
	public void initDexterPlugins() throws DexterRuntimeException{
		assert initializer != null;
		
		pluginList = new ArrayList<IDexterPlugin>(0);
		IDexterClient client = DexterClient.getInstance();
		
		initializer.init(pluginList);
		initSupportingFileExetensions();
		if(!DexterConfig.getInstance().isStandalone()
				&& client.isServerAlive()){  
			updateCheckerConfig();
		}
		LOG.info("Dexter plug-ins initialized successfully");
	}
	
	protected void initSupportingFileExetensions() {
		DexterConfig.getInstance().removeAllSupportingFileExtensions();
		for(IDexterPlugin plugin : pluginList){
			DexterConfig.getInstance().addSupprotingFileExtensions(plugin.getSupportingFileExtensions());
		}
    }

	protected void updateCheckerConfig() {
		Thread updateCheckerThread = new Thread(){
			@Override
			public void run() {
				for (int i=0; i< pluginList.size(); i++) {
					IDexterPlugin plugin = pluginList.get(i);
					String pluginName = plugin.getDexterPluginDescription().getPluginName();
					CheckerConfig cc = DexterClient.getInstance().getDexterPluginChecker(plugin, pluginName);
					plugin.setCheckerConfig(cc);
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
	
	@Override
	public List<AnalysisResult> analyze(final AnalysisConfig config){
		List<AnalysisResult> resultList = new ArrayList<AnalysisResult>();
		
	    for (final IDexterPlugin plugin : pluginList) {
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
	
	@Override
	public CheckerConfig getCheckerConfig(final String pluginName){
		assert Strings.isNullOrEmpty(pluginName) == false;
		
		for (final IDexterPlugin plugin : pluginList) {
			if (pluginName.equals(plugin.getDexterPluginDescription().getPluginName())) {
				final CheckerConfig config = plugin.getCheckerConfig();
				return config;
			}
		}

		throw new DexterRuntimeException("there is no proper Checker Config info for " + pluginName);
	}

	@Override
	public void setCheckerConfig(final String pluginName, final CheckerConfig config) {
		assert !Strings.isNullOrEmpty(pluginName);
		assert config != null;
		
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
		return pluginList;
	}

	public void setDexterPluginInitializer(final IDexterPluginInitializer initializer) {
		this.initializer = initializer;
    }

	@Override
    public void handleDexterHomeChanged(final String oldPath, final String newPath) {
		initDexterPlugins();
		
		for(IDexterPlugin plugin : pluginList)
			plugin.handleDexterHomeChanged(oldPath, newPath);
    }
}
