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
                throw new DexterRuntimeException("Invalid Analysis Config : fileName or sourceFileFullPath is null or empty");
            }

            if (!File.Exists(analysisConfig.SourceFileFullPath))
            {
                throw new DexterRuntimeException("Invalid Analysis Config : projectName or projectFullPath is null or empty");
            }
        }
    }
}