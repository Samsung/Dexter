package com.samsung.sec.dexter.core.config;

import java.nio.file.Path;

// DPR: 규모가 큰 소스 폴더에서 테스트 필요
public class PeerReviewWatch {
	private final PeerReviewHome home;
	private final Path watchingPath;
	
	public PeerReviewWatch(PeerReviewHome home, Path watchingPath) {
		this.home = home;
		this.watchingPath = watchingPath;
	}

	public PeerReviewHome getHome() {
		return home;
	}

	public Path getWatchingPath() {
		return watchingPath;
	}

}
