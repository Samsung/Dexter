using Newtonsoft.Json;
using System.IO;

namespace dexter_vs.Config
{
    /// <summary>
    /// Information about Dexter 
    /// </summary>
    public class DexterInfo
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
        /// Default path to dexter executable: dexterHome + "\bin\dexter-executor.jar"
        /// </summary>
        [JsonIgnore]
        public string DexterExecutorPath { get { return dexterHome + "\\bin\\dexter-executor.jar"; } }

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
                standalone = configuration.standalone
            };
        }
    }
}
