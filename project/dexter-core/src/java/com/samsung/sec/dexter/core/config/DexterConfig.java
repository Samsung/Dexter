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
package com.samsung.sec.dexter.core.config;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.samsung.sec.dexter.core.util.DexterServerConfig;
import com.samsung.sec.dexter.core.util.DexterUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

public class DexterConfig {
    int i = 1;
    final private static Logger LOG = Logger.getLogger(DexterConfig.class);

    public static final String PLUGIN_FOLDER_NAME = "plugin";
    public static final String RESULT_FOLDER_NAME = "result";
    public static final String OLD_FOLDER_NAME = "old";
    public static final String FILTER_FOLDER_NAME = "filter";
    public static final String DAEMON_FOLDER_NAME = "daemon";
    public static final String TEMP_FOLDER_NAME = "temp";
    public static final String LOG_FOLDER_NAME = "log";
    public static final String NO_UPDATE_CHECKER_CONFIG = "noUpdate";
    public static final String NOT_FOUND_FOLDER_NAME = "notFound";

    public static final String FILTER_DEFECT_FILENAME = "defect-filter.json";
    public static final String FILTER_FALSE_ALARM_LIST_FILENAME = "false-alarm-list.json";

    /** Service Address List */
    public static final String GET_ACCOUNT = "/api/v1/accounts/findById";
    public static final String CHECK_ACCOUNT = "/api/v1/accounts/checkLogin";
    public static final String CHECK_HAS_ACCOUNT = "/api/v1/accounts/hasAccount";
    public static final String ADD_ACCOUNT = "/api/v1/accounts/add";
    public static final String FIND_ACCOUNT = "/api/v1/accounts/findById";

    public static final String STOP_SERVER = "/api/v1/server";
    public static final String CHECK_SERVER_ADDRESS = "/api/v1/isServerAlive";
    public static final String PUT_ANALYSIS_RESULT = "/api/v1/analysis/result";
    public static final String PUT_ANALYSIS_RESULT_V2 = "/api/v2/analysis/result";
    public static final String PUT_ANALYSIS_RESULT_V3 = "/api/v3/analysis/result";
    public static final String DISMISS_DEFECT = "/api/v1/defect/dismiss";
    public static final String FILTER_FALSE_ALARM = "/api/v1/filter/false-alarm";
    public static final String FILTER_FALSE_ALARM_LIST = "/api/v1/filter/false-alarm-list";
    public static final String FILTER_DELETE_FALSE_ALARM = "/api/v1/filter/delete-false-alarm";
    public static final String GET_FALSE_ALARM_VERSION = "/api/v1/version/false-alarm";
    public static final String POST_GLOBAL_DID = "/api/v1/defect/gid";
    public static final String DEFECT_DELETE = "/api/v2/defect/deleteAll";
    public static final String DEFECT_GROUP = "/api/v1/config/defect-group";
    public static final String CODE = "/api/v1/config/code";
    public static final String POST_SNAPSHOT_SOURCECODE = "/api/v1/analysis/snapshot/source";
    public static final String SOURCE_CODE = "/api/v1/analysis/snapshot/source";

    public static final String POST_FUNCTION_METRICS = "/api/v2/functionMetrics";

    public static final String GET_DEXTER_PLUGIN_UPDATE_URL = "/api/v1/config/update-url";
    public static final String GET_DEXTER_PLUGIN_CHECKER_JSON_FILE = "/api/v1/config";

    public static final String GET_CODE_METRICS_INFO = "/api/vi/codeMetrics/";

    public static final Object DEFECT_HELP_BASE = "/tool";
    public static final Object DEFECT_HELP = "/help";
    public static final Object NOT_FOUND_CHECKER_DESCRIPTION = "/NotFoundCheckerDescription/";
    public static final Object EMPTY_HTML_FILE_NAME = "empty_checker_description";

    public static final Object CODE_METRICS_BASE = "/codeMetrics/#/";
    public static final Object CODE_METRICS_TOOLNAME = "/BOTH";
    public static final Object CODE_METRICS_FILE_NAME = "fileName";
    public static final Object CODE_METRICS_MODULE_PATH = "modulePath";

    public static final Object CODE_METRICS_VIEW_FILE_NAME = "/codeMetricsView";

    public static final Object FUNCTION_METRICS_BASE = "/functionMetrics/#/";
    public static final Object FUNCTION_METRICS_FUNCTION_LIST = "functionList";

    public static final String SECURITY_CHECK_PREFIX = "sec_";
    public static final String EXECUTION_PERMISSION = "chmod 755";
    public static final String DEXTER_SCRIPT_NAME = "dexter.sh";
    public static final String DEXTER2_SCRIPT_NAME = "dexter2.sh";

    /** common constants */
    public static final String DEXTER_HOME_KEY = "dexterHome";

    /** BROWSER constants */
    public static final String REGISTRY_FIREFOX = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\App Paths\\firefox.exe";
    public static final String REGISTRY_CHROME = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\App Paths\\chrome.exe";

    private RunMode runMode = RunMode.CLI;
    private String dexterHome = System.getenv(DEXTER_HOME_KEY);
    private Charset sourceEncoding = Charsets.UTF_8;
    private boolean doesSendResult = true;
    private boolean isStandalone = false;
    private int serverConnectionTimeOut = 15000; // ms

    public static enum AnalysisType {
        SAVE, FILE, FOLDER, PROJECT, SNAPSHOT, UNKNOWN
    };

    /**
     * only for CLI version
     * true : -e option is used
     */
    private boolean isSpecifiedCheckerOptionEnabledByCli = false;

    public int getServerConnectionTimeOut() {
        return serverConnectionTimeOut;
    }

    public void setServerConnectionTimeOut(int serverConnectionTimeOut) {
        this.serverConnectionTimeOut = serverConnectionTimeOut;
    }

    public static final String PLUGIN_LIST_FILENAME = "plugin-list.json";
    public static final int SERVER_TIMEOUT = 10000; // Milliseconds
    public static final String DEXTER_DEFAULT_FOLDER_NAME = "dexter-home";

    public static final String DEFECT_STATUS_NEW = "NEW";
    public static final String DEFECT_STATUS_FIX = "FIX";
    public static final String DEFECT_STATUS_DISMISSED = "EXC";
    public static final int MAX_LOG_COUNT = 200;

    public static final String SHEET_NAME_OF_CHECKER_CONFIG = "checker_config";

    public static final long NO_SNAPSHOT = -1;

    public static final long NO_DEFECT_GROUP = -1;

    public static final String SOURCE_INSIGHT_EXE_PATH_KEY = "sourceInsightExe";

    /**
     * If you change this value, you also have to change following file:
     * dexter-core/src/resource/dexter.cfg and dexter.cfg.help
     * dexter-executor/build-install.xml around 36~37 line
     * eg. <copy file="${core-prj-path}/src/resource/dexter_cfg.json" todir="${dist-bin}" />
     * <copy file="${core-prj-path}/src/resource/dexter_cfg.json.help" todir="${dist-bin}" />
     * we cannot use 'cfg' extension because of NASCA
     */
    public static final String DEXTER_CFG_FILENAME = "dexter_cfg.json";
    public static final String PLATZ_KEYWORD_FILENAME = "platz_keyword.json";
    public static final String PLATZ_API_URL = "http://platz.sec.samsung.net/tapi/#/";
    public static final String PLATZ_SEARCH_API_URL = "http://platz.sec.samsung.net/tapi/#/search/";
    public static final String PLATZ_DOMAIN = "platz.sec.samsung.net";

    /**
     * If you change this value, you also have to change following file:
     * dexter-core/src/resource/dexter.em (around 507 line)
     * we cannot use 'cfg' extension because of NASCA
     */
    public static final String DEXTER_DAEMON_CFG_FILENAME = "dexter_daemon_cfg.json";
    public static final int SOURCE_FILE_SIZE_LIMIT = 1024 * 2 * 1024;

    private boolean isReviewMode = false;

    //private ScheduledFuture<?> sendResultFuture = null;
    //private ScheduledFuture<?> mergeFilterFuture = null;

    ////////////////////////////////////////////////////////////////////////////////////////////
    
    public static final String DPR_REG_EXP = "(?:/\\*.*[w]*(DPR:)(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*[w]*(DPR:).*)";
	public static final String CRITICAL_REG_EXP = "(?i).*\\[.*cri.*\\] | (?i).*\\[.*critical.*\\]";
	public static final String MAJOR_REG_EXP = "(?i).*\\[.*maj.*\\] | (?i).*\\[.*major.*\\]";
	public static final String SIMPLE_REG_EXP = ".*(DPR:)";

    public static enum RunMode {
        CLI, ECLIPSE, DAEMON, INTELLIJ, NETBEANS, SOURCE_INSIGHT
    }

    public static enum LANGUAGE {
        JAVA, JAVASCRIPT, C, CPP, C_SHARP, UNKNOWN, ALL
    };

    private final List<IDexterHomeListener> dexterHomeListenerList = new ArrayList<IDexterHomeListener>(3);
    private final List<IDexterStandaloneListener> dexterStandaloneListenerList = new ArrayList<IDexterStandaloneListener>();

    private Set<String> supportingFileExtensions = new HashSet<String>(5);

    //private ScheduledExecutorService scheduler;

    private DexterConfig() {
        LOG.debug("DexterConfig");
    }

    private static class LazyHolder {
        private static final DexterConfig INSTANCE = new DexterConfig();
    }

    public static DexterConfig getInstance() {
        return LazyHolder.INSTANCE;
    }

    public synchronized void addDexterHomeListener(final IDexterHomeListener listener) {
        if (!dexterHomeListenerList.contains(listener)) {
            dexterHomeListenerList.add(listener);
        }
    }

    public synchronized void removeDexterHomeListener(final IDexterHomeListener listener) {
        dexterHomeListenerList.remove(listener);
    }

    public synchronized void runDexterHomeListener(final String oldPath, final String newPath) {
        createInitialFolderAndFiles();

        for (int i = 0; i < dexterHomeListenerList.size(); i++) {
            final IDexterHomeListener listener = dexterHomeListenerList.get(i);

            if (listener != null) {
                listener.handleDexterHomeChanged(oldPath, newPath);
            } else {
                dexterHomeListenerList.remove(i--);
            }
        }
    }

    public void createInitialFolderAndFiles() {
        if (Strings.isNullOrEmpty(this.dexterHome)) {
            return;
        }

        try {
            DexterUtil.createFolderWithParents(dexterHome);

            final String bin = dexterHome + "/bin";
            DexterUtil.createFolderWithParents(bin);
            DexterUtil.createFolderWithParents(bin + "/cppcheck");
            DexterUtil.createFolderWithParents(bin + "/cppcheck/cfg");
            DexterUtil.createFolderWithParents(bin + "/" + DAEMON_FOLDER_NAME);

            final String plugin = dexterHome + "/" + PLUGIN_FOLDER_NAME;
            DexterUtil.createFolderWithParents(plugin);

            final String result = dexterHome + "/" + DexterConfig.RESULT_FOLDER_NAME;
            DexterUtil.createFolderWithParents(result);
            DexterUtil.createFolderWithParents(result + "/" + DexterConfig.OLD_FOLDER_NAME);
            DexterUtil.createFolderWithParents(result + "/" + DexterConfig.DAEMON_FOLDER_NAME);

            DexterUtil.createFolderWithParents(dexterHome + "/" + DexterConfig.TEMP_FOLDER_NAME);
            DexterUtil.createFolderWithParents(dexterHome + "/" + DexterConfig.LOG_FOLDER_NAME);

            DexterUtil.createFolderWithParents(dexterHome + "/cfg");
            final String cfgSourceInsight = dexterHome + "/cfg/sourceInsight";
            DexterUtil.createFolderWithParents(cfgSourceInsight);

            final String filter = dexterHome + "/" + DexterConfig.FILTER_FOLDER_NAME;
            DexterUtil.createFolderWithParents(filter);

            // create log/sourceinsight.log
            DexterUtil.createEmptyFileIfNotExist(dexterHome + "/log/sourceinsight.log");

            // create bin/daemon/dexter.cfg
            DexterUtil.createEmptyFileIfNotExist(bin + "/" + DAEMON_FOLDER_NAME + "/" + DEXTER_DAEMON_CFG_FILENAME);

            // create filter/defect-filter.json
            DexterUtil.createEmptyFileIfNotExist(filter + "/defect-filter.json");

            // copy cfg/sourceinsight/dexter.em
            copyFile("/dexter.em", cfgSourceInsight + "/dexter.em");

            // copy bin/dexter.cfg & dexter.cfg.help
            final String dexterCfgFile = bin + DexterUtil.FILE_SEPARATOR + DEXTER_CFG_FILENAME;
            if (new File(dexterCfgFile).exists() == false) {
                copyFile("/" + DEXTER_CFG_FILENAME, dexterCfgFile);
                copyFile("/" + DEXTER_CFG_FILENAME + ".help", dexterCfgFile + ".help");
            }

            // create /bin/dexter.bat & dexter.sh
            final File dexterBatFile = new File(bin + "/dexter.bat");
            if (dexterBatFile.exists() == false) {
                Files.write("java -Xms256m -Xmx786m -XX:MaxPermSize=256m -jar dexter-executor.jar -u %1 -p %2"
                        + DexterUtil.LINE_SEPARATOR, dexterBatFile, Charsets.UTF_8);
                final File dexterShFile = new File(bin + "/dexter.sh");
                Files.write("java -Xms256m -Xmx786m -XX:MaxPermSize=256m -jar dexter-executor.jar -u $1 -p $2"
                        + DexterUtil.LINE_SEPARATOR, dexterShFile, Charsets.UTF_8);
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public void copyDexterMacroFileForSourceInsight(final String toPath) {
        if (Strings.isNullOrEmpty(toPath)) {
            LOG.error("can not copy dexter.em file into base project of Source Insight");
            return;
        }

        copyFile("/dexter.em", toPath);
    }

    private void copyFile(final String source, final String target) {
        final InputStream is = getClass().getResourceAsStream(source);
        if (is == null) {
            LOG.error("can't create file : " + target);
            return;
        }

        try {
            DexterUtil.copyFileFromJar(is, target);
        } catch (IllegalArgumentException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                LOG.warn(e.getMessage());
            }
        }
    }

    /**
     * @return the dEXTER_MODE
     */
    public RunMode getRunMode() {
        return runMode;
    }

    /**
     * @return the dEXTER_HOME
     */
    public String getDexterHome() {
        assert Strings.isNullOrEmpty(dexterHome) == false;

        return dexterHome;
    }

    /**
     * @return the sRC_ENCODING
     */
    public Charset getSourceEncoding() {
        return sourceEncoding;
    }

    /**
     * @return the rESULT_SEND_YN
     */
    public boolean isDoesSendResult() {
        return doesSendResult;
    }

    /**
     * @param dEXTER_MODE
     * the dEXTER_MODE to set
     */
    public void setRunMode(final RunMode dEXTER_MODE) {
        runMode = dEXTER_MODE;
    }

    /**
     * @param homePath
     * the dEXTER_HOME to set
     */
    public void setDexterHome(String homePath) {
        LOG.debug("DexterConfig.setDexterHome()");
        homePath = DexterUtil.refinePath(homePath);
        if (Strings.isNullOrEmpty(homePath) || homePath.equals(this.dexterHome)) {
            return;
        }

        final String oldPath = dexterHome;
        dexterHome = homePath;
        runDexterHomeListener(oldPath, dexterHome);
    }

    /**
     * @param encoding
     * the sRC_ENCODING to set
     */
    public void setSourceEncoding(final Charset encoding) {
        sourceEncoding = encoding;
    }

    /**
     * @param isSend
     * the rESULT_SEND_YN to set
     */
    public void setDoesSendResult(final boolean isSend) {
        doesSendResult = isSend;
    }

    /**
     * dexterHome + FilterFolder + FilterFlaseAlarmListFile
     * eg.) C:/dexter/filter/false-alarm-list.json/
     * 
     * @return
     */
    public String getFalseAlarmListFilePath() {
        return dexterHome + "/" + FILTER_FOLDER_NAME + "/" + FILTER_FALSE_ALARM_LIST_FILENAME;
    }

    /**
     * dexterHome + FilterFolder + filterDefectFileName
     * eg.) C:/dexter/filter/defect-filter.json
     * 
     * @return
     */
    public String getFilterFilePath() {
        return dexterHome + "/" + FILTER_FOLDER_NAME + "/" + FILTER_DEFECT_FILENAME;
    }

    /**
     * dexterHome + filterFolder + oldFolder
     * eg.) C:/dexter/filter/old
     * 
     * @return
     */
    public String getOldFilterFolderPath() {
        return dexterHome + "/" + FILTER_FOLDER_NAME + "/" + OLD_FOLDER_NAME;
    }

    public boolean isReviewMode() {
        return this.isReviewMode;
    }

    public void setReviewMode(boolean mode) {
        this.isReviewMode = mode;
    }

    public String getOldResultPath() {
        return this.dexterHome + "/" + DexterConfig.RESULT_FOLDER_NAME + "/" + OLD_FOLDER_NAME;
    }

    public String getErrorResultPath() {
        return this.dexterHome + "/" + DexterConfig.RESULT_FOLDER_NAME + "/" + "error";
    }

    public String getResultPath() {
        return this.dexterHome + "/" + DexterConfig.RESULT_FOLDER_NAME;
    }

    public String getresultOldPath() {
        return this.dexterHome + "/" + DexterConfig.RESULT_FOLDER_NAME + "/" + OLD_FOLDER_NAME;
    }

    public void addSupprotingFileExtensions(String[] supportingFileExtensions) {
        for (String extension : supportingFileExtensions) {
            if (this.supportingFileExtensions.contains(extension.toLowerCase()) == false) {
                this.supportingFileExtensions.add(extension.toLowerCase());
            }
        }
    }

    public boolean isAnalysisAllowedFile(String fileName) {
        String ext = Files.getFileExtension(fileName).toLowerCase();

        return this.supportingFileExtensions.contains(ext);
    }

    public void removeAllSupportingFileExtensions() {
        this.supportingFileExtensions.clear();
    }

    public boolean isStandalone() {
        return isStandalone;
    }

    public void setStandalone(boolean isStandalone) {
        if (this.isStandalone == isStandalone) {
            return;
        }

        this.isStandalone = isStandalone;
        if (this.runMode.equals(RunMode.CLI)) {
            return;
        }
    }

    public synchronized void runListenerHandlerWhenStandalone() {
        for (int i = 0; i < dexterStandaloneListenerList.size(); i++) {
            final IDexterStandaloneListener listener = dexterStandaloneListenerList.get(i);

            if (listener != null) {
                listener.handleWhenStandaloneMode();
            } else {
                dexterStandaloneListenerList.remove(i--);
            }
        }
    }

    public synchronized void runListenerHandlerWhenNotStandalone(DexterServerConfig serverConfig) {
        for (int i = 0; i < dexterStandaloneListenerList.size(); i++) {
            final IDexterStandaloneListener listener = dexterStandaloneListenerList.get(i);

            if (listener != null) {
                listener.handleWhenNotStandaloneMode(serverConfig);
            } else {
                dexterStandaloneListenerList.remove(i--);
            }
        }
    }

    public synchronized void addDexterStandaloneListener(final IDexterStandaloneListener listener) {
        if (!dexterStandaloneListenerList.contains(listener)) {
            dexterStandaloneListenerList.add(listener);
        }
    }

    public synchronized void removeDexterStandaloneListener(final IDexterStandaloneListener listener) {
        dexterStandaloneListenerList.remove(listener);
    }

    public String getDefaultDexterHome() {
        return System.getProperty("user.home") + "/" + DexterConfig.DEXTER_DEFAULT_FOLDER_NAME;
    }

    /**
     * only for CLI version
     * true : -e option is used
     * 
     * @return
     */
    public boolean isSpecifiedCheckerOptionEnabledByCli() {
        return isSpecifiedCheckerOptionEnabledByCli;
    }

    /**
     * only for CLI version
     * true : -e option is used
     * 
     * @param isCheckerEnableOption
     */
    public void setSpecifiedCheckerOptionEnabledByCli(boolean isCheckerEnableOption) {
        this.isSpecifiedCheckerOptionEnabledByCli = isCheckerEnableOption;
    }

    public String getDexterPluginFolderPath() {
        return getDexterHome() + "/" + PLUGIN_FOLDER_NAME;
    }

    public static boolean isFileSizeTooBigToAnalyze(final String filePath) {
        return new File(filePath).length() >= DexterConfig.SOURCE_FILE_SIZE_LIMIT;
    }

}
