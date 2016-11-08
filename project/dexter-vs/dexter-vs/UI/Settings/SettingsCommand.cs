
using Microsoft.VisualStudio.Shell;
using System;

namespace dexter_vs.UI.Settings
{
    /// <summary>
    /// Command handler - for opening Dexter Settings
    /// </summary>
    internal class SettingsCommand : DexterCommand 
    {
        public SettingsCommand(Package package, int commandId, Guid commandSet) 
            : base(package, commandId, commandSet)
        {
        }
          
        /// <summary>
        /// Opens Settings Page on Tools/Options...
        /// </summary>
        /// <param name="sender">Event sender.</param>
        /// <param name="e">Event args.</param>
        protected override void CommandClicked(object sender, EventArgs e)
        {
            (ServiceProvider as Package).ShowOptionPage(typeof(SettingsPage));
        }
    }
}
