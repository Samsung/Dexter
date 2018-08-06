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
using DexterCS.Client;
using log4net;
using System;
using System.Collections.Generic;
using System.IO;

namespace DexterCS
{
    public sealed class DexterAnalyzerThread
    {
        private static ILog CliLog = LogManager.GetLogger(typeof(DexterAnalyzerThread));

        private AnalysisConfig config;
        private IDexterPluginManager pluginManager;
        private IDexterClient client;

        public void SetFields(AnalysisConfig config, IDexterPluginManager pluginManager, IDexterClient client)
        {
            this.config = config;
            this.pluginManager = pluginManager;
            this.client = client;
        }

        public void Analyze(AnalysisConfig analysisConfig, IDexterPluginManager pluginManager, IDexterClient client)
        {
            try
            {
                DexterAnalyzer analyzer = DexterAnalyzer.Instance;
                CheckAnalysisConfig(analysisConfig);
                //analysisConfig.AddHeaderAndSourceConfiguration(analyzer.ProjectAnalysisConfigurationList);

                analyzer.PreSendSourceCode(analysisConfig);

                analyzer.PreRunStaticAnalysis(analysisConfig);
                List<AnalysisResult> resultList = RunStaticAnalysis(analysisConfig, pluginManager, client);
                analyzer.PostRunStaticAnalysis(analysisConfig, resultList);

            }
            catch (Exception e)
            {
                CliLog.Error(e.StackTrace);
            }
        }

        public void SendSourceCode(AnalysisConfig config, IDexterClient client)
        {
            try
            {
                client.StoreSourceCodeCharSequence(config.SnapshotId, config.GroupId,
                    config.ModulePath, config.FileName, config.SourcecodeFromFile).Wait();
            }
            catch (Exception e)
            {
                CliLog.Error(e.StackTrace);
            }
        }

        private List<AnalysisResult> RunStaticAnalysis(AnalysisConfig analysisConfig, IDexterPluginManager pluginManager, IDexterClient client)
        {
            List<AnalysisResult> resultList = pluginManager.Analyze(analysisConfig);
            AnalysisResultFileManager.Instance.WriteJson(resultList);
            analysisConfig.ResultHandler.HandleAnalysisResult(resultList, client);
            SendSourceCode(analysisConfig, client);

            return resultList;
        }

        private void CheckAnalysisConfig(AnalysisConfig analysisConfig)
        {
            if (string.IsNullOrEmpty(analysisConfig.SourceFileFullPath) || string.IsNullOrEmpty(analysisConfig.FileName))
            {
                throw new DexterRuntimeException("Invalid Analysis Config: SourceFileFullPath or FileName is null or empty");
            }

            if (!File.Exists(analysisConfig.SourceFileFullPath))
            {
                throw new DexterRuntimeException("Invalid Analysis Config: SourceFileFullPath does not exist: " + analysisConfig.SourceFileFullPath);
            }
        }
    }
}