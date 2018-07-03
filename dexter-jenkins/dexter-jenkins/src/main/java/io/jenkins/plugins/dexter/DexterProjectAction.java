package io.jenkins.plugins.dexter;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;

import java.util.ArrayList;
import java.util.List;


public class DexterProjectAction implements Action {

    private AbstractProject<?, ?> project;

    @Override
    public String getIconFileName() {
        return "/plugin/testExample/img/project_icon.png";
    }

    @Override
    public String getDisplayName() {
        return "";
    }

    @Override
    public String getUrlName() {
        return "dexterAnalysis";
    }

    public AbstractProject<?, ?> getProject() {
        return this.project;
    }

    public String getProjectName() {
        return this.project.getName();
    }

    public List<String> getProjectMessages() {
        List<String> projectMessages = new ArrayList<String>();
        List<? extends AbstractBuild<?, ?>> builds = project.getBuilds();
        String projectMessage="";
        final Class<DexterBuildAction> buildClass =DexterBuildAction.class;

        for (AbstractBuild<?, ?> currentBuild : builds) {
            projectMessage = "Build #"+currentBuild.getAction(buildClass).getBuildNumber()
                    +": \n"+currentBuild.getAction(buildClass).getMessage() + "\n";
            projectMessages.add(projectMessage);
        }
        return projectMessages;
    }

    DexterProjectAction(final AbstractProject<?, ?> project) {
        this.project = project;
    }
}
