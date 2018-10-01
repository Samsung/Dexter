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
                DexterConfig.Instance.AddSupportedFileExtensions(plugin.SupportingFileExtensions);
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