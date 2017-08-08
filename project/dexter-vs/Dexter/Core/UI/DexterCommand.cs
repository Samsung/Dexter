using EnvDTE;
using Microsoft.VisualStudio.Shell;
using System;
using System.ComponentModel.Design;

namespace Dexter.UI
{
    /// <summary>
    /// Base class for Dexter commands  
    /// Prvides access to menu item associated with command
    /// </summary>
    internal abstract class DexterCommand
    {
        /// <summary>
        /// Gets/sets the value indicating whether menu item associated with ths command is enabled  or not.
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
        /// Gets/sets the value indicating whether menu item associated with ths command is visible or not
        /// </summary>
        public bool Visible
        {
            get
            {
                return menuItem.Visible;
            }
            set
            {
                menuItem.Visible = value;
            }
        }
        
        /// <summary>
        /// Gets/sets the text for the command
        /// </summary>
        public string Text
        {
            get
            {
                return menuItem.Text;
            }
            set
            {
                menuItem.Text = value;
            }
        }

        /// <summary>
        /// Gets the service provider from the owner package.
        /// </summary>
        protected IServiceProvider ServiceProvider
        {
            get;
            private set;
        }

        /// <summary>
        /// DTE object
        /// </summary>
        protected DTE Dte
        {
            get;
            private set;
        }

        /// <summary>
        /// MenuItem associated with this command
        /// </summary>
        private readonly OleMenuCommand menuItem;

        /// <summary>
        /// Initializes a new instance of the <see cref="DexterAnalysisCommand"/> class.
        /// Adds our command handlers for menu (commands must exist in the command table file)
        /// </summary>
        /// <param name="package">Owner package, not null.</param>
        /// <param name="commandId">Command ID.</param>
        /// <param name="commandSet">Command menu group (command set GUID).</param>
        public DexterCommand(Package package, int commandId, Guid commandSet)
        {
            if (package == null)
            {
                throw new ArgumentNullException("package");
            }

            ServiceProvider = package;

            Dte = (DTE)ServiceProvider.GetService(typeof(DTE));
         
            OleMenuCommandService commandService = ServiceProvider.GetService(typeof(IMenuCommandService)) as OleMenuCommandService;
            if (commandService != null)
            {
                var menuCommandID = new CommandID(commandSet, commandId);
                menuItem = new OleMenuCommand(CommandClicked, menuCommandID);
                commandService.AddCommand(menuItem);
            }
        }

        /// <summary>
        /// This function is the callback used to execute the command when the menu item is clicked.
        /// Subclasses should implement it to provide command logic
        /// </summary>
        /// <param name="sender">Sender object</param>
        /// <param name="e">Event arguments</param>
        protected abstract void CommandClicked(object sender, EventArgs e);
    }
}
