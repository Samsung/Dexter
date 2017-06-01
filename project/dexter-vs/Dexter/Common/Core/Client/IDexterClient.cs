using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Dexter.Common.Defect;

namespace Dexter.Common.Client
{
    public interface IDexterClient
    {
        bool IsStandAloneMode();
        string SourceCode(string modulePath, string fileName);
        Task SendAnalysisResult(string result);
        Task SendAnalysisResult(DexterResult result);
        Task StoreSourceCodeCharSequence(long snapshotId, long groupId, string modulePath, string fileName, string sourcecode);
    }
}
