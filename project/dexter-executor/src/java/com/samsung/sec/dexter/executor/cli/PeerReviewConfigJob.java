package com.samsung.sec.dexter.executor.cli;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.IDexterHomeListener;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;

public class PeerReviewConfigJob implements Runnable, IDexterHomeListener {
	private final static Logger log = Logger.getLogger(PeerReviewConfigJob.class);
	private final DexterConfig dexterConfig;
	private final PeerReviewController peerReviewController;
	private final ScheduledExecutorService scheduler;
	private ScheduledFuture<?> configJobFuture;
	private File configFile;
	private long configFileSyncTime;
	
	private final static long SCHEDULE_INTERVAL = 5L;
	private final static String DEFAULT_CONFIG_DIR = "/cfg/";
	private final static String DEFAULT_CONFIG_NAME = "peerReview.json";
	
	public PeerReviewConfigJob(DexterConfig dexterConfig, PeerReviewController peerReviewController, ScheduledExecutorService scheduler) {
		this.dexterConfig = dexterConfig;
		this.peerReviewController = peerReviewController;
		this.scheduler = scheduler;
		
		configJobFuture = null;
		configFile = null;
		configFileSyncTime = 0;
	}

	@Override
	public void run() {
		if (isConfigFileChanged()) {
			log.info("Peer-review config file is changed");
			peerReviewController.update(configFile);
		}
	}
	
	private boolean isConfigFileChanged() {
		long configFileLastModifiedTime = configFile.lastModified();
		
		if (configFileLastModifiedTime == configFileSyncTime)
			return false;
		
		configFileSyncTime = configFileLastModifiedTime;
		return true;
	}
	
	public void start() {
		setConfigFile();
		startScheduler();
		registDexterHomeListener();
	}
	
	private void registDexterHomeListener() {
		dexterConfig.addDexterHomeListener(this);
	}

	private void startScheduler() {
		configJobFuture = scheduler.scheduleAtFixedRate(this, 0L, SCHEDULE_INTERVAL, TimeUnit.SECONDS);
	}
	
	private void cancelScheduler() {
		configJobFuture.cancel(false);
	}
	
	private void setConfigFile() {
		String dexterHome = dexterConfig.getDexterHome();
		if (dexterHome == null) 
			throw new DexterRuntimeException("Dexter home is null");
			
		configFile = new File(dexterHome + DEFAULT_CONFIG_DIR + DEFAULT_CONFIG_NAME);
		if (!configFile.exists())
			throw new DexterRuntimeException("Peer-review config file doesn't exist : " + configFile.getPath());
		
		this.configFileSyncTime = 0;
	}

	public File getConfigFile() {
		return configFile;
	}

	public PeerReviewController getPeerReviewController() {
		return peerReviewController;
	}

	@Override
	public void handleDexterHomeChanged(String oldPath, String newPath) {
		cancelScheduler();
		setConfigFile();
		startScheduler();
	}

	

}
