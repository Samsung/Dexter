using Microsoft.VisualStudio;
using Microsoft.VisualStudio.Shell;
using Microsoft.VisualStudio.Shell.Interop;
using Microsoft.VisualStudio.TextManager.Interop;
using System;

namespace dexter_vs.UI.Tasks
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

            IVsUIShellOpenDocument openDoc = serviceProvider.GetService(typeof(IVsUIShellOpenDocument)) as IVsUIShellOpenDocument;

            if (openDoc == null)
            {
                return;
            }

            IVsWindowFrame frame;
            Microsoft.VisualStudio.OLE.Interop.IServiceProvider sp;
            IVsUIHierarchy hierarchy;
            uint itemId;
            Guid logicalView = VSConstants.LOGVIEWID_Code;

            if (ErrorHandler.Failed(openDoc.OpenDocumentViaProject(
                task.Document, ref logicalView, out sp, out hierarchy, out itemId, out frame))
                || frame == null)
            {
                return;
            }

            object docData;
            frame.GetProperty((int)__VSFPROPID.VSFPROPID_DocData, out docData);

            VsTextBuffer buffer = docData as VsTextBuffer;
            if (buffer == null)
            {
                IVsTextBufferProvider bufferProvider = docData as IVsTextBufferProvider;
                if (bufferProvider != null)
                {
                    IVsTextLines lines;
                    ErrorHandler.ThrowOnFailure(bufferProvider.GetTextBuffer(out lines));
                    buffer = lines as VsTextBuffer;

                    if (buffer == null)
                    {
                        return;
                    }
                }
            }

            IVsTextManager mgr = serviceProvider.GetService(typeof(VsTextManagerClass)) as IVsTextManager;
            if (mgr == null)
            {
                return;
            }

            mgr.NavigateToLineAndColumn(buffer, ref logicalView, task.Line, task.Column, task.Line, task.Column);
        }
    }
}
