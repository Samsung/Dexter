using EnvDTE;
using System;
using System.IO;

namespace dexter_vs.Config.Providers
{
    /// <summary>
    /// Provides project info based on user preferences.
    /// Sets analysis scope to whole project.  
    /// </summary>
    internal class ProjectInfoProvider : IProjectInfoProvider
    {
        /// <summary>
        /// DTE object
        /// </summary>
        protected readonly DTE dte;

        /// <summary>
        /// Creates new ProjectInfoProvider
        /// </summary>
        /// <param name="serviceProvider"> service provider from the owner package</param>
        public ProjectInfoProvider(IServiceProvider serviceProvider)
        {
            dte = (DTE)serviceProvider.GetService(typeof(DTE));
        }

        /// <summary>
        /// Creates new ProjectInfo based on user preferences 
        /// </summary>
        /// <returns>new ProjectInfo</returns>
        public virtual ProjectInfo Create()
        {   
            Solution solution = dte.Solution;
            Projects projects = solution.Projects;
            Project project = projects.Item(1);

            return new ProjectInfo()
            {
                projectName = Path.GetFileNameWithoutExtension(project.FullName),
                projectFullPath = Path.GetDirectoryName(project.FullName),
                sourceDir = { Path.GetDirectoryName(project.FullName) },
                headerDir = { Path.GetDirectoryName(project.FullName) },
                type = "PROJECT"
            };
        }
    }
}
