using log4net;
using System;
using System.Collections.Generic;
using System.IO;
using System.Text;

namespace DexterCS
{
    public sealed class DexterConfig
    {
        private static ILog CliLog = LogManager.GetLogger(typeof(DexterConfig));

        private List<IDexterHomeListener> dexterHomeListenerList = new List<IDexterHomeListener>(3);

        public enum Run_Mode
        {
            CLI, ECLIPSE, DAEMON, INTELLIJ, NETBEANS, SOURCE_INSIGHT
        }

        public void AddDexterHomeListener(IDexterHomeListener listener)
        {
            if (dexterHomeListenerList.Contains(listener))
            {
                dexterHomeListenerList.Add(listener);
            }
        }

        public enum LANGUAGE
        {
            JAVA, JAVASCRIPT, C, CPP, C_SHARP, UNKNOWN, ALL
        };
        public enum AnalysisType
        {
            SAVE, FILE, FOLDER, PROJECT, SNAPSHOT, UNKNOWN
        };

        public bool IsSpecifiedCheckerOptionEnabledByCli { get; set; }



        public readonly String DEXTER_HOME_KEY = "dexterHome";
        private readonly string PLUGIN_FOLDER_NAME = "plugin";

        private static HashSet<string> supportingFileExtensions = new HashSet<string>();
        public void AddSupprotingFileExtensions(string[] fileExtensions)
        {
            foreach (string extension in fileExtensions)
            {
                if (supportingFileExtensions.Contains(extension.ToLowerInvariant()) == false)
                {
                    supportingFileExtensions.Add(extension.ToLowerInvariant());
                }
            }
        }

        public static readonly string RESULT_FOLDER_NAME = "result";
        public static readonly string OLD_FOLDER_NAME = "old";
        public static readonly string TEMP_FOLDER_NAME = "temp";
        public static readonly string LOG_FOLDER_NAME = "log";
        public static readonly string FILTER_FOLDER_NAME = "filter";
        public static readonly string POST_ANALYSIS_RESULT_V3 = "/api/v3/analysis/result";
        public static readonly string POST_SNAPSHOT_SOURCECODE = "/api/v1/analysis/snapshot/source";

        public Run_Mode RunMode { get; set; }

        private string HomePath { get; set; }
        private string dexterHome;
        public string DexterHome
        {
            get
            {
                return dexterHome;
            }
            set
            {
                HomePath = DexterUtil.RefinePath(value);
                dexterHome = HomePath;
            }
        }

        public static HashSet<string> SupportingFileExtensions
        {
            get
            {
                return supportingFileExtensions;
            }
            set
            {
                foreach (string extension in value)
                {
                    if (supportingFileExtensions.Contains(extension.ToLowerInvariant()) == false)
                    {
                        supportingFileExtensions.Add(extension.ToLowerInvariant());
                    }
                }
            }
        }

        public void RemoveAllSupportingFileExtensions()
        {
            supportingFileExtensions.Clear();
        }

        private static DexterConfig instance = null;
        private static readonly object padlock = new object();
        public Encoding SourceEncoding = new UTF8Encoding(true);

        public static DexterConfig Instance
        {
            get
            {
                lock (padlock)
                {
                    if (instance == null)
                    {
                        instance = new DexterConfig();
                    }
                    return instance;
                }
            }
        }

        public static readonly int SOURCE_FILE_SIZE_LIMIT = 1024 * 2 * 1024;
        public static readonly string DEXTER_CFG_FILENAME = "dexter_cfg.json";
        public string OldFilePath
        {
            get
            {
                return this.DexterHome + "/" + DexterConfig.RESULT_FOLDER_NAME + "/" + OLD_FOLDER_NAME;
            }
        }

        public void CreateInitialFolderAndFiles()
        {
            if (string.IsNullOrEmpty(DexterHome))
            {
                return;
            }
            try
            {
                DexterUtil.CreateFolderWithParents(DexterHome);

                string bin = DexterHome + "/bin";
                DexterUtil.CreateFolderWithParents(bin);
                DexterUtil.CreateFolderWithParents(bin + "/cppcheck");
                DexterUtil.CreateFolderWithParents(bin + "/cppcheck/cfg");

                string plugin = DexterHome + "/" + PLUGIN_FOLDER_NAME;
                DexterUtil.CreateFolderWithParents(plugin);

                string result = DexterHome + "/" + RESULT_FOLDER_NAME;
                DexterUtil.CreateFolderWithParents(result);
                DexterUtil.CreateFolderWithParents(result + "/" + OLD_FOLDER_NAME);

                DexterUtil.CreateFolderWithParents(DexterHome + "/" + TEMP_FOLDER_NAME);
                DexterUtil.CreateFolderWithParents(DexterHome + "/" + LOG_FOLDER_NAME);

                string filter = DexterHome + "/" + FILTER_FOLDER_NAME;
                DexterUtil.CreateFolderWithParents(filter);
            }
            catch (IOException)
            {
                CliLog.Error("IOException in DexterConfig");
            }
        }

        public void ChangeDexterHome(string homePath)
        {
            HomePath = DexterUtil.RefinePath(homePath);
            dexterHome = HomePath;
        }

        public bool IsAnalysisAllowedFile(string fileName)
        {
            string extension = Path.GetExtension(fileName).ToLowerInvariant().Replace(".", "");
            return SupportingFileExtensions.Contains(extension);
        }
    }
}
