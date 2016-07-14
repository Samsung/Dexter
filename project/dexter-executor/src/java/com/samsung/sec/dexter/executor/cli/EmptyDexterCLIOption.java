package com.samsung.sec.dexter.executor.cli;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EmptyDexterCLIOption implements IDexterCLIOption {

	@Override
	public void createCliOptionFromArguments(String[] args) {
	}

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
		return new ArrayList(0);
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
	public String[] getEnabledCheckerCodes() {
		return new String[0];
	}

	@Override
	public String[] getEnabledCheckerLanguages() {
		return new String[0];
	}

	@Override
	public String[] getEnabledCheckerToolNames() {
		return new String[0];
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
	public boolean isAccountCreationMode() {
		return false;
	}

	@Override
	public int getServerPort() {
		return 0;
	}

	@Override
	public String getServerHostIp() {
		return "";
	}

}
