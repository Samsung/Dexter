using System.Collections.Generic;
using DexterCS.Client;

namespace DexterCS
{
    public interface IAnalysisResultHandler
    {
        void HandleBeginningOfResultFile();
        void HandleEndOfResultFile();
        void PrintLogAfterAnalyze();
        void HandleAnalysisResult(List<AnalysisResult> resultList, IDexterClient client);
    }
}