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
using DexterCS;
using log4net;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp;
using Newtonsoft.Json;
using System;
using System.IO;
using System.Reflection;
using System.Text;

namespace DexterDepend
{
    public class DexterDependPlugin : IDexterPlugin
    {
        private const string DEFAULT_CHECKER_CONFIG_PATH = ".Resource.checker-config.json";
        private static ILog CliLog = LogManager.GetLogger(typeof(DexterDependPlugin));

        static Assembly assem = Assembly.GetExecutingAssembly();
        static AssemblyName assemName = assem.GetName();
        static Version version = assemName.Version;
        private static string pluginName = assemName.Name;

        private BaseCheckerConfig baseCheckerConfig;
        private ICheckerConfig checkerConfig = new CheckerConfig(pluginName, DexterConfig.LANGUAGE.C_SHARP);
        public ICheckerConfig CheckerConfig
        {
            get
            {
                return checkerConfig;
            }
            set
            {
                checkerConfig = value;
            }
        }

        public string PLUGIN_NAME
        {
            get
            {
                return pluginName;
            }
        }

        public string PLUGIN_DESCRIPTION
        {
            get { return "Dexter CSharp App Dependency Plug-in"; }
        }

        public string PLUGIN_AUTHOR
        {
            get { return "Samsung Electronics"; }
        }

        public Version VERSION
        {
            get { return version; }
        }

        public string[] SupportingFileExtensions
        {
            get { return new String[] { "cs" }; }
        }

        private string GetFileContents(string fileName)
        {
            string fileContent;
            var fileStream = new FileStream(fileName, FileMode.Open, FileAccess.Read);
            using (var streamReader = new StreamReader(fileStream, Encoding.UTF8))
            {
                fileContent = streamReader.ReadToEnd();
            }
            return fileContent;
        }

        public AnalysisResult Analyze(AnalysisConfig config)
        {
            IAnalysisEntityFactory factory = new AnalysisEntityFactory();
            AnalysisResult result = factory.CreateAnalysisResult(config);

            var tree = CSharpSyntaxTree.ParseText(GetFileContents(config.SourceFileFullPath));
            DexterDependUtil.ToolName = checkerConfig.ToolName;
            SyntaxNode syntaxRoot = tree.GetRoot();

            foreach (Checker checker in CheckerConfig.CheckerList)
            {
                if (!checker.IsActive)
                {
                    continue;
                }
                IDependLogic logic = GetCheckerLogic(checker.Code);
                logic.Analyze(config, result, checker, syntaxRoot);
            }
            return result;
        }

        private IDependLogic GetCheckerLogic(string code)
        {
            return (IDependLogic)Activator.CreateInstance(Type.GetType(PLUGIN_NAME + "." + code, true));
        }

        public void Dispose()
        {
            //TODO:
        }

        public void Init()
        {
            InitCheckerConfig();
            MakeCheckerConfig();
        }

        private void MakeCheckerConfig()
        {
            CheckerConfig.ToolName = baseCheckerConfig.ToolName;
            CheckerConfig.Language = baseCheckerConfig.Language;
            foreach (Checker checker in baseCheckerConfig.CheckerList)
            {
                CheckerConfig.AddCheckerList(checker);
            }
        }

        protected void InitCheckerConfig()
        {
            string dexterConfig;
            try
            {
                var assembly = Assembly.GetExecutingAssembly();
                var resourceName = pluginName + DEFAULT_CHECKER_CONFIG_PATH;
                using (Stream stream = assembly.GetManifestResourceStream(resourceName))
                using (StreamReader reader = new StreamReader(stream))
                {
                    dexterConfig = reader.ReadToEnd();
                }

                baseCheckerConfig = JsonConvert.DeserializeObject<BaseCheckerConfig>(dexterConfig
                    , new JsonSerializerSettings
                    {
                        NullValueHandling = NullValueHandling.Ignore
                    });
            }
            catch (Exception e)
            {
                CliLog.Error("There is no plug-in in directory.");
                CliLog.Error(e.StackTrace);
                Environment.Exit(0);
            }
        }

        public bool SupportLanguage(DexterConfig.LANGUAGE language)
        {
            if (language.Equals(DexterConfig.LANGUAGE.C_SHARP))
            {
                return true;
            }
            else
            {
                return false;
            }
        }

    }
}