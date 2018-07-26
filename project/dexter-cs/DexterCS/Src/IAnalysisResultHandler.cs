using DexterCS.Client;
using System.Collections.Generic;

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