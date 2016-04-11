using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace dexter_vs.Analysis
{
    /// <summary>
    /// Dexter configuration
    /// </summary>
    public class Configuration
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
        /// Source code encoding
        /// </summary>
        public string sourceEncoding { get; set; }
        
        /// <summary>
        /// Type of analysis (PROJECT, FILE or SNAPSHOT) 
        /// </summary>
        public string type { get; set; }

        /// <summary>
        /// Default path to dexter executable: dexterHome + "\bin\dexter-executor.jar"
        /// </summary>
        public string dexterExecutorPath { get { return dexterHome + "\\bin\\dexter-executor.jar"; } }

        /// <summary>
        /// Creates new Configuration instance
        /// </summary>
        public Configuration()
        {
            dexterHome = "";
            dexterServerIp = "";
            dexterServerPort = "0";
            projectName = "";
            projectFullPath = "";
            sourceDir = new List<string>();
            headerDir = new List<string>();
            binDir = "";
            libDir = new List<string>();
            sourceEncoding = "UTF-8";
            type = "PROJECT";
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
        /// Loads configuration from json file
        /// </summary>
        /// <param name="fileName">json file name</param>
        /// <returns></returns>
        public static Configuration Load(string fileName)
        {
            string json = File.ReadAllText(fileName);
            return JsonConvert.DeserializeObject<Configuration>(json);
        }
        
    }
}
