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
using dexter_vs.UI.Settings;
using dexter_vs.Config.Providers;
using dexter_vs.UI.Dashboard;
using dexter_vs.UI.Analysis;

namespace dexter_vs.UI
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

        DexterAnalysisCommand fileAnalysisCommand;
        DexterAnalysisCommand projectAnalysisCommand;
        DexterAnalysisCommand solutionAnalysisCommand;

        DexterAnalysisCommand projectSnapshotCommand;
        DexterAnalysisCommand solutionSnapshotCommand;

        SettingsCommand settingsCommand;
        DashboardCommand dashboardCommand;
        CancelCommand cancelCommand;

        DexterAnalysisCommand solutionAnalysisToolbarCommand;
        SettingsCommand settingsToolbarCommand;
        DashboardCommand dashboardToolbarCommand;
        CancelCommand cancelToolbarCommand;

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

            fileAnalysisCommand = new DexterFileAnalysisCommand(this, 0x0102, commandSet, fileConfigProvider);
            projectAnalysisCommand = new DexterAnalysisCommand(this, 0x0101, commandSet, projectConfigProvider);
            solutionAnalysisCommand = new DexterAnalysisCommand(this, 0x0100, commandSet, solutionConfigProvider);
            projectSnapshotCommand = new DexterAnalysisCommand(this, 0x0111, commandSet, projectSnapshotConfigProvider);
            solutionSnapshotCommand = new DexterAnalysisCommand(this, 0x0110, commandSet, solutionSnapshotConfigProvider);
            settingsCommand = new SettingsCommand(this, 0x0103, commandSet);
            dashboardCommand = new DashboardCommand(this, 0x0104, commandSet, dexterInfoProvider);
            cancelCommand = new CancelCommand(this, 0x0105, commandSet);

            solutionAnalysisToolbarCommand = new DexterAnalysisCommand(this, 0x0200, commandSet, solutionConfigProvider);
            settingsToolbarCommand = new SettingsCommand(this, 0x0203, commandSet);
            dashboardToolbarCommand = new DashboardCommand(this, 0x0204, commandSet, dexterInfoProvider);
            cancelToolbarCommand = new CancelCommand(this, 0x0205, commandSet);

            fileAnalysisCommand.AnalysisStarted += onAnalysisStarted;
            projectAnalysisCommand.AnalysisStarted += onAnalysisStarted;
            solutionAnalysisCommand.AnalysisStarted += onAnalysisStarted;
            projectSnapshotCommand.AnalysisStarted += onAnalysisStarted;
            solutionSnapshotCommand.AnalysisStarted += onAnalysisStarted;
            solutionAnalysisToolbarCommand.AnalysisStarted += onAnalysisStarted;

            fileAnalysisCommand.AnalysisFinished += onAnalysisFinished;
            projectAnalysisCommand.AnalysisFinished += onAnalysisFinished;
            solutionAnalysisCommand.AnalysisFinished += onAnalysisFinished;
            projectSnapshotCommand.AnalysisFinished += onAnalysisFinished;
            solutionSnapshotCommand.AnalysisFinished += onAnalysisFinished;
            solutionAnalysisToolbarCommand.AnalysisFinished += onAnalysisFinished;

            SettingsPage settingsPage = (SettingsPage)GetDialogPage(typeof(SettingsPage));
            settingsPage.SettingsChanged += onSettingsChanged;
                       
            base.Initialize();
        }

        private void onAnalysisStarted(object sender, EventArgs args)
        {
            fileAnalysisCommand.Enabled = false;
            projectAnalysisCommand.Enabled = false;
            solutionAnalysisCommand.Enabled = false;
            projectSnapshotCommand.Enabled = false;
            solutionSnapshotCommand.Enabled = false;
            solutionAnalysisToolbarCommand.Enabled = false;

            cancelCommand.AnalysisCommand = sender as DexterAnalysisCommand;
            cancelToolbarCommand.AnalysisCommand = sender as DexterAnalysisCommand;
            cancelCommand.Visible = true;
            cancelToolbarCommand.Enabled = true;
        }

        private void onAnalysisFinished(object sender, EventArgs args)
        {
            fileAnalysisCommand.Enabled = true;
            projectAnalysisCommand.Enabled = true;
            solutionAnalysisCommand.Enabled = true;
            projectSnapshotCommand.Enabled = true;
            solutionSnapshotCommand.Enabled = true;
            solutionAnalysisToolbarCommand.Enabled = true;
            cancelCommand.Visible = false;
            cancelToolbarCommand.Enabled = false;
        }
        
        private void onSettingsChanged(object sender, EventArgs args)
        {
            dashboardToolbarCommand.Refresh();
            dashboardCommand.Refresh();
        }       

        #endregion
    }
}
