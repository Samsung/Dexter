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
        string GetText(SnapshotSpan span);
        int GetStartLineNumber(SnapshotSpan span);
        int GetEndLineNumber(SnapshotSpan span);
        Span GetLineSpan(SnapshotSpan snapshotSpan);
    }

    /// <summary>
    /// Provides get/set functions for SnapshotSpan
    /// </summary>
    public class DexterTextService : IDexterTextService
    {
        /// <summary>
        /// Gets the end line number of SnapshotSpan
        /// </summary>
        public int GetEndLineNumber(SnapshotSpan span)
        {
            return span.End.GetContainingLine().LineNumber;
        }

        public Span GetLineSpan(SnapshotSpan span)
        {
            int lineOffset = span.Start.GetContainingLine().Start;

            return new Span(span.Start - lineOffset + 1, span.Length);
        }

        /// <summary>
        /// Gets the start line number of SnapshotSpan
        /// </summary>
        public int GetStartLineNumber(SnapshotSpan span)
        {
            return span.Start.GetContainingLine().LineNumber;
        }

        /// <summary>
        /// Gets text of SnapshotSpan
        /// </summary>
        public string GetText(SnapshotSpan span)
        {
            return span.GetText();
        }
    }
}
