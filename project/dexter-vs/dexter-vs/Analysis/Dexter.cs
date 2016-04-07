using dexter_vs.Defects;
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

        /// <summary>
        /// Creates new Dexter instance
        /// </summary>   
        /// <param name="dexterPath">path to a dexter-executor.jar</param>
        public Dexter(string dexterPath)
        {
            DexterPath = dexterPath;
        }

        /// <summary>
        /// Path to dexter
        /// </summary>
        public string DexterPath
        {
            get;
            set;
        }

        /// <summary>
        /// Checks if dexter-executor.jar is found under DexerPath
        /// </summary>
        public bool IsDexterFound
        {
            get
            {
                return File.Exists(DexterPath) && Path.GetExtension(DexterPath).Equals(".jar");
            }
        }

        /// <summary>
        /// Performs analysis of files in given path
        /// </summary>
        /// <param name="path">path to analysed directory</param>
        /// <returns>List of found defects</returns>
        public Result Analyse(string path = "/")
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
            if (!IsDexterFound) throw new FileNotFoundException("Cannot find dexter in specified path", DexterPath);
                   
            Process dexterProcess = new Process();
            dexterProcess.StartInfo = new ProcessStartInfo()
            {
                FileName = "java.exe",
                Arguments = "-jar " + DexterPath + " -s -x",
                WorkingDirectory = Path.GetDirectoryName(DexterPath),
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
       
            string resultFile = Path.GetDirectoryName(DexterPath) + "\\dexter-result.xml";

            if (!File.Exists(resultFile)) throw new FileNotFoundException("Cannot find result file: " + resultFile, resultFile);

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