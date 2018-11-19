#region Copyright notice
/**
 * Copyright (c) 2018 Samsung Electronics, Inc.,
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
#endregion

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
