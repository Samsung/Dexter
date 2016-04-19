using dexter_vs.Config;
using dexter_vs.Config.Providers;
using Microsoft.VisualStudio.Shell;
using System;
using System.Collections.Generic;
using System.ComponentModel.Design;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace dexter_vs.UI.Dashboard
{
    /// <summary>
    /// Command handler - for redirecting to Dexter Dashboard 
    /// </summary>
    internal class DashboardCommand
    {
        /// <summary>
        /// Menu item associated with this command
        /// </summary>
        protected readonly OleMenuCommand menuItem;

        /// <summary>
        /// Dexter info provider
        /// </summary>
        protected readonly IDexterInfoProvider dexterInfoProvider;

        public DashboardCommand(Package package, int commandId, Guid commandSet, IDexterInfoProvider dexterInfoProvider)
        {
            if (package == null)
            {
                throw new ArgumentNullException("package");
            }
            
            this.dexterInfoProvider = dexterInfoProvider;

            OleMenuCommandService commandService = ((IServiceProvider)package).GetService(typeof(IMenuCommandService)) as OleMenuCommandService;
            if (commandService != null)
            {
                var menuCommandID = new CommandID(commandSet, commandId);
                menuItem = new OleMenuCommand(MenuItemCallback, menuCommandID);
                commandService.AddCommand(menuItem);
                Refresh();
            }
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
            var uriString = getDashboardUri();

            if (Uri.IsWellFormedUriString(uriString, UriKind.Absolute))
            {
                System.Diagnostics.Process.Start(uriString);
            };
        }

        /// <summary>
        /// Refreshes the state of menu item
        /// </summary>
        public void Refresh()
        {
            var uriString = getDashboardUri();
            menuItem.Enabled = Uri.IsWellFormedUriString(uriString, UriKind.Absolute);
        }
        
        private string getDashboardUri()
        {
            DexterInfo dexterInfo = dexterInfoProvider.Load();
            return string.Format("http://{0}:{1}/defect/", dexterInfo.dexterServerIp, dexterInfo.dexterServerPort);
        }

    }
}
