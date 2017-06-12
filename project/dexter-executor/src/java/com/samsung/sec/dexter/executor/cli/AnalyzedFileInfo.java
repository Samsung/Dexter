package com.samsung.sec.dexter.executor.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AnalyzedFileInfo {
	private long fileSize;
	private long lastModifiedTime;
	private String filePathString;
	
	private static final long TIME_OFFSET = 200; // msec 
	
	public AnalyzedFileInfo() {
		this.fileSize = 0;
		this.lastModifiedTime = 0;
		this.filePathString = "";
	}
	
	public void set(Path filePath) {
		try {
			fileSize = Files.size(filePath);
			lastModifiedTime = filePath.toFile().lastModified();
			filePathString = filePath.toString();
		} catch (IOException e) {
			fileSize = 0;
			lastModifiedTime = 0;
			filePathString = "";
		}
	}
	
	public boolean equals(Path filePath) {
		boolean isSame = false;
		
		try {
			if (fileSize == Files.size(filePath) &&
					filePathString.equals(filePath.toString()) &&
					(filePath.toFile().lastModified()) - lastModifiedTime < TIME_OFFSET) {
				isSame = true;
			}
		} catch (IOException e) {
		}
	
		return isSame;
	}
}
