package com.samsung.sec.dexter.core.config;

import com.google.gson.annotations.SerializedName;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisEntityFactory;
import com.samsung.sec.dexter.core.analyzer.IAnalysisEntityFactory;
import com.samsung.sec.dexter.core.config.DexterConfig.AnalysisType;
import com.samsung.sec.dexter.core.util.DexterServerConfig;

public class PeerReviewHome {
	private transient DexterServerConfig dexterServerConfig;
	private String projectName;
	private String sourceDir;
	@SerializedName("active") 
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
	
	public AnalysisConfig toAnalysisConfig() {
        IAnalysisEntityFactory configFactory = new AnalysisEntityFactory();
        AnalysisConfig analysisConfig = configFactory.createAnalysisConfig();

        analysisConfig.setProjectName(projectName);
        analysisConfig.setProjectFullPath(sourceDir);
        analysisConfig.setAnalysisType(AnalysisType.FILE);

        return analysisConfig;
    }
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false; 
		
		if (this.getClass() != obj.getClass()) 
			return false;
		
		if (this == obj) 
			return true;

		PeerReviewHome that = (PeerReviewHome)obj;
		
		return isSameObject(this.projectName, that.projectName) &&
				isSameObject(this.sourceDir, that.sourceDir) &&
				isSameObject(this.dexterServerConfig, that.dexterServerConfig) &&
				this.isActive == that.isActive;
	}
	
	private boolean isSameObject(Object src, Object dest) {
		return src == null ? dest == null : src.equals(dest);
	}

}
