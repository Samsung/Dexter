using System;
using System.Collections.Generic;
using DexterCS.Client;

namespace DexterCS
{
    public class DexterAnalyzer : IDexterHomeListener
    {
        private static String CFG_PARM_JSON_FILE = "/cfg/dexter-config-parameter.json";
        private DexterAnalyzerThread dexterAnalyzerSync = new DexterAnalyzerThread();
        private List<IDexterAnalyzerListener> listenerList = new List<IDexterAnalyzerListener>(1);
        public List<ProjectAnalysisConfiguration> ProjectAnalysisConfigurationList { get; set; }

        private DexterAnalyzer()
        {
            DexterConfig.Instance.AddDexterHomeListener(this);
            LoadProjectAnalysisConfiguration();
        }

        private void LoadProjectAnalysisConfiguration()
        {
            ProjectAnalysisConfigurationList = new List<ProjectAnalysisConfiguration>();

            string cfgFilePath = DexterConfig.Instance.DexterHome + CFG_PARM_JSON_FILE;
            DexterUtil.CreateEmptyFileIfNotExist(cfgFilePath);

            string content = DexterUtil.GetContentsFromFile(cfgFilePath);
        }

        internal void PreSendSourceCode(AnalysisConfig config)
        {
            foreach(var listener in listenerList)
            {
                listener.HandlePreSendSourceCode(config);
            }
        }

        private static DexterAnalyzer instance = null;

        internal void PostRunStaticAnalysis(AnalysisConfig config, List<AnalysisResult> resultList)
        {
            foreach(var listener in listenerList)
            {
                listener.HandlePostRunStaticAnalysis(config, resultList);
            }
        }

        private static readonly object padlock = new object();
        public static DexterAnalyzer Instance
        {
            get {
                lock (padlock)
                {
                    if (instance == null)
                    {
                        instance = new DexterAnalyzer();
                    }
                    return instance;
                }
            }
        }

        public void RunSync(AnalysisConfig analysisConfig, IDexterPluginManager pluginManager, IDexterClient client)
        {
            dexterAnalyzerSync.Analyze(analysisConfig, pluginManager, client);
        }

        public void PreRunStaticAnalysis(AnalysisConfig config)
        {
            foreach(IDexterAnalyzerListener listener in listenerList)
            {
                listener.HandlePreRunStaticAnalysis(config);
            }
        }

        public void HandleDexterHomeChanged(string oldPath, string newPath)
        {
            //TODO:
        }

        internal static List<Defect> AllDefectList(List<AnalysisResult> resultList)
        {
            List<Defect> allDefectList = new List<Defect>();
            foreach(AnalysisResult result in resultList)
            {
                allDefectList.AddRange(result.DefectList);
            }
            return allDefectList;
        }
    }
}