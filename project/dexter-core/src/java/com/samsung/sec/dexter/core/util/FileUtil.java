package com.samsung.sec.dexter.core.util;

import java.nio.file.*;

import static java.nio.file.LinkOption.*;

public class FileService {
	public boolean exists(String filePath) {
		return Files.exists(Paths.get(filePath),  NOFOLLOW_LINKS);
	}
}
