using EnvDTE;
using System;
using System.Collections.Generic;
using System.IO;

namespace dexter_vs.Config.Providers
{
    /// <summary>
    /// Provides project info based on user preferences.
    /// Sets analysis scope to whole solution.  
    /// </summary>
    internal class SolutionInfoProvider : ProjectInfoProvider
    {
        /// <summary>
        /// Creates new ProjectInfoProvider
        /// </summary>
        /// <param name="serviceProvider"> service provider from the owner package</param>
        /// <param name="snapshot"> if true, adds "snapshot" flag to Dexter configuration</param>
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
                var sourceDirectories = GetDirectories(solution);
                return new ProjectInfo()
                {
                    projectName = Path.GetFileNameWithoutExtension(solution.FullName),
                    projectFullPath = Path.GetDirectoryName(solution.FullName),
                    sourceDir = sourceDirectories,
                    headerDir = sourceDirectories,
                    type = Snapshot ? "SNAPSHOT" : "PROJECT"
                };
            }
        }

        /// <summary>
        /// Returns all source directories in all projects and subprojects of given solution
        /// </summary>
        /// <param name="solution">solution</param>
        /// <returns>list of found directories (absolute paths) </returns>
        private static List<String> GetDirectories(Solution solution)
        {
            var directories = new List<String>();

            foreach (Project project in solution.Projects)
            {
                directories.AddRange(GetDirectories(project));
            }

            return directories;
        }

        /// <summary>
        /// Returns all source directories in given projects and their subprojects
        /// </summary>
        /// <param name="project">project</param>
        /// <returns>list of found directories (absolute paths) </returns>
        private static List<String> GetDirectories(Project project)
        {
            var directories = new List<String>();

            if (project == null || string.IsNullOrEmpty(project.FullName))
            {
                return directories;
            }

            directories.Add(Path.GetDirectoryName(project.FullName));

            foreach (ProjectItem projectItem in project.ProjectItems)
            {
                directories.AddRange(GetDirectories(projectItem.SubProject));
            }

            return directories;
        }
    }
}
