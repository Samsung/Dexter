using System.Collections.Generic;

namespace DexterCS
{
    public interface IAnalysisEntityFactory
    {
        AnalysisResult CreateAnalysisResult(AnalysisConfig config);
        AnalysisResult CreateAnalysisResultList(List<AnalysisResult> resultList);
    }
}