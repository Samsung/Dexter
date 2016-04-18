using System.Collections.Generic;

namespace dexter_vs.Config
{
    /// <summary>
    /// Information about project to analyse
    /// </summary>
    public class ProjectInfo
    {
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
        /// Library directories
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
        /// Creates new ProjectInfo instance with default values
        /// </summary>
        public ProjectInfo()
        {
            projectName = "";
            projectFullPath = "";
            sourceDir = new List<string>();
            headerDir = new List<string>();
            fileName = new List<string>();
            binDir = "";
            modulePath = "";
            libDir = new List<string>();
            sourceEncoding = "UTF-8";
            type = "PROJECT";
        }
    }
}
