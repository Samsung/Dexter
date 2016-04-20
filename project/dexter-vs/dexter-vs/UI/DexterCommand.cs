//------------------------------------------------------------------------------
// <copyright file="DexterCommand.cs" company="Company">
//     Copyright (c) Company.  All rights reserved.
// </copyright>
//------------------------------------------------------------------------------

using System;
using System.ComponentModel.Design;
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

namespace dexter_vs.UI
{
    /// <summary>
    /// Command handler
    /// </summary>
    internal class DexterCommand : VsSolutionEvents
    {
        /// <summary>
        /// Menu item associated with this command
        /// </summary>
        protected readonly OleMenuCommand menuItem;

        /// <summary>
        /// Dexter task provider
        /// </summary>
        private DexterTaskProvider taskProvider;
        
        /// <summary>
        /// DTE object
        /// </summary>
        private readonly DTE dte;

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
        /// Initializes a new instance of the <see cref="DexterCommand"/> class.
        /// Adds our command handlers for menu (commands must exist in the command table file)
        /// </summary>
        /// <param name="package">Owner package, not null.</param>
        /// <param name="commandId">Command ID.</param>
        /// <param name="commandSet">Command menu group (command set GUID).</param>
        public DexterCommand(Package package, ConfigurationProvider configurationProvider, int commandId, Guid commandSet)
        {
            if (package == null)
            {
                throw new ArgumentNullException("package");
            }

            ServiceProvider = package;
            ConfigurationProvider = configurationProvider;

            dte = (DTE)ServiceProvider.GetService(typeof(DTE));
            validator = new DexterInfoValidator();

            OleMenuCommandService commandService = ServiceProvider.GetService(typeof(IMenuCommandService)) as OleMenuCommandService;
            if (commandService != null)
            {
                var menuCommandID = new CommandID(commandSet, commandId);
                menuItem = new OleMenuCommand(MenuItemCallback, menuCommandID);
                menuItem.Enabled = dte.Solution.Projects.Count > 0;
                commandService.AddCommand(menuItem);
            }

            IVsSolution solution = ServiceProvider.GetService(typeof(SVsSolution)) as IVsSolution;
            uint cookie = 0;
            solution.AdviseSolutionEvents(this, out cookie);
        }

        /// <summary>
        /// Gets/sets the value indicating whether menu item associated with ths command is enabled
        /// </summary>
        public bool Enabled
        {
            get
            {
                return menuItem.Enabled;
            }
            set
            {
                menuItem.Enabled = value;
            }
        }

        /// <summary>
        /// Gets the service provider from the owner package.
        /// </summary>
        protected IServiceProvider ServiceProvider
        {
            get; 
        }

        /// <summary>
        /// Configuration provider
        /// </summary>
        protected IConfigurationProvider ConfigurationProvider
        {
            get; 
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
        /// This function is the callback used to execute the command when the menu item is clicked.
        /// See the constructor to see how the menu item is associated with this function using
        /// OleMenuCommandService service and MenuCommand class.
        /// </summary>
        /// <param name="sender">Event sender.</param>
        /// <param name="e">Event args.</param>
        private void MenuItemCallback(object sender, EventArgs e)
        {
            Configuration config = ConfigurationProvider.Load();
            DexterInfo dexterInfo = DexterInfo.fromConfiguration(config);
            string validationResult;
             
            if (!validator.ValidateDexterPath(dexterInfo))
            {
                MessageBox.Show("Dexter wasn't found in given path. You cannot perform analysis until you set a proper path.", "Dexter error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }
            if (!config.standalone && !validator.ValidateServerConnection(dexterInfo, out validationResult))
            {
               DialogResult result =  MessageBox.Show("Couldn't connect to Dexter server. Please check server address in Dexter/Settings window. Continue in standalone mode?", "Dexter warning", MessageBoxButtons.OKCancel, MessageBoxIcon.Warning);
                if (result == DialogResult.OK)
                {
                    config.standalone = true;
                }
                else
                {
                    return;
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
                    }
                    else
                    {
                        return;
                    }
                }
            }
            
            Dexter dexter = new Dexter(config);

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
            Array projects = (Array)dte.ActiveSolutionProjects;
            if (projects != null && projects.Length > 0)
            {
                return projects.GetValue(0) as Project;
            }
            projects = (Array)dte.Solution.SolutionBuild.StartupProjects;
            if (projects != null && projects.Length >= 1)
            {
                return projects.GetValue(0) as Project;
            }

            Projects projs = dte.Solution.Projects;
            if (projs != null && projs.Count > 0)
            {
                return projs.Item(1);
            }
            return null;
        }

        public override int OnAfterOpenProject(IVsHierarchy pHierarchy, int fAdded)
        {
            Project activeProject = getActiveProject();

            menuItem.Text = "On " + activeProject.Name;
            menuItem.Enabled = true;
            return base.OnAfterOpenProject(pHierarchy, fAdded);
        }
        
        public override int OnBeforeCloseProject(IVsHierarchy pHierarchy, int fRemoved)
        {
            menuItem.Enabled = false;
            return base.OnBeforeCloseProject(pHierarchy, fRemoved);
        }
    }
}
