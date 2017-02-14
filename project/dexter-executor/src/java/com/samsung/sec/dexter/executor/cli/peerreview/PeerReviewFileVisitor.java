package com.samsung.sec.dexter.executor.cli.peerreview;

import java.nio.file.SimpleFileVisitor;

import com.samsung.sec.dexter.core.config.PeerReviewHome;

public class PeerReviewFileVisitor<T> extends SimpleFileVisitor<T> {
	private final PeerReviewHome peerReviewHome;
	
	public PeerReviewFileVisitor(PeerReviewHome peerReviewHome) {
		this.peerReviewHome = peerReviewHome;
	}

	public PeerReviewHome getPeerReviewHome() {
		return peerReviewHome;
	}

}
