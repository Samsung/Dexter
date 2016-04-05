using System;
using System.Collections.Generic;
using System.Diagnostics;

using System.IO;

namespace dexter_vs.Analysis
{
    /// <summary>
    /// Adapter for Dexter application
    /// <param name="dexterPath">path to a dexter-executor.jar</param>
    /// </summary>
    public class Dexter
    {
        private string dexterPath;

        private List<Defect> defects = new List<Defect>();

        /// <summary>
        /// Occurs when dexter process writes to its standard output stream
        /// </summary>
        public event DataReceivedEventHandler OutputDataReceived;

        /// <summary>
        /// Occurs when dexter process writes to its standard error stream
        /// </summary>   
        public event DataReceivedEventHandler ErrorDataReceived;


        public Dexter(string dexterPath)
        {
            this.dexterPath = dexterPath;
        }
        
        /// <summary>
        /// Performs analysis of files in given path
        /// </summary>
        /// <param name="path">path to analysed directory</param>
        /// <returns>List of found defects</returns>
        public List<Defect> Analyse(string path = "/")
        {
            string fullPath = Path.GetFullPath(path);
       
            Process javaProcess = new Process();

            javaProcess.StartInfo.FileName = "java.exe";
            javaProcess.StartInfo.Arguments = "-jar " + dexterPath + " -s";
            javaProcess.StartInfo.WorkingDirectory = Path.GetDirectoryName(dexterPath);
            javaProcess.StartInfo.CreateNoWindow = true;
            javaProcess.StartInfo.UseShellExecute = false;
            javaProcess.StartInfo.RedirectStandardOutput = true;
            javaProcess.StartInfo.RedirectStandardError = true;

            javaProcess.OutputDataReceived += OutputDataReceived;
            javaProcess.ErrorDataReceived += ErrorDataReceived;

            javaProcess.Start();

            javaProcess.BeginErrorReadLine();
            javaProcess.BeginOutputReadLine();

            javaProcess.WaitForExit();

            return defects;
        }

        /// <summary>
        /// Checks if dexter-executor.jar was found in path
        /// </summary>
        /// <returns></returns>
        public bool IsDexterFound
        {
            get
            {
                return File.Exists(dexterPath) && Path.GetExtension(dexterPath).Equals(".jar");
            }
        }

    }
}