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

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.IDexterHomeListener;
import com.samsung.sec.dexter.core.config.ProjectAnalysisConfiguration;
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.exception.DexterException;
import com.samsung.sec.dexter.core.plugin.IDexterPluginManager;
import com.samsung.sec.dexter.core.util.DexterUtil;
import com.samsung.sec.dexter.core.util.IDexterClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class DexterAnalyzer implements IDexterHomeListener {
    private static final String CFG_PARM_JSON_FILE = "/cfg/dexter-config-parameter.json";
    private final static Logger LOG = Logger.getLogger(DexterAnalyzer.class);
    private final DexterAnalyzerThread dexterAnalyzerSync = new DexterAnalyzerThread();

    private List<IDexterAnalyzerListener> listenerList = new ArrayList<IDexterAnalyzerListener>(1);
    private List<ProjectAnalysisConfiguration> projectAnalysisConfigurationList = new ArrayList<ProjectAnalysisConfiguration>(
            0);

    private DexterAnalyzer() {
        DexterConfig.getInstance().addDexterHomeListener(this);
        loadProjectAnalysisConfiguration();
    }

    private static class SaExecutorHolder {
        private static final DexterAnalyzer INSTANCE = new DexterAnalyzer();
    }

    /**
     * @return DexterAnalyzer
     */
    public static DexterAnalyzer getInstance() {
        return SaExecutorHolder.INSTANCE;
    }

    public void runSync(final AnalysisConfig config, final IDexterPluginManager pluginManager,
            final IDexterClient client) {
        dexterAnalyzerSync.analyze(config, pluginManager, client);
    }

    public void runAsync(final AnalysisConfig config, final IDexterPluginManager pluginManager,
            final IDexterClient client) {
        DexterAnalyzerThread thread = new DexterAnalyzerThread();
        thread.setFields(config, pluginManager, client);
        thread.start();
    }

    protected void preSendSourceCode(final AnalysisConfig config) {
        for (final IDexterAnalyzerListener listener : listenerList) {
            listener.handlePreSendSourceCode(config);
        }
    }

    protected void postSendSourceCode(final AnalysisConfig config) {
        for (final IDexterAnalyzerListener listener : listenerList) {
            listener.handlePostSendSourceCode(config);
        }
    }

    protected void preRunCodeMetrics(final AnalysisConfig config) {
        for (final IDexterAnalyzerListener listener : listenerList) {
            listener.handlePreRunCodeMetrics(config);
        }
    }

    protected void postRunCodeMetrics(AnalysisConfig config) {
        for (final IDexterAnalyzerListener listener : listenerList) {
            listener.handlePostRunCodeMetrics(config);
        }
    }

    protected void preRunStaticAnalysis(final AnalysisConfig config) {
        for (final IDexterAnalyzerListener listener : listenerList) {
            listener.handlePreRunStaticAnalysis(config);
        }
    }

    protected void postRunStaticAnalysis(final AnalysisConfig config, final List<AnalysisResult> resultList) {
        for (final IDexterAnalyzerListener listener : listenerList) {
            listener.handlePostRunStaticAnalysis(config, resultList);
        }
    }

    public long getDefectGroup(final String projectName) throws DexterException {
        return -1;
        // TODO Implement Later
        // List<DefectGroup> results;
        // results =
        // DexterClient.getInstance().getDefectGroupByGroupName(projectName);
        // return results.get(0).getId();
    }

    public void createDefectGroup(final String projectName) throws DexterException {
        // TODO Implement Later
        // final DefectGroup group = new DefectGroup();
        // group.setGroupName(projectName);
        // group.setGroupType("PRJ");
        // DexterClient.getInstance().insertDefectGroup(group);
    }

    public void addListener(final IDexterAnalyzerListener listener) {
        if (!listenerList.contains(listener)) {
            listenerList.add(listener);
        }
    }

    public void removeListener(final IDexterAnalyzerListener listener) {
        listenerList.remove(listener);
    }

    public void addProjectAnalysisConfiguration(final ProjectAnalysisConfiguration param) {
        if (!projectAnalysisConfigurationList.contains(param)) {
            projectAnalysisConfigurationList.add(param);
            writeCfgParamToJsonFile();
        }
    }

    public void removeCfgParam(final ProjectAnalysisConfiguration param) {
        projectAnalysisConfigurationList.remove(param);
        writeCfgParamToJsonFile();
    }

    private void writeCfgParamToJsonFile() {
        final String dexterHome = DexterConfig.getInstance().getDexterHome();
        if (Strings.isNullOrEmpty(dexterHome)) {
            LOG.error("cannot write ProjectAnalysisConfiguration List because of Invalid of Dexter Home");
            return;
        }

        final File file = new File(dexterHome + CFG_PARM_JSON_FILE);

        try {
            Files.write(new Gson().toJson(this.projectAnalysisConfigurationList), file, Charsets.UTF_8);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public List<ProjectAnalysisConfiguration> getProjectAnalysisConfigurationList() {
        return projectAnalysisConfigurationList;
    }

    /**
     * @param key
     */
    public void removeCfgParam(final String key) {
        for (int i = 0; i < projectAnalysisConfigurationList.size(); i++) {
            ProjectAnalysisConfiguration param = projectAnalysisConfigurationList.get(i);
            if (param.getCfgKey().equals(key)) {
                projectAnalysisConfigurationList.remove(i);
                break;
            }
        }
    }

    /**
     *  
     */
    public void removeAllCfgParam() {
        projectAnalysisConfigurationList.clear();
        writeCfgParamToJsonFile();
    }

    /**
     * @param parameter
     */
    public void setCfgParam(final ProjectAnalysisConfiguration parameter) {
        final String key = parameter.getCfgKey();
        for (int i = 0; i < projectAnalysisConfigurationList.size(); i++) {
            ProjectAnalysisConfiguration p = projectAnalysisConfigurationList.get(i);
            if (p.getCfgKey().equals(key)) {
                projectAnalysisConfigurationList.remove(i);
                break;
            }
        }

        projectAnalysisConfigurationList.add(parameter);
        writeCfgParamToJsonFile();
    }

    /**
     * @param key
     * @return
     */
    public ProjectAnalysisConfiguration getConfParamByKey(final String key) {
        for (final ProjectAnalysisConfiguration p : projectAnalysisConfigurationList) {
            if (p.getCfgKey().equals(key)) {
                return p;
            }
        }

        return null;
    }

    @Override
    public void handleDexterHomeChanged(final String oldPath, final String newPath) {
        loadProjectAnalysisConfiguration();
    }

    private void loadProjectAnalysisConfiguration() {
        if (Strings.isNullOrEmpty(DexterConfig.getInstance().getDexterHome())) {
            LOG.warn("ProjectAnalysisConfiguration will be read later, because dexter home is not set yet");
            return;
        }

        projectAnalysisConfigurationList = new ArrayList<ProjectAnalysisConfiguration>();

        final String cfgFilePath = DexterConfig.getInstance().getDexterHome() + CFG_PARM_JSON_FILE;
        DexterUtil.createEmptyFileIfNotExist(cfgFilePath);

        final String content = DexterUtil.getContentsFromFile(cfgFilePath, Charsets.UTF_8);
        if (Strings.isNullOrEmpty(content))
            return;

        final Gson gson = new Gson();
        @SuppressWarnings({ "unchecked", "rawtypes" })
        final List<Map> list = gson.fromJson(content, List.class);

        for (@SuppressWarnings("rawtypes")
        final Map map : list) {
            final String jsonStr = gson.toJson(map);
            ProjectAnalysisConfiguration cfg = gson.fromJson(jsonStr, ProjectAnalysisConfiguration.class);
            addProjectAnalysisConfiguration(cfg);
        }
    }

    public static List<Defect> getAllDefectList(List<AnalysisResult> resultList) {
        assert resultList != null && resultList.size() > 0;

        List<Defect> allDefectList = new ArrayList<Defect>();

        for (AnalysisResult result : resultList) {
            allDefectList.addAll(result.getDefectList());
        }

        return allDefectList;
    }

    public static String getSourceFileFullPath(List<AnalysisResult> resultList) {
        assert resultList != null && resultList.size() > 0;

        return resultList.get(0).getSourceFileFullPath();
    }

    public static File getResultFile(List<AnalysisResult> resultList) {
        assert resultList != null && resultList.size() > 0
                && Strings.isNullOrEmpty(resultList.get(0).getResultFileFullPath()) == false;

        return new File(resultList.get(0).getResultFileFullPath());
    }

    public static String getFileName(List<AnalysisResult> resultList) {
        assert resultList != null && resultList.size() > 0
                && Strings.isNullOrEmpty(resultList.get(0).getFileName()) == false;

        return resultList.get(0).getFileName();
    }
}
