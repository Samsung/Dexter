package com.samsung.sec.dexter.core.util;

import java.io.Reader;

import com.google.gson.Gson;
import com.samsung.sec.dexter.core.config.PeerReviewHomeJson;

public class PeerReviewHomeUtil {
	private Gson gson;
	
	public PeerReviewHomeUtil(Gson gson) {
		this.gson = gson;
	}

	public PeerReviewHomeJson loadJson(Reader reader) {
		return gson.fromJson(reader, PeerReviewHomeJson.class);
	}
	
}
