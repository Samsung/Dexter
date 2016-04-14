using dexter_vs.Analysis.Config;
using dexter_vs.Defects;
using System;
using System.Diagnostics;

using System.IO;
using System.Xml;
using System.Xml.Serialization;

namespace dexter_vs.Analysis
{
    /// <summary>
    /// Adapter for Dexter application
    /// </summary>
    public class Dexter
    {
        /// <summary>
        /// Occurs when dexter process writes to its standard output stream
        /// </summary>
        public event DataReceivedEventHandler OutputDataReceived;

        /// <summary>
        /// Occurs when dexter process writes to its standard error stream
        /// </summary>   
        public event DataReceivedEventHandler ErrorDataReceived;

        private Configuration configuration;

        /// <summary>
        /// Creates new Dexter instance
        /// </summary>   
        /// <param name="dexterPath">path to a dexter-executor.jar</param>
        /// <param name="configuration">Configuration</param>
        public Dexter(Configuration configuration) 
        {
            this.configuration = configuration;
            if (!configuration.IsDexterFound) throw new FileNotFoundException("Cannot find dexter in specified path", configuration.DexterExecutorPath);
            configuration.Save();
        }
        
        /// <summary>
        /// Performs analysis of files in given path
        /// </summary>
        /// <param name="path">path to analysed directory</param>
        /// <returns>List of found defects</returns>
        public Result Analyse()
        {
            Process dexterProcess = CreateDexterProcess();

            dexterProcess.Start();
            dexterProcess.BeginErrorReadLine();
            dexterProcess.BeginOutputReadLine();
            dexterProcess.WaitForExit();

            Result result = GetAnalysisResult();

            return result;
        }

        /// <summary>
        /// Creates (but doesn't start) new Dexter process
        /// </summary>
        /// <returns>new dexter process</returns>
        private Process CreateDexterProcess()
        {
            string configFlag = File.Exists(Configuration.DefaultConfigurationPath) ? " -f " + Configuration.DefaultConfigurationPath : "";
            string credentialsParams = configuration.standalone ? " -s " : " -u " + configuration.userName + " -p " + configuration.userPassword;
                 
            Process dexterProcess = new Process();
            dexterProcess.StartInfo = new ProcessStartInfo()
            {
                FileName = "java.exe",
                Arguments = "-jar " + configuration.DexterExecutorPath + " -x " + configFlag + credentialsParams,
                WorkingDirectory = Path.GetDirectoryName(configuration.DexterExecutorPath),
                CreateNoWindow = true,
                UseShellExecute = false,
                RedirectStandardOutput = true,
                RedirectStandardError = true
            };
            
            dexterProcess.OutputDataReceived += OutputDataReceived;
            dexterProcess.ErrorDataReceived += ErrorDataReceived;
            return dexterProcess;
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

            Result result;
            using (XmlReader reader = XmlReader.Create(resultFile))
            {
                XmlSerializer serializer = new XmlSerializer(typeof(Result));
                result = (Result)serializer.Deserialize(reader);
            }

            return result;
        }
    }
}