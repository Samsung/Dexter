package com.samsung.sec.dexter.executor.cli;

import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.samsung.sec.dexter.core.config.PeerReviewHome;
import com.samsung.sec.dexter.core.config.PeerReviewWatch;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;

public class PeerReviewHomeMonitor implements Runnable {
	private final static Logger log = Logger.getLogger(PeerReviewHomeMonitor.class);
	private final ExecutorService executorService;
	final WatchService watchService;
	private Map<WatchKey, PeerReviewWatch> peerReviewWatchMap;
	
	private final static long STOP_WAIT_TIMEOUT = 10;
	private final static long WATCH_POLL_TIMEOUT = 5;
	
	public PeerReviewHomeMonitor(ExecutorService excutorService, WatchService watchService) {
		this.executorService = excutorService;
		this.watchService = watchService;
		this.peerReviewWatchMap = new HashMap<WatchKey, PeerReviewWatch>();
	}
	
	public void update(List<PeerReviewHome> peerReviewHomeList) {
		stopExcutorService();
		updatePeerReviewHomeMap(peerReviewHomeList);
		startExcutorService();
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
		registerPathForWatch(Paths.get(home.getSourceDir()), home, peerReviewWatchMap);
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
			});
		} catch (IOException e) {
			e.printStackTrace();
			throw new DexterRuntimeException("IOException occurred on registering peer-review home with watch");
		}
	}
	
	private void stopExcutorService() {
		executorService.shutdown();
		
		try {
			if (!executorService.awaitTermination(STOP_WAIT_TIMEOUT, TimeUnit.SECONDS)) {
				executorService.shutdownNow(); 
			}
		} catch (InterruptedException e) {
			executorService.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}
	
	private void startExcutorService() {
		while(true) {
			executorService.execute(this);
		}
	}

	public Map<WatchKey, PeerReviewWatch> getPeerReviewWatchMap() {
		return peerReviewWatchMap;
	}

	@Override
	public void run() {
		try {
			processWatchEvents();
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		
	}
	
	private void processWatchEvents() throws InterruptedException {
		WatchKey key = watchService.poll(WATCH_POLL_TIMEOUT, TimeUnit.SECONDS);
		PeerReviewWatch peerReviewWatch = peerReviewWatchMap.get(key);
		List<String> changedFileList = new ArrayList<String>(); 

		log.info("processWatchEvents called");

		for (WatchEvent<?> event: key.pollEvents()) {
			Kind<?> kind = event.kind();

			if (kind == OVERFLOW) {
				continue;
			}

			Path filePath = getFilePathFromWatchEvent(event, peerReviewWatch.getWatchingPath());
			log.info(String.format("%s: %s%n", event.kind().name(), filePath));


			if (kind == ENTRY_CREATE && Files.isDirectory(filePath, NOFOLLOW_LINKS)) {
				registerPathForWatch(filePath, peerReviewWatch.getHome(), peerReviewWatchMap);
			}

			if (Files.isRegularFile(filePath, NOFOLLOW_LINKS)) {
				changedFileList.add(filePath.toString());
			}
		}

		key.reset();
		// TODO analyze fileList
	}
	
	@SuppressWarnings("unchecked")
	private Path getFilePathFromWatchEvent(WatchEvent<?> event, Path parentPath) {
		WatchEvent<Path> ev = (WatchEvent<Path>)event;
        Path fileName = ev.context();
        return parentPath.resolve(fileName);
	}

}
