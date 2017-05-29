using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.VisualStudio.Text;

namespace Dexter.Common.Utils
{
    public interface IDexterTextService
    {
        string getText(SnapshotSpan span);
        int getStartLineNumber(SnapshotSpan span);
        int getEndLineNumber(SnapshotSpan span);
    }

    public class DexterTextService : IDexterTextService
    {
        public int getEndLineNumber(SnapshotSpan span)
        {
            return span.End.GetContainingLine().LineNumber;
        }

        public int getStartLineNumber(SnapshotSpan span)
        {
            return span.Start.GetContainingLine().LineNumber;
        }

        public string getText(SnapshotSpan span)
        {
            return span.GetText();
        }
    }
}
