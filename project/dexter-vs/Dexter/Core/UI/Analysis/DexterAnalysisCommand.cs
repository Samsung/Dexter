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
using Dexter.UI.Tasks;
using Dexter.Config;
using Dexter.Defects;
using Dexter.Analysis;
using Configuration = Dexter.Config.Configuration;
using Dexter.Config.Validation;
using Dexter.Config.Providers;

namespace Dexter.UI.Analysis
{
    /// <summary>
    /// Command handler - for analysis command
    /// </summary>
    internal class DexterAnalysisCommand : DexterCommand, IDisposable 
    {
        /// <summary>
        /// Dexter task provider
        /// </summary>
        private DexterTaskProvider taskProvider;
        
        /// <summary>
        /// DexterInfo validator
        /// </summary>
        private readonly DexterInfoValidator dexterInfoValidator;

        /// <summary>
        /// ProjectInfo validator
        /// </summary>
        private readonly ProjectInfoValidator projectInfoValidator;

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
        private DexterLegacyAnalyzer dexter;

        /// <summary>
        /// Determines whether command item should automatically change Enabled state 
        /// when project is open/closed
        /// </summary>
        public bool AutoEnabled
        {
            get;
            set;
        }

        /// <summary>
        /// Configuration provider
        /// </summary>
        protected IConfigurationProvider ConfigurationProvider
        {
            get;
            private set;
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
            AutoEnabled = true;
            dexterInfoValidator = new DexterInfoValidator();
            projectInfoValidator = new ProjectInfoValidator();
            ConfigurationProvider = configurationProvider;
            Refresh();
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
        /// Validates configuration and performs analysis
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

            OutputWindowPane outputPane = CreatePane("Dexter");
            outputPane.Clear();
            outputPane.Activate();

            System.Threading.Tasks.Task.Run(() =>
            {
                dexter = new DexterLegacyAnalyzer(config);
                DataReceivedEventHandler writeToOutputPane = (s, e1) => outputPane.OutputString(e1.Data + Environment.NewLine);
                dexter.OutputDataReceived += writeToOutputPane;
                dexter.ErrorDataReceived += writeToOutputPane;
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
        /// Refreshes the state of menu item.
        /// Works only, if AutoEnabled is true
        /// </summary>
        public void Refresh()
        {
            if (AutoEnabled)
            {
                DexterInfo dexterInfo = ConfigurationProvider.LoadDexterInfo();
                ProjectInfo projectInfo = ConfigurationProvider.LoadProjectInfo();

                Enabled = dexterInfoValidator.ValidateDexterPath(dexterInfo) && projectInfoValidator.ValidateProjectPath(projectInfo);
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

            if (!dexterInfoValidator.ValidateDexterPath(dexterInfo))
            {
                MessageBox.Show("Dexter wasn't found in given path. You cannot perform analysis until you set a proper path.", "Dexter error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return false;
            }
            if (!config.standalone && !dexterInfoValidator.ValidateServerConnection(dexterInfo, out validationResult))
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
                if (!config.standalone && !dexterInfoValidator.ValidateUserCredentials(dexterInfo, out validationResult))
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

        /// <summary>
        /// Disposes this command. 
        /// </summary>
        public void Dispose()
        {
            if (taskProvider!=null)
            {
                taskProvider.Dispose();
            }
        }
    }


}

 
