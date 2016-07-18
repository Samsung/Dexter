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
package com.samsung.sec.dexter.executor.cli;

import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.plugin.PluginDescription;

import java.io.File;
import java.io.PrintStream;

import org.apache.log4j.Logger;

public class CLILog implements ICLILog {
	private PrintStream out;
	private final static Logger log = Logger.getLogger(CLILog.class);
	private final static String errorPrefix = "★ ERROR : ";
	private final static String moreInfoDesc = " (see ../log/dexter-executor.log file for more information)";
	private final static String warnPrefix = "  [WARN] ";

	public CLILog(PrintStream out) {
		this.out = out;
	}

	@Override
	public void printStartingAnalysisMessage() {
		infoln("");
		infoln("===== Starting Dexter Analysis =====");
	}

	@Override
	public void printMessagePreSyncAnalysis() {
		infoln("");
		infoln("== Defects Report ==");
		infoln("analyze synchronously...");
	}

	@Override
	public void printMessagePreAsyncAnalysis(final String dexterWebUrl) {
		infoln("");
		infoln("== Defects Report ==");

		infoln("analyze asynchronously...");
		infoln("* Check Dexter web site in detail, after executing : " + dexterWebUrl);
	}

	@Override
	public void printResultFileLocation(final File file) {
		infoln("※ Refer to " + file.getAbsolutePath());
	}

	@Override
	public void printElapsedTime(final long elapsedSeconds) {
		infoln("===== End of Dexter Analysis (" + elapsedSeconds + " seconds) =====");
		infoln("");
	}

	@Override
	public void printErrorMessageWhenNoPlugins() {
		errorln("There is no static analysis plug-ins to use.");
		errorln("There are more than one plug-in(*.jar)s : ");
		errorln(DexterConfig.getInstance().getDexterPluginFolderPath());
	}

	@Override
	public void printMessageWhenPluginLoaded(PluginDescription desc) {
		infoln(" - Loaded " + desc.getPluginName() + " " + desc.getVersion());
	}

	@Override
	public void setPrintStream(PrintStream out) {
		this.out = out;
	}

	@Override
	public void info(String message) {
		out.print(message);
		log.info(message);
	}

	@Override
	public void warn(String message) {
		out.print(message);
		log.warn(message);
	}

	@Override
	public void error(String message) {
		out.print(errorPrefix + message);
		log.error(message);
	}

	@Override
	public void infoln(String message) {
		out.println(message);
		log.info(message);
	}

	@Override
	public void warnln(String message) {
		out.println(warnPrefix + message);
		log.warn(message);
	}

	@Override
	public void errorln(String message) {
		out.println(errorPrefix + message + moreInfoDesc);
		log.error(message);
	}

	@Override
	public void errorln(String message, Throwable t) {
		out.println(errorPrefix + message + moreInfoDesc);
		log.error(message);
		log.error(t.getMessage(), t);
	}
}
