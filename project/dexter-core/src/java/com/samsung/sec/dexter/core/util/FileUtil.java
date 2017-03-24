package com.samsung.sec.dexter.core.util;

import java.nio.file.*;

import static java.nio.file.LinkOption.*;

import java.io.IOException;
import java.io.Writer;

public class FileUtil {
	public boolean exists(String filePath) {
		return Files.exists(Paths.get(filePath),  NOFOLLOW_LINKS);
	}

	public void write(Writer writer, String msg) throws IOException {
		writer.write(msg);
	}
}
