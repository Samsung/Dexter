using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using log4net;
using log4net.Config;
using System.IO;
using DexterCS.Client;

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
