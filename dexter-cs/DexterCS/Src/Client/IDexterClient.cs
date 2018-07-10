using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DexterCS.Client
{
    public interface IDexterClient
    {
        int ServerPort { get; set; }
        string ServerHost { get; set; }
        string DexterWebUrl { get; set; }
        string SourceCode(string modulePath, string fileName);
        Task SendAnalysisResult(string result);
        Task StoreSourceCodeCharSequence(long snapshotId, long groupId, string modulePath, string fileName, string sourcecode);
        void InitHttpClient(string serverUrl);
    }
}
