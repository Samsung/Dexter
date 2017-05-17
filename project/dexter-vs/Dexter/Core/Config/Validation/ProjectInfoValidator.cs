using System;
using System.IO;
using System.Net;

namespace Dexter.Config.Validation
{
    /// <summary>
    /// Validates correctness of ProjectInfo
    /// </summary>
    internal sealed class ProjectInfoValidator
    {
        /// <summary>
        /// Project path validation
        /// </summary>
        /// <returns>true, if project sources exists in given path</returns>
        public bool ValidateProjectPath(ProjectInfo projectInfo)
        {
            return projectInfo.projectFullPath != "" && projectInfo.sourceDir.Count > 0;
        }


    }
}
