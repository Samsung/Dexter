using Dexter.Config.Providers;
using EnvDTE;
using EnvDTE80;
using Microsoft.VisualStudio.Shell;
using System;

namespace Dexter.UI.Analysis
{
    /// <summary>
    /// Command handler - for single file analysis. 
    /// Automatically sets menu text and Enabled property depending on currently opened file.
    /// </summary>
    internal class DexterFileAnalysisCommand : DexterAnalysisCommand
    {  

        public DexterFileAnalysisCommand(Package package, int commandId, Guid commandSet, ConfigurationProvider configurationProvider)
           : base(package, commandId, commandSet, configurationProvider)
        {
            WindowEvents events = ((Events2)Dte.Events).WindowEvents;

            events.WindowActivated += OnDocumentWindowActivated;
            events.WindowClosing += OnDocumentWindowClosed;
        }

        /// <summary>
        /// Saves currently opened file, validates configuration and performs analysis
        /// </summary>
        /// <param name="sender">Event sender.</param>
        /// <param name="e">Event args.</param>
        protected override void CommandClicked(object sender, EventArgs e)
        {
            Document document = Dte.ActiveDocument;
            if (document!=null && !document.Saved)
            {
                document.Save();
            }

            base.CommandClicked(sender, e);
        }

        private void OnDocumentWindowActivated(Window gotFocus, Window lostFocus)
        {
            var document = gotFocus.Document;

            if (document ==null)
            {
                Text = "On File";
                Refresh();
                return;
            }

            var docName = document.Name.ToLower();
            if (docName.EndsWith("c") || docName.EndsWith("h") ||
                docName.EndsWith("cpp") || docName.EndsWith("hpp") ||
                docName.EndsWith("cxx") || docName.EndsWith("c++") ||
                docName.EndsWith("cc"))
            {
                
                Text = "On " + document.Name;
                Refresh();
            }
        }

        private void OnDocumentWindowClosed(Window window)
        {
            Refresh();
        }
    }
}
