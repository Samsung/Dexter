using DexterCS.Client;
using log4net;
using System;
using System.Collections.Generic;

namespace DexterCS
{
    public class BaseDexterPluginManager : IDexterPluginManager
    {
        private static ILog CliLog = LogManager.GetLogger(typeof(BaseDexterPluginManager));

        protected List<IDexterPlugin> pluginList = new List<IDexterPlugin>(0);
        protected IDexterPluginInitializer initializer;
        protected IDexterClient client;

        public BaseDexterPluginManager(IDexterPluginInitializer initializer, IDexterClient client)
        {
            this.client = client;
            this.initializer = initializer;
        }

        public void InitDexterPlugins()
        {
            initializer.Init(pluginList);
            InitSupportingFileExtensions();
            CliLog.Info("Dexter plug-ins initialized successfully!");
        }

        private void InitSupportingFileExtensions()
        {
            DexterConfig.Instance.RemoveAllSupportingFileExtensions();
            foreach (IDexterPlugin plugin in pluginList)
            {
                DexterConfig.Instance.AddSupprotingFileExtensions(plugin.SupportingFileExtensions);
            }
        }

        public List<AnalysisResult> Analyze(AnalysisConfig config)
        {
            List<AnalysisResult> resultList = new List<AnalysisResult>();

            foreach (IDexterPlugin plugin in pluginList)
            {
                if (plugin.SupportLanguage(config.GetLanguageEnum()))
                {
                    try
                    {
                        var result = plugin.Analyze(config);
                        resultList.Add(result);
                    }
                    catch (Exception e)
                    {
                        CliLog.Error("Analysis Exception:" + config.SourceFileFullPath + "\n" + e.Message);
                    }
                }
            }
            return resultList;
        }
    }
}