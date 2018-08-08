//------------------------------------------------------------------------------
// <copyright file="DexterCommandPackage.cs" company="Company">
//     Copyright (c) Company.  All rights reserved.
// </copyright>
//------------------------------------------------------------------------------

using System;
using System.Diagnostics.CodeAnalysis;
using System.Runtime.InteropServices;
using Microsoft.VisualStudio;
using Microsoft.VisualStudio.Shell;
using Microsoft.VisualStudio.Shell.Interop;
using Dexter.UI.Settings;
using Dexter.Common.Config.Providers;
using Dexter.UI.Dashboard;
using Dexter.UI.Analysis;
using Dexter.Common.Utils;
using Dexter.Common.Client;
using Dexter.PeerReview;
using Dexter.PeerReview.Utils;
using System.Collections.Generic;

namespace Dexter.UI
{
    /// <summary>
    /// This is the class that implements the package exposed by this assembly.
    /// </summary>
    /// <remarks>
    /// <para>
    /// The minimum requirement for a class to be considered a valid package for Visual Studio
    /// is to implement the IVsPackage interface and register itself with the shell.
    /// This package uses the helper classes defined inside the Managed Package Framework (MPF)
    /// to do it: it derives from the Package class that provides the implementation of the
    /// IVsPackage interface and uses the registration attributes defined in the framework to
    /// register itself and its components with the shell. These attributes tell the pkgdef creation
    /// utility what data to put into .pkgdef file.
    /// </para>
    /// <para>
    /// To get loaded into VS, the package must be referred by &lt;Asset Type="Microsoft.VisualStudio.VsPackage" ...&gt; in .vsixmanifest file.
    /// </para>
    /// </remarks>
    [PackageRegistration(UseManagedResourcesOnly = true)]
    [InstalledProductRegistration("#110", "#112", "1.0", IconResourceID = 400)] // Info on this package for Help/About
    [ProvideMenuResource("Menus.ctmenu", 1)]
    [ProvideAutoLoad(VSConstants.UICONTEXT.NoSolution_string)]
    [ProvideAutoLoad(VSConstants.UICONTEXT.SolutionExists_string)]
    [ProvideAutoLoad(VSConstants.UICONTEXT.SolutionHasMultipleProjects_string)]
    [ProvideAutoLoad(VSConstants.UICONTEXT.SolutionHasSingleProject_string)]
    [ProvideOptionPage(typeof(SettingsPage),"Dexter", "Dexter options", 0, 0, true)]
    [Guid(DexterCommandPackage.PackageGuidString)]
    [SuppressMessage("StyleCop.CSharp.DocumentationRules", "SA1650:ElementDocumentationMustBeSpelledCorrectly", Justification = "pkgdef, VS and vsixmanifest are valid VS terms")]
    public sealed class DexterCommandPackage : Package
    {
        /// <summary>
        /// DexterCommandPackage GUID string.
        /// </summary>
        public const string PackageGuidString = "0a9fa7af-84c6-4922-8734-38772fcd67b1";

        List<DexterAnalysisCommand> analysisCommands = new List<DexterAnalysisCommand>();

        SettingsCommand settingsCommand;
        DashboardCommand dashboardCommand;
        CancelCommand cancelCommand;

        SettingsCommand settingsToolbarCommand;
        DashboardCommand dashboardToolbarCommand;
        CancelCommand cancelToolbarCommand;

        DexterSolutionManager dexterSolutionManager;

        /// <summary>
        /// Initializes a new instance of the <see cref="DexterAnalysisCommand"/> class.
        /// </summary>
        public DexterCommandPackage()
        {
            // Inside this method you can place any initialization code that does not require
            // any Visual Studio service because at this point the package object is created but
            // not sited yet inside Visual Studio environment. The place to do all the other
            // initialization is the Initialize method.
        }

        #region Package Members

        /// <summary>
        /// Initialization of the package; this method is called right after the package is sited, so this is the place
        /// where you can put all the initialization code that rely on services provided by VisualStudio.
        /// </summary>
        protected override void Initialize()
        {
            IProjectInfoProvider solutionInfoProvider = new SolutionInfoProvider(this);
            IProjectInfoProvider projectInfoProvider = new ProjectInfoProvider(this);
            IProjectInfoProvider fileInfoProvider = new FileInfoProvider(this);

            IProjectInfoProvider solutionSnapshotInfoProvider = new SolutionInfoProvider(this, true);
            IProjectInfoProvider projectSnapshotInfoProvider = new ProjectInfoProvider(this, true);
            
            IDexterInfoProvider dexterInfoProvider = new SettingsStoreDexterInfoProvider(this);

            ConfigurationProvider solutionConfigProvider = new ConfigurationProvider(solutionInfoProvider, dexterInfoProvider);
            ConfigurationProvider projectConfigProvider = new ConfigurationProvider(projectInfoProvider, dexterInfoProvider);
            ConfigurationProvider fileConfigProvider = new ConfigurationProvider(fileInfoProvider, dexterInfoProvider);

            ConfigurationProvider solutionSnapshotConfigProvider = new ConfigurationProvider(solutionSnapshotInfoProvider, dexterInfoProvider);
            ConfigurationProvider projectSnapshotConfigProvider = new ConfigurationProvider(projectSnapshotInfoProvider, dexterInfoProvider);

            var commandSet = new Guid("2ed6d891-bce1-414d-8251-80a0800a831f");

            DexterAnalysisCommand fileAnalysisCommand = new DexterFileAnalysisCommand(this, 0x0102, commandSet, fileConfigProvider);
            DexterAnalysisCommand projectAnalysisCommand = new DexterSolutionAnalysisCommand(this, 0x0101, commandSet, projectConfigProvider);
            DexterAnalysisCommand solutionAnalysisCommand = new DexterSolutionAnalysisCommand(this, 0x0100, commandSet, solutionConfigProvider);
            DexterAnalysisCommand projectSnapshotCommand = new DexterSolutionAnalysisCommand(this, 0x0111, commandSet, projectSnapshotConfigProvider);
            DexterAnalysisCommand solutionSnapshotCommand = new DexterSolutionAnalysisCommand(this, 0x0110, commandSet, solutionSnapshotConfigProvider);

            settingsCommand = new SettingsCommand(this, 0x0103, commandSet);
            dashboardCommand = new DashboardCommand(this, 0x0104, commandSet, dexterInfoProvider);
            cancelCommand = new CancelCommand(this, 0x0105, commandSet);

            DexterAnalysisCommand solutionAnalysisToolbarCommand = new DexterSolutionAnalysisCommand(this, 0x0200, commandSet, solutionConfigProvider);
            settingsToolbarCommand = new SettingsCommand(this, 0x0203, commandSet);
            dashboardToolbarCommand = new DashboardCommand(this, 0x0204, commandSet, dexterInfoProvider);
            cancelToolbarCommand = new CancelCommand(this, 0x0205, commandSet);

            analysisCommands.Add(fileAnalysisCommand);
            analysisCommands.Add(projectAnalysisCommand);
            analysisCommands.Add(solutionAnalysisCommand);
            analysisCommands.Add(projectSnapshotCommand);
            analysisCommands.Add(solutionSnapshotCommand);
            analysisCommands.Add(solutionAnalysisToolbarCommand);

            foreach(DexterAnalysisCommand analysisCommand in analysisCommands)
            {
                analysisCommand.AnalysisStarted += onAnalysisStarted;
                analysisCommand.AnalysisFinished += onAnalysisFinished;
            }         

            SettingsPage settingsPage = (SettingsPage)GetDialogPage(typeof(SettingsPage));
            settingsPage.SettingsChanged += onSettingsChanged;

            PeerReviewService.Instance = new PeerReviewService(new DexterTextService());

            RegisterSolutionManager();
            CreateReviewCommentManager();

            uint cookie;
            var runningDocumentTable = (IVsRunningDocumentTable)GetGlobalService(typeof(SVsRunningDocumentTable));
            runningDocumentTable.AdviseRunningDocTableEvents(new RunningDocTableEventsHandler(fileAnalysisCommand), out cookie);

            base.Initialize();
        }

        class RunningDocTableEventsHandler : IVsRunningDocTableEvents3
        {
            DexterAnalysisCommand DexterAnalysisCommand;

            #region Methods

            public RunningDocTableEventsHandler(DexterAnalysisCommand dexterAnalysisCommand)
            {
                DexterAnalysisCommand = dexterAnalysisCommand;
            }

            public int OnAfterFirstDocumentLock(uint docCookie, uint dwRDTLockType, uint dwReadLocksRemaining, uint dwEditLocksRemaining)
            {
                return VSConstants.S_OK;
            }

            public int OnBeforeLastDocumentUnlock(uint docCookie, uint dwRDTLockType, uint dwReadLocksRemaining, uint dwEditLocksRemaining)
            {
                return VSConstants.S_OK;
            }

            public int OnAfterSave(uint docCookie)
            {
                return VSConstants.S_OK;
            }

            public int OnAfterAttributeChange(uint docCookie, uint grfAttribs)
            {
                return VSConstants.S_OK;
            }

            public int OnBeforeDocumentWindowShow(uint docCookie, int fFirstShow, IVsWindowFrame pFrame)
            {
                return VSConstants.S_OK;
            }

            public int OnAfterDocumentWindowHide(uint docCookie, IVsWindowFrame pFrame)
            {
                return VSConstants.S_OK;
            }

            public int OnAfterAttributeChangeEx(uint docCookie, uint grfAttribs, IVsHierarchy pHierOld, uint itemidOld, string pszMkDocumentOld, IVsHierarchy pHierNew, uint itemidNew, string pszMkDocumentNew)
            {
                return VSConstants.S_OK;
            }

            public int OnBeforeSave(uint docCookie)
            {
                DexterAnalysisCommand.ValidateConfigurationAndAnalyse();
                return VSConstants.S_OK;
            }

            #endregion Methods
        }

        private void CreateReviewCommentManager()
        {
            PeerReviewCommentManager.Instance = new PeerReviewCommentManager(
                new DexterFileService(), PeerReviewService.Instance, dexterSolutionManager,
                new PeerReviewTaskProviderWrapper(this), new DexterDocumentService(this));
        }

        private void RegisterSolutionManager()
        {
            dexterSolutionManager = new DexterSolutionManager(new DexterHierarchyService());

            uint eventCookie;
            var solutionService = GetService(typeof(SVsSolution)) as IVsSolution;
            solutionService.AdviseSolutionEvents(dexterSolutionManager, out eventCookie);
            dexterSolutionManager.EventCookie = eventCookie;
        }

        protected override void Dispose(bool disposing)
        {
            base.Dispose(disposing);
            UnregisterSolutionManager();
        }

        private void UnregisterSolutionManager()
        {
            var solutionService = GetService(typeof(SVsSolution)) as IVsSolution;
            // solutionService can be null; hence the ?. operator is used
            solutionService?.UnadviseSolutionEvents(dexterSolutionManager.EventCookie); 
        }

        private void onAnalysisStarted(object sender, EventArgs args)
        {
            foreach(DexterAnalysisCommand analysisCommand in analysisCommands)
            {
                analysisCommand.AutoEnabled = false;
                analysisCommand.Enabled = false;
            }

            cancelCommand.AnalysisCommand = sender as DexterAnalysisCommand;
            cancelToolbarCommand.AnalysisCommand = sender as DexterAnalysisCommand;
            cancelCommand.Visible = true;
            cancelCommand.Enabled = true;
            cancelToolbarCommand.Enabled = true;
        }

        private void onAnalysisFinished(object sender, EventArgs args)
        {
            foreach (DexterAnalysisCommand analysisCommand in analysisCommands)
            {
                analysisCommand.AutoEnabled = true;
                analysisCommand.Refresh();
            }

            cancelCommand.Visible = false;
            cancelCommand.Enabled = false;
            cancelToolbarCommand.Enabled = false;
        }
        
        private void onSettingsChanged(object sender, EventArgs args)
        {
            dashboardToolbarCommand.Refresh();
            dashboardCommand.Refresh();

            foreach (DexterAnalysisCommand analysisCommand in analysisCommands)
            {
                analysisCommand.Refresh();
            }
        }

        #endregion
    }
}
