//------------------------------------------------------------------------------
// <copyright file="DexterCommand.cs" company="Company">
//     Copyright (c) Company.  All rights reserved.
// </copyright>
//------------------------------------------------------------------------------

using System;
using Microsoft.VisualStudio.Shell;
using EnvDTE80;
using EnvDTE;
using System.Diagnostics;
using System.Windows.Forms;
using Microsoft.VisualStudio.Shell.Interop;
using dexter_vs.UI.Tasks;
using dexter_vs.Config;
using dexter_vs.Defects;
using dexter_vs.Analysis;
using Configuration = dexter_vs.Config.Configuration;
using dexter_vs.Config.Validation;
using dexter_vs.Config.Providers;

namespace dexter_vs.UI.Analysis
{
    /// <summary>
    /// Command handler - for analysis command
    /// </summary>
    internal class DexterAnalysisCommand : DexterCommand
    {
        /// <summary>
        /// Dexter task provider
        /// </summary>
        private DexterTaskProvider taskProvider;
        
        /// <summary>
        /// DexterInfo validator
        /// </summary>
        private readonly DexterInfoValidator validator;

        /// <summary>
        /// Invoked when analysis is started
        /// </summary>
        public event EventHandler AnalysisStarted;

        /// <summary>
        /// Invoked when analysis is finished
        /// </summary>
        public event EventHandler AnalysisFinished;

        /// <summary>
        /// Dexter instance 
        /// </summary>
        private Dexter dexter;

        /// <summary>
        /// Configuration provider
        /// </summary>
        protected IConfigurationProvider ConfigurationProvider
        {
            get;
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="DexterAnalysisCommand"/> class.
        /// Adds our command handlers for menu (commands must exist in the command table file)
        /// </summary>
        /// <param name="package">Owner package, not null.</param>
        /// <param name="commandId">Command ID.</param>
        /// <param name="commandSet">Command menu group (command set GUID).</param>
        public DexterAnalysisCommand(Package package, int commandId, Guid commandSet, ConfigurationProvider configurationProvider)
            :base(package,commandId,commandSet)
        {
            menuItem.Enabled = Dte.Solution.Projects.Count > 0;
            validator = new DexterInfoValidator();
            ConfigurationProvider = configurationProvider;

            AdviseSolutionEvents();
        }


        /// <summary>
        /// For event handling; invoked when analysis is started
        /// </summary>
        protected virtual void OnAnalysisStarted(EventArgs e)
        {
            if (AnalysisStarted != null)
                AnalysisStarted(this,e);
        }

        /// <summary>
        /// For event handling; invoked when analysis is finished
        /// </summary>
        protected virtual void OnAnalysisFinished(EventArgs e)
        {
            if (AnalysisFinished != null)
                AnalysisFinished(this, e);
        }

        /// <summary>
        /// Validates configuration and perform analysis
        /// </summary>
        /// <param name="sender">Event sender.</param>
        /// <param name="e">Event args.</param>
        protected override void CommandClicked(object sender, EventArgs e)
        {
            Configuration config = ConfigurationProvider.Load();

            if (!ValidateConfiguration(config))
            {
                return;
            }
            
            dexter = new Dexter(config);

            OutputWindowPane outputPane = CreatePane("Dexter");
            outputPane.Clear();
            outputPane.Activate();
        
            DataReceivedEventHandler writeToOutputPane = (s, e1) => outputPane.OutputString(e1.Data + Environment.NewLine);
            dexter.OutputDataReceived += writeToOutputPane;
            dexter.ErrorDataReceived += writeToOutputPane;

            System.Threading.Tasks.Task.Run(() => 
            {
                OnAnalysisStarted(EventArgs.Empty);
                Result result = dexter.Analyse();
                OnAnalysisFinished(EventArgs.Empty);
                ReportResult(result);
            });
        }

        /// <summary>
        /// Cancels analysis
        /// </summary>
        public void Cancel()
        {
            if (dexter!=null)
            {
                dexter.Cancel();
                OnAnalysisFinished(EventArgs.Empty);
            }
        }

        /// <summary>
        /// Validates Dexter configuration
        /// </summary>
        /// <param name="config">configuration</param>
        /// <returns>true, if validation was successfull </returns>
        private bool ValidateConfiguration(Configuration config)
        {
            DexterInfo dexterInfo = DexterInfo.fromConfiguration(config);
            string validationResult;

            if (!validator.ValidateDexterPath(dexterInfo))
            {
                MessageBox.Show("Dexter wasn't found in given path. You cannot perform analysis until you set a proper path.", "Dexter error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return false;
            }
            if (!config.standalone && !validator.ValidateServerConnection(dexterInfo, out validationResult))
            {
                DialogResult result = MessageBox.Show("Couldn't connect to Dexter server. Please check server address in Dexter/Settings window. Continue in standalone mode?", "Dexter warning", MessageBoxButtons.OKCancel, MessageBoxIcon.Warning);
                if (result == DialogResult.OK)
                {
                    config.standalone = true;
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                if (!config.standalone && !validator.ValidateUserCredentials(dexterInfo, out validationResult))
                {
                    DialogResult result = MessageBox.Show("Couldn't login to Dexter server. Please check user credentials in Dexter/Settings window. Continue in standalone mode?", "Dexter warning", MessageBoxButtons.OKCancel, MessageBoxIcon.Warning);
                    if (result == DialogResult.OK)
                    {
                        config.standalone = true;
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
            }
            return true;
        }
        
        /// <summary>
        /// Clears task provider
        /// </summary>
        private void ClearTaskProvider()
        {
            taskProvider = taskProvider ?? new DexterTaskProvider(ServiceProvider);
            taskProvider.Tasks.Clear();
        }
                
        /// <summary>
        /// Reports defects from analysis result
        /// </summary>
        /// <param name="result">analysis result</param>
        private void ReportResult(Result result)
        {
            ClearTaskProvider();

            taskProvider.ReportResult(result);
            taskProvider.Show();
            taskProvider.BringToFront();
        }
                       
        /// <summary>
        /// Creates (or returns, if exists) Output Pane
        /// </summary>
        /// <param name="title">Pane title</param>
        private OutputWindowPane CreatePane(string title)
        {
            DTE2 dte = (DTE2)ServiceProvider.GetService(typeof(DTE));
            dte.ExecuteCommand("View.Output");
            OutputWindowPanes panes = dte.ToolWindows.OutputWindow.OutputWindowPanes;
            
            try
            {
                // If the pane exists already, write to it.
                return panes.Item(title);
            }
            catch (ArgumentException)
            {
                // Create a new pane and write to it.
                return panes.Add(title);
            }
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
        /// Registers custom event handler for solution events
        /// </summary>
        protected virtual void AdviseSolutionEvents()
        {
            IVsSolution solution = ServiceProvider.GetService(typeof(SVsSolution)) as IVsSolution;
            var solutionEvents = new DexterAnalysisCommandSolutionEvents(this);
            uint cookie = 0;
            solution.AdviseSolutionEvents(solutionEvents, out cookie);
        }
        
        /// <summary>
        /// Event handler for solution events.
        /// Sets the state and text of menu controls based on current project
        /// </summary>
        class DexterAnalysisCommandSolutionEvents : VsSolutionEvents
        {
            private DexterAnalysisCommand dexterCommand;

            public DexterAnalysisCommandSolutionEvents(DexterAnalysisCommand dexterCommand)
            {
                this.dexterCommand = dexterCommand;
            }
            
            public override int OnAfterOpenProject(IVsHierarchy pHierarchy, int fAdded)
            {
                Project activeProject = dexterCommand.getActiveProject();

                dexterCommand.menuItem.Text = "On " + activeProject.Name;
                dexterCommand.menuItem.Enabled = true;
                return base.OnAfterOpenProject(pHierarchy, fAdded);
            }

            public override int OnBeforeCloseProject(IVsHierarchy pHierarchy, int fRemoved)
            {
                dexterCommand.menuItem.Enabled = false;
                return base.OnBeforeCloseProject(pHierarchy, fRemoved);
            }
        }

    }


}

 
