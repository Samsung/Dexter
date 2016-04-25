using dexter_vs.Config.Providers;
using EnvDTE;
using Microsoft.VisualStudio.Shell;
using Microsoft.VisualStudio.Shell.Interop;
using System;

namespace dexter_vs.UI.Analysis
{
    /// <summary>
    /// Command handler - for solution analysis. 
    /// Automatically sets menu text and Enabled property depending on currently opened solution.
    /// </summary>
    internal class DexterSolutionAnalysisCommand : DexterAnalysisCommand
    {  

        public DexterSolutionAnalysisCommand(Package package, int commandId, Guid commandSet, ConfigurationProvider configurationProvider)
           : base(package, commandId, commandSet, configurationProvider)
        {
            AdviseSolutionEvents();
        }

        /// <summary>
        /// Registers custom event handler for solution events
        /// </summary>
        private void AdviseSolutionEvents()
        {
            IVsSolution solution = ServiceProvider.GetService(typeof(SVsSolution)) as IVsSolution;
            var solutionEvents = new DexterAnalysisCommandSolutionEvents(this);
            uint cookie = 0;
            solution.AdviseSolutionEvents(solutionEvents, out cookie);
        }

        private Project getActiveProject()
        {
            Array projects = (Array)Dte.ActiveSolutionProjects;
            if (projects != null && projects.Length > 0)
            {
                return projects.GetValue(0) as Project;
            }
            projects = (Array)Dte.Solution.SolutionBuild.StartupProjects;
            if (projects != null && projects.Length >= 1)
            {
                return projects.GetValue(0) as Project;
            }

            Projects projs = Dte.Solution.Projects;
            if (projs != null && projs.Count > 0)
            {
                return projs.Item(1);
            }
            return null;
        }

        /// <summary>
        /// Event handler for solution events.
        /// Sets the state and text of menu controls based on current project
        /// </summary>
        class DexterAnalysisCommandSolutionEvents : VsSolutionEvents
        {
            private DexterSolutionAnalysisCommand dexterCommand;

            public DexterAnalysisCommandSolutionEvents(DexterSolutionAnalysisCommand dexterCommand)
            {
                this.dexterCommand = dexterCommand;
            }

            public override int OnAfterOpenProject(IVsHierarchy pHierarchy, int fAdded)
            {
                Project activeProject = dexterCommand.getActiveProject();

                dexterCommand.Text = "On " + activeProject.Name;
                dexterCommand.Refresh();
                return base.OnAfterOpenProject(pHierarchy, fAdded);
            }

            public override int OnBeforeCloseProject(IVsHierarchy pHierarchy, int fRemoved)
            {
                dexterCommand.Enabled = false;
                return base.OnBeforeCloseProject(pHierarchy, fRemoved);
            }
        }
    }
}
