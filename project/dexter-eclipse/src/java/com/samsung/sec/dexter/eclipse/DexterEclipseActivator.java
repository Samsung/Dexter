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
package com.samsung.sec.dexter.eclipse;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.eclipse.builder.DexterResourceChangeHandler;
import com.samsung.sec.dexter.eclipse.ui.util.EclipseLog;
import com.samsung.sec.dexter.eclipse.ui.util.EclipseUtil;
import com.samsung.sec.dexter.eclipse.util.EmptyCDTUtil;
import com.samsung.sec.dexter.eclipse.util.EmptyJDTUtil;
import com.samsung.sec.dexter.eclipse.util.ICDTUtil;
import com.samsung.sec.dexter.eclipse.util.IJDTUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class DexterEclipseActivator extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "dexter-eclipse";
	private final LoadingCache<String, AnalysisConfig> configCache;
	private static DexterEclipseActivator plugin;
	private static final int SERVER_TIMEOUT = 500;
	public static EclipseLog LOG;
	private static IJDTUtil jdtUtil;
	private static ICDTUtil cdtUtil;

	static {
		if (DexterConfig.getInstance().getRunMode() != DexterConfig.RunMode.DAEMON)
			DexterConfig.getInstance().setRunMode(DexterConfig.RunMode.ECLIPSE);
	}

	/**
	 * The constructor
	 */
	public DexterEclipseActivator() {
		configCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).maximumSize(5)
				.build(new CacheLoader<String, AnalysisConfig>() {
					@Override
					public AnalysisConfig load(String key) throws Exception {
						return null;
					}

				});
	}

	public LoadingCache<String, AnalysisConfig> getConfigCache() {
		return this.configCache;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.
	 * BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		setPlugin(this);

		LOG = new EclipseLog(PLUGIN_ID);
		LOG.setPlugin(this);

		CheckPlatzServer();

		DexterResourceChangeHandler.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.
	 * BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		setPlugin(null);
		super.stop(context);
	}

	private static void setPlugin(DexterEclipseActivator p) {
		plugin = p;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static DexterEclipseActivator getDefault() {
		return plugin;
	}

	public synchronized static IJDTUtil getJDTUtil() {
		if (jdtUtil == null) {
			jdtUtil = (IJDTUtil) EclipseUtil.loadSingleExtensionObject(DexterEclipseActivator.PLUGIN_ID, "JDTUtil",
					EmptyJDTUtil.class);
		}

		return jdtUtil;
	}

	public synchronized static ICDTUtil getCDTUtil() {
		if (cdtUtil == null) {
			cdtUtil = (ICDTUtil) EclipseUtil.loadSingleExtensionObject(DexterEclipseActivator.PLUGIN_ID, "CDTUtil",
					EmptyCDTUtil.class);
		}

		return cdtUtil;
	}

	private void CheckPlatzServer() {
		try {
			if (!InetAddress.getByName(DexterConfig.PLATZ_DOMAIN).isReachable(SERVER_TIMEOUT)) {
				java.lang.System.setProperty("isPlatzAlive", "true");
			}
		} catch (UnknownHostException e) {
			java.lang.System.setProperty("isPlatzAlive", "False");
		} catch (IOException e) {
			java.lang.System.setProperty("isPlatzAlive", "False");
		}
	}

}
