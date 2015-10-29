/**
 * Copyright (c) 2014 Samsung Electronics, Inc.,
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package com.samsung.sec.dexter.core.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.samsung.sec.dexter.core.analyzer.EndOfAnalysisHandler;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.metrics.CodeMetrics;

public class DexterUtil {
	public final static String LINE_SEPARATOR = System.getProperty("line.separator");
	public final static String PATH_SEPARATOR = getPathSeparator();
	private final static Logger logger = Logger.getLogger(DexterUtil.class);

	public static enum OS {
		WINDOWS, LINUX, MAC, UNKNOWN
	};

	public static enum BIT {
		_32, _64, _128, UNKNOWN
	};

	public static enum OS_BIT {
		WIN32, WIN64, WIN128, LINUX32, LINUX64, LINUX128, UNKNOWN
	};

	public final static String currentDateTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		return format.format(Calendar.getInstance().getTime()).toString();
	}

	public final static String currentDateTimeMillis() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		return format.format(Calendar.getInstance().getTime()).toString();
	}

	private static String getPathSeparator() {
		String yourOs = System.getProperty("os.name").toLowerCase();

		if (Strings.isNullOrEmpty(yourOs) == false && yourOs.indexOf("win") >= 0) {
			return "\\";
		} else if (Strings.isNullOrEmpty(yourOs) == false && (yourOs.indexOf("nix") >= 0 || yourOs.indexOf("nux") >= 0)) {
			return "/";
		} else {
			return "/";
		}
	}

	public static OS getOS() {
		String yourOs = System.getProperty("os.name").toLowerCase();

		if (Strings.isNullOrEmpty(yourOs) == false && yourOs.indexOf("win") >= 0) {
			return OS.WINDOWS;
		} else if (Strings.isNullOrEmpty(yourOs) == false && (yourOs.indexOf("nix") >= 0 || yourOs.indexOf("nux") >= 0)) {
			return OS.LINUX;
		} else {
			return OS.UNKNOWN;
		}
	}

	public static BIT getBit() {
		String bit = System.getProperty("sun.arch.data.model").toLowerCase(); // 32
																			  // |
																			  // 64

		if (Strings.isNullOrEmpty(bit)) {
			bit = System.getProperty("os.arch").toLowerCase();

			if (Strings.isNullOrEmpty(bit)) {
				return BIT.UNKNOWN;
			} else if (bit.indexOf(32) >= 0) {
				return BIT._32;
			} else if (bit.indexOf(64) >= 0) {
				return BIT._64;
			} else if (bit.indexOf(128) >= 0) {
				return BIT._128;
			} else {
				return BIT.UNKNOWN;
			}
		} else if (bit.equals("32")) {
			return BIT._32;
		} else if (bit.equals("64")) {
			return BIT._64;
		} else if (bit.equals("128")) {
			return BIT._128;
		} else {
			return BIT.UNKNOWN;
		}
	}

	public static OS_BIT getOsBit() {
		if (getOS() == OS.WINDOWS) {
			BIT bit = getBit();
			if (bit == BIT._32) {
				return OS_BIT.WIN32;
			} else if (bit == BIT._64) {
				return OS_BIT.WIN64;
			} else if (bit == BIT._128) {
				return OS_BIT.WIN128;
			} else {
				return OS_BIT.UNKNOWN;
			}
		} else if (getOS() == OS.LINUX) {
			BIT bit = getBit();
			if (bit == BIT._32) {
				return OS_BIT.LINUX32;
			} else if (bit == BIT._64) {
				return OS_BIT.LINUX64;
			} else if (bit == BIT._128) {
				return OS_BIT.LINUX128;
			} else {
				return OS_BIT.UNKNOWN;
			}
		} else {
			return OS_BIT.UNKNOWN;
		}
	}

	/**
	 * Utf-8 String to Base64 Encoding
	 * 
	 * @param string
	 * @return String
	 */
	public static String getBase64String(final String string) {
		try {
			return BaseEncoding.base64().encode(string.getBytes("utf8"));
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
			return "";
		}
	}

	public static StringBuilder readFile(final String filePath){
		assert Strings.isNullOrEmpty(filePath) == false;

		final File file = new File(filePath);

		checkFileExistence(filePath, file);

		final StringBuilder contents = new StringBuilder(10000);
		try {
			for (String content : Files.readLines(file, Charsets.UTF_8)) {
				contents.append(content).append(DexterUtil.LINE_SEPARATOR);
			}

			return contents;
		} catch (IOException e) {
			throw new DexterRuntimeException(e.getMessage(), e);
		}
	}

	private static void checkFileExistence(final String filePath,
			final File file) {
		if (file.exists() == false || file.isDirectory()) 
			throw new DexterRuntimeException("There is no file to read : " + filePath);
	}
	
	public static String getContentsFromFile(final String filePath, final Charset charset) {
		assert Strings.isNullOrEmpty(filePath) == false;

		final File file = new File(filePath);
		checkFileExistence(filePath, file);

		final StringBuilder contents = new StringBuilder(10000);
		try {
			for (String content : Files.readLines(file, charset)) {
				contents.append(content).append(DexterUtil.LINE_SEPARATOR);
			}

			return contents.toString();
		} catch (IOException e) {
			throw new DexterRuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * @param file
	 * @return
	 */
	public static int getLines(final File file) {
		if (file.exists() == false || file.isDirectory()) {
			logger.error("There is no file");
			return 0;
		}

		try {
			return Files.readLines(file, DexterConfig.getInstance().getSourceEncoding()).size();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return 0;
		}
	}

	public static boolean copyFileInClassPath(final ClassLoader classLoader, String sourceFilePath, String targetFilePath) {
		sourceFilePath = DexterUtil.refinePath(sourceFilePath);
		targetFilePath = DexterUtil.refinePath(targetFilePath);
		
		InputStream is = classLoader.getClass().getResourceAsStream(sourceFilePath);
		
		if (is == null) {
			logger.error("can't find file: " + sourceFilePath);
			return false;
		}

		try {
			FileUtils.copyInputStreamToFile(is, new File(targetFilePath));
			return true;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return false;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
	}
	
	public static boolean copyFilesInClassPath(@SuppressWarnings("rawtypes") Class clazz, final String sourcePath, final String targetPath) {

		List<String> fileList = new ArrayList<String>();
		BufferedReader br = null;
        try {
        	br = new BufferedReader(new InputStreamReader(clazz.getResourceAsStream(sourcePath)));
        	
        	String fileName;
        	while( (fileName = br.readLine()) != null){
        		fileList.add(fileName);
        	}
        	
	        fileList = IOUtils.readLines(clazz.getResourceAsStream(sourcePath), Charsets.UTF_8);
        } catch (IOException e) {
        	logger.error(e.getMessage(), e);
        	return false;
        } finally {
        	if(br != null){
        		try {
	                br.close();
                } catch (IOException e) {
                }
        	}
        }
		
		if(fileList == null || fileList.size() == 0){
			logger.warn("there is no files to copy : " + sourcePath);
			return false;
		}
		
		boolean isOk = true;
		
		for(String filePath : fileList){
			final InputStream is = clazz.getResourceAsStream(filePath);
			
			if (is == null) {
				logger.error("can't find file: " + sourcePath);
				isOk = false;
				continue;
			}
			
			try {
				FileUtils.copyInputStreamToFile(is, new File(targetPath));
				logger.debug("file copied:  " + targetPath);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				isOk =  false;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				isOk = false;
			} finally {
				try {
	                is.close();
                } catch (IOException e) {
                }
			}
		}
		
		return isOk;
	}

	/**
	 * Size of the buffer to read/write data
	 */
	private static final int BUFFER_SIZE = 4096;

	/**
	 * 
	 * This utility extracts files and directories of a standard zip file to a
	 * destination directory.
	 * 
	 * @author www.codejava.net
	 * 
	 *         Extracts a zip file specified by the zipFilePath to a directory
	 *         specified by destDirectory (will be created if does not exists)
	 * @param zipFilePath
	 * @param destDirectory
	 * @throws IOException
	 */
	public static void unzip(final String zipFilePath, final String destDirectory) throws IOException {
		final File destDir = new File(destDirectory);
		if (!destDir.exists()) {
			if (destDir.mkdir() == false) {
				Files.createParentDirs(destDir);

				if (destDir.mkdir() == false) {
					logger.error("can't create a destDirectory : " + destDirectory);
					return;
				}
			}
		}

		final ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
		ZipEntry entry = zipIn.getNextEntry();
		// iterates over entries in the zip file
		while (entry != null) {
			String filePath = destDirectory + File.separator + entry.getName();
			filePath = filePath.replace("\\", "/");
			filePath = filePath.replace(DexterUtil.PATH_SEPARATOR, "/");

			if (Strings.isNullOrEmpty(filePath)) {
				continue;
			}

			final int lastIndex = filePath.lastIndexOf("/");
			if (lastIndex > 0) {
				String fileDir = filePath.substring(0, lastIndex);
				if (!Strings.isNullOrEmpty(fileDir)) {
					File fileDirFile = new File(fileDir);
					if (fileDirFile.exists() == false) {
						logger.debug("creating folder " + fileDir);
						fileDirFile.mkdir();
					}
				}
			}

			if (entry.isDirectory()) {
				// if the entry is a directory, make the directory
				logger.debug("creating folder " + filePath + " ...");
				final File dir = new File(filePath);
				dir.mkdir();
			} else {
				// if the entry is a file, extracts it
				logger.debug("extracting " + filePath + " ...");
				extractFile(zipIn, filePath);
			}
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		zipIn.close();
	}

	/**
	 * Extracts a zip entry (file entry)
	 * 
	 * @param zipIn
	 * @param filePath
	 * @throws IOException
	 */
	private static void extractFile(final ZipInputStream zipIn, final String filePath){
		BufferedOutputStream bos = null;
        try {
	        bos = new BufferedOutputStream(new FileOutputStream(filePath));
	        byte[] bytesIn = new byte[BUFFER_SIZE];
	        int read = 0;
	        while ((read = zipIn.read(bytesIn)) != -1) {
	        	bos.write(bytesIn, 0, read);
	        }
	        
        } catch (FileNotFoundException e) {
        	logger.error(e.getMessage(), e);
        } catch (IOException e) {
        	logger.error(e.getMessage(), e);
        } finally {
        	if(bos != null){
        		try {
	                bos.close();
                } catch (IOException e) {
                }
        	}
        }
	}

	/**
	 * @param is you have to close InputStream by yourself
	 * @param targetDir 
	 * void
	 */
	public static void copyFileFromJar(final InputStream is, final String targetDir) {
		if (is == null) {
			throw new IllegalArgumentException();
		}

		try {
			final File file = new File(targetDir);
			FileUtils.copyInputStreamToFile(is, file);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static enum REG_TYPE {
		REG_SZ, REG_MULTI_SZ, REG_EXPAND_SZ, REG_DWORD, REG_QWORD, REG_BINARY, REG_NONE
	}

	public static void setRegistry(final String homeKey, final String key, final String value, final REG_TYPE type) {
		if (DexterUtil.getOS() != DexterUtil.OS.WINDOWS) {
			return;
		}

		final StringBuilder cmd = new StringBuilder();
		cmd.append("REG ADD ").append(homeKey).append(" /f /v ").append(key).append(" /t ").append(type.toString())
		        .append(" /d ").append(value);

		try {
			Runtime.getRuntime().exec(cmd.toString());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/*
	 * public static synchronized String getRegistryStringForHCU(String homeKey,
	 * String key){ return
	 * Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER, homeKey,
	 * key); }
	 * 
	 * public static synchronized void setRegistryStringForHCU(String homeKey,
	 * String key, String value){
	 * Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, homeKey,
	 * key, value); }
	 */

	/**
	 * 
	 * @param location
	 *            path in the registry
	 * @param key
	 *            registry key
	 * @return registry value or null if not found
	 */
	public static final String readRegistry(final String location, final String key) {
		try {
			final Process process = Runtime.getRuntime().exec("reg query " + '"' + location + "\" /v " + key);

			final StreamReader reader = new StreamReader(process.getInputStream());
			reader.start();
			process.waitFor();
			reader.join();

			// Parse out the value
			final String[] parsed = reader.getResult().split("\n");
			if (parsed.length > 3) {
				return parsed[2].substring(parsed[2].indexOf("REG_SZ") + 6).trim();
			}
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}

		return "";
	}

	static class StreamReader extends Thread {
		private InputStream is;
		private StringWriter sw = new StringWriter();

		public StreamReader(final InputStream is) {
			this.is = is;
		}

		public void run() {
			try {
				int c;
				while ((c = is.read()) != -1)
					sw.write(c);
			} catch (IOException e) {
			}
		}

		public String getResult() {
			return sw.toString();
		}
	}

	public static void setSystemEnv(final String key, final String value) {
		if (DexterUtil.getOS() != DexterUtil.OS.WINDOWS) {
			return;
		}

		try {
			Runtime.getRuntime().exec("setx " + key + " " + value);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static File createEmptyFileIfNotExist(final String filePath) {
		assert Strings.isNullOrEmpty(filePath) == false;

		final File file = new File(filePath);
		
		if (file.exists() == false) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw new DexterRuntimeException(e.getMessage() + " : " + filePath, e);
			}
		}
		
		return file;
	}

	/**
	 * @param dexterHome
	 * @throws IOException
	 */
	public static void createFolderWithParents(final String dir){
		try{
			if (new File(dir).exists() == false) {
				Files.createParentDirs(new File(dir));
				new File(dir).mkdir();
			}
		} catch(IOException e){
			throw new DexterRuntimeException(e.getMessage(), e);
		}
		
	}

	/**
	 * @param double1
	 * @return
	 */
	public static int toInt(final Double value) {
		return Integer.parseInt("" + ((long) Math.floor(value)));
	}

	/**
	 * @param createdTime
	 * @return
	 */
	public static String formatDate(final Date date) {
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		return sdf.format(date);
	}

	/**
	 * @param languageList
	 * @param commaStrings
	 */
	public static void addItemFromCommaStrings(final List<String> list, final String commaStrings) {
		for (final String str : commaStrings.split(",")) {
			list.add(str);
		}
	}
	
	public static void addItemFromCommaStrings(final Set<String> list, final String commaStrings) {
		for (final String str : commaStrings.split(",")) {
			list.add(str);
		}
	}

	/**
	 * @param text
	 * @return
	 */
	public static List<String> getSourceDirsFromProjectPath(final String baseDir) {
		final List<String> result = new ArrayList<String>();

		final File dir = new File(baseDir);
		if (dir.exists() == false) {
			return new ArrayList<String>(0);
		}

		addSourceDir(dir, result);

		return result;
	}

	/**
	 * @param dir
	 * @param result
	 */
	private static void addSourceDir(final File dir, final List<String> result) {
		final String dirName = dir.getName().toLowerCase();
		if ("src".equals(dirName) || "source".equals(dirName)) {
			result.add(dir.getPath());
			return;
		}

		for(final File sub : dir.listFiles()) {
			if (sub.isDirectory()) {
				addSourceDir(sub, result);
			}
		}
	}
	
	/**
	 * @param text
	 * @return
	 */
	public static List<String> getHeaderDirsFromProjectPath(final String baseDir) {
		final List<String> result = new ArrayList<String>();

		final File dir = new File(baseDir);
		if (dir.exists() == false) {
			return new ArrayList<String>(0);
		}

		addHeaderDir(dir, result);

		return result;
	}

	/** 
	 * @param dir
	 * @param result
	 */
	private static void addHeaderDir(final File dir, final List<String> result) {
		final String dirName = dir.getName().toLowerCase();
		if ("src".equals(dirName) || "source".equals(dirName) || "inc".equals(dirName) || "include".equals(dirName) || "i".equals(dirName)) {
			result.add(dir.getPath());
			return;
		}

		for (final File sub : dir.listFiles()) {
			if (sub.isDirectory()) {
				addHeaderDir(sub, result);
			}
		}
	}

	/**
	 * @param items
	 * @return
	 */
	public static String toPathsFromArray(final String[] items) {
		if (items == null) {
			return "";
		}

		final StringBuilder dirStr = new StringBuilder();
		for (final String dir : items) {
			dirStr.append(dir).append(";");
		}

		return dirStr.toString();
	}

	/**
	 * @param projectFullPath
	 * @return
	 */
	public static String refinePath(final String path) {
		// TODO: 올바른 코드인가? 
		if(Strings.isNullOrEmpty(path)){
			return "";
		}
		return path.replace("\\", "/").replace(DexterUtil.PATH_SEPARATOR, "/").replace("//", "/");
	}
	
	/**
	 * cf)
	 * Thread.dumpStack() : prints all stack traces
	 * Thread.getAllStackTraces() : get all stack traces for all live threads
	 * Thread.getStackTrace() : return an array of stack trace elements for this thread
	 * @param log
	 */
	public static void dumpAllStackTraces(final Logger log){
		StringBuilder msg = new StringBuilder();
		
		for(Map.Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()){
			msg.append(entry.getKey().getName()).append(":").append(DexterUtil.LINE_SEPARATOR);
			for(StackTraceElement element: entry.getValue()){
				msg.append("\t").append(element).append(DexterUtil.LINE_SEPARATOR);
			}
		}
		
		log.error(msg.toString());
	}
	
	public static void dumpAllStackTracesForCurrentThread(final Logger log){
		StringBuilder msg = new StringBuilder();
		
		msg.append(Thread.currentThread().getName()).append(":").append(DexterUtil.LINE_SEPARATOR);
		for(StackTraceElement element : Thread.currentThread().getStackTrace()){
			msg.append("\t").append(element).append(DexterUtil.LINE_SEPARATOR);
		}
		
		log.error(msg.toString());
	}

	public static boolean isAvailablePort(final String host, final int port) {
		if(port <= 1023 || port >= 65535){
			return false;
		}
		
		Socket socket = null;
		try{
			socket = new Socket(host, port);
			return false;
		} catch (IOException e){
			return true;
		} finally {
			if(socket != null){
				try {
					socket.close();
				} catch (IOException e) {
					// intentionally nothing
				}
			}
		}
	}

	public static File getFileFullPath(final String base, final String modulePath, final String fileName) {
		final String filePath = base + "/" + modulePath + "/" + fileName;
		return new File(filePath.replace("//", "/"));
    }

	public static String makePath(final String... names) {
		if(names == null){
			throw new DexterRuntimeException("cannot make a path because of null parameter");
		}
		
		StringBuilder path = new StringBuilder();
		for(int i=0; i<names.length; i++){
			if(Strings.isNullOrEmpty(names[i])){
				continue;
			}
			
			if(i == 0){
				path.append(names[i]);
			} else {
				path.append("/").append(names[i]);
			}
		}
		
	    return path.toString();
    }

	public static void handleClosingFileInputStream(final FileInputStream fis) {
		if(fis != null) {
			try {
				fis.close();
			} catch (IOException e) {
				// Intentionally empty
			}
		}
    }
	
	public static File toFile(final String fileFullPath) {
	    File excelFile = new File(fileFullPath);
		if(excelFile.exists() == false){
			throw new DexterRuntimeException("File is not exist : " + fileFullPath);
		}
		
	    return excelFile;
    }
	
	/**
	 * 'A' --> 0, 'B' --> 1, ...
	 * 'a' --> 0, 'b' --> 1, ...
	 */
	public static int alphabetToInt(final String string) {
		if(Strings.isNullOrEmpty(string) || "N/A".equals(string)){
			return -1;
		}
		
		char[] strs = string.toCharArray();
		int value = 0;
		
		for(int i=0; i<strs.length; i++){
			if('a' <= strs[i] && strs[i] <= 'z'){
				value += strs[i] - 'a';
			} else if('A' <= strs[i] && strs[i] <= 'Z'){
				value += strs[i] - 'A';
			} else {
				return -1;
			}
		}
		
		return value;
	}

	public static void closeInputStream(final InputStream input) {
		if(input == null) return;
		
		try {
	        input.close();
        } catch (IOException e) {
        	// intentionally empty
        }
    }

	public static void closeOutputStream(final OutputStream output) {
		if(output == null) return;
		
		try {
	        output.close();
        } catch (IOException e) {
        	// intentionally empty
        }
    }
	
	public static void unzipInClassPath(final InputStream sourceZipInputStream, final String targetTempZipFile, final String basePath){
		final File file = new File(targetTempZipFile);
		if(file.exists()) return;
		
		try {
			FileUtils.copyInputStreamToFile(sourceZipInputStream, file);
			DexterUtil.unzip(targetTempZipFile, basePath);
		} catch (IOException e) {
			throw new DexterRuntimeException(e.getMessage(), e);
		} catch (Exception e) {
			throw new DexterRuntimeException(e.getMessage(), e);
		} finally {
			DexterUtil.closeInputStream(sourceZipInputStream);
		}
	}

	public static void checkFolderExistence(final Map<String, Object> map, final String key) {
	    if(null == map.get(key)) return;
	    
	    final String dir = (String) map.get(key);
	    checkFolderExistence(dir);
    }

	public static void checkFoldersExistence(final Map<String, Object> map, final String key) {
		if(null == map.get(key)) return;

		Object dirObject = map.get(key);
		
		if(dirObject instanceof ArrayList){
			@SuppressWarnings("unchecked")
            final ArrayList<String> dirs = (ArrayList<String>) dirObject;
			
			for(String dir : dirs){
				checkFolderExistence(dir);
			}
		}
		
    }

	public static void checkFolderExistence(final String dir) {
		if(new File(dir).exists() == false){
	    	throw new DexterRuntimeException("Folder(Directory) is not exist : " + dir);
	    }
    }

	public static void checkStringField(String fieldName) {
	    if(Strings.isNullOrEmpty(fieldName)){
	    	throw new DexterRuntimeException("field is empty or null");
	    }
    }

	public static void checkNullField(EndOfAnalysisHandler field) {
	    if(field == null){
	    	throw new DexterRuntimeException("field is null");
	    }
    }

	public static void checkListFieldHasMoreThanOne(@SuppressWarnings("rawtypes") List listField) {
		if(listField == null){
	    	throw new DexterRuntimeException("list field is null");
	    }
		
		if(listField.size() == 0){
			throw new DexterRuntimeException("list field size is 0");
		}
    }

	public static int getIntFromMap(Map<String, Object> map, String key) {
		Object obj = map.get(key);
		
		if(obj instanceof Integer){
			return (int) obj;
		} else if(obj instanceof String){
			return Integer.parseInt((String) obj);
		} else {
			throw new DexterRuntimeException("unknown type to convert int");
		}
    }

	public static String addPaths(String... paths) {
		assert paths != null;
		
		StringBuilder pathStr = new StringBuilder();
		for(int i=0; i< paths.length; i++){
			String path = paths[i];
			
			if(i==0){
				pathStr.append(path);
			} else {
				pathStr.append("/").append(path);
			}
		}
		
		return DexterUtil.refinePath(pathStr.toString());
    }

	public static InputStream getResourceAsStreamInClassPath(@SuppressWarnings("rawtypes") Class clazz, 
			String filepath) {
		return clazz.getClassLoader().getResourceAsStream(filepath);
    }

	public static void createDirectoryIfNotExist(String directoryString) {
		final File directory = new File(directoryString);
		if (directory.exists() == false) {
			directory.mkdirs();
		}
	}

	public static void writeFileContents(final String contents, final File file) {
		try {
			Files.write(contents, file, Charsets.UTF_8);
		} catch (IOException e) {
			throw new DexterRuntimeException(e.getMessage() + " : " + file.getAbsolutePath(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getMapFromJsonString(StringBuilder contents) {
		assert contents != null && contents.length() > 0;
		
		Gson gson = new Gson();
		return gson.fromJson(contents.toString(), Map.class);
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getMapFromJsonString(String contents) {
		assert Strings.isNullOrEmpty(contents) == false;
		
		Gson gson = new Gson();
		return gson.fromJson(contents.toString(), Map.class);
	}

	@SuppressWarnings("unchecked")
	public static Object getObjectFromJsonString(String jsonString, Class clazz) {
		Gson gson = new Gson();
		return gson.fromJson(jsonString, clazz);
	}
	
	public static void deleteDirectory(File directory) throws IOException {
			if (!directory.exists()) {
				return;
			}
			cleanDirectory(directory);
			if (!directory.delete()) {
				String message = "Unable to delete directory " + directory + ".";
				throw new DexterRuntimeException(message);
			}
		}

	public static void cleanDirectory(File directory) throws IOException {
		if (!directory.exists()) {
			String message = directory + " does not exist";
			throw new DexterRuntimeException(message);
		}

		if (!directory.isDirectory()) {
			String message = directory + " is not a directory";
			throw new DexterRuntimeException(message);
		}

		File[] files = directory.listFiles();
		if (files == null) {  // null if security restricted
			throw new DexterRuntimeException("Failed to list contents of " + directory);
		}

		IOException exception = null;
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			try {
				forceDelete(file);
			} catch (IOException ioe) {
				exception = ioe;
			}
		}

		if (null != exception) {
			throw new DexterRuntimeException("Faile to remove directory");
		}
	}
	
	public static void forceDelete(File file) throws IOException {
		if (file.isDirectory()) {
			deleteDirectory(file);
		} else {
			if (!file.exists()) {
			throw new FileNotFoundException("File does not exist: " + file);
			}
			if (!file.delete()) {
				String message = "Unable to delete file: " + file;
				throw new DexterRuntimeException(message);
			}
		}
	}
	
}
