using Microsoft.VisualStudio.Shell;
using System;
using System.Collections.Generic;
using System.ComponentModel.Design;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace dexter_vs.UI
{
    /// <summary>
    /// Command handler - for cancelling analysis
    /// </summary>
    internal class CancelCommand
    {
        /// <summary>
        /// Analysis command to cancel
        /// </summary>
        public DexterCommand AnalysisCommand
        {
            get;
            set;
        }

        /// <summary>
        /// Value indicating whether menu item is visible or not
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
                menuItem.Enabled = value;
            }
        }

        /// <summary>
        /// Menu item associated with this command
        /// </summary>
        protected readonly OleMenuCommand menuItem;
        
        public CancelCommand(Package package, int commandId, Guid commandSet)
        {
            if (package == null)
            {
                throw new ArgumentNullException("package");
            }

            ServiceProvider = package;

            OleMenuCommandService commandService = ServiceProvider.GetService(typeof(IMenuCommandService)) as OleMenuCommandService;
            if (commandService != null)
            {
                var menuCommandID = new CommandID(commandSet, commandId);
                menuItem = new OleMenuCommand(MenuItemCallback, menuCommandID);
                commandService.AddCommand(menuItem);
                Visible = false;
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
        /// This function is the callback used to execute the command when the menu item is clicked.
        /// See the constructor to see how the menu item is associated with this function using
        /// OleMenuCommandService service and MenuCommand class.
        /// </summary>
        /// <param name="sender">Event sender.</param>
        /// <param name="e">Event args.</param>
        private void MenuItemCallback(object sender, EventArgs e)
        {
            if (AnalysisCommand != null)
            {
                AnalysisCommand.Cancel();
            }
        }
    }
}
