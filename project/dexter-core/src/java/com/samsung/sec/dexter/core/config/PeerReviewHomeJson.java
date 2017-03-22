package com.samsung.sec.dexter.core.config;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.samsung.sec.dexter.core.util.DexterServerConfig;

public class PeerReviewHomeJson {
	@SerializedName("server")
	private DexterServerConfig serverConfig;
	@SerializedName("home")
	private List<PeerReviewHome> homeList;

	public PeerReviewHomeJson(DexterServerConfig serverConfig, List<PeerReviewHome> homeList) {
		this.serverConfig = serverConfig;
		this.homeList = homeList;
	}

	public DexterServerConfig getServerConfig() {
		return serverConfig;
	}

	public void setServerConfig(DexterServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}

	public List<PeerReviewHome> getHomeList() {
		return homeList;
	}

	public void setHomeList(List<PeerReviewHome> homeList) {
		this.homeList = homeList;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false; 
		
		if (this.getClass() != obj.getClass()) 
			return false;
		
		if (this == obj) 
			return true;

		PeerReviewHomeJson that = (PeerReviewHomeJson)obj;
		
		return isSameObject(this.serverConfig, that.serverConfig) &&
				isSameObject(this.homeList, that.homeList);

	}
	
	private boolean isSameObject(Object src, Object dest) {
		return src == null ? dest == null : src.equals(dest);
	}

	
}
