package io.jenkins.plugins.dexter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.commons.io.IOUtils;

import io.jenkins.plugins.dexter.*;
import hudson.Launcher;
import hudson.Extension;
import hudson.model.Action;
import java.util.logging.ConsoleHandler;
import hudson.tasks.*;
import hudson.util.FormValidation;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.AbstractProject;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class DexterPublisher extends Recorder {

	private boolean createServer;
	private String pathDexter;
	private String projectFullPath;
	private String sourceDir;
	private String binDir;
	private String headerDir;
	private final String analyseFileName;
	private final boolean file;
	private final boolean folder;
	private final boolean project;
	private final boolean snapshot;
	private String dexterServer;
	private String dexterPort;
	private String dexterUser;
	private String dexterPassword;
	private String pathToBat = "";
	private String serverDisc = "";
	private String dexterDisc = "";
	private String dexterServerPath = "";
	private String pathConfig = "";
	private String projectName = "";
	private String projectType = "";
	String message;

	public String getPathConfig() {
		return pathConfig;
	}

	// Fields in config.jelly must match the parameter names in the
	// "DataBoundConstructor"
	@DataBoundConstructor
	public DexterPublisher(boolean createServer, String dexterServerPath, String dexterServer, String dexterPort,
			String dexterUser, String dexterPassword, String pathDexter, String projectFullPath, String sourceDir,
			String binDir, String headerDir, String analyseFileName, boolean file, boolean folder, boolean project,
			boolean snapshot) {
		this.createServer = createServer;
		this.dexterServerPath = dexterServerPath;
		this.dexterServer = dexterServer;
		this.dexterPort = dexterPort;
		this.dexterUser = dexterUser;
		this.dexterPassword = dexterPassword;
		this.pathDexter = pathDexter;
		this.projectFullPath = projectFullPath;
		this.sourceDir = sourceDir;
		this.binDir = binDir;
		this.headerDir = headerDir;
		this.analyseFileName = analyseFileName;
		this.file = file;
		this.folder = folder;
		this.project = project;
		this.snapshot = snapshot;
	}

	public boolean isCreateServer() {
		return createServer;
	}

	public void setCreateServer(boolean createServer) {
		this.createServer = createServer;
	}

	public String getAnalyseFileName() {
		return analyseFileName;
	}

	public String getHeaderDir() {
		return headerDir;
	}

	public static String getFilename() {
		return filename;
	}

	public String getProjectName() {
		return "project";
	}

	public String getProjectFullPath() {
		return projectFullPath;
	}

	public String getSourceDir() {
		return sourceDir;
	}

	public String getBinDir() {
		return binDir;
	}

	public boolean isFile() {
		return file;
	}

	public boolean isFolder() {
		return folder;
	}

	public boolean isProject() {
		return project;
	}

	public boolean isSnapshot() {
		return snapshot;
	}

	public String getName() {
		return pathDexter;
	}

	public String getDexterServer() {
		return dexterServer;
	}

	public String getDexterPort() {
		return dexterPort;
	}

	public void setDexterPort(String dexterPort) {
		this.dexterPort = dexterPort;
	}

	public String getDexterUser() {
		return dexterUser;
	}

	public void setDexterUser(String dexterUser) {
		this.dexterUser = dexterUser;
	}

	public String getDexterPassword() {
		return dexterPassword;
	}

	public void setDexterPassword(String dexterPassword) {
		this.dexterPassword = dexterPassword;
	}

	public void setDexterServer(String dexterServer) {
		this.dexterServer = dexterServer;
	}

	private static String OS = null;

	public static String getOsName() {
		if (OS == null) {
			OS = System.getProperty("os.name");
		}
		return OS;
	}

	public static boolean isWindows() {
		return getOsName().startsWith("Windows");
	}

	@Override
	public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
		pathToBat = pathDexter + "/bin";
		pathConfig = pathDexter + "/bin/dexter_cfg.json";
		projectName = getProjectName();
		serverDisc = dexterServerPath.substring(0, 2);
		dexterDisc = pathDexter.substring(0, 2);
		if (file) {
			projectType = "FILE";
		}
		if (folder) {
			projectType = "FOLDER";
		}
		if (project) {
			projectType = "PROJECT";
		}
		if (snapshot) {
			projectType = "SNAPSHOT";
		}

		if (isCreateServer()) {
			if (isWindows()) {
				writeToBatFile();
				Runtime runtime = Runtime.getRuntime();
				String command = "cmd /c start /wait cmd.exe /K " + "\"cd " + dexterServerPath + " && " + serverDisc
						+ " && run.bat \"";
				try {
					runtime.exec(command);
				} catch (IOException e) {
					e.printStackTrace();
				}

				writeToFile();

				String commandDexter = "cmd /c start cmd.exe /K " + "\"cd " + pathToBat + " && " + dexterDisc
						+ " && dexter.bat " + dexterUser + " " + dexterPassword + " > logs.txt \"";
				try {
					runtime.exec(commandDexter);
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (readFile(pathToBat + "//logs.txt")) {
					message = stringBufferOfData.toString();
				} else
					message = "Error saving logs";
			} else {

				writeToShFile();
				writeToFile();

				try {
					message = "";
					int ch;

					ProcessBuilder pb = new ProcessBuilder(dexterServerPath + "/run.sh");
					pb.directory(new File(dexterServerPath));

					Process shellProcess = pb.start();

					ProcessBuilder pbClient = new ProcessBuilder(pathToBat + "/dexter.sh", dexterUser, dexterPassword);
					pbClient.directory(new File(pathToBat));

					Process shellProcessClient = pbClient.start();

					InputStreamReader myIStreamReader = new InputStreamReader(shellProcessClient.getInputStream());
					
					String result = IOUtils.toString(shellProcessClient.getInputStream(), StandardCharsets.UTF_8);
					message = result;
					shellProcess.destroy();
				} catch (IOException anIOException) {
					System.out.println(anIOException);
				} catch (Exception e) {
					e.printStackTrace();

				}
			}
		}

		DexterBuildAction buildAction = new DexterBuildAction(message, build);
		build.addAction(buildAction);
		return true;
	}

	static StringBuffer stringBufferOfData = new StringBuffer();
	static String filename = null;
	static Scanner sc = new Scanner(System.in, "UTF-8");

	private boolean readFile(String filenameRead) {
		filename = filenameRead;
		Scanner fileToRead = null;
		try {
			stringBufferOfData.replace(0, stringBufferOfData.length(), " ");
			fileToRead = new Scanner(new File(filename), "UTF-8");
			for (String line; fileToRead.hasNextLine() && (line = fileToRead.nextLine()) != null;) {
				System.out.println(line);
				stringBufferOfData.append(line).append("\n");
			}
			fileToRead.close();
			return true;
		} catch (FileNotFoundException ex) {
			System.out.println("The file " + filename + " could not be found! " + ex.getMessage());
			return false;
		} finally {
			if (fileToRead != null) {
				fileToRead.close();
			}
			return true;
		}

	}

	private void writeToBatFile() {

		OpenOption myOpt = StandardOpenOption.CREATE;
		try {
			Path fileToWrite = Paths.get(dexterServerPath + "/run.bat");
			BufferedWriter bufwriter = Files.newBufferedWriter(fileToWrite, Charset.forName("UTF-8"), myOpt);
			bufwriter.write("node server.js -database.host=");
			bufwriter.write(dexterServer);
			bufwriter.write(" -p=");
			bufwriter.write(dexterPort);
			bufwriter.write(" -database.name=my_dexter_db -database.user=");
			bufwriter.write(dexterUser);
			bufwriter.write(" -database.password=");
			bufwriter.write(dexterPassword);
			bufwriter.close();
		} catch (Exception e) {
			System.out.println("Error occured while attempting to write to file: " + e.getMessage());
		}
	}

	private void writeToShFile() {

		OpenOption myOpt = StandardOpenOption.CREATE;
		try {
			Path fileToWrite = Paths.get(dexterServerPath + "/run.sh");

			BufferedWriter bufwriter = Files.newBufferedWriter(fileToWrite, Charset.forName("UTF-8"), myOpt);
			bufwriter.write("node server.js -database.host=");
			bufwriter.write(dexterServer);
			bufwriter.write(" -p=");
			bufwriter.write(dexterPort);
			bufwriter.write(" -database.name=my_dexter_db -database.user=");
			bufwriter.write(dexterUser);
			bufwriter.write(" -database.password=");
			bufwriter.write(dexterPassword);
			bufwriter.close();
		} catch (Exception e) {
			System.out.println("Error occured while attempting to write to file: " + e.getMessage());
		}
	}

	private void writeToFile() {

		OpenOption myOpt = StandardOpenOption.CREATE;
		try {
			Path fileToWrite = Paths.get(pathConfig);
			BufferedWriter bufwriter = Files.newBufferedWriter(fileToWrite, Charset.forName("UTF-8"), myOpt);
			bufwriter.write("{\n");
			bufwriter.write("\"dexterHome\":\"" + pathDexter + "\", \n");
			bufwriter.write("\"dexterServerIp\":\"" + dexterServer + "\", \n");
			bufwriter.write("\"dexterServerPort\":\"" + dexterPort + "\", \n");
			bufwriter.write("\"projectName\":\"" + projectName + "\", \n");
			bufwriter.write("\"projectFullPath\":\"" + projectFullPath + "\", \n");
			bufwriter.write("\"sourceDir\":[\"" + sourceDir + "\"], \n");
			bufwriter.write("\"headerDir\":[\"" + headerDir + "\"], \n");
			bufwriter.write("\"sourceEncoding\":\"UTF-8\", \n");
			bufwriter.write("\"libDir\":[], \n");
			bufwriter.write("\"binDir\":\"\", \n");
			bufwriter.write("\"modulePath\":\"\", \n");
			bufwriter.write("\"fileName\":[\"" + analyseFileName + "\"], \n");
			bufwriter.write("\"type\":\"" + projectType + "\" \n");
			bufwriter.write("}\n");
			bufwriter.close();
		} catch (Exception e) {
			System.out.println("Error occured while attempting to write to file: " + e.getMessage());
		}
	}

	@Override
	public DescriptorImpl getDescriptor() {
		return (DescriptorImpl) super.getDescriptor();
	}

	@Override
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}

	@Override
	public Action getProjectAction(AbstractProject<?, ?> project) {
		return new DexterProjectAction(project);
	}

	@Extension
	public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

		public DescriptorImpl() {
			load();
		}

		public boolean isApplicable(Class<? extends AbstractProject> aClass) {

			return true;
		}

		public String getDisplayName() {
			return "Dexter Static Analysis";
		}

		@Override
		public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {

			save();
			return super.configure(req, formData);
		}

	}
}