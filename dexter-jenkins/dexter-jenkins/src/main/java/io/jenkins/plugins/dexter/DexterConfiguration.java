package io.jenkins.plugins.dexter;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.FreeStyleProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import hudson.views.ViewsTabBarDescriptor;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;


@Extension
public class DexterConfiguration extends GlobalConfiguration {




	private static String DEXTER_SERVER = "";
	private static String DEXTER_PORT = "";
	private static String DEXTER_USER = "";
	private static  String DEXTER_PASSWORD = "";


	
	    public static DexterConfiguration get() {
	        return GlobalConfiguration.all().get(DexterConfiguration.class);
	    }

	     public DexterConfiguration() {
	        load();
	    }

	 
	    public FormValidation doTestConnection(@QueryParameter("dexterServer") final String dexterServer, @QueryParameter("dexterPort") final String dexterPort, @QueryParameter("dexterUser") final String dexterUser, @QueryParameter("dexterPassword") final String dexterPassword)
	           throws IOException, ServletException {
	    	  Socket socket;
	        try {
	        	socket = new Socket();
	            socket.connect(new InetSocketAddress(dexterServer, Integer.parseInt(dexterPort)), 5000);
	            URL url = new URL("http://" + dexterServer + ":" + dexterPort + "/api/accounts/userId");
	            URLConnection connection = url.openConnection();
	            String login = dexterUser + ":" + dexterPassword;         
	            connection.setRequestProperty("Authorization", "Basic " + login);  
	            save();
	            return FormValidation.ok("Success");
	        } catch (UnknownHostException e) {
	            return FormValidation.error("Client error : "+e.getMessage());
	        } 
	    }
	    
   
	    @Extension
	    public static class Descriptor extends  hudson.model.Descriptor<GlobalConfiguration>{
	 
	        private String dexterServer;
	        private String dexterPort;
	        private String dexterUser;
	        private String dexterPassword;
	        
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

	       
	    	 @Override
	   	  public boolean configure(StaplerRequest req, JSONObject formData) throws FormException{
	   	    dexterServer = formData.getString("dexterServer");
	   	    dexterPort = formData.getString("dexterPort");
	      	dexterUser = formData.getString("dexterUser");
	      	dexterPassword = formData.getString("dexterPassword");
	   	    save();
	   	    
	   	    DEXTER_SERVER = dexterServer;
	   	    DEXTER_PORT = dexterPort;
	   	    DEXTER_USER = dexterUser;
	   	    DEXTER_PASSWORD = dexterPassword;
	   	
	   	    return true;
	   	  }
	       

	    } 
	    

	}
