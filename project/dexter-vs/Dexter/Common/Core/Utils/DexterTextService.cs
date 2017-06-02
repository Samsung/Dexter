using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.VisualStudio.Text;

namespace Dexter.Common.Utils
{
    /// <summary>
    /// Provides get/set functions for SnapshotSpan
    /// </summary>
    public interface IDexterTextService
    {
        string getText(SnapshotSpan span);
        int getStartLineNumber(SnapshotSpan span);
        int getEndLineNumber(SnapshotSpan span);
    }

    /// <summary>
    /// Provides get/set functions for SnapshotSpan
    /// </summary>
    public class DexterTextService : IDexterTextService
    {
        /// <summary>
        /// Gets the end line number of SnapshotSpan
        /// </summary>
        public int getEndLineNumber(SnapshotSpan span)
        {
            return span.End.GetContainingLine().LineNumber;
        }

        /// <summary>
        /// Gets the start line number of SnapshotSpan
        /// </summary>
        public int getStartLineNumber(SnapshotSpan span)
        {
            return span.Start.GetContainingLine().LineNumber;
        }

        /// <summary>
        /// Gets text of SnapshotSpan
        /// </summary>
        public string getText(SnapshotSpan span)
        {
            return span.GetText();
        }
    }
}
