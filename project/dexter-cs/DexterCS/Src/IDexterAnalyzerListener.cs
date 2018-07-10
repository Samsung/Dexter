using System.Collections.Generic;

namespace DexterCS
{
    internal interface IDexterAnalyzerListener
    {
        void HandlePreRunStaticAnalysis(AnalysisConfig config);
        void HandlePostRunStaticAnalysis(AnalysisConfig config, List<AnalysisResult> resultList);
        void HandlePreSendSourceCode(AnalysisConfig config);
    }
}