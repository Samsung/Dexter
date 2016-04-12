using EnvDTE;
using System;
using System.IO;

namespace dexter_vs.UI.Config
{
    /// <summary>
    /// Provides configuration object for Dexter based on user preferences.
    /// Sets analysis scope to whole solution.  
    /// </summary>
    internal sealed class ConfigurationProvider : IConfigurationProvider
    {
        /// <summary>
        /// Gets the service provider from the owner package.
        /// </summary>
        private readonly IServiceProvider serviceProvider;

        /// <summary>
        /// DTE object
        /// </summary>
        private readonly DTE dte;

        /// <summary>
        /// Creates new ConfigurationProvider
        /// </summary>
        /// <param name="serviceProvider"> service provider from the owner package</param>
        public ConfigurationProvider(IServiceProvider serviceProvider)
        {
            this.serviceProvider = serviceProvider;
            this.dte = (DTE)serviceProvider.GetService(typeof(DTE));
        }

        /// <summary>
        /// Creates new configuration 
        /// </summary>
        /// <returns>new configuration</returns>
        public Analysis.Configuration Create()
        {
            Solution solution = dte.Solution;
        
            var projectName = Path.GetFileNameWithoutExtension(solution.FullName);
            var projectFullPath = Path.GetDirectoryName(solution.FullName);
            var sourceDir =  projectFullPath ;
            var headerDir =  projectFullPath ;

            //TODO: Add configuration of these properties
            var dexterHome = "D:/Applications/dexter/0.9.2/dexter-cli_0.9.2_32/";
            var dexterServerIp = "dexter-server";
            var dexterServerPort = "0000";
            var type = "PROJECT";

            return new Analysis.Configuration()
            {
                dexterHome = dexterHome,
                dexterServerIp = dexterServerIp,
                dexterServerPort = dexterServerPort,
                projectName = projectName,
                type = type,
                projectFullPath = projectFullPath,
                sourceDir = { sourceDir },
                headerDir = { headerDir }, 
            };
        } 
    }
}
