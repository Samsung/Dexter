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

import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.DexterConfigFile;
import com.samsung.sec.dexter.core.config.IDexterConfigFile;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.DexterClient;
import com.samsung.sec.dexter.core.util.EmptyDexterClient;
import com.samsung.sec.dexter.core.util.IDexterClient;

import org.junit.Test;
import org.mockito.Mockito;

public class MainTest {
    @Test
    public void test_constructor_empty_args() {
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
    public void test_createAccount_invalid_args_only_c_option() {
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
    public void test_createAccount_invalid_args_without_host_and_port() {
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
    public void test_createAccount_invalid_args_without_port() {
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
    public void test_createAccount_valid_args() {
        String id = "user";
        String password = "password";
        String ip = "100.100.100.100";
        int port = 1234;

        String[] args = { "-c", "-u", id, "-p", password, "-h", ip, "-o", "" + port };

        Main cliMain = new Main();
        Main spyMain = spy(cliMain);
        IDexterCLIOption cliOption = new DexterCLIOption(args);
        IDexterClient client = new EmptyDexterClient();
        when(spyMain.createDexterClient(cliOption)).thenReturn(client);

        spyMain.createAccount(cliOption);

        verify(spyMain).createAccount(cliOption);
        verify(spyMain).createDexterClient(cliOption);
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
    public void test_createAccountHandler_should_return_empty_object_when_standalone_mode() {
        String[] args = { "-s", "-f", "./src/test/dexter_conf_java.json" };

        Main cliMain = new Main();
        IDexterCLIOption cliOption = new DexterCLIOption(args);
        IDexterClient client = new EmptyDexterClient();

        IAccountHandler accountHandler = cliMain.createAccountHandler(client, cliOption);

        assertNotNull(accountHandler);
        assertTrue(accountHandler instanceof EmptyAccountHandler);
    }

    @Test
    public void test_createAccountHandler_should_return_valid_object_when_not_standalone_mode() {
        String id = "user";
        String password = "password";
        String[] args = { "-u", id, "-p", password, "-f", "./src/test/dexter_conf_java.json" };

        Main cliMain = new Main();
        IDexterCLIOption cliOption = new DexterCLIOption(args);
        IDexterClient client = new EmptyDexterClient();

        IAccountHandler accountHandler = cliMain.createAccountHandler(client, cliOption);

        assertNotNull(accountHandler);
        assertTrue(accountHandler instanceof AccountHandler);
    }

    @Test
    public void test_createDexterClient_should_return_empty_object_when_standalone_mode() {
        String id = "user";
        String password = "password";
        String[] args = { "-u", id, "-p", password, "-f", "./src/test/dexter_conf_java.json" };

        Main cliMain = new Main();
        IDexterCLIOption cliOption = new DexterCLIOption(args);
        DexterConfig.getInstance().addSupprotingFileExtensions(new String[] { "java" });
        final IDexterConfigFile configFile = cliMain.createDexterConfigFile(cliOption);

        assertNotNull(configFile);
        assertTrue(configFile instanceof DexterConfigFile);

        assertEquals("./src/test/dexter-home", configFile.getDexterHome());
        assertEquals("123.123.123.123", configFile.getDexterServerIp());
        assertEquals(4982, configFile.getDexterServerPort());
        assertEquals("ProjectName", configFile.getProjectName());
        assertEquals("./src/test/myproject/", configFile.getProjectFullPath());
        assertTrue(configFile.getSourceDirList().size() == 1);
        assertEquals("./src/test", configFile.getSourceDirList().get(0));
        assertTrue(configFile.getHeaderDirList().size() == 0);
        assertEquals("UTF-8", configFile.getSourceEncoding());
        assertTrue(configFile.getLibDirList().size() == 1);
        assertEquals("./lib", configFile.getLibDirList().get(0));
        assertEquals("./bin", configFile.getBinDir());
        assertEquals("FILE", configFile.getType().toString());
        assertEquals("com.samsung.sec.dexter.executor.cli", configFile.getModulePath());
        assertTrue(configFile.getFileNameList().size() == 1);
        assertEquals("MainTest.java", configFile.getFileNameList().get(0));
        assertEquals("MainTest.java", configFile.getFirstFileName());
    }

    @Test
    public void test_createDexterClient_should_return_valid_object_when_not_standalone_mode() {
        String id = "user";
        String password = "password";
        String[] args = { "-u", id, "-p", password, "-f", "./src/test/dexter_conf_java.json" };

        Main cliMain = new Main();
        IDexterCLIOption cliOption = new DexterCLIOption(args);
        DexterConfig.getInstance().addSupprotingFileExtensions(new String[] { "java" });

        IDexterClient client = cliMain.createDexterClient(cliOption);

        assertNotNull(client);
        assertTrue(client instanceof DexterClient);
    }

    @Test
    public void test_createDexterConfigFile_should_return_valid_object() {
        String[] args = { "-s", "-f", "./src/test/dexter_conf_java.json" };

        Main cliMain = new Main();
        IDexterCLIOption cliOption = new DexterCLIOption(args);
        DexterConfig.getInstance().addSupprotingFileExtensions(new String[] { "java" });

        IDexterClient client = cliMain.createDexterClient(cliOption);

        assertNotNull(client);
        assertTrue(client instanceof EmptyDexterClient);
    }

    @Test
    public void test_createAccountHandler_should_throw_exception_when_standalone_mode_and_u_option() {
        String id = "user";
        String password = "password";
        String ip = "100.100.100.100";
        int port = 1234;

        String[] args = { "-u", id, "-p", password, "-h", ip, "-o", "" + port, "-s" };

        try {
            Main cliMain = new Main();
            IDexterCLIOption cliOption = new DexterCLIOption(args);
            IDexterClient client = new EmptyDexterClient();
            cliMain.createAccountHandler(client, cliOption);
            fail();
        } catch (DexterRuntimeException e) {
            assertTrue(e.getMessage().startsWith("you cannot use option '-s' and with '-u'"));
        }

    }

    @Test
    public void test_runAnalysis_should_throw_exception_when_no_dexter_cfg_file() {
        String id = "user";
        String password = "password";
        String ip = "100.100.100.100";
        int port = 1234;

        String[] args = { "-u", id, "-p", password, "-h", ip, "-o", "" + port };

        try {
            Main spyMain = spy(Main.class);

            IDexterCLIOption cliOption = new DexterCLIOption(args);
            final IDexterConfigFile configFile = spyMain.createDexterConfigFile(cliOption);
            IDexterClient client = new EmptyDexterClient();
            when(spyMain.createDexterClient(cliOption)).thenReturn(client);

            spyMain.analyze(cliOption, configFile, client);
            fail();
        } catch (DexterRuntimeException e) {
            assertTrue(e.getMessage().startsWith("there is no file : ./dexter_cfg.json"));
        }
    }

    @Test
    public void test_runAnalysis_should_throw_exception_when_dexter_cfg_file_not_exist() {
        String id = "user";
        String password = "password";
        String ip = "100.100.100.100";
        int port = 1234;

        String[] args = { "-u", id, "-p", password, "-h", ip, "-o", "" + port, "-f", "./dexter_conf_java1.json" };

        try {
            Main spyMain = spy(Main.class);

            IDexterCLIOption cliOption = new DexterCLIOption(args);
            final IDexterConfigFile configFile = spyMain.createDexterConfigFile(cliOption);
            IDexterClient client = new EmptyDexterClient();
            when(spyMain.createDexterClient(cliOption)).thenReturn(client);

            spyMain.analyze(cliOption, configFile, client);
            fail();
        } catch (DexterRuntimeException e) {
            assertTrue(e.getMessage().startsWith("there is no file : ./dexter_conf_java1.json"));
        }
    }

    @Test
    public void test_runAnalysis_should_throw_exception_when_project_full_path_not_exist() {
        String id = "user";
        String password = "password";
        String ip = "100.100.100.100";
        int port = 1234;

        String[] args = { "-u", id, "-p", password, "-h", ip, "-o", "" + port, "-f",
                "./src/test/dexter_conf_java_invalid.json" };

        try {
            Main spyMain = spy(Main.class);

            IDexterCLIOption cliOption = new DexterCLIOption(args);
            final IDexterConfigFile configFile = spyMain.createDexterConfigFile(cliOption);
            IDexterClient client = Mockito.mock(IDexterClient.class);
            when(spyMain.createDexterClient(cliOption)).thenReturn(client);
            when(client.getServerHost()).thenReturn(ip);
            when(client.getServerPort()).thenReturn(port);

            spyMain.analyze(cliOption, configFile, client);
            fail();
        } catch (DexterRuntimeException e) {
            assertTrue(e.getMessage().startsWith("Folder(Directory) is not exist : ./src/test/myproject1"));
        }
    }

    // TODO in working
    /*
     * @Test
     * public void testRunAnalysis_run_successfully() {
     * String id = "user";
     * String password = "password";
     * String ip = "100.100.100.100";
     * int port = 1234;
     * 
     * String[] args = { "-u", id, "-p", password, "-h", ip, "-o", "" + port, "-f",
     * "./src/test/dexter_conf_java.json" };
     * 
     * try {
     * Main spyMain = spy(Main.class);
     * 
     * DexterConfig.getInstance().addSupprotingFileExtensions(new String[] { "java" });
     * IDexterCLIOption cliOption = new DexterCLIOption(args);
     * 
     * final IDexterConfigFile configFile = spyMain.createDexterConfigFile(cliOption);
     * DexterConfig.getInstance().setDexterHome(configFile.getDexterHome());
     * 
     * IDexterClient client = Mockito.mock(IDexterClient.class);
     * when(client.getServerHost()).thenReturn(ip);
     * when(client.getServerPort()).thenReturn(port);
     * when(client.isServerAlive()).thenReturn(false);
     * 
     * when(spyMain.createDexterClient(cliOption, new EmptyDexterConfigFile())).thenReturn(client);
     * 
     * IDexterPluginManager pluginManager = Mockito.mock(IDexterPluginManager.class);
     * when(spyMain.loadDexterPlugins(client, cliOption)).thenReturn(pluginManager);
     * 
     * spyMain.analyze(cliOption, configFile, client);
     * } catch (DexterRuntimeException e) {
     * e.printStackTrace();
     * }
     * }
     */
}
