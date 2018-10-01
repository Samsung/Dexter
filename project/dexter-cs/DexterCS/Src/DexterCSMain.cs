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
using System.Collections.Generic;
using System.IO;
using System.Text;

[assembly: log4net.Config.XmlConfigurator(Watch = true)]
namespace DexterCS
{
    public class DexterCSMain
    {
        private static ILog CliLog = LogManager.GetLogger(typeof(DexterCSMain));
        private DexterConfig config = DexterConfig.Instance;

        public List<string> sourceFileFullPathList = new List<string>();

        static void Main(string[] args)
        {
            DexterCSMain cliMain = new DexterCSMain();
            IDexterCLIOption cliOption = new DexterCLIOption(args);
            IDexterConfigFile configFile = cliMain.CreateDexterConfigFile(cliOption);
            IDexterClient client = cliMain.CreateDexterClient(cliOption);
            cliMain.Analyze(cliOption, configFile, client);
        }

        private static IDexterPluginManager LoadDexterPlugin(IDexterClient client, IDexterCLIOption cliOption)
        {
            IDexterPluginInitializer initializer = new CLIPluginInitializer(CliLog);
            IDexterPluginManager pluginManager = new CLIDexterPluginManager(initializer, client, CliLog, cliOption);
            pluginManager.InitDexterPlugins();

            return pluginManager;
        }

        private void Analyze(IDexterCLIOption cliOption, IDexterConfigFile configFile, IDexterClient client)
        {
            CliLog.Info(DexterLogConstant.GetStartingAnalysisMessage());

            AnalysisConfig baseAnalysisConfig = CreateBaseAnalysisConfig(cliOption, configFile);
            IAnalysisResultHandler cliAnalysisResultHandler = CreateCLIAnalysisResultHandler(client, cliOption);
            IDexterPluginManager pluginManager = LoadDexterPlugin(client, cliOption);
            InitSourceFileFullPathList(configFile, cliOption);
            AnalyzeSynchronously(pluginManager, cliAnalysisResultHandler, baseAnalysisConfig, client);
        }

        private void AnalyzeSynchronously(IDexterPluginManager pluginManager, IAnalysisResultHandler cliAnalysisResultHandler, AnalysisConfig baseAnalysisConfig, IDexterClient client)
        {
            cliAnalysisResultHandler.HandleBeginningOfResultFile();
            foreach (string fileFullPath in sourceFileFullPathList)
            {
                AnalysisConfig analysisConfig = CreateAnalysisConfig(fileFullPath, cliAnalysisResultHandler, baseAnalysisConfig);
                DexterAnalyzer.Instance.RunSync(analysisConfig, pluginManager, client);
            }
            cliAnalysisResultHandler.HandleEndOfResultFile();
            cliAnalysisResultHandler.PrintLogAfterAnalyze();
        }

        private AnalysisConfig CreateAnalysisConfig(string fileFullPath, IAnalysisResultHandler cliAnalysisResultHandler, AnalysisConfig baseAnalysisConfig)
        {
            AnalysisConfig _config = new AnalysisEntityFactory().CopyAnalysisConfigWithoutSourcecode(baseAnalysisConfig);
            _config.ResultHandler = cliAnalysisResultHandler;
            _config.SourceFileFullPath = fileFullPath;
            _config.GenerateFileNameWithSourceFileFullPath();
            _config.GenerateModulePath();

            return _config;
        }

        private void InitSourceFileFullPathList(IDexterConfigFile configFile, IDexterCLIOption cliOption)
        {
            if (cliOption.IsTargetFileOptionEnabled)
            {
                this.sourceFileFullPathList = cliOption.TargetFileFullPathList;
            }
            else
            {
                this.sourceFileFullPathList = configFile.GenerateSourceFileFullPathList();
            }
        }

        private IAnalysisResultHandler CreateCLIAnalysisResultHandler(IDexterClient client, IDexterCLIOption cliOption)
        {
            return new CLIAnalysisResultHandler(client.DexterWebUrl, cliOption, CliLog);
        }

        private AnalysisConfig CreateBaseAnalysisConfig(IDexterCLIOption cliOption, IDexterConfigFile configFile)
        {
            InitDexterConfig(cliOption, configFile);
            AnalysisConfig baseAnalysisConfig = configFile.ToAnalysisConfig();
            return baseAnalysisConfig;
        }

        private void InitDexterConfig(IDexterCLIOption cliOption, IDexterConfigFile configFile)
        {
            config.IsSpecifiedCheckerOptionEnabledByCli = cliOption.IsSpisSpecifiedCheckerEnabledMode;
            config.DexterHome = configFile.DexterHome;
            config.CreateInitialFolderAndFiles();
        }

        public IDexterClient CreateDexterClient(IDexterCLIOption cliOption)
        {
            IHttpClient httpWrapper = new DexterHttpClientWrapper(
                new DexterServerConfig(cliOption.ServerHostIp, cliOption.ServerPort, cliOption.UserId, cliOption.UserPassword));

            return new DexterClient(httpWrapper);
        }

        private IDexterConfigFile CreateDexterConfigFile(IDexterCLIOption cliOption)
        {
            IDexterConfigFile configFile = new DexterConfigFile();
            configFile.LoadFromFile(File.ReadAllText(cliOption.ConfigFilePath, Encoding.UTF8));

            return configFile;
        }
    }
}
