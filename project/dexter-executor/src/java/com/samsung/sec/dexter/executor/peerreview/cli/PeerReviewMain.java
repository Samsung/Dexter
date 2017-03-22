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
package com.samsung.sec.dexter.executor.peerreview.cli;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import org.apache.commons.cli.HelpFormatter;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.samsung.sec.dexter.core.analyzer.AnalysisEntityFactory;
import com.samsung.sec.dexter.core.config.*;
import com.samsung.sec.dexter.core.config.DexterConfig.RunMode;
import com.samsung.sec.dexter.core.exception.InvalidArgumentRuntimeException;
import com.samsung.sec.dexter.core.plugin.IDexterPluginInitializer;
import com.samsung.sec.dexter.core.plugin.IDexterPluginManager;
import com.samsung.sec.dexter.core.util.EmptyDexterClient;
import com.samsung.sec.dexter.core.util.FileUtil;
import com.samsung.sec.dexter.core.util.IDexterClient;
import com.samsung.sec.dexter.core.util.PeerReviewHomeUtil;
import com.samsung.sec.dexter.executor.CLIPluginInitializer;
import com.samsung.sec.dexter.executor.DexterAnalyzer;
import com.samsung.sec.dexter.executor.cli.CLIDexterPluginManager;
import com.samsung.sec.dexter.executor.cli.CLILog;
import com.samsung.sec.dexter.executor.cli.DexterCLIOption;
import com.samsung.sec.dexter.executor.cli.ICLILog;
import com.samsung.sec.dexter.executor.cli.IDexterCLIOption;
import com.samsung.sec.dexter.executor.peerreview.PeerReviewConfigJob;
import com.samsung.sec.dexter.executor.peerreview.PeerReviewController;
import com.samsung.sec.dexter.executor.peerreview.PeerReviewHomeMonitor;

public class PeerReviewMain {
	private final static ICLILog cliLog = new CLILog(System.out);
	private final static Logger log = Logger.getLogger(PeerReviewMain.class);
	private final IDexterCLIOption cliOption;
	private final IDexterConfigFile dexterConfigFile;
	private final PeerReviewConfigJob configJob;
	private final DexterConfig dexterConfig;
	private final IDexterPluginManager pluginManager;

	public PeerReviewMain(DexterConfig dexterConfig, IDexterCLIOption cliOption, IDexterConfigFile dexterConfigFile, 
			PeerReviewConfigJob configJob, IDexterPluginManager pluginManager) {
		this.dexterConfig = dexterConfig;
		this.cliOption = cliOption;
		this.dexterConfigFile = dexterConfigFile;
		this.configJob = configJob;
		this.pluginManager = pluginManager;
	}

	public static void main(String[] args) {
		try {
			IDexterCLIOption cliOption = new DexterCLIOption(args, new HelpFormatter());
			IDexterPluginManager pluginManager = loadDexterPlugins(new EmptyDexterClient(), cliOption);
			PeerReviewMain peerReviewMain;
			
			peerReviewMain = new PeerReviewMain(
					DexterConfig.getInstance(),
					cliOption,
					new PeerReviewConfigFile(new FileUtil()),
					new PeerReviewConfigJob(
							DexterConfig.getInstance(), 
							createPeerReviewController(cliOption, pluginManager),
							Executors.newScheduledThreadPool(1)),
					pluginManager);
			
			peerReviewMain.initDexterConfig();
			peerReviewMain.startConfigJob();
			
		} catch (IOException | InterruptedException | ExecutionException e) {
			log.error(e.getMessage(), e);
			System.exit(-1);
		} catch (InvalidArgumentRuntimeException e) {
			// print usage message
			System.exit(-1);
		}
	}

	private static PeerReviewController createPeerReviewController(IDexterCLIOption cliOption, IDexterPluginManager pluginManager) throws IOException {
		return new PeerReviewController(
				new PeerReviewHomeMonitor(
						Executors.newFixedThreadPool(1), 
						FileSystems.getDefault().newWatchService(),
						new PeerReviewCLIAnalyzer(cliOption, 
								cliLog,  
								DexterAnalyzer.getInstance(), 
								pluginManager,
								new AnalysisEntityFactory())),
				new PeerReviewHomeUtil(new Gson()));
		
		
	}
	
	private static IDexterPluginManager loadDexterPlugins(final IDexterClient client, final IDexterCLIOption cliOption) {
        IDexterPluginInitializer initializer = new CLIPluginInitializer(cliLog);
        IDexterPluginManager pluginManager = new CLIDexterPluginManager(initializer, client, cliLog, cliOption);

        return pluginManager;
    }
	
	public void initDexterPlungins() {
		pluginManager.initDexterPlugins();
	}

	public void startConfigJob() throws InterruptedException, ExecutionException {
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
