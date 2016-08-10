/**
 * Copyright (c) 2014 Samsung Electronics, Inc.,
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.samsung.sec.dexter.core.config.EmptyDexterConfigFile;
import com.samsung.sec.dexter.core.config.IDexterConfigFile;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.EmptyDexterClient;
import com.samsung.sec.dexter.core.util.IDexterClient;

import org.junit.Test;
import org.mockito.Mockito;

public class MainTest {
    @Test
    public void testConstructor_empty_args() {
        String[] args = {};
        try {
            Main cliMain = new Main();
            new DexterCLIOption(args);
            assertNotNull(cliMain.getCLILog());
            fail();
        } catch (DexterRuntimeException e) {
            assertTrue(e.getMessage().startsWith("You missed option(s) :  -u -p"));
        }
    }

    @Test
    public void testCreateAccount_invalid_args_only_c_option() {
        String[] args = { "-c" };
        try {
            Main cliMain = new Main();
            new DexterCLIOption(args);
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
            Main cliMain = new Main();
            new DexterCLIOption(args);
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
            Main cliMain = new Main();
            new DexterCLIOption(args);
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

        Main cliMain = new Main();
        Main spyMain = spy(cliMain);
        IDexterCLIOption cliOption = new DexterCLIOption(args);
        final IDexterConfigFile configFile = new EmptyDexterConfigFile();
        IDexterClient client = new EmptyDexterClient();
        when(spyMain.createDexterClient(cliOption, configFile)).thenReturn(client);

        spyMain.createAccount(cliOption, configFile);

        verify(spyMain).createAccount(cliOption, configFile);
        verify(spyMain).createDexterClient(cliOption, configFile);
        verify(spyMain).createAccountHandler(client, cliOption);

        assertTrue(cliOption.getCommandMode() == IDexterCLIOption.CommandMode.CREATE_ACCOUNT);
        assertEquals(id, cliOption.getUserId());
        assertEquals(password, cliOption.getUserPassword());
        assertEquals(ip, cliOption.getServerHostIp());
        assertEquals(port, cliOption.getServerPort());
        assertNotNull(spyMain.createAccountHandler(client, cliOption));
        assertNotNull(spyMain.createAccountHandler(client, cliOption));
        assertTrue(spyMain.createAccountHandler(client, cliOption) instanceof AccountHandler);
    }

    @Test
    public void testRunAnalysis_should_throw_exception_when_no_dexter_cfg_file() {
        String id = "user";
        String password = "password";
        String ip = "100.100.100.100";
        int port = 1234;

        String[] args = { "-u", id, "-p", password, "-h", ip, "-o", "" + port };

        try {
            Main cliMain = new Main();
            Main spyMain = spy(cliMain);
            IDexterCLIOption cliOption = new DexterCLIOption(args);
            final IDexterConfigFile configFile = cliMain.createDexterConfigFile(cliOption);
            IDexterClient client = new EmptyDexterClient();
            when(spyMain.createDexterClient(cliOption, configFile)).thenReturn(client);

            spyMain.analyze(cliOption, configFile);
            fail();
        } catch (DexterRuntimeException e) {
            assertTrue(e.getMessage().startsWith("there is no file : ./dexter_cfg.json"));
        }
    }

    @Test
    public void testRunAnalysis_should_throw_exception_when_dexter_cfg_file_not_exist() {
        String id = "user";
        String password = "password";
        String ip = "100.100.100.100";
        int port = 1234;

        String[] args = { "-u", id, "-p", password, "-h", ip, "-o", "" + port, "-f", "./dexter_conf_java1.json" };

        try {
            Main cliMain = new Main();
            Main spyMain = spy(cliMain);
            IDexterCLIOption cliOption = new DexterCLIOption(args);
            final IDexterConfigFile configFile = cliMain.createDexterConfigFile(cliOption);
            IDexterClient client = new EmptyDexterClient();
            when(spyMain.createDexterClient(cliOption, configFile)).thenReturn(client);

            spyMain.analyze(cliOption, configFile);
            fail();
        } catch (DexterRuntimeException e) {
            assertTrue(e.getMessage().startsWith("there is no file : ./dexter_conf_java1.json"));
        }
    }

    @Test
    public void testRunAnalysis_should_throw_exception_when_project_full_path_not_exist() {
        String id = "user";
        String password = "password";
        String ip = "100.100.100.100";
        int port = 1234;

        String[] args = { "-u", id, "-p", password, "-h", ip, "-o", "" + port, "-f",
                "./src/test/dexter_conf_java_invalid.json" };

        try {
            Main cliMain = new Main();
            Main spyMain = spy(cliMain);
            IDexterCLIOption cliOption = new DexterCLIOption(args);
            final IDexterConfigFile configFile = cliMain.createDexterConfigFile(cliOption);
            IDexterClient client = Mockito.mock(IDexterClient.class);
            when(spyMain.createDexterClient(cliOption, configFile)).thenReturn(client);
            when(client.getServerHost()).thenReturn(ip);
            when(client.getServerPort()).thenReturn(port);

            spyMain.analyze(cliOption, configFile);
            fail();
        } catch (DexterRuntimeException e) {
            assertTrue(e.getMessage().startsWith("Folder(Directory) is not exist : ./src/test/myproject1"));
        }
    }

    @Test
    public void testRunAnalysis_should_throw_exception_when_file_type_is_not_supported_to_analyze() {
        String id = "user";
        String password = "password";
        String ip = "100.100.100.100";
        int port = 1234;

        String[] args = { "-u", id, "-p", password, "-h", ip, "-o", "" + port, "-f",
                "./src/test/dexter_conf_java_invalid2.json" };

        try {
            Main cliMain = new Main();
            Main spyMain = spy(cliMain);
            IDexterCLIOption cliOption = new DexterCLIOption(args);
            final IDexterConfigFile configFile = cliMain.createDexterConfigFile(cliOption);
            IDexterClient client = Mockito.mock(IDexterClient.class);
            when(spyMain.createDexterClient(cliOption, configFile)).thenReturn(client);
            when(client.getServerHost()).thenReturn(ip);
            when(client.getServerPort()).thenReturn(port);

            spyMain.analyze(cliOption, configFile);
            fail();
        } catch (DexterRuntimeException e) {
            assertTrue(e.getMessage().startsWith("not supported file : dexter_conf_java.json"));
        }
    }

    // TODO compleate more UTs
    //	@Test
    //	public void testRunAnalysis_should_throw_exception_when_no_existing_static_analsysis_plugins() {
    //		String id = "user";
    //		String password = "password";
    //		String ip = "100.100.100.100";
    //		int port = 1234;
    //		final String dexterHome = "./src/test/dexter-home";
    //
    //		String[] args = { "-u", id, "-p", password, "-h", ip, "-o", "" + port, "-f",
    //				"./src/test/dexter_conf_java.json" };
    //
    //		try {
    //			Main cliMain = new Main();
    //			Main spyMain = spy(cliMain);
    //			IDexterCLIOption cliOption = new DexterCLIOption(args);
    //			IDexterClient client = Mockito.mock(IDexterClient.class);
    //			when(spyMain.createDexterClient(cliOption)).thenReturn(client);
    //			when(client.getServerHost()).thenReturn(ip);
    //			when(client.getServerPort()).thenReturn(port);
    //
    //			spyMain.analyze(cliOption);
    //			fail();
    //
    //		} catch (DexterRuntimeException e) {
    //			e.printStackTrace();
    //			assertTrue(e.getMessage().startsWith("there is no existing plug-in(s)"));
    //		}
    //	}
    //
    //	@Test
    //	public void testRunAnalysis_should_throw_exception_when_no_existing_static_analsysis_plugins1() {
    //		String id = "user";
    //		String password = "password";
    //		String ip = "100.100.100.100";
    //		int port = 1234;
    //		final String dexterHome = "./src/test/dexter-home";
    //
    //		String[] args = { "-u", id, "-p", password, "-h", ip, "-o", "" + port, "-f",
    //				"./src/test/dexter_conf_java.json" };
    //
    //		try {
    //			Main cliMain = new Main();
    //			Main spyMain = spy(cliMain);
    //			IDexterCLIOption cliOption = new DexterCLIOption(args);
    //			IDexterClient client = Mockito.mock(IDexterClient.class);
    //			when(spyMain.createDexterClient(cliOption)).thenReturn(client);
    //			when(client.getServerHost()).thenReturn(ip);
    //			when(client.getServerPort()).thenReturn(port);
    //
    //			spyMain.analyze(cliOption);
    //			fail();
    //		} catch (DexterRuntimeException e) {
    //			e.printStackTrace();
    //			assertTrue(e.getMessage().startsWith("there is no existing plug-in(s)"));
    //		}
    //	}

}
