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
using Configuration = dexter_vs.Config.Configuration;
using dexter_vs.Config.Providers;

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

        /// <summary>
        /// Initializes a new instance of the <see cref="DexterCommand"/> class.
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
            IDexterInfoProvider dexterInfoProvider = new SettingsStoreDexterInfoProvider(this);

            ConfigurationProvider solutionConfigProvider = new ConfigurationProvider(solutionInfoProvider, dexterInfoProvider);
            ConfigurationProvider projectConfigProvider = new ConfigurationProvider(projectInfoProvider, dexterInfoProvider);
            ConfigurationProvider fileConfigProvider = new ConfigurationProvider(fileInfoProvider, dexterInfoProvider);

            DexterCommand fileAnalysisCommand = new DexterFileCommand(this, fileConfigProvider, 0x0102, new Guid("2ed6d891-bce1-414d-8251-80a0800a831f"));
            DexterCommand projectAnalysisCommand = new DexterCommand(this, projectConfigProvider, 0x0101, new Guid("2ed6d891-bce1-414d-8251-80a0800a831f"));
            DexterCommand solutionAnalysisCommand = new DexterCommand(this, solutionConfigProvider, 0x0100, new Guid("2ed6d891-bce1-414d-8251-80a0800a831f"));
            SettingsCommand settingsCommand = new SettingsCommand(this, 0x0103, new Guid("2ed6d891-bce1-414d-8251-80a0800a831f"));

            DexterCommand solutionAnalysisToolbarCommand = new DexterCommand(this, solutionConfigProvider, 0x0200, new Guid("2ed6d891-bce1-414d-8251-80a0800a831f"));
            SettingsCommand settingsToolbarCommand = new SettingsCommand(this, 0x0203, new Guid("2ed6d891-bce1-414d-8251-80a0800a831f"));

            base.Initialize();
        }

        #endregion
    }
}
