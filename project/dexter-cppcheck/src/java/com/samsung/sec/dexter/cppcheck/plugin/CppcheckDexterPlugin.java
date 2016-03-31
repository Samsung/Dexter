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

package com.samsung.sec.dexter.cppcheck.plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import net.xeoh.plugins.base.annotations.PluginImplementation;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisEntityFactory;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.analyzer.IAnalysisEntityFactory;
import com.samsung.sec.dexter.core.checker.CheckerConfig;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.DexterConfig.LANGUAGE;
import com.samsung.sec.dexter.core.plugin.IDexterPlugin;
import com.samsung.sec.dexter.core.plugin.PluginDescription;
import com.samsung.sec.dexter.core.plugin.PluginVersion;
import com.samsung.sec.dexter.core.util.DexterUtil;

@PluginImplementation
public class CppcheckDexterPlugin implements IDexterPlugin {
	public final static String PLUGIN_NAME = "cppcheck";
	//public final static PluginVersion version = PluginVersion.fromImplementationVersion(CppcheckDexterPlugin.class);
	private PluginDescription pluginDescription;
	private CppcheckWrapper cppcheck = new CppcheckWrapper();
	private final static Logger logger = Logger.getLogger(CppcheckWrapper.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.samsung.sec.dexter.core.plugin.IDexterPlugin#init()
	 */
	@Override
	public void init() {
		cppcheck.initCheckerConfig();
	}
	
	/* (non-Javadoc)
	 * @see com.samsung.sec.dexter.core.plugin.IDexterPlugin#destroy()
	 */
	@Override
	public void destroy() {
		// do nothing
	}

	public boolean copyCppcheckRunModule() {
		String dexterHome = DexterConfig.getInstance().getDexterHome();
		if (Strings.isNullOrEmpty(dexterHome)) {
			logger.error("Can't initialize Cppcheck plugin, because the dexter_home is not initialized");
			return false;
		}

		// copy %DEXTER_HOME%/bin/cppcheck
		String zipFilePath = dexterHome;
		String cppcheckPath = "";

		if (DexterUtil.getOS() == DexterUtil.OS.WINDOWS) {
			zipFilePath += "/temp/cppcheck-windows_" + PluginVersion.fromImplementationVersion(CppcheckDexterPlugin.class) + ".zip";
			cppcheckPath = "/cppcheck-windows.zip";
		} else {
			return true;
			/*
			 * zipFilePath += "/temp/cppcheck-linux_" +
			 * CppcheckDexterPlugin.version.getVersion() + ".zip"; cppcheckPath
			 * = "/cppcheck-linux.zip";
			 */
		}

		final File file = new File(zipFilePath);
		if (!file.exists()) {
			final InputStream is = getClass().getResourceAsStream(cppcheckPath);
			if (is == null) {
				logger.error("can't find cppcheck.zip file: " + cppcheckPath);
				return false;
			}

			try {
				FileUtils.copyInputStreamToFile(is, file);

				if (DexterUtil.getOS() == DexterUtil.OS.WINDOWS) {
					DexterUtil.unzip(zipFilePath, dexterHome + CppcheckWrapper.CPPCHECK_HOME_DIR);
				} else { // LINUX or MAC
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				return false;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				return false;
			} finally {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.samsung.sec.dexter.core.plugin.IDexterPlugin#getSaPluginDescription()
	 */
	@Override
	public PluginDescription getDexterPluginDescription() {
		if (this.pluginDescription == null) {
			this.pluginDescription = new PluginDescription(CppcheckDexterPlugin.PLUGIN_NAME, PLUGIN_NAME, 
					PluginVersion.fromImplementationVersion(CppcheckDexterPlugin.class),
			        DexterConfig.LANGUAGE.CPP, "Dexter plug-in for Cppcheck");
		}
		return this.pluginDescription;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.samsung.sec.dexter.core.plugin.IDexterPlugin#analyze(com.samsung.
	 * sec.dexter.core.analyzer.AnalysisConfig,
	 * com.samsung.sec.dexter.core.analyzer.AnalysisResult)
	 */
	@Override
	public AnalysisResult analyze(final AnalysisConfig config) {
		if (config != null) {
			cppcheck.setAnalysisConfig(config);
		}
		
		File bin = new File(DexterConfig.getInstance().getDexterHome() + "/bin");
		if (bin.exists() == false){
			DexterConfig.getInstance().createInitialFolderAndFiles();
			copyCppcheckRunModule();
		}

		IAnalysisEntityFactory factory = new AnalysisEntityFactory();
		AnalysisResult result = factory.createAnalysisResult(config);
		cppcheck.analyze(result);
		
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.samsung.sec.dexter.core.plugin.IDexterPlugin#setCheckerConfig(com
	 * .samsung.sec.dexter.core.checker.CheckerConfig)
	 */
	@Override
	public void setCheckerConfig(final CheckerConfig cc) {
		cppcheck.setCheckerConfig(cc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.samsung.sec.dexter.core.plugin.IDexterPlugin#getCheckerConfig()
	 */
	@Override
	public CheckerConfig getCheckerConfig() {
		return cppcheck.getCheckerConfig();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.samsung.sec.dexter.core.plugin.IDexterPlugin#supportLanguage(com.
	 * samsung.sec.dexter.core.util.DexterConfig.LANGUAGE)
	 */
	@Override
	public boolean supportLanguage(final LANGUAGE language) {
		if (language == DexterConfig.LANGUAGE.C || language == DexterConfig.LANGUAGE.CPP) {
			return true;
		} else {
			return false;
		}
	}

	@Override
    public void handleDexterHomeChanged(String oldPath, String newPath) {
		copyCppcheckRunModule();
    }

	@Override
    public String[] getSupportingFileExtensions() {
		return new String[] {"c", "cpp", "h", "hpp"};
    }
}
