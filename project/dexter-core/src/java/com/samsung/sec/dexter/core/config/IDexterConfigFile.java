package com.samsung.sec.dexter.core.config;

import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface IDexterConfigFile {
	public enum Type {
		FILE, FOLDER, PROJECT, SNAPSHOT;

		public static boolean hasValue(final String value) {
			if (Strings.isNullOrEmpty(value))
				return false;

			for (Type type : Type.values()) {
				if (type.toString().equals(value)) {
					return true;
				}
			}

			return false;
		}
	};

	void setSnapshotId(String snapshotId);

	String getSnapshotId();

	String getFirstFileName();

	List<String> generateSourceFileFullPathList();

	void setType(Type type);

	Type getType();

	void setResultFileFullPath(String resultFileFullPath);

	String getResultFileFullPath();

	void setFileNameList(List<String> fileNameList);

	List<String> getFileNameList();

	void setModulePath(String modulePath);

	String getModulePath();

	void setLanguage(String language);

	String getLanguage();

	void setBinDir(String binDir);

	String getBinDir();

	void setLibDirList(List<String> libDirList);

	List<String> getLibDirList();

	void setSourceEncoding(String sourceEncoding);

	String getSourceEncoding();

	void setFunctionList(List<String> functionList);

	List<String> getFunctionList();

	void setHeaderDirList(List<String> headerDirList);

	List<String> getHeaderDirList();

	void setSourceDirList(List<String> sourceDirList);

	List<String> getSourceDirList();

	void setProjectFullPath(String projectFullPath);

	String getProjectFullPath();

	void setProjectName(String projectName);

	String getProjectName();

	void setDexterServerPort(int dexterServerPort);

	int getDexterServerPort();

	void setDexterServerIp(String dexterServerIp);

	String getDexterServerIp();

	void setDexterHome(String dexterHome);

	String getDexterHome();

	void setFields(final Map<String, Object> params);

	AnalysisConfig toAnalysisConfig();

	void loadFromFile(final File file, final String host, final int port);

}
