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
        /// <summary>
        /// Gets text of SnapshotSpan
        /// </summary>
        string GetText(SnapshotSpan span);
        /// <summary>
        /// Gets the start line number of SnapshotSpan
        /// </summary>
        int GetStartLineNumber(SnapshotSpan span);
        /// <summary>
        /// Gets the end line number of SnapshotSpan
        /// </summary>
        int GetEndLineNumber(SnapshotSpan span);
        /// <summary>
        /// Convert SnapshotSpan to Span. However, the meaning of offset 0 is different.
        /// </summary>
        /// <param name="span">A snapshotSpan with a zero offset at the beginning of the document.</param>
        /// <returns>The span at which the start of the line is 0 offset</returns>
        Span GetLineSpan(SnapshotSpan snapshotSpan);
        /// <summary>
        /// Encodings text to Base64
        /// </summary>
        /// <param name="text">Text to encode</param>
        /// <returns>Base64-encoded text</returns>
        string Base64Encoding(string text);
    }

    /// <summary>
    /// Provides get/set functions for SnapshotSpan
    /// </summary>
    public class DexterTextService : IDexterTextService
    {
        public int GetEndLineNumber(SnapshotSpan span)
        {
            return span.End.GetContainingLine().LineNumber;
        }

        public Span GetLineSpan(SnapshotSpan span)
        {
            int lineOffset = span.Start.GetContainingLine().Start;

            return new Span(span.Start - lineOffset + 1, span.Length);
        }

        public int GetStartLineNumber(SnapshotSpan span)
        {
            return span.Start.GetContainingLine().LineNumber + 1;
        }

        public string GetText(SnapshotSpan span)
        {
            return span.GetText();
        }

        public string Base64Encoding(string text)
        {
            byte[] arr = Encoding.UTF8.GetBytes(text);
            return System.Convert.ToBase64String(arr);
        }
    }
}
