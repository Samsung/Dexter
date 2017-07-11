using Dexter.Common.Utils;
using EnvDTE;
using Microsoft.VisualStudio;
using Microsoft.VisualStudio.Shell;
using Microsoft.VisualStudio.Shell.Interop;
using Microsoft.VisualStudio.TextManager.Interop;
using System;

namespace Dexter.UI.Tasks
{
    /// <summary>
    /// Event handler used to navigate to the task occurence in code editor
    /// </summary>
    internal sealed class NavigateTaskEventHandler
    {

        /// <summary>
        /// Service provider
        /// </summary>
        private readonly IServiceProvider serviceProvider;

        /// <summary>
        /// Creates new NavigateTaskEventHandler
        /// </summary>
        /// <param name="serviceProvider">Service provider</param>
        public NavigateTaskEventHandler(IServiceProvider serviceProvider)
        {
            this.serviceProvider = serviceProvider;
        }

        /// <summary>
        /// Navigates to defect occurence in code editor
        /// </summary>
        /// <param name="sender">Sender</param>
        /// <param name="arguments">Event arguments</param>
        public void Navigate(object sender, EventArgs arguments)
        {
            Task task = sender as Task;

            if (task == null)
            {
                throw new ArgumentException("sender parm cannot be null");
            }

            if (string.IsNullOrEmpty(task.Document))
            {
                return;
            }

            var dte = (DTE)serviceProvider.GetService(typeof(DTE));
            var activeDocument = dte.ActiveDocument;

            if (activeDocument == null || !PathUtils.AreEquals(activeDocument.FullName,task.Document))
            {
                VsShellUtilities.OpenDocument(serviceProvider, task.Document);
                activeDocument = dte.ActiveDocument;
            }

            TextSelection textSelection = activeDocument.Selection as TextSelection;
            textSelection.MoveToLineAndOffset(task.Line + 1, 1);
        }
    }
}
