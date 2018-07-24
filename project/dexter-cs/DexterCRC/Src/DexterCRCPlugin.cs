using DexterCS;
using log4net;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp;
using Newtonsoft.Json;
using System;
using System.IO;
using System.Reflection;
using System.Text;

namespace DexterCRC
{
    public class DexterCRCPlugin : IDexterPlugin
    {
        private const string DEFAULT_CHECKER_CONFIG_PATH = ".Resource.checker-config.json";
        private static ILog CliLog = LogManager.GetLogger(typeof(DexterCRCPlugin));

        static Assembly assem = Assembly.GetExecutingAssembly();
        static AssemblyName assemName = assem.GetName();
        static Version version = assemName.Version;
        private static string pluginName = assemName.Name;
        public string PLUGIN_NAME
        {
            get
            {
                return pluginName;
            }
        }

        private BaseCheckerConfig baseCheckerConfig;
        private ICheckerConfig checkerConfig = new CheckerConfig(pluginName, DexterConfig.LANGUAGE.C_SHARP);
        public ICheckerConfig CheckerConfig { get { return checkerConfig; } set { checkerConfig = value; } }
        public DexterCRCPlugin() { }
        public string[] SupportingFileExtensions
        {
            get { return new String[] { "cs" }; }
        }

        public string PLUGIN_DESCRIPTION
        {
            get { return "Dexter CSharp CRC Checker Plug-in"; }
        }

        public string PLUGIN_AUTHOR
        {
            get { return "Samsung Electroincs"; }
        }

        public Version VERSION
        {
            get { return version; }
        }

        public void Init()
        {
            InitCheckerConfig();
            MakeCheckerConfig();
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
            } catch(Exception e)
            {
                CliLog.Error("There is no plug-in in directory.");
                CliLog.Error(e.StackTrace);
                Environment.Exit(0);
            }
        }

        protected void MakeCheckerConfig()
        {
            CheckerConfig.ToolName = baseCheckerConfig.ToolName;
            CheckerConfig.Language = baseCheckerConfig.Language;
            foreach (Checker checker in baseCheckerConfig.CheckerList)
            {
                CheckerConfig.AddCheckerList(checker);
            }
        }

        public bool SupportLanguage(DexterConfig.LANGUAGE language)
        {
            if (language.Equals(DexterConfig.LANGUAGE.C_SHARP)) {
                return true;
            }
            else
            {
                return false;
            }
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
            DexterCRCUtil.ToolName = checkerConfig.ToolName;
            SyntaxNode syntaxRoot = tree.GetRoot();

            foreach (Checker checker in CheckerConfig.CheckerList)
            {
                if (false.Equals(checker.IsActive))
                {
                    continue;
                }
                ICRCLogic logic = GetCheckerLogic(checker.Code);
               
                logic.Analyze(config, result, checker, syntaxRoot);
            }
            return result;
        }

        private ICRCLogic GetCheckerLogic(string code)
        {
            return (ICRCLogic)Activator.CreateInstance(Type.GetType(PLUGIN_NAME + "." + code, true));
        }

        public void Dispose()
        {
            //TODO:
        }
    }
}
