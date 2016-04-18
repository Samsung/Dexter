
using Microsoft.VisualStudio.Shell;
using System;
using System.ComponentModel.Design;
using System.Windows;

namespace dexter_vs.UI.Settings
{
    /// <summary>
    /// Command handler - for Dexter Settings
    /// </summary>
    internal class SettingsCommand
    {
        public SettingsCommand(Package package, int commandId, Guid commandSet)
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
                var menuItem = new OleMenuCommand(MenuItemCallback, menuCommandID);
                commandService.AddCommand(menuItem);
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
            (ServiceProvider as Package).ShowOptionPage(typeof(SettingsPage));
        }
    }
}
