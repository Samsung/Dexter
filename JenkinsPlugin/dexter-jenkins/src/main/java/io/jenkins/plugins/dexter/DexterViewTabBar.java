package io.jenkins.plugins.dexter;

import hudson.Extension;
import hudson.views.ViewsTabBar;
import hudson.views.ViewsTabBarDescriptor;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

public class DexterViewTabBar extends ViewsTabBar{
	 @DataBoundConstructor
	    public DexterViewTabBar() {
	        super();
	    }
	    
	    @Extension
	    public static final class CustomViewsTabBarDescriptor extends ViewsTabBarDescriptor {

	        public CustomViewsTabBarDescriptor() {
	            load();
	        }

	        @Override
	        public String getDisplayName() {
	            return "Dexter view";
	        }

	        @Override
	        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
	            save();
	            return false;
	        }
	    }
}
