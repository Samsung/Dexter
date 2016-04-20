using dexter_vs.Config;
using dexter_vs.Config.Providers;
using Microsoft.VisualStudio.Shell;
using System;

namespace dexter_vs.UI.Dashboard
{
    /// <summary>
    /// Command handler - for redirecting to Dexter Dashboard 
    /// </summary>
    internal class DashboardCommand: DexterCommand
    {

        /// <summary>
        /// Dexter info provider
        /// </summary>
        protected readonly IDexterInfoProvider dexterInfoProvider;

        public DashboardCommand(Package package, int commandId, Guid commandSet, IDexterInfoProvider dexterInfoProvider)
            : base(package, commandId, commandSet)
        {
            this.dexterInfoProvider = dexterInfoProvider;
            Refresh();
        }

        /// <summary>
        /// Opens Dexter Dashboard in a browser 
        /// </summary>
        /// <param name="sender">Event sender.</param>
        /// <param name="e">Event args.</param>
        protected override void CommandClicked(object sender, EventArgs e)
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
