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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.IDexterConfigFile;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.plugin.IDexterPlugin;
import com.samsung.sec.dexter.core.plugin.IDexterPluginManager;
import com.samsung.sec.dexter.core.util.IDexterClient;

public class MainTest {
	/*
	 * @Test public void
	 * createResultChangeHandler_should_print_result_of_static_analysis_on_cli()
	 * { String[] args = {}; Main cliMain = new Main(args);
	 * 
	 * //AnalysisResult result =
	 * AnalysisEntityTestUtil.createSampleAnalysisResult();
	 * 
	 * ByteArrayOutputStream out = new ByteArrayOutputStream();
	 * cliMain.setCLILog(new CLILog(new PrintStream(out)));
	 * 
	 * String expectStr = " ● C:/test-project/src/test-module/test-filename\r\n"
	 * + "    ▶ Total Defects: 1\r\n" + "    ▶ test-checker-code / CRI / 1\r\n"
	 * + "       └ 10 test-occurence-message\r\n"; assertEquals(expectStr,
	 * out.toString()); }
	 */
	@Test
	public void testConstructor_empty_args() {
		String[] args = {};
		try {
			Main cliMain = new Main(args);
			assertNotNull(cliMain.getCLILog());
			fail();
		} catch (DexterRuntimeException e) {
			assertTrue(e.getMessage().startsWith("Your ID is not valid"));
		}
	}

	@Test
	public void testCreateAccount_invalid_args_only_c_option() {
		String[] args = { "-c" };
		try {
			Main cliMain = new Main(args);
			assertNotNull(cliMain.getCLILog());
			fail();
		} catch (DexterRuntimeException e) {
			assertTrue(e.getMessage().startsWith("You missed option(s) :  -h -o -u -p"));
		}
	}

	@Test
	public void testCreateAccount_invalid_args_without_host_and_port() {
		String[] args = { "-c", "-u", "user", "-p", "password" };
		try {
			Main cliMain = new Main(args);
			assertNotNull(cliMain.getCLILog());
			fail();
		} catch (DexterRuntimeException e) {
			assertTrue(e.getMessage().startsWith("You missed option(s) :  -h -o"));
		}
	}

	@Test
	public void testCreateAccount_invalid_args_without_port() {
		String[] args = { "-c", "-u", "user", "-p", "password", "-h", "100.100.100.100" };
		try {
			Main cliMain = new Main(args);
			assertNotNull(cliMain.getCLILog());
			fail();
		} catch (DexterRuntimeException e) {
			assertTrue(e.getMessage().startsWith("You missed option(s) :  -o"));
		}
	}

	@Test
	public void testCreateAccount_valid_args() {
		String id = "user";
		String password = "password";
		String ip = "100.100.100.100";
		int port = 1234;

		String[] args = { "-c", "-u", id, "-p", password, "-h", ip, "-o", "" + port };
		
		Main cliMain = new Main(args);
		IDexterConfigFile configFile = cliMain.init();
		
		IAccount account = mock(IAccount.class);
		cliMain.setAccount(account);

		Main spyMain = spy(cliMain);
//		spyMain.setAccount(account);

		spyMain.run(configFile);


		verify(spyMain).createAccount();
		//verify(account, atLeast(1)).createAccount(ip, port, id, password);
		verify(account, atLeast(1)).createAccount(id, password);
	}
	
	@Test
	public void testRunAnalysis_should_throw_exception_when_no_dexter_cfg_file() {
		String id = "user";
		String password = "password";
		String ip = "100.100.100.100";
		int port = 1234;

		String[] args = { "-u", id, "-p", password, "-h", ip, "-o", "" + port };

		try{
			Main cliMain = new Main(args);
			IDexterConfigFile configFile = cliMain.init();
			cliMain.run(configFile);
			fail();
		} catch (DexterRuntimeException e){
			assertTrue(e.getMessage().startsWith("There is no dexter_cfg.json file : dexter_cfg.json"));
		}
	}
	
	@Test
	public void testRunAnalysis_should_throw_exception_when_dexter_cfg_file_dose_not_exist() {
		String id = "user";
		String password = "password";
		String ip = "100.100.100.100";
		int port = 1234;

		String[] args = { "-u", id, "-p", password, "-h", ip, "-o", "" + port, "-f", "./dexter_conf_java1.json" };

		try{
			Main cliMain = new Main(args);
			IDexterConfigFile configFile = cliMain.init();
			cliMain.run(configFile);
			fail();
		} catch (DexterRuntimeException e){
			assertTrue(e.getMessage().startsWith("There is no dexter_cfg.json file : dexter_conf_java1.json"));
		}
	}
	
	@Test
	public void testRunAnalysis_should_throw_exception_when_project_full_path_dose_not_exist() {
		String id = "user";
		String password = "password";
		String ip = "100.100.100.100";
		int port = 1234;

		String[] args = { "-u", id, "-p", password, "-h", ip, "-o", "" + port, "-f", "./src/test/dexter_conf_java_invalid.json" };

		try{
			Main cliMain = new Main(args);
			IDexterConfigFile configFile = cliMain.init();
			cliMain.run(configFile);
			fail();
		} catch (DexterRuntimeException e){
			assertTrue(e.getMessage().startsWith("Folder(Directory) is not exist : ./src/test/myproject1"));
		}
	}
	
	@Test
	public void testRunAnalysis_should_throw_exception_when_file_type_is_not_supported_to_analyze() {
		String id = "user";
		String password = "password";
		String ip = "100.100.100.100";
		int port = 1234;

		String[] args = { "-u", id, "-p", password, "-h", ip, "-o", "" + port, "-f", "./src/test/dexter_conf_java_invalid2.json" };

		try{
			Main cliMain = new Main(args);
			IDexterConfigFile configFile = cliMain.init();
			cliMain.run(configFile);
			fail();
		} catch (DexterRuntimeException e){
			assertTrue(e.getMessage().startsWith("not supported file : dexter_conf_java.json"));
		}
	}
	
	@Test
	public void testRunAnalysis_should_throw_exception_when_no_existing_static_analsysis_plugins() {
		String id = "user";
		String password = "password";
		String ip = "100.100.100.100";
		int port = 1234;
		final String dexterHome = "./src/test/dexter-home";

		String[] args = { "-u", id, "-p", password, "-h", ip, "-o", "" + port, "-f", "./src/test/dexter_conf_java.json" };

		try{
			DexterConfig.getInstance().addSupprotingFileExtensions(new String[]{"java","c","cpp"});
			Main cliMain = new Main(args);
			
			IDexterClient client = mock(IDexterClient.class);
			cliMain.setDexterClient(client);
			
			IDexterPluginManager pluginManager = mock(IDexterPluginManager.class);
			cliMain.setDexterPluginManager(pluginManager);
			
			IDexterPlugin plugin = mock(IDexterPlugin.class);
			
			Main spyMain = spy(cliMain);
			
			IDexterConfigFile configFile = cliMain.init();
			
			IAccount account = mock(IAccount.class);
			cliMain.setAccount(account);
			
			spyMain.run(configFile);
			
			IDexterCLIOption cliOption = cliMain.getCLIOption();
			
			assertEquals(dexterHome, configFile.getDexterHome());
			assertEquals(dexterHome, DexterConfig.getInstance().getDexterHome());

			verify(spyMain).runAnalysis(configFile);
		} catch (DexterRuntimeException e){
			assertTrue(e.getMessage().startsWith("there is no existing plug-in(s)"));
		}
	}

	@Test
	public void should_create_account_when_using_c_option() {
		IDexterClient client = mock(IDexterClient.class);

		when(client.hasAccount(anyString())).thenReturn(false);
		// when(client.login()).thenThrow(new DexterRuntimeException(""));
		// doThrow(new DexterRuntimeException("")).when(client).login();
		/*
		 * IDexterClient client = mock(IDexterClient.class);
		 * when(client.hasAccount(anyString())).thenReturn(false);
		 */

		// account.when(account.createAccount(ip, port, id,
		// password)).doNothing();

		// System.out.println(client.hasAccount("abc"));

		// verify(client).hasAccount("abc");
	}
}
