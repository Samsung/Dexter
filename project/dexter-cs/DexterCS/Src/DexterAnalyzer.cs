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
using System;
using System.Collections.Generic;

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
            foreach (var listener in listenerList)
            {
                listener.HandlePreSendSourceCode(config);
            }
        }

        private static DexterAnalyzer instance = null;

        internal void PostRunStaticAnalysis(AnalysisConfig config, List<AnalysisResult> resultList)
        {
            foreach (var listener in listenerList)
            {
                listener.HandlePostRunStaticAnalysis(config, resultList);
            }
        }

        private static readonly object padlock = new object();
        public static DexterAnalyzer Instance
        {
            get
            {
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
            foreach (IDexterAnalyzerListener listener in listenerList)
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
            foreach (AnalysisResult result in resultList)
            {
                allDefectList.AddRange(result.DefectList);
            }
            return allDefectList;
        }
    }
}