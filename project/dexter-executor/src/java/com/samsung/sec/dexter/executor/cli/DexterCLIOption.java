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

import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.samsung.sec.dexter.core.checker.ICheckerConfig;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.exception.InvalidArgumentRuntimeException;
import com.samsung.sec.dexter.core.plugin.IDexterPlugin;
import com.samsung.sec.dexter.core.plugin.PluginDescription;
import com.samsung.sec.dexter.core.util.DexterUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.Charsets;

public class DexterCLIOption implements IDexterCLIOption {	
	private final static ICLILog cliLog = new CLILog(System.out);
    // configuration
    private CommandMode commandMode = CommandMode.STATIC_ANALYSIS;
    private boolean isAsynchronous = false;
    private boolean isStandAlone = false;
    private boolean isJsonFile = false;
    private boolean isXmlFile = false;
    private boolean isXml2File = false;
    private boolean isSpecifiedCheckerEnabled = false;
    private boolean isTargetFilesOptionEnabled = false;
    private File jsonResultFile;
    private File xmlResultFile;
    private File xml2ResultFile;
    private List<EnabledChecker> enabledCheckerList = new ArrayList<EnabledChecker>();
    private String[] targetFiles = new String[0];
    private String configFilePath;
    private String userId = "";
    private String password = "";

    private String serverHost = "";
    private int serverPort = -1;
    
    private Options rawOptions = null;
    private final static String commandFormat = "peer-review [options] ...";

    public DexterCLIOption(String[] args, HelpFormatter helpFormatter) {
    	try {
    		createCliOptionFromArguments(args);
    	} catch (InvalidArgumentRuntimeException e) {
    		if (rawOptions != null) {
    			cliLog.info(e.getMessage() + "\n\n");
    			helpFormatter.printHelp(commandFormat, rawOptions);
    		}
    	}
	}

	@Override
    public void createCliOptionFromArguments(final String[] args) {
        final CommandLine commandLine = createCommandLine(args);
        setFieldsByCommandLine(commandLine);
    }

    @Override
    public String getConfigFilePath() {
        return this.configFilePath;
    }

    private CommandLine createCommandLine(final String[] args) {
    	rawOptions = createCliOptions(args);

        try {
            return new PosixParser().parse(rawOptions, args);
        } catch (final ParseException e) {
            throw new DexterRuntimeException(e.getMessage(), e);
        }
    }

    protected Options createCliOptions(final String[] args) {
        final Options options = new Options();

        options.addOption("a", false,
                "Asynchronous Analysis. It is faster than synchronous. No Result Log and File(Check on Dexter WEB). e.g. -a");
        options.addOption("c", false,
                "Create an account. Use this option with -u your_id -p your_password. e.g. -c -u myid -p mypwd");
        options.addOption("e", true,
                "Enable only specified checker(s), checkercode1;checkercode2:language:toolname;... e.g. -e nullpointer;initializerlist:CPP:cppcheck");
        options.addOption("f", true,
                "Analysis Configuration File. e.g. -f C:/dexter/" + DexterConfig.DEXTER_CFG_FILENAME);
        options.addOption("h", true, "Dexter Server IP address. e.g. 123.123.123.123");
        options.addOption("j", false, "Create Json result file - dexter-result.json. e.g. -j");
        options.addOption("n", true,
                "File name for result file without an extension(.xml) - myreport.xml e.g. -n myreport");
        options.addOption("o", true, "Dexter Server Port address. e.g. -p 4982");
        options.addOption("p", true, "User Password. e.g. -p password");
        options.addOption("r", false, "Reset password. User this option with -u and -p, e.g. -r -u my_id -p my_password");
        options.addOption("s", false,
                "Standalone. Run Dexter Analysis without DexterServer. you don't need LOG in(id & password) e.g. -s");
        options.addOption("t", true,
                "Target source code file names and paths. e.g. -t C:/myproject/src/main.cpp;C:/myproject/src/util.cpp");
        options.addOption("u", true, "User ID. e.g. -u id");
        options.addOption("x", false, "Create XML result file - dexter-result.xml. e.g. -x");
        options.addOption("X", false,
                "Create XML result file with timestamp - dexter-result_yyyyMMddhh:mm:ss.xml. e.g. -X");

        return options;
    }

    private void setFieldsByCommandLine(final CommandLine cmd) {
        checkValidationOfOptionCombination(cmd);

        setAsynchronous(cmd.hasOption("a"));
        setStandAlone(cmd.hasOption("s"));

        if (cmd.hasOption("e")) {
            setSpecifiedCheckerEnabled(true);
            setEnabledCheckers(cmd.getOptionValue("e").split(";"));
        }

        if (cmd.hasOption("t")) {
            setTargetFilesOptionEnabled(true);
            setTargetFiles(cmd.getOptionValue("t").split(";"));
        }

        String filename = "." + DexterUtil.FILE_SEPARATOR + "dexter-result";
        if (cmd.hasOption("n")) {
            filename = cmd.getOptionValue("n");
            if (Strings.isNullOrEmpty(filename) == false && filename.contains(DexterUtil.FILE_SEPARATOR) == false) {
                filename = "." + DexterUtil.FILE_SEPARATOR + filename;
            }
        }

        if (cmd.hasOption("j")) {
            this.isJsonFile = true;
            createJsonResultFile(filename);
        }

        if (cmd.hasOption("x")) {
            this.isXmlFile = true;
            createXmlResultFile(filename);
        }

        if (cmd.hasOption("X")) {
            this.isXml2File = true;
            createXml2ResultFile(filename);
        }

        if (cmd.hasOption("c")) {
            this.commandMode = CommandMode.CREATE_ACCOUNT;
            setHostAndPort(cmd.getOptionValue("h"), cmd.getOptionValue("o"));
        } else if (cmd.hasOption("r")) {
            this.commandMode = CommandMode.RESET_PASSWORD;
            setHostAndPort(cmd.getOptionValue("h"), cmd.getOptionValue("o"));
        } else {
            if (cmd.hasOption("f")) {
                setConfigFilePath(cmd.getOptionValue("f"));
            } else {
                setConfigFilePath("./" + DexterConfig.DEXTER_CFG_FILENAME);
            }

            DexterUtil.throwExceptionWhenFileNotExist(getConfigFilePath());
        }

        if (isStandAlone == false)
            setUserAndPassword(cmd.getOptionValue("u"), cmd.getOptionValue("p"));
    }

    private void checkValidationOfOptionCombination(final CommandLine cmd) {
        CommandLineAssert.assertExclusiveOptions(cmd, 'a', 'n');
        CommandLineAssert.assertExclusiveOptions(cmd, 'a', 'j');
        CommandLineAssert.assertExclusiveOptions(cmd, 'a', 'x');
        CommandLineAssert.assertExclusiveOptions(cmd, 'a', 'X');
        CommandLineAssert.assertExclusiveOptions(cmd, 's', 'u');
        CommandLineAssert.assertExclusiveOptions(cmd, 's', 'p');

        CommandLineAssert.assertMissingMandatoryOptions(cmd, 'c', 'h', 'o', 'u', 'p');

        CommandLineAssert.assertExclusiveMissingMandatoryOptions(cmd, 's', 'u', 'p');
    }

    private void setConfigFilePath(final String configFilePath) {
        if (Strings.isNullOrEmpty(configFilePath))
            throw new DexterRuntimeException("Invalid CommandLine Option for filePath(null or empty)");

        this.configFilePath = configFilePath;
    }

    private void setAsynchronous(final boolean value) {
        this.isAsynchronous = value;
    }

    private void setStandAlone(final boolean value) {
        this.isStandAlone = value;
    }

    private void setEnabledCheckers(final String[] values) {
        for (int i = 0; i < values.length; i++) {
            String[] checkerUnits = values[i].split(":");
            enabledCheckerList.add(new EnabledChecker(getStringFromStringArray(checkerUnits, 2),
                    getStringFromStringArray(checkerUnits, 1), getStringFromStringArray(checkerUnits, 0)));
        }
    }

    private String getStringFromStringArray(final String[] strings, final int index) {
        if (strings.length >= (index + 1) && Strings.isNullOrEmpty(strings[index]) == false)
            return strings[index];
        else
            return "";
    }

    private void setTargetFiles(final String[] values) {
        targetFiles = values;
    }

    private void createJsonResultFile(final String resultFileName) {
        try {
            this.jsonResultFile = new File(resultFileName + ".json");
            Files.write("", jsonResultFile, Charsets.UTF_8);
        } catch (IOException e) {
            throw new DexterRuntimeException(e.getMessage(), e);
        }
    }

    private void createXmlResultFile(String resultFileName) {
        try {
            this.xmlResultFile = new File(resultFileName + ".xml");
            Files.write("", xmlResultFile, Charsets.UTF_8);
        } catch (IOException e) {
            throw new DexterRuntimeException(e.getMessage(), e);
        }
    }

    private void createXml2ResultFile(String resultFileName) {
        try {
            this.xml2ResultFile = new File(resultFileName + "_" + DexterUtil.currentDateTime() + ".xml");
            Files.write("", xml2ResultFile, Charsets.UTF_8);
        } catch (IOException e) {
            throw new DexterRuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public File getJsonResultFile() {
        return this.jsonResultFile;
    }

    @Override
    public File getXmlResultFile() {
        return this.xmlResultFile;
    }

    @Override
    public File getXml2ResultFile() {
        return this.xml2ResultFile;
    }

    private void setUserAndPassword(final String userId, final String password) {
        if (Strings.isNullOrEmpty(userId)) {
            throwUserIdOrPasswordException("ID");
        } else if (Strings.isNullOrEmpty(password)) {
            throwUserIdOrPasswordException(password);
        }

        this.userId = userId;
        this.password = password;
    }

    private void throwUserIdOrPasswordException(String invalidItem) {
        StringBuilder errMsg = new StringBuilder(1024);
        errMsg.append("Your ").append(invalidItem).append(" is not valid").append(System.lineSeparator());
        errMsg.append("use -u and -p options:").append(System.lineSeparator());
        errMsg.append("eg) dexter id password").append(System.lineSeparator());
        errMsg.append("eg) java -jar dexter-executor_#.#.#.jar -u id -p password").append(System.lineSeparator());
        errMsg.append("If you want create an account. use -c -u your_id -p your_password")
                .append(System.lineSeparator());
        errMsg.append("If you want reset your password. use -r -u your_id").append(System.lineSeparator());
        errMsg.trimToSize();

        throw new DexterRuntimeException(errMsg.toString());
    }

    private void setHostAndPort(final String host, final String port) {
        if (Strings.isNullOrEmpty(host)) {
            throw new DexterRuntimeException(
                    "You have to use both -h dexter_server_host_ip and -o dexter_server_port_number options");
        }

        if (Strings.isNullOrEmpty(port) && port.matches("[0-9]{2,}") == false) {
            throw new DexterRuntimeException(
                    "You have to use both -h dexter_server_host_ip and -o dexter_server_port_number options");
        }

        this.serverHost = host;
        this.serverPort = Integer.parseInt(port);
    }

    public void setSpecifiedCheckerEnabled(boolean value) {
        this.isSpecifiedCheckerEnabled = value;
    }

    public void setTargetFilesOptionEnabled(boolean value) {
        this.isTargetFilesOptionEnabled = value;
    }

    @Override
    public boolean isStandAloneMode() {
        return isStandAlone;
    }

    @Override
    public boolean isSpecifiedCheckerEnabledMode() {
        return isSpecifiedCheckerEnabled;
    }

    @Override
    public List<String> getTargetFileFullPathList() {
        if (isTargetFilesOptionEnabled == false)
            return new ArrayList<String>(0);

        return Arrays.asList(targetFiles);
    }

    @Override
    public boolean isTargetFilesOptionEnabled() {
        return this.isTargetFilesOptionEnabled;
    }

    @Override
    public CommandMode getCommandMode() {
        return this.commandMode;
    }

    @Override
    public String getUserId() {
        return this.userId;
    }

    @Override
    public String getUserPassword() {
        return this.password;
    }

    @Override
    public String getServerHostIp() {
        return this.serverHost;
    }

    @Override
    public int getServerPort() {
        return this.serverPort;
    }

    @Override
    public boolean isAsynchronousMode() {
        return this.isAsynchronous;
    }

    @Override
    public boolean isJsonFile() {
        return isJsonFile;
    }

    @Override
    public boolean isXmlFile() {
        return isXmlFile;
    }

    @Override
    public boolean isXml2File() {
        return isXml2File;
    }

    @Override
    public void checkCheckerEnablenessByCliOption(final IDexterPlugin plugin) {
        ICheckerConfig checkerConfig = plugin.getCheckerConfig();
        checkerConfig.disableAllCheckers();
        PluginDescription description = plugin.getDexterPluginDescription();

        for (EnabledChecker enabledChecker : enabledCheckerList) {
            if (Strings.isNullOrEmpty(enabledChecker.getToolName()) == false &&
                    enabledChecker.getToolName().equals(description.getPluginName()) == false)
                continue;

            if (Strings.isNullOrEmpty(enabledChecker.getLanguage()) == false &&
                    enabledChecker.getLanguage().equals(description.getLanguage().name()) == false)
                continue;

            final String checkerCode = enabledChecker.getCode().intern();
            if (checkerConfig.hasChecker(checkerCode))
                checkerConfig.setCheckerActive(checkerCode, true);
        }
    }

    @Override
    public void setDexterServerIP(String dexterServerIp) {
        this.serverHost = dexterServerIp;
    }

    @Override
    public void setDexterServerPort(int dexterServerPort) {
        this.serverPort = dexterServerPort;
    }

}
