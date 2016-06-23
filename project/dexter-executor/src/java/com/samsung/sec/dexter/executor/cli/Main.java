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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.Charsets;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisEntityFactory;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.analyzer.EndOfAnalysisHandler;
import com.samsung.sec.dexter.core.analyzer.IAnalysisEntityFactory;
import com.samsung.sec.dexter.core.checker.Checker;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.DexterConfig.RunMode;
import com.samsung.sec.dexter.core.config.DexterConfigFile;
import com.samsung.sec.dexter.core.config.DexterConfigFile.Type;
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.defect.Occurence;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.job.DeleteResultLogJob;
import com.samsung.sec.dexter.core.job.SendResultJob;
import com.samsung.sec.dexter.core.plugin.DexterPluginManager;
import com.samsung.sec.dexter.core.plugin.IDexterPlugin;
import com.samsung.sec.dexter.core.plugin.PluginDescription;
import com.samsung.sec.dexter.core.util.DexterClient;
import com.samsung.sec.dexter.core.util.DexterUtil;
import com.samsung.sec.dexter.core.util.IDexterClient;
import com.samsung.sec.dexter.executor.CliPluginInitializer;
import com.samsung.sec.dexter.executor.DexterAnalyzer;

public class Main {
	private enum ExitCode {
		SUCCESS,
		BY_USER, 
		ERROR_INVALID_PROGRAM_PARAMETER,
		ERROR_NO_DEXTER_CONF_FILE,
		ERROR_FAIL_TO_CREATE_ACCOUNT,
		ERROR_NO_LOGIN,
		ERROR_NOT_ADMIN,
		ERROR_INVALID_ANALYSIS_TYPE,
		ERROR_NO_PLUGINS, ERROR_WRONG_OPTION_COMBINATION;
	}
	
	private static ICliLog LOG = new CliLogger(System.out);
	
	private IAnalysisEntityFactory analysisEntityFactory = new AnalysisEntityFactory();

	private String userId;
	private String password;
	private List<String> sourceFileFullPathList;

	private int totalCnt = 0;
	private int totalOccurenceCnt = 0;
	private int criticalCnt = 0;
	private int majorCnt = 0;
	private int minorCnt = 0;
	private int etcCnt = 0;
	private int crcCnt = 0;

	//private AnalysisConfig analysisConfig;
	
	// configuration
	private boolean isAsynchronous = false;
	private boolean isStandAlone	= false;
	private boolean isJsonFile = false;
	private boolean isXmlFile = false;
	private boolean isXml2File = false;
	private File jsonResultFile;
	private File xmlResultFile;
	private File xml2ResultFile;
	private String[] enabledCheckers = new String[0];
	
	final DexterConfig config = DexterConfig.getInstance();
	final DexterAnalyzer analyzer = DexterAnalyzer.getInstance();
	private IDexterClient client = DexterClient.getInstance();
	
	private IAccount account = new Account();
	
	public static void main(String[] args) {
		final Main cliMain = new Main();
		
		final DexterConfigFile configFile = cliMain.initialize(args);
		cliMain.run(configFile);
	}
	
	public Main(){
		LOG.startMessage();
		
		DexterConfig.getInstance().setRunMode(RunMode.CLI);
	}
	
	public DexterConfigFile initialize(final String[] args){
		final CommandLine commandLine = handleProgramArguments(args);
		final String configFilePath = getConfigFilePath(commandLine);
		DexterConfigFile configFile = createDexterConfigFile(configFilePath);
		DexterPluginManager.getInstance().setDexterPluginInitializer(new CliPluginInitializer());
		
		initDexterClient(configFile.getDexterServerIp(), configFile.getDexterServerPort(), configFile.getDexterHome());
		initDexterPlugins();
		initSourceFileFullPathList(configFile);
		initFolderAndFiles();
		
		return configFile;
	}
	
	public void setDexterClient(final IDexterClient client, final String ip, final int port, 
			final String dexterHome){
		this.client = client;
		initDexterClient(ip, port, dexterHome);
	}
	
	protected CommandLine handleProgramArguments(final String[] args){
		final CommandLine commandLine = createCommandLine(args);
		setFieldsByCommandLine(commandLine);
		handleAccount(commandLine);
		return commandLine;
	}
	
	protected CommandLine createCommandLine(final String[] args){
		final Options options = createCliOptions(args);

		try {
			return new PosixParser().parse(options, args);
		} catch (final ParseException e) {
			LOG.errorln(e.getMessage(), e);
			exit(ExitCode.ERROR_INVALID_PROGRAM_PARAMETER);
		}
		
		return null;
	}
	
	protected Options createCliOptions(final String[] args) {
		final Options options = new Options();
		
		options.addOption("a", false, "Asynchronous Analysis. It is faster than synchronous. No Result Log and File(Check on Dexter WEB). eg) -a");
		options.addOption("c", false, "Create an account. use this option with -u your_id -p your_password. eg) -c -u myid -p mypwd");
		options.addOption("e", true, "Enable only specified checker(s), checkercode1;checkercode2:language:toolname;... eg) -e nullpointer;initializerlist:CPP:cppcheck");
		options.addOption("f", true, "Analysis Configuration File. eg) -f C:/dexter/" + DexterConfig.DEXTER_CFG_FILENAME);
		options.addOption("h", true, "Dexter Server IP address. eg) 123.123.123.123");
		options.addOption("j", false, "Create Json result file - dexter-result.json. eg) -j");
		options.addOption("n", true, "File name for result file without an extension(.xml) - myreport.xml eg) -n myreport");
		options.addOption("o", true, "Dexter Server Port address. eg) -p 4982");
		options.addOption("p", true, "User Password. eg) -p password");
		options.addOption("s", false, "Standalone. Run Dexter Analysis without DexterServer. you don't need LOG in(id & password) eg) -s");
		options.addOption("t", true, "Target source code file names and paths. eg) -t C:/myproject/src/main.cpp;C:/myproject/src/util.cpp");
		options.addOption("u", true, "User ID. eg) -u id");
		options.addOption("x", false, "Create XML result file - dexter-result.xml. eg) -x");
		options.addOption("X", false, "Create XML result file with timestamp - dexter-result_yyyyMMddhh:mm:ss.xml. eg) -X");
		
		return options;
	}
	
	protected void setFieldsByCommandLine(final CommandLine cmd){
		checkAndExitIfWrongOptionCombination(cmd);
		
		setAsynchronous(cmd);
		setStandAlone(cmd); 
		setEnabledCheckers(cmd);
		setTargeFiles(cmd);
		setResultFileName(cmd);
		setUserAndPassword(cmd);
	}
	
	protected void handleAccount(CommandLine cmd) {
		if (cmd.hasOption("c")){
			if(cmd.hasOption("h") == false || cmd.hasOption("o") == false){
				LOG.errorln("absense/invalid -h for host or -o for port options for connecting Dexter Server");
				exit(ExitCode.ERROR_FAIL_TO_CREATE_ACCOUNT);
			}
			
			try {
				final String serverHost = cmd.getOptionValue("h").toString();
				final int serverPort = Integer.parseInt(cmd.getOptionValue("o").toString());
				
				client.setDexterServer(serverHost, serverPort);
				account.createAccount(userId, password);
				exitSuccess();
			} catch(DexterRuntimeException e){
				LOG.errorln(e.getMessage(), e);
				exit(ExitCode.ERROR_FAIL_TO_CREATE_ACCOUNT);
			}
		}
    }

	private void setTargeFiles(final CommandLine cmd) {
	    if (cmd.hasOption("t")){
	    	final String[] targetFiles = cmd.getOptionValue("t").split(";");
	    	this.sourceFileFullPathList = new ArrayList<String>(targetFiles.length);
		    
		    for(String fileName : targetFiles){
		    	this.sourceFileFullPathList.add(fileName);
		    }
		}
    }
	
	private void setEnabledCheckers(final CommandLine cmd) {
	    if (cmd.hasOption("e")){	    	
			this.enabledCheckers = cmd.getOptionValue("e").split(";");
			DexterConfig.getInstance().setCheckerEnableOptionForCLI(true);
		}else{
			DexterConfig.getInstance().setCheckerEnableOptionForCLI(false);
		}
    }

	private void setStandAlone(final CommandLine cmd) {
	    if (cmd.hasOption("s")){
			this.isStandAlone = true;
			config.setStandalone(true);
		}
    }

	private void setAsynchronous(final CommandLine cmd) {
	    if (cmd.hasOption("a")){
			this.isAsynchronous = true;
		}
    }

	private void setUserAndPassword(final CommandLine cmd) {
		if(isStandAlone) return; 
	    if(cmd.hasOption("u") && cmd.hasOption("p")){
			userId = cmd.getOptionValue("u");
			password = cmd.getOptionValue("p");
		} else {
			LOG.errorln("use -u and -p options:");
			LOG.errorln("eg) dexter id password");
			LOG.errorln("eg) java -jar dexter-executor_#.#.#.jar -u id -p password");
			LOG.infoln("If you want create an account. use -c -u your_id -p your_password");
			LOG.infoln("If you want reset your password. use -r -u your_id");
			exit(ExitCode.ERROR_INVALID_PROGRAM_PARAMETER);
		}
    }

	private void checkAndExitIfWrongOptionCombination(final CommandLine cmd) {
	    exitForWrongOptionCombination(cmd, 'a', 'n');
		exitForWrongOptionCombination(cmd, 'a', 'j');
		exitForWrongOptionCombination(cmd, 'a', 'x');
		exitForWrongOptionCombination(cmd, 'a', 'X');
		exitForWrongOptionCombination(cmd, 's', 'u');
		exitForWrongOptionCombination(cmd, 's', 'p');
    }

	private void setResultFileName(final CommandLine cmd) {
	    final String resultFileName = getResultFileName(cmd);
	    setJsonResultFile(cmd, resultFileName);
	    setXmlResultFile(cmd, resultFileName);
	    setXml2ResultFile(cmd, resultFileName);
    }

	private String getResultFileName(final CommandLine cmd) {
		String resultFileName = "dexter-result";
		
	    if(cmd.hasOption("n")){
	    	resultFileName = cmd.getOptionValue("n");
	    }
	    return resultFileName;
    }

	private void setXml2ResultFile(final CommandLine cmd, String resultFileName) {
	    if (cmd.hasOption("X")){
	    	this.isXml2File = true;
	    	this.xml2ResultFile = new File(resultFileName + "_" + DexterUtil.currentDateTime() + ".xml");
	    	try {
	            Files.write("", xml2ResultFile, Charsets.UTF_8);
	        } catch (IOException e) {
	        	LOG.errorln(e.getMessage(), e);
	        }
	    }
    }

	private void setXmlResultFile(final CommandLine cmd, String resultFileName) {
	    if (cmd.hasOption("x")){
	    	this.isXmlFile = true;
	    	this.xmlResultFile = new File(resultFileName + ".xml");
	    	try {
	            Files.write("", xmlResultFile, Charsets.UTF_8);
	        } catch (IOException e) {
	        	LOG.errorln(e.getMessage(), e);
	        }
	    }
    }

	private void setJsonResultFile(final CommandLine cmd, String resultFileName) {
	    if (cmd.hasOption("j")){
	    	this.isJsonFile = true;
	    	this.jsonResultFile = new File(resultFileName + ".json");
	    	try {
	            Files.write("", jsonResultFile, Charsets.UTF_8);
	        } catch (IOException e) {
	        	LOG.errorln(e.getMessage(), e);
	        }
	    }
    }
	
	private DexterConfigFile createDexterConfigFile(final String configFilePath) {
		final File confFile =  new File(configFilePath);
		final DexterConfigFile configFile = new DexterConfigFile();
		configFile.loadFromFile(confFile);
		
		return configFile;
	}

	public String getConfigFilePath(final CommandLine cmd) {
	    String filePath = "./" + DexterConfig.DEXTER_CFG_FILENAME;
		
		if (cmd.hasOption("f")) {
			filePath = cmd.getOptionValue("f");
			LOG.infoln("Reading dexter configuration file : " + filePath);
			
			if(Strings.isNullOrEmpty(filePath)){
				LOG.errorln("Invalid CommandLine Option for filePath(null or empty)");
				exit(ExitCode.ERROR_NO_DEXTER_CONF_FILE);
			}
		}
		
	    return filePath;
    }
	
	private void initDexterClient(final String ip, final int port, final String dexterHome) {
		client.setDexterServer(ip, port);
		config.setDexterHome(dexterHome);
		loginOrCreateAccount();
	}
	
	private void loginOrCreateAccount() {
		if(isStandAlone) return;

		try{
			client.login(userId, password);
		} catch (DexterRuntimeException e){
			LOG.errorln("Invalid userId ID(" + userId + ") or password(" + password + ")");
			LOG.infoln("If you want create an account. use -c -u your_id -p your_password");
			LOG.infoln("If you want reset your password. use -r -u your_id");
			exit(ExitCode.BY_USER);
		}
	}
	
	private void initSourceFileFullPathList(DexterConfigFile configFile){
		if(this.sourceFileFullPathList == null){
			this.sourceFileFullPathList = configFile.generateSourceFileFullPathList();
		}
	}

	private void initFolderAndFiles(){
		DexterConfig.getInstance().createInitialFolderAndFiles();
	}
	
	private void initDexterPlugins(){
		final DexterPluginManager pluginManager = DexterPluginManager.getInstance();
		pluginManager.setDexterPluginInitializer(new CliPluginInitializer());
		pluginManager.initDexterPlugins();

		if(pluginManager.getPluginList().size() == 0){
			LOG.errorln("There is no static analysis plug-ins to use");
			exit(ExitCode.ERROR_NO_PLUGINS);
		}
		
		for(IDexterPlugin handler : pluginManager.getPluginList()){
			PluginDescription desc = handler.getDexterPluginDescription();
			LOG.infoln(" - Loaded " + desc.getPluginName()	+ " " + desc.getVersion());
			
			setCheckerEnableness(desc.getPluginName(), desc.getLanguage().toString(), 
					handler.getCheckerConfig().getCheckerList());
		}
	}
	
	private void setCheckerEnableness(final String toolName, final String language, final List<Checker> checkers) {
		if(this.enabledCheckers.length == 0){
			return;
		}
		
		for(Checker checker : checkers){
			boolean isEnable = checkCheckerEnablenessByCliOption(toolName, language, checker);
			checker.setActive(isEnable);
		}
	}

	private boolean checkCheckerEnablenessByCliOption(final String toolName, final String language, Checker checker) {
		final int CODE = 0;
		final int LANGUAGE = 1;
		final int TOOLNAME = 2;
		
	    for(String enChecker : this.enabledCheckers){
	    	if(enChecker.indexOf(checker.getCode()) != -1){
	    		String[] values = enChecker.split(":");
	    		
	    		if(values.length == 1){
	    			return true;
	    		} else if(values.length == 3 && values[CODE].equals(checker.getCode())
	    	    		&& values[LANGUAGE].equals(language) && values[TOOLNAME].equals(toolName)){
	    			return true;
	    		}
	    	}
	    }
	    
	    return false;
    }

	private void run(DexterConfigFile configFile) {
		logBeforeAnalyze();
		handleBeginnigOfResultFile();
		
		final Stopwatch timer = Stopwatch.createStarted();
		
		final AnalysisConfig analysisConfig = configFile.toAnalysisConfig();
		setGroupAndSnapshotId(configFile, analysisConfig);
		analysis(analysisConfig);
		
		logAfterAnalyze(timer);
		handleEndOfResultFile();
		
		if(isStandAlone == false){
			try {
				SendResultJob.send();
	            DeleteResultLogJob.deleteOldLog();
            } catch (DexterRuntimeException e) {
            	LOG.errorln(e.getMessage(), e);
            }
		} else {
			LOG.infoln("※ See Dexter WEB site for more information : " + DexterClient.getInstance().getDexterWebUrl());
		}
	}

	private void logAfterAnalyze(final Stopwatch s) {
	    if(isAsynchronous) return;
	    
		LOG.infoln("");
		LOG.infoln("====================================================");
		LOG.infoln("- Total Defects: " + totalCnt);
		LOG.infoln("- Critical Defects: " + criticalCnt);
		LOG.infoln("- Major Defects: " + majorCnt);
		LOG.infoln("- Minor Defects: " + minorCnt);
		LOG.infoln("- CRC Defects: " + crcCnt);
		LOG.infoln("- Etc. Defects: " + etcCnt);
		LOG.infoln("- Total Occurences: " + totalOccurenceCnt);
		LOG.infoln("====================================================");
		LOG.infoln("");
		
		LOG.infoln("===== End of Dexter Analysis (" + s.elapsed(TimeUnit.SECONDS) + " seconds) =====");
		LOG.infoln("");
    }
	
	private void handleEndOfResultFile(){
		if(isAsynchronous) return;
		
		try {
	        writeResultFilePostfix();
        } catch (IOException e) {
	        LOG.errorln(e.getMessage(), e);
        }
	}

	private void setGroupAndSnapshotId(DexterConfigFile configFile, final AnalysisConfig analysisConfig) {
	    Type type = configFile.getType();
		if ("PROJECT".equals(type)) {
			analysisConfig.setNoDefectGroupAndSnapshotId();
		} else if ("SNAPSHOT".equals(type)) {
			final long defectGroupId = analyzer.getDefectGroupByCreating(analysisConfig.getProjectName());
			analysisConfig.setDefectGroupId(defectGroupId);
			final long snapshotId = System.currentTimeMillis();
			analysisConfig.setSnapshotId(snapshotId);
		}
    }

	private void handleBeginnigOfResultFile() {
		if(isAsynchronous) {
			return;
		}
		
		try {
			writeResultFilePrefix();
		} catch (IOException e) {
			LOG.errorln(e.getMessage(), e);
		}
    }

	private void logBeforeAnalyze() {
		LOG.infoln("");
		LOG.infoln("== Defects Report ==");
		
	    if(isAsynchronous) {
			LOG.infoln("analyze asynchronously...");
			LOG.infoln("* Check Dexter web site in detail, after executing : " + DexterClient.getInstance().getDexterWebUrl());
		} else {
			LOG.infoln("analyze synchronously...");
		}
    }

	private void writeResultFilePrefix() throws IOException {
		if(this.isJsonFile){
			Files.append("[\n", jsonResultFile, Charsets.UTF_8);
		}
		
		if(this.isXmlFile){
			Files.append("<dexter-result created=\"" + DexterUtil.currentDateTime() + "\">\n", xmlResultFile, Charsets.UTF_8);
		}
		
		if(this.isXml2File){
			Files.append("<dexter-result created=\"" + DexterUtil.currentDateTime() + "\">\n", xml2ResultFile, Charsets.UTF_8);
		}
	}
	
	private void writeResultFilePostfix() throws IOException {
		if(this.isJsonFile){
			Files.append("]", jsonResultFile, Charsets.UTF_8);
			LOG.infoln("※ Refer to " + jsonResultFile.getAbsolutePath());
		}
		
		if(this.isXmlFile){
			Files.append("</dexter-result>", xmlResultFile, Charsets.UTF_8);
			LOG.infoln("※ Refer to " + xmlResultFile.getAbsolutePath());
		}
		
		if(this.isXml2File){
			Files.append("</dexter-result>", xml2ResultFile, Charsets.UTF_8);
			LOG.infoln("※ Refer to " + xml2ResultFile.getAbsolutePath());
		}
    }

	protected void analysis(AnalysisConfig baseConfig) {
		for (final String fileFullPath : sourceFileFullPathList) {
			if (DexterConfig.getInstance().isAnalysisAllowedFile(fileFullPath) == false) {
				LOG.warnln("\tnot support : " + fileFullPath);
				continue;
			}
			
			final AnalysisConfig config = analysisEntityFactory.copyAnalysisConfigWithoutSourcecode(baseConfig);

			config.setResultHandler(createResultChanageHandler());
			config.setSourceFileFullPath(fileFullPath);
			config.generateFileNameWithSourceFileFullPath();
			config.generateModulePath();

			run(config);
		}
	}
	
	private void run(AnalysisConfig config){
		if(isAsynchronous){
			analyzer.runAsync(config);
		} else {
			analyzer.runSync(config);
		}
	}

	protected EndOfAnalysisHandler createResultChanageHandler(){
		return new EndOfAnalysisHandler() {
			@Override
			public void handleAnalysisResult(List<AnalysisResult> resultList) {
				assert resultList.size() > 0;
				
				List<Defect> allDefectList = DexterAnalyzer.getAllDefectList(resultList);
				final String sourceFileFullPath = DexterAnalyzer.getSourceFileFullPath(resultList);
				
				try {
	                writeResultFile(allDefectList, sourceFileFullPath);
                } catch (IOException e) {
	                LOG.errorln(e.getMessage(), e);
                }
				
				
				LOG.infoln(" - " + sourceFileFullPath);
				
				if(allDefectList.size() == 0){
					LOG.infoln("    > No Defect");
					return;
				} else {
					LOG.infoln("    > Total Defects: " + allDefectList.size());
				}

				printDefect(allDefectList);
			}

			private void writeResultFile(List<Defect> allDefectList, String sourceFileFullPath) throws IOException {
				if(isJsonFile){
					for(Defect defect : allDefectList){
						Files.append(defect.toJson(), jsonResultFile, Charsets.UTF_8);
						Files.append(",\n", jsonResultFile, Charsets.UTF_8);
					}
				}
				
				if(isXmlFile){
					StringBuilder m = new StringBuilder();
					m.append("\t<error filename=\"").append(sourceFileFullPath).append("\">\n");
					
					for(Defect defect : allDefectList){
						m.append("\t\t<defect checker=\"").append(defect.getCheckerCode()).append("\">\n");
						for(Occurence o : defect.getOccurences()){
							m.append("\t\t\t<occurence startLine=\"").append(o.getStartLine()).append("\" ")
								.append("endLine=\"").append(o.getEndLine()).append("\" ")
								.append(" message=\"").append(o.getMessage()).append("\" />\n");
						}
						m.append("\t\t</defect>\n");
					}
					
					m.append("\t</error>\n");
					
					Files.append(m.toString(), xmlResultFile, Charsets.UTF_8);
				}
				if(isXml2File){
					StringBuilder m = new StringBuilder();
					m.append("\t<error filename=\"").append(sourceFileFullPath).append("\">\n");
					
					for(Defect defect : allDefectList){
						m.append("\t\t<defect checker=\"").append(defect.getCheckerCode()).append("\">\n");
						for(Occurence o : defect.getOccurences()){
							m.append("\t\t\t<occurence startLine=\"").append(o.getStartLine()).append("\" ")
								.append("endLine=\"").append(o.getEndLine()).append("\" ")
								.append(" message=\"").append(o.getMessage()).append("\" />\n");
						}
						m.append("\t\t</defect>\n");
					}
					
					m.append("\t</error>\n");
					
					Files.append(m.toString(), xml2ResultFile, Charsets.UTF_8);
				}
            }

			private void printDefect(List<Defect> allDefectList) {
				for (final Defect defect : allDefectList) {
					switch(defect.getSeverityCode()){
						case "CRI":
							criticalCnt++;
							break;
						case "MAJ":
							majorCnt++;
							break;
						case "MIN":
							minorCnt++;
							break;
						case "CRC":
							crcCnt++;
							break;
						case "ETC":
							etcCnt++;
							break;
						default:
							defect.setSeverityCode("ETC");
							etcCnt++;
							break;
					}
					
					totalCnt++;
					totalOccurenceCnt += defect.getOccurences().size();
					
					LOG.infoln("    > " + defect.getCheckerCode() + " / " + defect.getSeverityCode() + " / " + defect.getOccurences().size());
					printOccurences(defect.getOccurences().toArray(new Occurence[defect.getOccurences().size()]));
				}
            }
			
			private void printOccurences(Occurence[] occs){
				for(int i=0; i<occs.length; i++){
					final Occurence o = occs[i];
					
					if((i+1) == occs.length){
						LOG.infoln("       └ " + o.getStartLine() + " " + o.getMessage());
					} else {
						LOG.infoln("       ├ " + o.getStartLine() + " " + o.getMessage());
					}
				}
			}
		};
	}
	
	private void exitForWrongOptionCombination(final CommandLine cmd, final char firstOption, 
			final char secondOption) {
		if (cmd.hasOption("a") && cmd.hasOption("n")){
			LOG.errorln("you cannot use option '-" + firstOption + "' and with '-" + secondOption + "'");
			exit(ExitCode.ERROR_WRONG_OPTION_COMBINATION);
		}
    }
	
	private void exit(ExitCode code){
		LOG.error("system exit code : " + code.ordinal());
		System.exit(code.ordinal());
	}
	
	private void exitSuccess(){
		System.exit(ExitCode.SUCCESS.ordinal());
	}

	public static void setLog(CliLogger cliLogger) {
		LOG = cliLogger;
    }
}
