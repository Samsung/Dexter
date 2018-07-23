using Dexter.Common.Config;
using Dexter.Defects;
using System;
using System.Diagnostics;

using System.IO;
using System.Xml;
using System.Xml.Serialization;

namespace Dexter.Analysis
{
    /// <summary>
    /// Adapter for Dexter application
    /// </summary>
    public class DexterLegacyAnalyzer
    {
        /// <summary>
        /// Occurs when dexter process writes to its standard output stream
        /// </summary>
        public event DataReceivedEventHandler OutputDataReceived;

        /// <summary>
        /// Occurs when dexter process writes to its standard error stream
        /// </summary>   
        public event DataReceivedEventHandler ErrorDataReceived;

        /// <summary>
        /// external process running Dexter
        /// </summary>
        private Process dexterProcess;

        /// <summary>
        /// Configuration
        /// </summary>
        private Configuration configuration;

        /// <summary>
        /// Creates new Dexter instance
        /// </summary>   
        /// <param name="dexterPath">path to a dexter-executor.jar</param>
        /// <param name="configuration">Configuration</param>
        public DexterLegacyAnalyzer(Configuration configuration) 
        {
            this.configuration = configuration;
            if (configuration == null)
                throw new ArgumentNullException("configuration");
            if (!configuration.IsDexterFound)
                throw new FileNotFoundException("Cannot find dexter in specified path", configuration.DexterExecutorPath);
            configuration.Save();
        }

        /// <summary>
        /// Performs analysis of files in given path
        /// </summary>
        /// <param name="path">path to analysed directory</param>
        /// <returns>List of found defects</returns>
        /// <exception cref="XmlException">When result file cannot be parsed</exception>
        public Result Analyse()
        {
            CreateDexterProcess();
            StartDexterProcess();
            return GetAnalysisResult();
        }

        /// <summary>
        /// Cancels current dexter process
        /// </summary>
        public void Cancel()
        {
            if (dexterProcess!=null && !dexterProcess.HasExited)
            {
                dexterProcess.Kill();
            }
        }

        /// <summary>
        /// Attempts to create new user account using credentials provided in configuration
        /// </summary>
        public void CreateUser()
        {
            CreateDexterProcess(true);
            StartDexterProcess();
        }

        /// <summary>
        /// Creates (but doesn't start) new Dexter process
        /// </summary>
        /// <param name="createUser">if true, this process will create new user account</param>
        private void CreateDexterProcess(bool createUser = false)
        {
            string configFlag = File.Exists(Configuration.DefaultConfigurationPath) ? " -f " + Configuration.DefaultConfigurationPath : "";
            string createUserFlag = createUser ? " -c " : "";
            string createXmlResultFlag = " -x ";
            string credentialsParams = (configuration.standalone && !createUser) 
                ? " -s " 
                : " -u " + configuration.userName + " -p " + configuration.userPassword + " -h " + configuration.dexterServerIp + " -o " + configuration.dexterServerPort;
                
            dexterProcess = new Process();
            dexterProcess.StartInfo = new ProcessStartInfo()
            {
                FileName = configuration.DexterCSPath,
                Arguments = createUserFlag + createXmlResultFlag + configFlag + credentialsParams,
                WorkingDirectory = Path.GetDirectoryName(configuration.DexterExecutorPath),
                CreateNoWindow = true,
                UseShellExecute = false,
                RedirectStandardOutput = true,
                RedirectStandardError = true
            };
            
            dexterProcess.OutputDataReceived += OutputDataReceived;
            dexterProcess.ErrorDataReceived += ErrorDataReceived;
            dexterProcess.Disposed += (s,e) => dexterProcess = null;
        }

        /// <summary>
        /// Starts dexter process
        /// </summary>
        private void StartDexterProcess()
        {
            using (dexterProcess)
            {
                dexterProcess.Start();
                dexterProcess.BeginErrorReadLine();
                dexterProcess.BeginOutputReadLine();
                dexterProcess.WaitForExit();
            }
        }

        /// <summary>
        /// Loads analysis result from generated file
        /// </summary>
        /// <returns></returns>
        private Result GetAnalysisResult()
        {
            string resultFile = Path.GetDirectoryName(configuration.DexterExecutorPath) + "\\dexter-result.xml";
            var resultFileInfo = new FileInfo(resultFile);

            if (!resultFileInfo.Exists) throw new FileNotFoundException("Cannot find result file: " + resultFile, resultFile);

            if (resultFileInfo.Length == 0)
            {
                return new Result();
            }

            using (XmlReader reader = XmlReader.Create(resultFile))
            {
               XmlSerializer serializer = new XmlSerializer(typeof(Result));
               return (Result)serializer.Deserialize(reader);
            }
        }

    }
}