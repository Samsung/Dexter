package com.samsung.sec.dexter.executor.cli.peerreview;

import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisEntityFactory;
import com.samsung.sec.dexter.core.analyzer.IAnalysisEntityFactory;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.PeerReviewHome;
import com.samsung.sec.dexter.core.config.PeerReviewWatch;
import com.samsung.sec.dexter.core.config.DexterConfig.AnalysisType;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.executor.cli.AnalyzedFileInfo;

public class PeerReviewHomeMonitor implements Runnable {
	private final static Logger log = Logger.getLogger(PeerReviewHomeMonitor.class);
	private final ExecutorService executorService;
	final WatchService watchService;
	private final PeerReviewCLIAnalyzer peerReviewCLIAnalyzer;
	private Map<WatchKey, PeerReviewWatch> peerReviewWatchMap;
	private Future<?> monitoringFuture;
	private MonitoringState monitoringState;
	private final AnalyzedFileInfo lastAnalyzedFileInfo;
	
	public enum MonitoringState { STOP, RUNNING, CANCEL }; 
	private final static long WATCH_POLL_TIMEOUT = 5;
	
	public PeerReviewHomeMonitor(ExecutorService excutorService, WatchService watchService, PeerReviewCLIAnalyzer peerReviewCLIAnalyzer) {
		this.executorService = excutorService;
		this.watchService = watchService;
		this.peerReviewCLIAnalyzer = peerReviewCLIAnalyzer;
		this.peerReviewWatchMap = new HashMap<WatchKey, PeerReviewWatch>();
		monitoringFuture = null;
		monitoringState = MonitoringState.STOP;
		lastAnalyzedFileInfo = new AnalyzedFileInfo();
	}
	
	public void update(List<PeerReviewHome> peerReviewHomeList) {
		cancelMonitoring();
		updatePeerReviewHomeMap(peerReviewHomeList);
		startMonitoring();
	}
	
	private void updatePeerReviewHomeMap(List<PeerReviewHome> peerReviewHomeList) {
		peerReviewWatchMap = registerHomeListForWatch(peerReviewHomeList);
	}
	
	private Map<WatchKey, PeerReviewWatch> registerHomeListForWatch(List<PeerReviewHome> peerReviewHomeList) {
		Map<WatchKey, PeerReviewWatch> peerReviewWatchMap = new HashMap<WatchKey, PeerReviewWatch>();
		
		for (PeerReviewHome home : peerReviewHomeList) {
			registerHomeForWatch(home, peerReviewWatchMap);
		}
		
		return peerReviewWatchMap;
	}
	
	private void registerHomeForWatch(final PeerReviewHome home, final Map<WatchKey, PeerReviewWatch> peerReviewWatchMap) {
		try {
			registerPathForWatch(Paths.get(home.getSourceDir()), home, peerReviewWatchMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void registerPathForWatch(final Path dir, final PeerReviewHome home, final Map<WatchKey, PeerReviewWatch> peerReviewWatchMap) {
		try {
			Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
			    @Override
			    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
			        throws IOException
			    {
			    	WatchKey key = dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
			    	peerReviewWatchMap.put(key, new PeerReviewWatch(home, dir));
			        return FileVisitResult.CONTINUE;
			    }
			    
			    @Override
			    public FileVisitResult visitFileFailed(Path file, IOException io) {
			    	log.error("Access failed >> " + io.getMessage());
			    	return FileVisitResult.SKIP_SUBTREE;
			    }
			});
		} catch (IOException e) {
			e.printStackTrace();
			throw new DexterRuntimeException("IOException occurred on registering peer-review home with watch");
		} 
	}
	
	private void cancelMonitoring() {
		if (monitoringFuture != null) {
			monitoringState = MonitoringState.CANCEL;
			
			try {
				monitoringFuture.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			} finally {
				log.info("Monitoring is canceled.");
			}
		}
	}
	
	private void startMonitoring() {
		log.info("Start monitoring...");
		monitoringFuture = executorService.submit(this);

	}

	public Map<WatchKey, PeerReviewWatch> getPeerReviewWatchMap() {
		return peerReviewWatchMap;
	}

	@Override
	public void run() {
		monitoringState = MonitoringState.RUNNING;
		
		try {
			while(true) {
				processWatchEvents();
				
				if (monitoringState == MonitoringState.CANCEL) {
					log.info("Stop monitoring");
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		} finally {
			monitoringState = MonitoringState.STOP;
		}
		
	}
	
	private void processWatchEvents() throws InterruptedException {
		WatchKey key = watchService.poll(WATCH_POLL_TIMEOUT, TimeUnit.SECONDS);
		if (key == null) return;
		
		PeerReviewWatch peerReviewWatch = peerReviewWatchMap.get(key);
		List<String> changedFileList = new ArrayList<String>(); 

		for (WatchEvent<?> event: key.pollEvents()) {
			Kind<?> kind = event.kind();

			if (kind == OVERFLOW) {
				continue;
			}

			Path filePath = getFilePathFromWatchEvent(event, peerReviewWatch.getWatchingPath());
			log.debug(String.format("Watched >> %s: %s%n", event.kind().name(), filePath));


			if (kind == ENTRY_CREATE && Files.isDirectory(filePath, NOFOLLOW_LINKS)) {
				registerPathForWatch(filePath, peerReviewWatch.getHome(), peerReviewWatchMap);
			}

			if (isValidSourceFile(filePath) && !lastAnalyzedFileInfo.equals(filePath)) {
				changedFileList.add(filePath.toString());
				lastAnalyzedFileInfo.set(filePath);
			}
		}

		key.reset();
		
		if (changedFileList.size() > 0) {
			log.info("Changed source list : " + changedFileList.toString());
			peerReviewCLIAnalyzer.analyze(changedFileList, peerReviewWatch.getHome());
		}
	}

	@SuppressWarnings("unchecked")
	private Path getFilePathFromWatchEvent(WatchEvent<?> event, Path parentPath) {
		WatchEvent<Path> ev = (WatchEvent<Path>)event;
        Path fileName = ev.context();
        return parentPath.resolve(fileName);
	}
	
	private boolean isValidSourceFile(Path filePath) {
		Pattern pattern = Pattern.compile(
				"^\\w+.*\\.(cpp|c\\+\\+|c|h|hpp|h\\+\\+|java|cs|js|html)$", 
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(filePath.getFileName().toString());
		
		return Files.isRegularFile(filePath, NOFOLLOW_LINKS) && matcher.matches();
	}
	
	public MonitoringState getMonitoringState() {
		return monitoringState;
	}
	
	public void cancel() {
		cancelMonitoring();
	}
	
}
