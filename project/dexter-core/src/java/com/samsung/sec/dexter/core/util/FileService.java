package com.samsung.sec.dexter.core.util;

import java.io.File;

public class FileService {
	public boolean exists(String filePath) {
		return new File(filePath).exists();
	}
}
