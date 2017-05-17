using Dexter.Config.Providers;
using EnvDTE;
using Microsoft.VisualStudio.Shell;
using Microsoft.VisualStudio.Shell.Interop;
using System;

namespace Dexter.UI.Analysis
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

        /// <summary>
        /// Saves currently opened project, validates configuration and performs analysis
        /// </summary>
        /// <param name="sender">Event sender.</param>
        /// <param name="e">Event args.</param>
        protected override void CommandClicked(object sender, EventArgs e)
        {
            Project project = getActiveProject();
            if (project!=null && !project.Saved)
            {
                project.Save();
            }
            base.CommandClicked(sender, e);
        }

        private Project getActiveProject()
        {
            Array projects = (Array) (Dte.ActiveSolutionProjects ?? Dte.Solution.SolutionBuild.StartupProjects);

            if (projects != null && projects.Length > 0)
            {
                var project = projects.GetValue(0) as Project;
                if (project != null)
                {
                    return project;
                }
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
