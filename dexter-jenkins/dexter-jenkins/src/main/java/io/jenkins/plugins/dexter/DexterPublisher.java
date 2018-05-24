package io.jenkins.plugins.dexter;

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

    private final String path;
    private final String pathConfig;
    private final String projectName;
    private final String projectFullPath;
    private final String sourceDir;
    private final String binDir;
    private final String headerDir;
    private final String analyseFileName;
    private final String language;
    private final String type;
    private String dexterServer = "";
    private String dexterPort = "";
    private String dexterUser = "";
    private String dexterPassword="";
    String message;

	public String getPathConfig() {
		return pathConfig;
	}

	// Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public DexterPublisher(String path, String pathConfig, String projectName, String projectFullPath, String sourceDir, String binDir, String headerDir,
    		String analyseFileName, String language, String type) {
        this.path = path;
        this.pathConfig = pathConfig;
        this.projectName = projectName;
        this.projectFullPath = projectFullPath;
        this.sourceDir = sourceDir;
        this.binDir = binDir;
        this.headerDir = headerDir;
        this.analyseFileName = analyseFileName;
        this.language = language;
        this.type = type;
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


	public String getPath() {
		return path;
	}

	
	public String getProjectName() {
		return projectName;
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

	public String getLanguage() {
		return language;
	}

	public String getType() {
		return type;
	}

	
    public String getName() {
        return path;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
     
 
  String way =  System.getProperty("user.dir") + "\\work\\io.jenkins.plugins.dexter.DexterConfiguration.xml"; 
   if (readFile(way))
   {
	   char[] chArr = new char[20];
	   char[] portArr = new char[20];
	   char[] userArr = new char[50];
	   char[] passwordArr = new char[50];
	   int startServer = stringBufferOfData.indexOf("<dexterServer>") + 14;
	   int endServer = stringBufferOfData.indexOf("</dexterServer>");
	   stringBufferOfData.getChars(startServer, endServer, chArr, 0);
	   dexterServer = new String(chArr).trim();
	   
	   int startPort = stringBufferOfData.indexOf("<dexterPort>") + 12;
	   int endPort= stringBufferOfData.indexOf("</dexterPort>");
	   stringBufferOfData.getChars(startPort, endPort, portArr, 0);
	   dexterPort = new String(portArr).trim();
	   
	   int startUser = stringBufferOfData.indexOf("<dexterUser>") + 12;
	   int endUser= stringBufferOfData.indexOf("</dexterUser>");
	   stringBufferOfData.getChars(startUser, endUser, userArr, 0);
	   dexterUser = new String(userArr).trim();

	   
	   int startPassword = stringBufferOfData.indexOf("<dexterPassword>") + 16;
	   int endPassword = stringBufferOfData.indexOf("</dexterPassword>");
	   stringBufferOfData.getChars(startPassword, endPassword, passwordArr, 0);
	   dexterPassword = new String(passwordArr).trim();

	   stringBufferOfData.delete(0,  stringBufferOfData.length());
   }
       
         Runtime runtime = Runtime.getRuntime();
         writeToFile();
              
              String command = "cmd /c start cmd.exe /K "  + "\"cd " + path + " && D: && dexter.bat user dexter > logs.txt \"";
              try {
				runtime.exec(command);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
   
       if(readFile(path + "//logs.txt")) {       
    	   message = stringBufferOfData.toString();
       }
       else
    	   message = "Error saving logs";
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
            fileToRead = new Scanner(new File(filename), "UTF-8"); 
            for (String line; fileToRead.hasNextLine() && (line = fileToRead.nextLine()) != null; ) {
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
    private  void writeToFile() {
    	OpenOption myOpt = StandardOpenOption.CREATE;
        try {
        	Path fileToWrite = Paths.get(pathConfig);
            BufferedWriter bufwriter = Files.newBufferedWriter(fileToWrite, Charset.forName("UTF-8"),  myOpt);
            bufwriter.write("{\n");
            bufwriter.write("\"dexterHome\":\"D:\\Dexter\\Dexter_client\", \n"); 
            bufwriter.write("\"dexterServerIp\":\"" + dexterServer + "\", \n"); 
            bufwriter.write("\"dexterServerPort\":\"" + dexterPort + "\", \n"); 
            bufwriter.write("\"projectName\":\"" + projectName + "\", \n"); 
            bufwriter.write("\"projectFullPath\":\"" + projectFullPath + "\", \n");   //D:\\tests\\project\\example\\example
            bufwriter.write("\"sourceDir\":[\"" + sourceDir + "\"], \n");  //D:\\tests\\project\\example\\example
            bufwriter.write("\"headerDir\":[\"" + headerDir+ "\"], \n"); //D:\\tests\\project\\example\\example
            bufwriter.write("\"sourceEncoding\":\"UTF-8\", \n"); 
            bufwriter.write("\"libDir\":[], \n"); 
            bufwriter.write("\"binDir\":\"\", \n"); 
         //   bufwriter.write("\"language\":\"JAVA\", \n"); 
            bufwriter.write("\"modulePath\":\"\", \n");
            bufwriter.write("\"fileName\":[\"" + analyseFileName + "\"], \n");  //main.cpp
            bufwriter.write("\"type\":\""+ type +"\" \n"); 
            bufwriter.write("}\n"); 
            bufwriter.write(dexterPassword);
            bufwriter.write(dexterUser);
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

   
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        
       
        public DescriptorImpl() {
            load();
        }
     
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types 
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