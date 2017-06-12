package com.samsung.sec.dexter.executor.peerreview;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.samsung.sec.dexter.core.config.PeerReviewHome;
import com.samsung.sec.dexter.core.config.PeerReviewHomeJson;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.DexterServerConfig;
import com.samsung.sec.dexter.core.util.PeerReviewHomeUtil;
import com.samsung.sec.dexter.executor.cli.Main;

public class PeerReviewController {
	private final static Logger log = Logger.getLogger(Main.class);
	private PeerReviewHomeMonitor peerReviewHomeMonitor;
	private List<PeerReviewHome> peerReviewHomeList;
	private PeerReviewHomeUtil homeUtil;
	
	public PeerReviewController(PeerReviewHomeMonitor peerReviewHomeMonitor, PeerReviewHomeUtil homeUtil) {
		this.peerReviewHomeMonitor = peerReviewHomeMonitor;
		this.peerReviewHomeList = new ArrayList<PeerReviewHome>();
		this.homeUtil = homeUtil;
	}

	public void update(File configFile) {
		try {
			updatePeerReviewHome(configFile);
			restartMonitor();
		} catch (Exception e) {
			cancelMonitor();
			throw e;
		}
	}
	
	private void restartMonitor() {
		peerReviewHomeMonitor.restart(peerReviewHomeList);
	}
	
	private void cancelMonitor() {
		peerReviewHomeMonitor.cancel();
	}
	
	private void updatePeerReviewHome(File configFile) {
		log.info("Update peer review home");
		try {
			PeerReviewHomeJson homeJson = homeUtil.loadJson(new FileReader(configFile));
			peerReviewHomeList = homeJson.getHomeList();
			
		} catch (IOException e) {
			throw new DexterRuntimeException("Can't parse peer-review config file : " + configFile.getPath());
		}
	}
	
	public List<PeerReviewHome> getPeerReviewHomeList() {
		return peerReviewHomeList;
	}

	public void createHomeJsonConfigFile(Writer writer, PeerReviewHomeJson homeJson) throws IOException {
		homeUtil.saveJson(writer, homeJson);
	}
}
