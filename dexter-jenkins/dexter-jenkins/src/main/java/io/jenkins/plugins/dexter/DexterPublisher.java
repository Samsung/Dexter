package io.jenkins.plugins.dexter;

import io.jenkins.plugins.dexter.*;
import hudson.Launcher;
import hudson.Extension;
import hudson.model.Action;
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
    private final String sourceEncoding;
    private final String binDir;
    private final String language;
    private final String type;



    public String getPathConfig() {
		return pathConfig;
	}

	// Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public DexterPublisher(String path, String pathConfig, String projectName, String projectFullPath, String sourceDir, String sourceEncoding, String binDir, 
    		String language, String type) {
        this.path = path;
        this.pathConfig = pathConfig;
        this.projectName = projectName;
        this.projectFullPath = projectFullPath;
        this.sourceDir = sourceDir;
        this.sourceEncoding = sourceEncoding;
        this.binDir = binDir;
        this.language = language;
        this.type = type;
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

	public String getSourceEncoding() {
		return sourceEncoding;
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

	/**
     * We'll use this from the <tt>config.jelly</tt>.
     */
    public String getName() {
        return path;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
     
  String message;
          
          message="Hello, " + path + "!";
        //  Runtime runtime = Runtime.getRuntime();
          try {
        	  boolean fileRead = readFile();
              if (fileRead) {
                  replacement();
                  writeToFile();
              }
              String command = "cmd /c start cmd.exe /K "  + "\"cd " + path + " && D: && dexter.bat user dexter\"";
              message = command;
          //    Path fileToWrite = Paths.get(pathConfig);
              OpenOption myOpt = StandardOpenOption.APPEND;
              //  Process proc = runtime.exec(command);
              Path fileToWrite = Paths.get(pathConfig);
                java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(command).getInputStream(), "UTF-8").useDelimiter("\\A");
                BufferedWriter bufwriter = Files.newBufferedWriter(fileToWrite, Charset.forName("UTF-8"),  myOpt);
                if(s.hasNext())
                {
                bufwriter.write("HMMdvdgxvcxbvHG");
                }
                bufwriter.close();
                s.close();
                  message = command;
   
              } catch(IOException e) {
                      System.out.println(e.getMessage() );
              } catch(Exception e){
                  System.out.println(e.getMessage() );
              }
        DexterBuildAction buildAction = new DexterBuildAction(message, build);
        build.addAction(buildAction);

        return true;
    }
    
    static StringBuffer stringBufferOfData = new StringBuffer();
    static String filename = null;
    static Scanner sc = new Scanner(System.in, "UTF-8");
   
    private boolean readFile() {
    	
       filename = pathConfig;        
        Scanner fileToRead = null;
        try {
            fileToRead = new Scanner(new File(filename), "UTF-8"); 
            for (String line; fileToRead.hasNextLine() && (line = fileToRead.nextLine()) != null; ) {
                System.out.println(line);
                stringBufferOfData.append(line).append("\r\n");
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
            bufwriter.write("\"dexterServerIp\":\"127.0.0.1\", \n"); 
            bufwriter.write("\"dexterServerPort\":\"4982\", \n"); 
            bufwriter.write("\"projectName\":\"example\", \n"); 
            bufwriter.write("\"projectFullPath\":\"D:\\tests\\project\\example\\example\", \n"); 
            bufwriter.write("\"sourceDir\":[\"D:\\tests\\project\\example\\example\"], \n"); 
            bufwriter.write("\"headerDir\":[\"D:\\tests\\project\\example\\example\"], \n"); 
            bufwriter.write("\"sourceEncoding\":\"UTF-8\", \n"); 
            bufwriter.write("\"libDir\":[], \n"); 
            bufwriter.write("\"binDir\":\"\", \n"); 
         //   bufwriter.write("\"language\":\"JAVA\", \n"); 
            bufwriter.write("\"modulePath\":\"\", \n");
            bufwriter.write("\"fileName\":[\"main.cpp\"], \n");
            bufwriter.write("\"type\":\"FILE\" \n"); 
            bufwriter.write("}\n"); 
            bufwriter.close();
        } catch (Exception e) {
            System.out.println("Error occured while attempting to write to file: " + e.getMessage());
        }
    }
    private  void replacement() {
        readFile();
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

      
        public FormValidation doCheckName(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Please set dexter client path");
            if (value.length() < 4)
                return FormValidation.warning("Isn't the path too short?");
            return FormValidation.ok();
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
