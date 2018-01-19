using EnvDTE;
using System;
using System.IO;
using Dexter.Common.Config;

namespace Dexter.Common.Config.Providers
{
    /// <summary>
    /// Provides project info based on user preferences.
    /// Sets analysis scope to whole project.  
    /// </summary>
    public class ProjectInfoProvider : IProjectInfoProvider
    {
        /// <summary>
        /// DTE object
        /// </summary>
        protected readonly DTE dte;

        /// <summary>
        /// Whether project type should be set to 'Snapshot'
        /// </summary>
        public bool Snapshot
        {
            get;
            set;
        }

        /// <summary>
        /// Creates new ProjectInfoProvider
        /// </summary>
        /// <param name="serviceProvider"> service provider from the owner package</param>
        /// <param name="serviceProvider"> whether analysis type should be set to 'Snapshot' </param>
        public ProjectInfoProvider(IServiceProvider serviceProvider, bool snapshot = false)
        {
            dte = (DTE)serviceProvider.GetService(typeof(DTE));
            Snapshot = snapshot;
        }

        /// <summary>
        /// Creates new ProjectInfo based on user preferences 
        /// </summary>
        /// <returns>new ProjectInfo</returns>
        public virtual ProjectInfo Load()
        {   
            Projects projects = dte.Solution?.Projects;

            if (projects == null || projects.Count == 0 || string.IsNullOrEmpty(projects.Item(1).FullName))
            {
                return new ProjectInfo();
            }

            Project project = projects.Item(1);

            return new ProjectInfo()
            {
                projectName = Path.GetFileNameWithoutExtension(project.FullName),
                projectFullPath = Path.GetDirectoryName(project.FullName),
                sourceDir = { Path.GetDirectoryName(project.FullName) },
                headerDir = { Path.GetDirectoryName(project.FullName) },
                type = Snapshot ? "SNAPSHOT" : "PROJECT"
            };
        }
    }
}
