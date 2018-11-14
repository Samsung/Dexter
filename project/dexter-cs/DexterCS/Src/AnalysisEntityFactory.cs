
using System.Collections.Generic;

namespace DexterCS
{
    public class AnalysisEntityFactory : IAnalysisEntityFactory
    {
        public AnalysisConfig CreateAnalysisConfig()
        {
            return new AnalysisConfig();
        }

        public AnalysisConfig CopyAnalysisConfigWithoutSourcecode(AnalysisConfig baseAnalysisConfig)
        {
            return new AnalysisConfig(baseAnalysisConfig);
        }

        public AnalysisResult CreateAnalysisResult(AnalysisConfig config)
        {
            AnalysisResult result = new AnalysisResult();
            result.FileName = config.FileName;
            result.ModulePath = config.ModulePath;
            result.ProjectName = config.ProjectName;
            result.ProjectFullPath = config.ProjectFullPath;
            result.SnapshotId = config.SnapshotId;
            result.SourceFileFullPath = config.SourceFileFullPath;
            result.ResultFileFullPath = config.ResultFileFullPath;
            result.AnalysisType = config.AnalysisConfigType;
            return result;
        }
        public AnalysisResult CreateAnalysisResultList(List<AnalysisResult> resultList)
        {
            AnalysisResult result = new AnalysisResult();
            AnalysisResult baseResult = resultList[0];

            result.FileName = baseResult.FileName;
            result.ModulePath = baseResult.ModulePath;
            result.ProjectFullPath = baseResult.ProjectFullPath;
            result.ProjectName = baseResult.ProjectName;
            result.SnapshotId = baseResult.SnapshotId;
            result.SourceFileFullPath = baseResult.SourceFileFullPath;
            result.ResultFileFullPath = baseResult.ResultFileFullPath;

            foreach (var temp in resultList)
            {
                result.DefectList.AddRange(temp.DefectList);
            }
            return result;
        }

    }
}