/**
 * Copyright (c) 2014 Samsung Electronics, Inc.,
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
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
package com.samsung.sec.dexter.executor;

import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.plugin.IDexterPlugin;
import com.samsung.sec.dexter.core.plugin.IDexterPluginInitializer;
import com.samsung.sec.dexter.core.plugin.PluginDescription;
import com.samsung.sec.dexter.core.util.DexterUtil;
import com.samsung.sec.dexter.executor.cli.ICLILog;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;

public class CLIPluginInitializer implements IDexterPluginInitializer {
    private final static Logger logger = Logger.getLogger(CLIPluginInitializer.class);
    private ICLILog cliLog;

    public CLIPluginInitializer(final ICLILog cliLog) {
        assert cliLog != null;

        this.cliLog = cliLog;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.samsung.sec.dexter.executor.DexterPluginInitializer#init(java.util.List)
     */
    @Override
    public void init(final List<IDexterPlugin> pluginHandlerList) {
        assert Strings.isNullOrEmpty(DexterConfig.getInstance().getDexterHome()) == false;

        final String pluginBasePath = DexterConfig.getInstance().getDexterHome() + "/plugin";
        final File pluginBaseDir = new File(pluginBasePath);

        if (pluginBaseDir.exists() == false) {
            throw new DexterRuntimeException("there is no exist DEXTER_HOME : " + pluginBasePath);
        }

        File[] files = DexterUtil.getSubFiles(pluginBaseDir);
        if (files.length == 0) {
            return;
        }

        for (final File file : files) {
            if (file.isFile() == false) {
                continue;
            }

            logger.info("reading plugin info from " + file.toPath());

            final PluginManager pm = PluginManagerFactory.createPluginManager();
            pm.addPluginsFrom(file.toURI());
            final IDexterPlugin plugin = pm.getPlugin(IDexterPlugin.class);

            if (plugin == null) {
                logger.error("There is no plugin file in path: " + file.toURI());
                continue;
            }
            addHandler(pluginHandlerList, plugin);
        }

        initAllHandler(pluginHandlerList);
    }

    private void addHandler(final List<IDexterPlugin> pluginHandlerList, final IDexterPlugin handler) {
        final PluginDescription pd = handler.getDexterPluginDescription();

        for (int i = 0; i < pluginHandlerList.size(); i++) {
            final IDexterPlugin h = pluginHandlerList.get(i);
            final PluginDescription pd1 = h.getDexterPluginDescription();

            if (pd.getPluginName().equals(pd1.getPluginName())) {
                if (pd.getVersion().compare(pd1.getVersion()) > 0) { // if it has bigger version, replace it.
                    pluginHandlerList.remove(i);
                    pluginHandlerList.add(handler);
                    return;
                }
            }
        }

        if (!pluginHandlerList.contains(handler)) {
            pluginHandlerList.add(handler);
        }
    }

    /**
     * @param pluginHandlerList
     */
    private void initAllHandler(final List<IDexterPlugin> pluginHandlerList) {
        if (pluginHandlerList.size() == 0) {
            throw new DexterRuntimeException("There are no dexter plug-ins to add");
        }

        StringBuilder err = new StringBuilder(500);

        for (int i = 0; i < pluginHandlerList.size(); i++) {
            final IDexterPlugin plugin = pluginHandlerList.get(i);

            try {
                plugin.init();

                PluginDescription desc = plugin.getDexterPluginDescription();
                cliLog.printMessageWhenPluginLoaded(desc);

                logger.info(plugin.getDexterPluginDescription().getPluginName() + " plugin is loaded successfully.\n");
            } catch (DexterRuntimeException e) {
                err.append("Failed to initialize " + plugin.getDexterPluginDescription().getPluginName());
                pluginHandlerList.remove(i--);
            }
        }

        err.trimToSize();
        if (!Strings.isNullOrEmpty(err.toString())) {
            throw new DexterRuntimeException(err.toString());
        }
    }
}
