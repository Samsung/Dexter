using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.VisualStudio.Shell;
using EnvDTE;

namespace Dexter.Common.Utils
{
    /// <summary>
    /// Provides the manipulation functions for the VS editor's document.
    /// </summary>
    public interface IDexterDocumentService
    {
        /// <summary>
        /// Opens a document specified in the path in the VS editor
        /// </summary>
        /// <param name="filePath">The path of the document should be opened</param>
        void OpenDocument(string filePath);
        /// <summary>
        /// Moves the active point to the given position
        /// </summary>
        /// <param name="Line">line number</param>
        /// <param name="Offset"></param>
        /// <param name="Extend"></param>
        void MoveActivePoint(int Line, int Offset, bool Extend = false);
    }

    public class DexterDocumentService : IDexterDocumentService
    {
        private readonly IServiceProvider serviceProvider;
        private readonly DTE dte;

        public DexterDocumentService(IServiceProvider serviceProvider)
        {
            this.serviceProvider = serviceProvider;
            dte = (DTE)serviceProvider.GetService(typeof(DTE));
        }

        public void MoveActivePoint(int Line, int Offset, bool Extend = false)
        {
            var activeDocument = dte.ActiveDocument;

            TextSelection textSelection = activeDocument.Selection as TextSelection;
            textSelection.MoveToLineAndOffset(Line+1, Offset, Extend);
        }

        public void OpenDocument(string filePath)
        {
            VsShellUtilities.OpenDocument(serviceProvider, filePath);
        }
    }
}
