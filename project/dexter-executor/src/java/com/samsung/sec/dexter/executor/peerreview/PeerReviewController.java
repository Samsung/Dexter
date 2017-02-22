package com.samsung.sec.dexter.executor.peerreview;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.samsung.sec.dexter.core.config.PeerReviewHome;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.DexterServerConfig;
import com.samsung.sec.dexter.executor.cli.Main;

public class PeerReviewController {
	private final static Logger log = Logger.getLogger(Main.class);
	private PeerReviewHomeMonitor peerReviewHomeMonitor;
	private List<PeerReviewHome> peerReviewHomeList;
	
	public PeerReviewController(PeerReviewHomeMonitor peerReviewHomeMonitor) {
		this.peerReviewHomeMonitor = peerReviewHomeMonitor;
		this.peerReviewHomeList = new ArrayList<PeerReviewHome>();
	}

	public void update(File configFile) {
		try {
			updatePeerReviewHome(configFile);
			notifyToMonitor();
		} catch (Exception e) {
			cancelMonitor();
			throw e;
		}
	}
	
	private void notifyToMonitor() {
		peerReviewHomeMonitor.update(peerReviewHomeList);
	}
	
	private void cancelMonitor() {
		peerReviewHomeMonitor.cancel();
	}
	
	private void updatePeerReviewHome(File configFile) {
		log.info("Update peer review home");
		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject)jsonParser.parse(new FileReader(configFile));		
			
			DexterServerConfig dexterServerConfig = createDexterServerConfig(jsonObject);
			peerReviewHomeList = createPeerReviewHomeList(jsonObject, dexterServerConfig);
			
		} catch (IOException | ParseException e) {
			throw new DexterRuntimeException("Can't parse peer-review config file : " + configFile.getPath());
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<PeerReviewHome> createPeerReviewHomeList(JSONObject jsonObject, DexterServerConfig dexterServerConfig) {
		JSONArray homes = (JSONArray)jsonObject.get("home");
		if (homes == null || homes.size() == 0)
			throw new DexterRuntimeException("Home info dosen't exit in json");
		
		
		List<PeerReviewHome> peerReviewHomeList = new ArrayList<PeerReviewHome>();
		Iterator<JSONObject> iterator = homes.iterator();
		
		while (iterator.hasNext()) {
			JSONObject home = iterator.next();
			PeerReviewHome peerReviewHome = getPeerReviewHome(home, dexterServerConfig);
			
			peerReviewHomeList.add(peerReviewHome);
		}
		
		return peerReviewHomeList;
	}
	
	private PeerReviewHome getPeerReviewHome(JSONObject home, DexterServerConfig dexterServerConfig) {
		String projectName = (String)home.get("projectName");
		String sourceDir = (String)home.get("sourceDir");
		boolean isActive = (home.get("active") != null && 
				((String)home.get("active")).equals("on")) ? true : false;
		
		if (projectName == null || sourceDir == null) 
			throw new DexterRuntimeException("Invalid home info in json");
		
		return new PeerReviewHome(dexterServerConfig, projectName, sourceDir, isActive);
	}

	private DexterServerConfig createDexterServerConfig(JSONObject jsonObject) {
		JSONObject serverInfo = (JSONObject)jsonObject.get("server");
		if (serverInfo == null)
			throw new DexterRuntimeException("Server info dosen't exit in json");
			
		String id = (String)serverInfo.get("id");
		String pw = (String)serverInfo.get("pw");
		String ip = (String)serverInfo.get("ip");
		int port;
		
		try {
			port = Integer.parseInt((String)serverInfo.get("port"));
		} catch (NumberFormatException e) {
			throw new DexterRuntimeException("Invalid server port in json");
		}
		
		if (id == null || pw == null || ip == null)
			throw new DexterRuntimeException("Invalid server info in json");
		
		return new DexterServerConfig(id, pw, ip, port);
	}

	public List<PeerReviewHome> getPeerReviewHomeList() {
		return peerReviewHomeList;
	}
}
