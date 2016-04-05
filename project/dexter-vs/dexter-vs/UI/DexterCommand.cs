//------------------------------------------------------------------------------
// <copyright file="DexterCommand.cs" company="Company">
//     Copyright (c) Company.  All rights reserved.
// </copyright>
//------------------------------------------------------------------------------

using System;
using System.ComponentModel.Design;
using System.Globalization;
using Microsoft.VisualStudio.Shell;
using Microsoft.VisualStudio.Shell.Interop;
using dexter_vs.Analysis;
using System.Collections.Generic;
using EnvDTE80;
using EnvDTE;
using System.Diagnostics;

namespace dexter_vs.UI
{
    /// <summary>
    /// Command handler
    /// </summary>
    internal sealed class DexterCommand
    {
        /// <summary>
        /// Command ID.
        /// </summary>
        public const int CommandId = 0x0100;

        /// <summary>
        /// Command menu group (command set GUID).
        /// </summary>
        public static readonly Guid CommandSet = new Guid("2ed6d891-bce1-414d-8251-80a0800a831f");

        /// <summary>
        /// VS Package that provides this command, not null.
        /// </summary>
        private readonly Package package;

        /// <summary>
        /// Initializes a new instance of the <see cref="DexterCommand"/> class.
        /// Adds our command handlers for menu (commands must exist in the command table file)
        /// </summary>
        /// <param name="package">Owner package, not null.</param>
        private DexterCommand(Package package)
        {
            if (package == null)
            {
                throw new ArgumentNullException("package");
            }

            this.package = package;

            OleMenuCommandService commandService = this.ServiceProvider.GetService(typeof(IMenuCommandService)) as OleMenuCommandService;
            if (commandService != null)
            {
                var menuCommandID = new CommandID(CommandSet, CommandId);
                var menuItem = new MenuCommand(this.MenuItemCallback, menuCommandID);
                commandService.AddCommand(menuItem);
            }

        }

        /// <summary>
        /// Gets the instance of the command.
        /// </summary>
        public static DexterCommand Instance
        {
            get;
            private set;
        }

        /// <summary>
        /// Gets the service provider from the owner package.
        /// </summary>
        private IServiceProvider ServiceProvider
        {
            get
            {
                return this.package;
            }
        }

        /// <summary>
        /// Initializes the singleton instance of the command.
        /// </summary>
        /// <param name="package">Owner package, not null.</param>
        public static void Initialize(Package package)
        {
            Instance = new DexterCommand(package);
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
            Dexter dexter = new Dexter("D:/Applications/dexter/0.9.2/dexter-cli_0.9.2_32/bin/dexter-executor.jar");

            OutputWindowPane outputPane = CreatePane("Dexter");

            outputPane.Activate();

            DataReceivedEventHandler writeToOutputPane = (s, e1) => outputPane.OutputString(e1.Data + Environment.NewLine);  
            dexter.OutputDataReceived += writeToOutputPane;
            dexter.ErrorDataReceived += writeToOutputPane;

            List<Defect> defects = dexter.Analyse();
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
    }
}
