using dexter_vs.Config.Providers;
using EnvDTE;
using EnvDTE80;
using Microsoft.VisualStudio;
using Microsoft.VisualStudio.Shell;
using Microsoft.VisualStudio.Shell.Interop;
using System;

namespace dexter_vs.UI.Analysis
{
    /// <summary>
    /// Command handler - for single file analysis
    /// </summary>
    internal class DexterFileAnalysisCommand : DexterAnalysisCommand
    {  

        public DexterFileAnalysisCommand(Package package, int commandId, Guid commandSet, ConfigurationProvider configurationProvider)
           : base(package, commandId, commandSet, configurationProvider)
        {
            WindowEvents events = ((Events2)Dte.Events).WindowEvents;

            events.WindowActivated += OnDocumentWindowActivated;
            events.WindowClosing += OnDocumentWindowClosed;

            menuItem.Enabled = false;
        }

        private void OnDocumentWindowActivated(Window gotFocus, Window lostFocus)
        {
            var document = gotFocus.Document;

            if (document ==null)
            {
                menuItem.Text = "On File";
                menuItem.Enabled = false;
                return;
            }

            var docName = document.Name.ToLower();
            if (docName.EndsWith("c") || docName.EndsWith("h") ||
                docName.EndsWith("cpp") || docName.EndsWith("hpp") ||
                docName.EndsWith("cxx") || docName.EndsWith("c++") ||
                docName.EndsWith("cc"))
            {
                
                menuItem.Text = "On " + document.Name;
                menuItem.Enabled = true;
            }
        }

        private void OnDocumentWindowClosed(Window window)
        {
            menuItem.Enabled = false;
        }

        /// <summary>
        /// Does nothing; we don't need to handle solution events for this class
        /// </summary>
        protected override void AdviseSolutionEvents()
        {
        }
    }
}
