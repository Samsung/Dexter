package com.samsung.sec.dexter.core.config;

import com.samsung.sec.dexter.core.util.DexterServerConfig;

public class PeerReviewHome {
	private DexterServerConfig dexterServerConfig;
	private String projectName;
	private String sourceDir;
	private boolean isActive;
	
	public PeerReviewHome(DexterServerConfig dexterServerConfig, String projectName, String sourceDir, boolean isActive) {
		this.setDexterServerConfig(dexterServerConfig);
		this.setProjectName(projectName);
		this.setSourceDir(sourceDir);
		this.setActive(isActive);
	}

	public DexterServerConfig getDexterServerConfig() {
		return dexterServerConfig;
	}

	public void setDexterServerConfig(DexterServerConfig dexterServerConfig) {
		this.dexterServerConfig = dexterServerConfig;
	}

	public String getSourceDir() {
		return sourceDir;
	}

	public void setSourceDir(String sourceDir) {
		this.sourceDir = sourceDir;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
}
