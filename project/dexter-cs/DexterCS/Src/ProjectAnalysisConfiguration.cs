using System.Collections.Generic;
using System.ComponentModel;

namespace DexterCS
{
    public class ProjectAnalysisConfiguration
    {
        public string ProjectName { get; set; }
        public string ProjectFullPath { get; set; }
        private List<string> sourceDirs;
        public List<string> SourceDirs {
            get { return sourceDirs == null ? new List<string>() : sourceDirs; }
            set { sourceDirs = value; }
        }

        private List<string> headerDirs;
        public List<string> HeaderDirs {
            get { return headerDirs == null ? new List<string>() : headerDirs; }
            set { headerDirs = value; }
        }

        private List<string> targetDir;
        public List<string> TargetDirs {
            get { return targetDir == null ? new List<string>() : targetDir;
            }
            set { targetDir = value; } }
        public string Type { get; set; }
    }
}