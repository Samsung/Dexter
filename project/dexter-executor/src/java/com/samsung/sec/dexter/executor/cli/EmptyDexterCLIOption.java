package com.samsung.sec.dexter.executor.cli;

import com.samsung.sec.dexter.core.plugin.IDexterPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EmptyDexterCLIOption implements IDexterCLIOption {

    @Override
    public void createCliOptionFromArguments(String[] args) {}

    @Override
    public String getConfigFilePath() {
        return "";
    }

    @Override
    public boolean isStandAloneMode() {
        return false;
    }

    @Override
    public boolean isAsynchronousMode() {
        return false;
    }

    @Override
    public boolean isSpecifiedCheckerEnabledMode() {
        return false;
    }

    @Override
    public boolean isTargetFilesOptionEnabled() {
        return false;
    }

    @Override
    public List<String> getTargetFileFullPathList() {
        return new ArrayList<String>(0);
    }

    @Override
    public String getUserId() {
        return "";
    }

    @Override
    public String getUserPassword() {
        return "";
    }

    @Override
    public boolean isXml2File() {
        return false;
    }

    @Override
    public boolean isXmlFile() {
        return false;
    }

    @Override
    public boolean isJsonFile() {
        return false;
    }

    @Override
    public File getXml2ResultFile() {
        return null;
    }

    @Override
    public File getXmlResultFile() {
        return null;
    }

    @Override
    public File getJsonResultFile() {
        return null;
    }

    @Override
    public CommandMode getCommandMode() {
        return CommandMode.NONE;
    }

    @Override
    public int getServerPort() {
        return 0;
    }

    @Override
    public String getServerHostIp() {
        return "";
    }

    @Override
    public void setDexterServerIP(String dexterServerIp) {}

    @Override
    public void setDexterServerPort(int dexterServerPort) {}

    @Override
    public void checkCheckerEnablenessByCliOption(IDexterPlugin plguin) {}

	@Override
	public void printHelp(String message) {}

}
