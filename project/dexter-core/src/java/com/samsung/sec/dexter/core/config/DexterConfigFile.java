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
import com.google.gson.Gson;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisEntityFactory;
import com.samsung.sec.dexter.core.analyzer.IAnalysisEntityFactory;
import com.samsung.sec.dexter.core.analyzer.ResultFileConstant;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.DexterUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class DexterConfigFile implements IDexterConfigFile {
    static Logger log = Logger.getLogger(DexterConfigFile.class);

    private String dexterHome;
    private String dexterServerIp;
    private int dexterServerPort;
    private String projectName;
    private String projectFullPath;
    private boolean hasOneSourceDir = false;
    private String firstSourceDir = "";
    private List<String> sourceDirList;
    private List<String> headerDirList;
    private List<String> functionList;
    private String sourceEncoding;
    private List<String> libDirList;
    private String binDir;
    private String language;
    private String modulePath;
    private List<String> fileNameList;
    private String resultFileFullPath;
    private String snapshotId;

    private Type type = Type.FILE;

    @Override
    public void loadFromFile(final File file) {
        assert file != null;

        Map<String, Object> configMap = getConfigurationMap(file);
        setFields(configMap);
    }

    protected Map<String, Object> getConfigurationMap(final File confFile) {
        if (confFile.exists() == false) {
            throw new DexterRuntimeException(
                    "There is no " + DexterConfig.DEXTER_CFG_FILENAME + " file : " + confFile.getName());
        }

        final StringBuilder confJson = new StringBuilder(50);

        try {
            for (String content : Files.readLines(confFile, Charsets.UTF_8)) {
                confJson.append(content.replace('\\', '/').replace("//", "/"));
                if (content.indexOf('}') >= 0) {
                    break;
                }
            }

            final Gson gson = new Gson();
            @SuppressWarnings("unchecked")
            Map<String, Object> configMap = gson.fromJson(confJson.toString(), Map.class);
            addAdditionalInfoWhenDaemon(configMap);

            return configMap;
        } catch (IOException e) {
            throw new DexterRuntimeException(e.getMessage(), e);
        } catch (com.google.gson.JsonSyntaxException e) {
            throw new DexterRuntimeException("Dexter Configuration Json file is not valid. " + e.getMessage(), e);
        }
    }

    private void addAdditionalInfoWhenDaemon(Map<String, Object> configMap) {
        if (DexterConfig.getInstance().getRunMode() != DexterConfig.RunMode.DAEMON) {
            return;
        }

        configMap.put("dexterHome", DexterConfig.getInstance().getDexterHome());
        configMap.put("sourceDir", generateSourceDirListForDaemon(configMap));
    }

    private Object generateSourceDirListForDaemon(Map<String, Object> configMap) {
        String projectFullPath = (String) configMap.get("projectFullPath");
        List<String> srcDirList = new ArrayList<String>(1);
        srcDirList.add(projectFullPath);

        return srcDirList;
    }

    @Override
    public AnalysisConfig toAnalysisConfig() {
        IAnalysisEntityFactory configFactory = new AnalysisEntityFactory();
        final AnalysisConfig analysisConfig = configFactory.createAnalysisConfig();

        analysisConfig.setProjectName(getProjectName());
        analysisConfig.setProjectFullPath(getProjectFullPath() + "/");
        analysisConfig.setOutputDir(getBinDir());
        analysisConfig.setModulePath(getModulePath());
        analysisConfig.setSourceBaseDirList(getSourceDirList());
        analysisConfig.setHeaderBaseDirList(getHeaderDirList());
        analysisConfig.setLibDirList(getLibDirList());
        analysisConfig.setSnapshotId(generateSnapshotId());
        analysisConfig.setResultFileFullPath(getResultFileFullPath());
        analysisConfig.setFunctionList(getFunctionList());
        analysisConfig.setNoDefectGroupAndSnapshotId();

        return analysisConfig;
    }

    @Override
    public void setFields(final Map<String, Object> params) {
        checkDexterConfigMap(params);

        setDexterHome((String) params.get("dexterHome"));
        setDexterServerIp((String) params.get("dexterServerIp"));
        setDexterServerPort(DexterUtil.getIntFromMap(params, "dexterServerPort"));

        setProjectName((String) params.get(ResultFileConstant.PROJECT_NAME));
        setProjectFullPath((String) params.get("projectFullPath") + "/");
        setSourceDirList(getStringListFromMap(params, "sourceDir"));
        setHeaderDirList(getStringListFromMap(params, "headerDir"));
        setSourceEncoding((String) params.get("sourceEncoding"));
        setLibDirList(getStringListFromMap(params, "libDir"));
        setBinDir((String) params.get("binDir"));
        setType((String) params.get("type"));
        setModulePath((String) params.get(ResultFileConstant.MODULE_PATH));
        setFileNameList(getStringListFromMap(params, ResultFileConstant.FILE_NAME));
        setResultFileFullPath((String) params.get("resultFileFullPath"));
        setFunctionList(getStringListFromMap(params, "functionList"));
        setSnapshotId((String) params.get(ResultFileConstant.SNAPSHOT_ID));
        setDexterServerIp((String) params.get("dexterServerIp"));
    }

    protected void setType(String value) {
        if (Type.hasValue(value)) {
            this.type = Type.valueOf(value);
        }
    }

    @SuppressWarnings("unchecked")
    protected List<String> getStringListFromMap(final Map<String, Object> map, String key) {
        if (null == map.get(key) || (map.get(key) instanceof ArrayList) == false) {
            return new ArrayList<String>(0);
        }

        return (ArrayList<String>) map.get(key);
    }

    private long generateSnapshotId() {
        if ("SNAPSHOT".equals(type.toString()) == false) {
            return -1;
        }

        if (Strings.isNullOrEmpty(this.snapshotId)) {
            return System.currentTimeMillis();
        } else {
            return Integer.parseInt(this.snapshotId);
        }
    }

    protected void checkDexterConfigMap(final Map<String, Object> map) {
        checkNullofMap(map);
        checkFieldExistence(map);
        checkFolderExistence(map);
        checkTypeAndFollowingFields(map);
    }

    private void checkNullofMap(final Map<String, Object> map) {
        if (map == null || map.size() == 0)
            throw new DexterRuntimeException("Dexter Configuration Error : empty");
    }

    private void checkFieldExistence(final Map<String, Object> map) {
        checkFieldEmptyInDexterConfigurationMap(map, ResultFileConstant.PROJECT_NAME);
        checkFieldEmptyInDexterConfigurationMap(map, "projectFullPath");
        checkFieldEmptyInDexterConfigurationMap(map, "sourceEncoding");
        checkFieldEmptyInDexterConfigurationMap(map, "type");
    }

    private void checkFolderExistence(final Map<String, Object> map) {
        DexterUtil.checkFolderExistence(map, "projectFullPath");
    }

    private void checkTypeAndFollowingFields(final Map<String, Object> map) {
        final String type = (String) map.get("type");

        if (!"FILE".equalsIgnoreCase(type) && !"FOLDER".equalsIgnoreCase(type) && !"PROJECT".equalsIgnoreCase(type)
                && !"SNAPSHOT".equalsIgnoreCase(type)) {
            throw new DexterRuntimeException(
                    "'type' field can be {FILE,FOLDER,PROJECT,SNAPSHOT}. your input : " + type);
        }

        if ("FILE".equalsIgnoreCase(type)) {
            checkFieldEmptyInDexterConfigurationMap(map, ResultFileConstant.FILE_NAME);
        }
    }

    private void checkFieldEmptyInDexterConfigurationMap(final Map<String, Object> map, final String key) {
        if (null == map.get(key))
            throw new DexterRuntimeException("Dexter Configuration Error : '" + key + "' field is empty");
    }

    @Override
    public String getDexterHome() {
        return dexterHome;
    }

    @Override
    public void setDexterHome(String dexterHome) {
        if (new File(dexterHome).exists() == false)
            throw new DexterRuntimeException("there is no dexter home folder : " + dexterHome);

        this.dexterHome = dexterHome;
    }

    @Override
    public String getDexterServerIp() {
        return dexterServerIp;
    }

    @Override
    public void setDexterServerIp(String dexterServerIp) {
        this.dexterServerIp = dexterServerIp;
    }

    @Override
    public int getDexterServerPort() {
        return dexterServerPort;
    }

    @Override
    public void setDexterServerPort(int dexterServerPort) {
        this.dexterServerPort = dexterServerPort;
    }

    @Override
    public String getProjectName() {
        return projectName;
    }

    @Override
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public String getProjectFullPath() {
        return projectFullPath;
    }

    @Override
    public void setProjectFullPath(String projectFullPath) {
        if (new File(projectFullPath).exists() == false)
            throw new DexterRuntimeException("there is no project path : " + projectFullPath);

        this.projectFullPath = projectFullPath;
    }

    @Override
    public List<String> getSourceDirList() {
        return sourceDirList;
    }

    @Override
    public void setSourceDirList(List<String> sourceDirList) {
        assert sourceDirList != null;

        for (String dir : sourceDirList) {
            if (new File(dir).exists() == false)
                throw new DexterRuntimeException("there is no source folder : " + dir);
        }

        this.sourceDirList = sourceDirList;
        this.firstSourceDir = this.sourceDirList.get(0);
        this.hasOneSourceDir = this.sourceDirList.size() == 1;
    }

    @Override
    public List<String> getHeaderDirList() {
        return headerDirList;
    }

    @Override
    public void setHeaderDirList(List<String> headerDirList) {
        assert headerDirList != null;

        for (String dir : headerDirList) {
            if (new File(dir).exists() == false)
                throw new DexterRuntimeException("there is no header folder : " + dir);
        }

        this.headerDirList = headerDirList;
    }

    @Override
    public List<String> getFunctionList() {
        return this.functionList;
    }

    @Override
    public void setFunctionList(List<String> functionList) {
        this.functionList = functionList;
    }

    @Override
    public String getSourceEncoding() {
        return sourceEncoding;
    }

    @Override
    public void setSourceEncoding(String sourceEncoding) {
        this.sourceEncoding = sourceEncoding;
    }

    @Override
    public List<String> getLibDirList() {
        return libDirList;
    }

    @Override
    public void setLibDirList(List<String> libDirList) {
        assert libDirList != null;

        for (String dir : libDirList) {
            if (new File(dir).exists() == false)
                throw new DexterRuntimeException("there is no lib file or folder : " + dir);
        }

        this.libDirList = libDirList;
    }

    @Override
    public String getBinDir() {
        return binDir;
    }

    @Override
    public void setBinDir(String binDir) {
        assert binDir != null;

        if (Strings.isNullOrEmpty(binDir) == false && new File(binDir).exists() == false)
            throw new DexterRuntimeException("there is no bin folder : " + binDir);

        this.binDir = binDir;
    }

    @Override
    public String getLanguage() {
        return language;
    }

    @Override
    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String getModulePath() {
        return modulePath;
    }

    @Override
    public void setModulePath(String modulePath) {
        this.modulePath = (modulePath == null) ? "" : modulePath;
    }

    @Override
    public List<String> getFileNameList() {
        return fileNameList;
    }

    @Override
    public void setFileNameList(List<String> fileNameList) {
        for (int i = 0; i < fileNameList.size(); i++) {
            final String fileName = fileNameList.get(0);

            if (DexterConfig.getInstance().isAnalysisAllowedFile(fileName) == false) {
                throw new DexterRuntimeException("not supported file : " + fileName);
            }
        }

        this.fileNameList = fileNameList;
    }

    @Override
    public String getResultFileFullPath() {
        return resultFileFullPath;
    }

    @Override
    public void setResultFileFullPath(String resultFileFullPath) {
        if (Strings.isNullOrEmpty(resultFileFullPath) == false && new File(resultFileFullPath).exists() == false)
            throw new DexterRuntimeException("there is no result file path : " + resultFileFullPath);

        this.resultFileFullPath = resultFileFullPath;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public List<String> generateSourceFileFullPathList() {
        assert type != null;

        List<String> sourceFileFullPathList = new ArrayList<String>(0);

        if (this.fileNameList == null)
            return sourceFileFullPathList;

        if (type == Type.FILE) {
            sourceFileFullPathList = generateSourceFileFullPathListAsFileType();
        } else if (type == Type.FOLDER) {
            sourceFileFullPathList = generateSourceFileFullPathListAsFolderType();
        } else {
            sourceFileFullPathList = generateSourceFileFullPathListAsProjectType();
        }

        return sourceFileFullPathList;
    }

    private List<String> generateSourceFileFullPathListAsFileType() {
        final List<String> sourceFileFullPathList = new ArrayList<String>(this.fileNameList.size());

        // for performance, it is nested
        for (String fileName : this.fileNameList) {
            String filePathFromModule = this.modulePath + "/" + fileName;
            String fileFullPath = "";

            if (this.hasOneSourceDir) {
                fileFullPath = DexterUtil.refinePath(this.firstSourceDir + "/" + filePathFromModule);
            } else {
                for (String srcDir : this.sourceDirList) {
                    fileFullPath = DexterUtil.refinePath(srcDir + "/" + filePathFromModule);

                    if (new File(fileFullPath).exists()) {
                        break;
                    }
                }
            }

            if (Strings.isNullOrEmpty(fileFullPath))
                continue;

            sourceFileFullPathList.add(fileFullPath);
        }

        return sourceFileFullPathList;
    }

    private List<String> generateSourceFileFullPathListAsFolderType() {
        final String moduleFullPath = getExistingModuleFullPathWithSourceDirList();

        if (Strings.isNullOrEmpty(moduleFullPath))
            return new ArrayList<String>(0);

        List<String> sourceFileFullPathList = new ArrayList<String>(10);

        for (String filePath : DexterUtil.getSubFilenames(moduleFullPath)) {
            final File file = new File(filePath);

            if (file.isDirectory()) {
                continue;
            }

            if (DexterConfig.getInstance().isAnalysisAllowedFile(file.getName()) == false) {
                log.warn("not supported file : " + filePath);
                continue;
            }

            if (file.length() == 0L) {
                log.warn("zero size file : " + filePath);
                continue;
            }

            sourceFileFullPathList.add(filePath);
        }

        return sourceFileFullPathList;
    }

    private String getExistingModuleFullPathWithSourceDirList() {
        for (String srcDir : this.sourceDirList) {
            String moduleFullPath = DexterUtil.refinePath(srcDir + "/" + this.modulePath);

            if (new File(moduleFullPath).exists()) {
                return moduleFullPath;
            }
        }

        return "";
    }

    private List<String> generateSourceFileFullPathListAsProjectType() {
        List<String> sourceFileFullPathList = new ArrayList<String>(50);

        for (String srcDir : this.sourceDirList) {
            File baseFile = new File(srcDir);
            addSourceFileFullPathHierachy(baseFile, sourceFileFullPathList);
        }

        return sourceFileFullPathList;
    }

    private void addSourceFileFullPathHierachy(File baseFile, List<String> sourceFileFullPathList) {
        if (baseFile.isFile()) {
            if (DexterConfig.getInstance().isAnalysisAllowedFile(baseFile.getName()) == false) {
                log.warn("not supported file : " + baseFile.getAbsolutePath());
                return;
            }

            sourceFileFullPathList.add(DexterUtil.refinePath(baseFile.getAbsolutePath()));
        } else {
            for (File subFile : DexterUtil.getSubFiles(baseFile)) {
                addSourceFileFullPathHierachy(subFile, sourceFileFullPathList);
            }
        }
    }

    @Override
    public String getFirstFileName() {
        return this.fileNameList.get(0);
    }

    @Override
    public String getSnapshotId() {
        return snapshotId;
    }

    @Override
    public void setSnapshotId(String snapshotId) {
        this.snapshotId = snapshotId;
    }
}
