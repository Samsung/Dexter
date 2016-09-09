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

import java.io.File;
import java.util.List;

import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;

import org.apache.log4j.Logger;

import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.DexterUtil;

public class PluginInitializerForUT implements IDexterPluginInitializer {
	static Logger logger = Logger.getLogger(PluginInitializerForUT.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.samsung.sec.dexter.executor.DexterPluginInitializer#init(java.util.List)
	 */
	@Override
	public void init(final List<IDexterPlugin> pluginHandlerList){
		String pluginBasePath = "lib";
		File pluginBaseDir = new File(pluginBasePath);
		
		if(pluginBaseDir.exists() == false){
			throw new DexterRuntimeException("there is no exist DEXTER_HOME : " + pluginBasePath);
		}
		
		File[] files = DexterUtil.getSubFiles(pluginBaseDir);
		if(files.length == 0){
			throw new DexterRuntimeException("there is no exist plug-in(s)");
		}

		for(File file : files){
			if(file.exists() == false || file.isFile() == false){
				continue; 
			}
			
			logger.info("reading plugin info from " + file.toPath());
			PluginManager pm = PluginManagerFactory.createPluginManager();

			pm.addPluginsFrom(file.toURI());
			IDexterPlugin handler = pm.getPlugin(IDexterPlugin.class);

			if (handler == null) {
				logger.error("There is no plugin file in path: " + file.toURI());
				continue;
			}
			
			// IDexterPlugin handler = new PluginManagerImpl(); // delete this line when deploy

			addHandler(pluginHandlerList, handler);
		}
		
		if(pluginHandlerList.size() > 0){
			initAllHandler(pluginHandlerList);
		} else {
			throw new DexterRuntimeException("There are no dexter plug-ins to add");
		}
	}
	
	private void addHandler(final List<IDexterPlugin> pluginHandlerList, final IDexterPlugin handler){
		PluginDescription pd = handler.getDexterPluginDescription();

		for(int i = 0; i < pluginHandlerList.size(); i++){
			IDexterPlugin h = pluginHandlerList.get(i);
			PluginDescription pd1 = h.getDexterPluginDescription();
			
			if(pd.getPluginName().equals(pd1.getPluginName())){
				if(pd.getVersion().compare(pd1.getVersion()) > 0){	// if it has bigger version, replace it.
					pluginHandlerList.remove(i);
					pluginHandlerList.add(handler);
					return;
				}
			}
		}
		
		if(!pluginHandlerList.contains(handler)){
			pluginHandlerList.add(handler);
		}
	}
	
	/**
	 * @param pluginHandlerList 
	 */
    private void initAllHandler(final List<IDexterPlugin> pluginHandlerList) {
    	for(int i = 0; i < pluginHandlerList.size(); i++){
    		IDexterPlugin handler = pluginHandlerList.get(i);

   			handler.init();
			
			logger.info(handler.getDexterPluginDescription().getPluginName() + " plugin is loaded successfully");
    	}
    }
}
