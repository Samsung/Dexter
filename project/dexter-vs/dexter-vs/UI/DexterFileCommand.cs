using dexter_vs.UI.Config;
using EnvDTE;
using EnvDTE80;
using Microsoft.VisualStudio;
using Microsoft.VisualStudio.Shell;
using Microsoft.VisualStudio.Shell.Interop;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace dexter_vs.UI
{
    /// <summary>
    /// Command handler - for single file analysis
    /// </summary>
    internal class DexterFileCommand : DexterCommand
    {
        DocumentEvents events;

        public DexterFileCommand(Package package, ConfigurationProvider configurationProvider, int commandId, Guid commandSet)
           : base(package, configurationProvider, commandId, commandSet)
        {
            DTE dte = (DTE)ServiceProvider.GetService(typeof(DTE));
            events = ((Events2)dte.Events).DocumentEvents;

            events.DocumentOpened += OnDocumentOpened;
            events.DocumentClosing += OnDocumentClosed;

            menuItem.Enabled = false;
        }

        private void OnDocumentOpened(Document document)
        {
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

        private void OnDocumentClosed(Document document)
        {
            menuItem.Enabled = false;
        }

        public override int OnAfterOpenProject(IVsHierarchy pHierarchy, int fAdded)
        {
            return VSConstants.S_OK;
        }

        public override int OnBeforeCloseProject(IVsHierarchy pHierarchy, int fRemoved)
        {
            return VSConstants.S_OK;
        }
    }
}
