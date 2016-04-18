namespace dexter_vs.Config.Providers
{
    /// <summary>
    /// Provides configuration object for Dexter based on user preferences.
    /// Sets analysis scope to whole solution.  
    /// </summary>
    internal class ConfigurationProvider : IConfigurationProvider
    {
        /// <summary>
        /// ProjectInfo provider
        /// </summary>
        protected readonly IProjectInfoProvider projectInfoProvider;

        /// <summary>
        /// DexterInfo provider
        /// </summary>
        protected readonly IDexterInfoProvider dexterInfoProvider;

        /// <summary>
        /// Creates new ConfigurationProvider
        /// </summary>
        /// <param name="serviceProvider"> service provider from the owner package</param>
        public ConfigurationProvider(IProjectInfoProvider projectInfoProvider, IDexterInfoProvider dexterInfoProvider)
        {
            this.projectInfoProvider = projectInfoProvider;
            this.dexterInfoProvider = dexterInfoProvider;
        }

        /// <summary>
        /// Creates new configuration 
        /// </summary>
        /// <returns>new configuration</returns>
        public Configuration Create()
        {   
            return new Configuration(projectInfoProvider.Create() , dexterInfoProvider.Create());
        }
    }
}
