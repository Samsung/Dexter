using dexter_vs.Config.Providers;
using EnvDTE;
using EnvDTE80;
using Microsoft.VisualStudio.Shell;
using System;

namespace dexter_vs.UI.Analysis
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
