package com.samsung.sec.dexter.core.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

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

	public void saveJson(Writer writer, PeerReviewHomeJson testJson) throws IOException {
		String jsonStr = gson.toJson(testJson);
		writer.write(jsonStr);
		writer.flush();
	}
	
}
