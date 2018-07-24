using Newtonsoft.Json;
using System.IO;

namespace Dexter.Common.Config
{
    /// <summary>
    /// Information about Dexter 
    /// </summary>
    public class DexterInfo
    {
        /// <summary>
        /// Dexter/DexterCS Home path
        /// </summary>
        public string dexterHome { get; set; }

        /// <summary>
        /// IP address of Dexter server
        /// </summary>
        public string dexterServerIp { get; set; }

        /// <summary>
        /// Port of Dexter server
        /// </summary>
        public int dexterServerPort { get; set; }

        /// <summary>
        /// Dexter Server user name 
        /// </summary>
        public string userName { get; set; }

        /// <summary>
        /// Dexter Server user password 
        /// </summary>
        public string userPassword { get; set; }

        /// <summary>
        /// Whether analysis result should be standalone or sent to a server
        /// </summary>
        public bool standalone { get; set; }

        /// <summary>
        ///  Whether Dexter/DexterCS home is enabled (Dexter/DexterCS home is available for only c/c++ analysis)
        /// </summary>
        public bool IsDexterHomeEnabled { get; set; }

        /// <summary>
        /// Default path to dexter-executor: dexterHome + "\bin\dexter-executor.jar"
        /// </summary>
        [JsonIgnore]
        public string DexterExecutorPath { get { return dexterHome + "\\bin\\dexter-executor.jar"; } }

        /// <summary>
        /// Default path to DexterCS: dexterHome + "\bin\DexterCS.exe"
        /// </summary>
        [JsonIgnore]
        public string DexterCSPath { get { return dexterHome + "\\bin\\DexterCS.exe"; } }

        /// <summary>
        /// Checks if dexter-executor.jar is found under dexterExecutorPath or DexterCS.exe is found under DexterCSPath
        /// </summary>
        [JsonIgnore]
        public bool IsDexterFound
        {
            get
            {
                return (File.Exists(DexterExecutorPath) || File.Exists(DexterCSPath));
            }
        }

        /// <summary>
        /// Creates new DexterInfo instance with default values
        /// </summary>
        public DexterInfo()
        {
            dexterHome = "";
            dexterServerIp = "";
            dexterServerPort = 0;
            userName = "";
            userPassword = "";
            standalone = true;
            IsDexterHomeEnabled = false;
        }

        /// <summary>
        /// Loads DexterInfo from json file
        /// </summary>
        /// <param name="fileName">json file name</param>
        /// <returns>DexterInfo</returns>
        public static DexterInfo Load(string fileName)
        {
            string json = File.ReadAllText(fileName);
            Configuration configuration = JsonConvert.DeserializeObject<Configuration>(json);

            return fromConfiguration(configuration);
        }

        /// <summary>
        /// Dreates DexterInfo from Configuration
        /// </summary>
        /// <param name="configuration">Dexter configuration</param>
        /// <returns>DexterInfo</returns>
        public static DexterInfo fromConfiguration(Configuration configuration)
        {
            return new DexterInfo()
            {
                dexterHome = configuration.dexterHome,
                dexterServerIp = configuration.dexterServerIp,
                dexterServerPort = int.Parse(configuration.dexterServerPort),
                userName = configuration.userName,
                userPassword = configuration.userPassword,
                standalone = configuration.standalone,
                IsDexterHomeEnabled = configuration.IsDexterHomeEnabled
            };
        }
    }
}
