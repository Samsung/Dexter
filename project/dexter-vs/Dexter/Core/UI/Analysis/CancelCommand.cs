using Microsoft.VisualStudio.Shell;
using System;

namespace Dexter.UI.Analysis
{
    /// <summary>
    /// Command handler - for cancelling analysis
    /// </summary>
    internal class CancelCommand : DexterCommand
    {
        /// <summary>
        /// Analysis command to cancel
        /// </summary>
        public DexterAnalysisCommand AnalysisCommand
        {
            get;
            set;
        }
        
        public CancelCommand(Package package, int commandId, Guid commandSet) : base(package, commandId, commandSet)
        {
            Enabled = false;
            Visible = false;
        }

        /// <summary>
        /// Cancel analysis after clicking.
        /// </summary>
        /// <param name="sender">Event sender.</param>
        /// <param name="e">Event args.</param>
        protected override void CommandClicked(object sender, EventArgs e)
        {
            if (AnalysisCommand != null)
            {
                AnalysisCommand.Cancel();
            }
        }
    }
}
