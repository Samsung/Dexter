package com.samsung.sec.dexter.core.config;

import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EmptyDexterConfigFile implements IDexterConfigFile {

    @Override
    public void setSnapshotId(String snapshotId) {}

    @Override
    public String getSnapshotId() {
        return "";
    }

    @Override
    public String getFirstFileName() {
        return "";
    }

    @Override
    public List<String> generateSourceFileFullPathList() {
        return new ArrayList<String>(0);
    }

    @Override
    public void setType(Type type) {}

    @Override
    public Type getType() {
        return Type.FILE;
    }

    @Override
    public void setResultFileFullPath(String resultFileFullPath) {}

    @Override
    public String getResultFileFullPath() {
        return "";
    }

    @Override
    public void setFileNameList(List<String> fileNameList) {}

    @Override
    public List<String> getFileNameList() {
        return new ArrayList<String>(0);
    }

    @Override
    public void setModulePath(String modulePath) {}

    @Override
    public String getModulePath() {
        return "";
    }

    @Override
    public void setLanguage(String language) {}

    @Override
    public String getLanguage() {
        return "";
    }

    @Override
    public void setBinDir(String binDir) {}

    @Override
    public String getBinDir() {
        return "";
    }

    @Override
    public void setLibDirList(List<String> libDirList) {}

    @Override
    public List<String> getLibDirList() {
        return new ArrayList<String>(0);
    }

    @Override
    public void setSourceEncoding(String sourceEncoding) {}

    @Override
    public String getSourceEncoding() {
        return "";
    }

    @Override
    public void setFunctionList(List<String> functionList) {}

    @Override
    public List<String> getFunctionList() {
        return new ArrayList<String>(0);
    }

    @Override
    public void setHeaderDirList(List<String> headerDirList) {}

    @Override
    public List<String> getHeaderDirList() {
        return new ArrayList<String>(0);
    }

    @Override
    public void setSourceDirList(List<String> sourceDirList) {}

    @Override
    public List<String> getSourceDirList() {
        return new ArrayList<String>(0);
    }

    @Override
    public void setProjectFullPath(String projectFullPath) {}

    @Override
    public String getProjectFullPath() {
        return "";
    }

    @Override
    public void setProjectName(String projectName) {}

    @Override
    public String getProjectName() {
        return "";
    }

    @Override
    public void setDexterServerPort(int dexterServerPort) {}

    @Override
    public int getDexterServerPort() {
        return 0;
    }

    @Override
    public void setDexterServerIp(String dexterServerIp) {}

    @Override
    public String getDexterServerIp() {
        return "";
    }

    @Override
    public void setDexterHome(String dexterHome) {}

    @Override
    public String getDexterHome() {
        return "";
    }

    @Override
    public void setFields(Map<String, Object> params) {}

    @Override
    public AnalysisConfig toAnalysisConfig() {
        return null;
    }

    @Override
    public void loadFromFile(final File file) {}
}
