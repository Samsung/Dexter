using EnvDTE;
using System;
using System.IO;
using Dexter.Common.Config;

namespace Dexter.Common.Config.Providers
{
    /// <summary>
    /// Provides project info based on user preferences.
    /// Sets analysis scope to whole solution.  
    /// </summary>
    public class SolutionInfoProvider : ProjectInfoProvider
    {
        /// <summary>
        /// Creates new ProjectInfoProvider
        /// </summary>
        /// <param name="serviceProvider"> service provider from the owner package</param>
        public SolutionInfoProvider(IServiceProvider serviceProvider, bool snapshot = false) 
            : base(serviceProvider, snapshot)
        {
        }

        /// <summary>
        /// Creates new ProjectInfo based on user preferences 
        /// </summary>
        /// <returns>new ProjectInfo</returns>
        public override ProjectInfo Load()
        {   
            Solution solution = dte.Solution;

            if (solution == null || solution.Count==0)
            {
                return new ProjectInfo();
            }
            else
            {
                return new ProjectInfo()
                {
                    projectName = Path.GetFileNameWithoutExtension(solution.FullName),
                    projectFullPath = Path.GetDirectoryName(solution.FullName),
                    sourceDir = { Path.GetDirectoryName(solution.FullName) },
                    headerDir = { Path.GetDirectoryName(solution.FullName) },
                    type = Snapshot ? "SNAPSHOT" : "PROJECT"
                };
            }
        }
    }
}
