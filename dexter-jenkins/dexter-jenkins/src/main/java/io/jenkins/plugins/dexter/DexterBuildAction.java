package io.jenkins.plugins.dexter;
import hudson.model.AbstractBuild;
import hudson.model.Action;


public class DexterBuildAction implements Action {

    private String message;
    private AbstractBuild<?, ?> build;

    @Override
    public String getIconFileName() {
        return "/plugin/testExample/img/build-goals.png";
    }

    @Override
    public String getDisplayName() {
        return "Static analysis results";
    }

    @Override
    public String getUrlName() {
        return "analysisResults";
    }

    public String getMessage() {
        return this.message;
    }

    public int getBuildNumber() {
        return this.build.number;
    }

    public AbstractBuild<?, ?> getBuild() {
        return build;
    }

   DexterBuildAction(final String message, final AbstractBuild<?, ?> build)
    {
        this.message = message;
        this.build = build;
    }
}
