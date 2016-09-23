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

package com.samsung.sec.dexter.cppcheck.plugin;

import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisEntityFactory;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.analyzer.IAnalysisEntityFactory;
import com.samsung.sec.dexter.core.checker.CheckerConfig;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.DexterConfig.LANGUAGE;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.plugin.IDexterPlugin;
import com.samsung.sec.dexter.core.plugin.PluginDescription;
import com.samsung.sec.dexter.core.plugin.PluginVersion;
import com.samsung.sec.dexter.core.util.DexterUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import net.xeoh.plugins.base.annotations.PluginImplementation;

@PluginImplementation
public class CppcheckDexterPlugin implements IDexterPlugin {
    public final static String PLUGIN_NAME = "cppcheck";
    public final static PluginVersion PLUGIN_VERSION = new PluginVersion("0.10.5");

    private CppcheckWrapper cppcheck = new CppcheckWrapper();
    private final static Logger logger = Logger.getLogger(CppcheckWrapper.class);

    private static PluginDescription PLUGIN_DESCRIPTION = new PluginDescription(CppcheckDexterPlugin.PLUGIN_NAME,
            PLUGIN_NAME,
            PLUGIN_VERSION,
            DexterConfig.LANGUAGE.CPP, "Dexter plug-in for Cppcheck");;

    /*
     * (non-Javadoc)
     * 
     * @see com.samsung.sec.dexter.core.plugin.IDexterPlugin#init()
     */
    @Override
    public void init() {
        cppcheck.initCheckerConfig();

        copyCppcheckRunModule();

        if (DexterUtil.getOS() == DexterUtil.OS.LINUX || DexterUtil.getOS() == DexterUtil.OS.MAC) {
            checkCppcheckPermission();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.samsung.sec.dexter.core.plugin.IDexterPlugin#destroy()
     */
    @Override
    public void destroy() {
        // do nothing
    }

    public boolean checkCppcheckPermission() {
        String dexterHome = DexterConfig.getInstance().getDexterHome();

        Process changePermissionProcess = null;
        StringBuilder changePermissionCmd = new StringBuilder(500);

        String dexterBin = dexterHome + DexterUtil.FILE_SEPARATOR + "bin";
        String cppcheckHome = dexterBin + DexterUtil.FILE_SEPARATOR + "cppcheck";

        if (Strings.isNullOrEmpty(dexterBin)) {
            logger.error("Can't initialize Cppcheck plugin, because the dexter_home/bin is not initialized");
            return false;
        }

        if (Strings.isNullOrEmpty(cppcheckHome)) {
            logger.error("Can't initialize Cppcheck plugin, because the cppcheckHome is not initialized");
            return false;
        }

        String baseCommand = DexterConfig.EXECUTION_PERMISSION + " ";
        changePermissionCmd.append(baseCommand).append(cppcheckHome).append(DexterUtil.FILE_SEPARATOR)
                .append("cppcheck");

        try {
            changePermissionProcess = Runtime.getRuntime().exec(changePermissionCmd.toString());
        } catch (IOException e) {
            throw new DexterRuntimeException(e.getMessage() + " changePermissionCmd: " + changePermissionCmd.toString(),
                    e);
        } finally {
            if (changePermissionProcess != null) {
                changePermissionProcess.destroy();
            }
        }

        return true;
    }

    private void copyCppcheckRunModule() {
        String dexterHome = DexterConfig.getInstance().getDexterHome();
        if (Strings.isNullOrEmpty(dexterHome)) {
            throw new DexterRuntimeException(
                    "Can't initialize Cppcheck plugin, because the dexter_home is not initialized");
        }

        // copy %DEXTER_HOME%/bin/cppcheck
        String zipFilePath = dexterHome;
        String cppcheckPath = "";

        if (DexterUtil.getOS() == DexterUtil.OS.WINDOWS) {
            //zipFilePath += "/temp/cppcheck-windows_0.10.2.zip";
            zipFilePath += "/temp/cppcheck-windows_" + PLUGIN_VERSION + ".zip";
            cppcheckPath = "/cppcheck-windows.zip";
        } else { // LINUX or MAC
        	if(DexterUtil.getBit() == DexterUtil.BIT._32){
        		//zipFilePath += "/temp/cppcheck-linux_0.10.2.zip";
        		zipFilePath += "/temp/cppcheck-linux_"+ PLUGIN_VERSION + "_32.zip";
                cppcheckPath = "/cppcheck-linux-32.zip";	
        	}else{
        		//zipFilePath += "/temp/cppcheck-linux_0.10.2.zip";
        		zipFilePath += "/temp/cppcheck-linux_"+ PLUGIN_VERSION + "_64.zip";
                cppcheckPath = "/cppcheck-linux-64.zip";	
        	}
            
        }

        final File file = new File(zipFilePath);
        if (!file.exists()) {
            final InputStream is = getClass().getResourceAsStream(cppcheckPath);
            if (is == null) {
                throw new DexterRuntimeException("can't find cppcheck.zip file: " + cppcheckPath);
            }

            try {
                FileUtils.copyInputStreamToFile(is, file);
                DexterUtil.unzip(zipFilePath, dexterHome + CppcheckWrapper.CPPCHECK_HOME_DIR);
            } catch (Exception e) {
                throw new DexterRuntimeException(e.getMessage(), e);
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    // do nothing
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.samsung.sec.dexter.core.plugin.IDexterPlugin#getSaPluginDescription()
     */
    @Override
    public PluginDescription getDexterPluginDescription() {
        /*
         * it does not work on Eclipse Plugin version
         * if (this.pluginDescription == null) {
         * this.pluginDescription = new PluginDescription(CppcheckDexterPlugin.PLUGIN_NAME, PLUGIN_NAME,
         * PluginVersion.fromImplementationVersion(CppcheckDexterPlugin.class),
         * DexterConfig.LANGUAGE.CPP, "Dexter plug-in for Cppcheck");
         * }
         * return this.pluginDescription;
         */

        return PLUGIN_DESCRIPTION;
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
        } else {
            throw new DexterRuntimeException("analysis config is null");
        }

        /*
         * File bin = new File(DexterConfig.getInstance().getDexterHome() + "/bin");
         * if (bin.exists() == false) {
         * copyCppcheckRunModule();
         * if (DexterUtil.getOS() == DexterUtil.OS.LINUX || DexterUtil.getOS() == DexterUtil.OS.MAC) {
         * checkCppcheckPermission();
         * }
         * }
         * 
         * File cppcheckFolder = new File(DexterConfig.getInstance().getDexterHome() +
         * CppcheckWrapper.CPPCHECK_HOME_DIR);
         * if (cppcheckFolder.exists() == false)
         * DexterConfig.getInstance().createInitialFolderAndFiles();
         */

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
        if (DexterUtil.getOS() == DexterUtil.OS.LINUX || DexterUtil.getOS() == DexterUtil.OS.MAC) {
            checkCppcheckPermission();
        }
    }

    @Override
    public String[] getSupportingFileExtensions() {
        return new String[] { "c", "cpp", "h", "hpp" };
    }
}
