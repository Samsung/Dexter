using System.Collections.Generic;

namespace DexterCS
{
    public class ProjectAnalysisConfiguration
    {
        public string ProjectName { get; set; }
        public string ProjectFullPath { get; set; }
        private List<string> sourceDirs;
        public List<string> SourceDirs
        {
            get { return sourceDirs ?? new List<string>(); }
            set { sourceDirs = value; }
        }

        private List<string> headerDirs;
        public List<string> HeaderDirs
        {
            get { return headerDirs ?? new List<string>(); }
            set { headerDirs = value; }
        }

        private List<string> targetDir;
        public List<string> TargetDirs
        {
            get
            {
                return targetDir ?? new List<string>();
            }
            set { targetDir = value; }
        }
        public string Type { get; set; }
    }
}