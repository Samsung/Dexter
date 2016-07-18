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
package com.samsung.sec.dexter.executor;

import com.google.common.base.Stopwatch;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisEntityFactory;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.analyzer.EndOfAnalysisHandler;
import com.samsung.sec.dexter.core.analyzer.IAnalysisEntityFactory;
import com.samsung.sec.dexter.core.checker.CheckerConfig;
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.exception.DexterException;
import com.samsung.sec.dexter.core.plugin.IDexterPluginManager;
import com.samsung.sec.dexter.core.util.EmptyDexterClient;
import com.samsung.sec.dexter.core.util.IDexterClient;
import com.samsung.sec.dexter.executor.cli.CLIDexterPluginManager;
import com.samsung.sec.dexter.executor.cli.CLILog;
import com.samsung.sec.dexter.executor.cli.EmptyDexterCLIOption;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DexterAnalyzerTest {
	static private IDexterPluginManager pluginManager = new CLIDexterPluginManager(new DexterPluginInitializerMock(),
			new EmptyDexterClient(), new CLILog(System.out), new EmptyDexterCLIOption());

	private DexterAnalyzer analyzer;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		pluginManager.initDexterPlugins();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		this.analyzer = DexterAnalyzer.getInstance();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void analysisOneFile() {
		class TempHandler implements EndOfAnalysisHandler {
			private IDexterClient client;

			@Override
			public void handleAnalysisResult(List<AnalysisResult> resultList, final IDexterClient client) {
				System.out.println("AnalysisResult changed =====================================");

				this.client = client;

				List<Defect> allDefectList = DexterAnalyzer.getAllDefectList(resultList);

				for (Defect d : allDefectList) {
					System.out.println(">>> " + d);
				}
			}

			public void execute() {
				// 1. set static analysis objects : source file, class file
				// path, classpath
				IAnalysisEntityFactory analysisFactory = new AnalysisEntityFactory();
				AnalysisConfig ac = analysisFactory.createAnalysisConfig();
				ac.setProjectName("DefectTest");
				ac.setProjectFullPath("C:\\DEV\\workspace\\dexter\\DefectTest");
				ac.addSourceBaseDirList("C:\\DEV\\workspace\\dexter\\DefectTest\\src");
				ac.addLibDirList("C:\\DEV\\workspace\\dexter\\DefectTest\\lib");
				// ac.addLibFile("");
				// ac.addTargetSourceList("C:\\DEV\\workspace\\dexter_20140407\\DefectTest\\src\\defect\\example\\ConstructorCallsOverridableMethod.java");
				// ac.addTargetCompiledDirList("C:/DEV/workspace/dexter/DefectTest/bin/defect/example/");
				// ac.addTargetCompiledDirList("C:/DEV/workspace/dexter/DefectTest/bin/");
				ac.addLibFile("C:/DEV/workspace/dexter/DefectTest/lib/guava-16.0.1.jar");

				// It dosen't work
				// ac.addLibDirList("C:/DEV/workspace/dexter/DefectTest/lib");

				// 2. execution
				Stopwatch s = Stopwatch.createStarted();
				ac.setResultHandler(this);
				// AnalysisResultManager.getInstance().addListener("defect.example.ConstructorCallsOverridableMethod.java",
				// this);
				analyzer.runSync(ac, pluginManager, client);
				System.out.println("first executed : " + s.elapsed(TimeUnit.MILLISECONDS));

				// s = Stopwatch.createStarted();
				// analysisResult =
				// AnalysisResultManager.getInstance().createSaResult();
				// AnalysisResultManager.getInstance().addListener(analysisResult.getId(),
				// this);
				// analyzer.analysis(ac, analysisResult);
				// System.out.println("second executed : " +
				// s.elapsed(TimeUnit.MILLISECONDS));

				// assertNotNull(this.executor.getResult(exeId));
			}
		}

		TempHandler handler = new TempHandler();
		handler.execute();

		// AnalysisResultManager.getInstance().removeListener(handler);
	}

	@Test
	public void customizeChecker() throws DexterException {
		class TempHandler implements EndOfAnalysisHandler {
			private IDexterClient client;

			@Override
			public void handleAnalysisResult(List<AnalysisResult> resultList, final IDexterClient client) {
				System.out.println("AnalysisResult changed =====================================");

				this.client = client;
				List<Defect> allDefectList = DexterAnalyzer.getAllDefectList(resultList);
				for (Defect d : allDefectList) {
					System.out.println(">>> " + d);
				}
			}

			public void execute() throws DexterException {
				String pluginName = "dexter-findbugs";

				// 1. customizing rules, then...
				CheckerConfig cc = pluginManager.getCheckerConfig(pluginName);

				if (cc == null) {
					return;
				}

				pluginManager.setCheckerConfig(pluginName, cc);

				// 2. set static analysis objects : source file, class file
				// path, classpath
				IAnalysisEntityFactory analysisFactory = new AnalysisEntityFactory();
				AnalysisConfig ac = analysisFactory.createAnalysisConfig();
				ac.setProjectName("DefectTest");
				ac.setProjectFullPath("C:\\DEV\\workspace\\dexter\\DefectTest");
				ac.addSourceBaseDirList("C:\\DEV\\workspace\\dexter\\DefectTest\\src");
				ac.addLibDirList("C:\\DEV\\workspace\\dexter\\DefectTest\\lib");
				ac.addLibFile("C:/DEV/workspace/dexter/DefectTest/lib/guava-16.0.1.jar");

				// 3. execution
				Stopwatch s = Stopwatch.createStarted();
				ac.setResultHandler(this);

				// AnalysisResultManager.getInstance().addListener("defect.example.ConstructorCallsOverridableMethod.java",
				// this);
				analyzer.runSync(ac, pluginManager, client);
				System.out.println("first executed : " + s.elapsed(TimeUnit.MILLISECONDS));

				// assertNotNull(this.executor.getResult(exeId));
			}
		}

		TempHandler handler = new TempHandler();
		handler.execute();

		// AnalysisResultManager.getInstance().removeListener(handler);
	}
}
