/**
 * Copyright (c) 2016 Samsung Electronics, Inc.,
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
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

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;

import com.samsung.sec.dexter.core.config.*;
import com.samsung.sec.dexter.core.config.DexterConfig.RunMode;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.executor.DexterAnalyzer;

public class PeerReviewMain {
	private final static ICLILog cliLog = new CLILog(System.out);
	private final static Logger log = Logger.getLogger(Main.class);
	private final IDexterCLIOption cliOption;
	private final IDexterConfigFile dexterConfigFile;
	private final PeerReviewConfigJob configJob;
	private final DexterConfig dexterConfig;

	public PeerReviewMain(DexterConfig dexterConfig, IDexterCLIOption cliOption, IDexterConfigFile dexterConfigFile, PeerReviewConfigJob configJob) {
		this.dexterConfig = dexterConfig;
		this.cliOption = cliOption;
		this.dexterConfigFile = dexterConfigFile;
		this.configJob = configJob;
	}

	public static void main(String[] args) {
		IDexterCLIOption cliOption = new DexterCLIOption(args);
		PeerReviewMain peerReviewMain;

		try {
			peerReviewMain = new PeerReviewMain(
					DexterConfig.getInstance(),
					cliOption,
					new DexterConfigFile(),
					new PeerReviewConfigJob(
							DexterConfig.getInstance(), 
							new PeerReviewController(
									new PeerReviewHomeMonitor(
											Executors.newFixedThreadPool(1), 
											FileSystems.getDefault().newWatchService(),
											new PeerReviewCLIAnalyzer(cliOption, cliLog,  DexterAnalyzer.getInstance()))), 
							Executors.newScheduledThreadPool(1)));
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new DexterRuntimeException("IOEception occurred on Init");
		}

		peerReviewMain.initDexterConfig();
		peerReviewMain.startConfigJob();
		

        

	}

	public void startConfigJob() {
		configJob.start();
	}

	public void initDexterConfig() {
		dexterConfigFile.loadFromFile(new File(cliOption.getConfigFilePath()));
		dexterConfig.setRunMode(RunMode.CLI);
		dexterConfig.setDexterHome(dexterConfigFile.getDexterHome());
		dexterConfig.createInitialFolderAndFiles();
		
	}

	public IDexterCLIOption getCliOption() {
		return cliOption;
	}

}
