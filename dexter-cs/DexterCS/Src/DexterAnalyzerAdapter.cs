using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

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
