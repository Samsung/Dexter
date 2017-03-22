package com.samsung.sec.dexter.core.util;

import java.io.Reader;

import com.google.gson.Gson;
import com.samsung.sec.dexter.core.config.PeerReviewHome;
import com.samsung.sec.dexter.core.config.PeerReviewHomeJson;

public class PeerReviewHomeUtil {
	private Gson gson;
	
	public PeerReviewHomeUtil(Gson gson) {
		this.gson = gson;
	}

	public PeerReviewHomeJson loadJson(Reader reader) {
		PeerReviewHomeJson homeJson = gson.fromJson(reader, PeerReviewHomeJson.class);
		setServerConfigToHomeList(homeJson);
		
		return homeJson;
	}

	private void setServerConfigToHomeList(PeerReviewHomeJson homeJson) {
		DexterServerConfig serverConfig = homeJson.getServerConfig();
		
		for (PeerReviewHome home : homeJson.getHomeList()) {
			home.setDexterServerConfig(serverConfig);
		}
	}
	
}
