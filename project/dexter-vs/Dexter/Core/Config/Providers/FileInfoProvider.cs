using EnvDTE;
using System;
using System.IO;

namespace Dexter.Config.Providers
{
    /// <summary>
    /// Provides project info based on user preferences.
    /// Sets analysis scope to currently opened file.  
    /// </summary>
    internal class FileInfoProvider : IProjectInfoProvider
    {
        /// <summary>
        /// DTE object
        /// </summary>
        protected readonly DTE dte;

        /// <summary>
        /// Creates new ProjectInfoProvider
        /// </summary>
        /// <param name="serviceProvider"> service provider from the owner package</param>
        public FileInfoProvider(IServiceProvider serviceProvider)
        {
            dte = (DTE)serviceProvider.GetService(typeof(DTE));
        }

        /// <summary>
        /// Creates new ProjectInfo based on currently opened file
        /// </summary>
        /// <returns>new ProjectInfo</returns>
        public virtual ProjectInfo Load()
        {
            Document doc = dte.ActiveDocument;

            return doc == null ?
            new ProjectInfo() :
            new ProjectInfo()
            {
                projectName = Path.GetFileNameWithoutExtension(doc.FullName),
                projectFullPath = Path.GetDirectoryName(doc.FullName),
                sourceDir = { Path.GetDirectoryName(doc.FullName) },
                headerDir = { Path.GetDirectoryName(doc.FullName) },
                fileName = { Path.GetFileName(doc.FullName) },
                type = "FILE"
            };
        }
    }
}
