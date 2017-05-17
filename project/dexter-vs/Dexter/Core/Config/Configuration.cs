using Dexter.Utils;
using Newtonsoft.Json;
using System.Collections.Generic;
using System.IO;

namespace Dexter.Config
{
    /// <summary>
    /// Dexter configuration
    /// </summary>
    public sealed class Configuration
    {
        /// <summary>
        /// Dexter Home path
        /// </summary>
        public string dexterHome { get; set; }

        /// <summary>
        /// IP address of Dexter server
        /// </summary>
        public string dexterServerIp { get; set; }

        /// <summary>
        /// Port of Dexter server
        /// </summary>
        public string dexterServerPort { get; set; }

        /// <summary>
        /// Dexter Server user name 
        /// </summary>
        [JsonIgnore]
        public string userName { get; set; }

        /// <summary>
        /// Dexter Server user password 
        /// </summary>
        [JsonIgnore]
        public string userPassword { get; set; }

        /// <summary>
        /// Project name
        /// </summary>
        public string projectName { get; set; }

        /// <summary>
        /// Full path to project
        /// </summary>
        public string projectFullPath { get; set; }

        /// <summary>
        /// Source code directories
        /// </summary>
        public List<string> sourceDir { get; set; }

        /// <summary>
        /// Header directories
        /// </summary>
        public List<string> headerDir { get; set; }
        /// <summary>
        /// Binaries directory
        /// </summary>
        public string binDir { get; set; }

        /// <summary>
        /// Library directiores
        /// </summary>
        public List<string> libDir { get; set; }

        /// <summary>
        /// File names (use only with FILE analysis type)
        /// </summary>
        public List<string> fileName { get; set; }

        /// <summary>
        /// Module path (use only with FILE analysis type)
        /// </summary>
        public string modulePath { get; set; }

        /// <summary>
        /// Source code encoding
        /// </summary>
        public string sourceEncoding { get; set; }

        /// <summary>
        /// Type of analysis (PROJECT, FILE or SNAPSHOT) 
        /// </summary>
        public string type { get; set; }


        /// <summary>
        /// Whether analysis result should be standalone or sent to a server
        /// </summary>
        public bool standalone { get; set; }

        /// <summary>
        /// Default path to dexter executable: dexterHome + "\bin\dexter-executor.jar"
        /// </summary>
        [JsonIgnore]
        public string DexterExecutorPath { get { return dexterHome + "\\bin\\dexter-executor.jar"; } }

        /// <summary>
        /// Default path to dexter configuration file: "\dexter-config-vsplugin.json"
        /// </summary>
        [JsonIgnore]
        public static string DefaultConfigurationPath { get { return PathUtils.GetAppDataPath("dexter-config-vsplugin.json"); } }

        /// <summary>
        /// Checks if dexter-executor.jar is found under dexterExecutorPath
        /// </summary>
        [JsonIgnore]
        public bool IsDexterFound
        {
            get
            {
                return File.Exists(DexterExecutorPath);
            }
        }

        /// <summary>
        /// Creates new instance of Configuration with default values
        /// </summary>
        public Configuration() : this(new ProjectInfo(), new DexterInfo())
        {
        }

        /// <summary>
        /// Creates new instance of Configuration from ProjectInfo and DexterInfo 
        /// </summary>
        /// <param name="projectInfo">information about project</param>
        /// <param name="dexterInfo">information about Dexter</param>
        public Configuration(ProjectInfo projectInfo, DexterInfo dexterInfo)
        {
            projectName = projectInfo.projectName;
            projectFullPath = projectInfo.projectFullPath;
            sourceDir = projectInfo.sourceDir;
            binDir = projectInfo.binDir;
            headerDir = projectInfo.headerDir;
            libDir = projectInfo.libDir;
            fileName = projectInfo.fileName;
            modulePath = projectInfo.modulePath;
            type = projectInfo.type;
            sourceEncoding = projectInfo.sourceEncoding;

            dexterHome = dexterInfo.dexterHome;
            dexterServerIp = dexterInfo.dexterServerIp;
            dexterServerPort = dexterInfo.dexterServerPort.ToString();
            userName = dexterInfo.userName;
            userPassword = dexterInfo.userPassword;
            standalone = dexterInfo.standalone;
        }


        /// <summary>
        /// Saves configuration to json file
        /// </summary>
        /// <param name="fileName">target file name</param>
        public void Save(string fileName)
        {
            string json = JsonConvert.SerializeObject(this, Formatting.Indented);
            File.WriteAllText(fileName, json);
        }

        /// <summary>
        /// Saves configuration to json file under default path
        /// </summary>
        public void Save()
        {
            Save(DefaultConfigurationPath);
        }

        /// <summary>
        /// Loads configuration from json file
        /// </summary>
        /// <param name="fileName">json file name</param>
        /// <returns>Configuration</returns>
        public static Configuration Load(string fileName)
        {
            string json = File.ReadAllText(fileName);
            return JsonConvert.DeserializeObject<Configuration>(json);
        }

        /// <summary>
        /// Loads configuration from json file from default path
        /// </summary>
        /// <returns>Configuration</returns>
        public static Configuration Load()
        {
            return Load(DefaultConfigurationPath);
        }
    }
}
