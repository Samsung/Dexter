using System.Collections.Generic;

namespace DexterCS
{
    class DexterAnalyzerAdapter : IDexterAnalyzerListener
    {
        public void HandlePostRunStaticAnalysis(AnalysisConfig config, List<AnalysisResult> resultList)
        {
        }

        public void HandlePreRunStaticAnalysis(AnalysisConfig config)
        {
        }

        public void HandlePreSendSourceCode(AnalysisConfig config)
        {
        }
    }
}
