namespace Dexter.Config.Providers
{
    /// <summary>
    /// Provides configuration object for Dexter based on info providers
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
        /// <param name="projectInfoProvider"> provides info about project to analyze </param>
        /// <param name="dexterInfoProvider"> provides info about Dexter configuration </param>
        public ConfigurationProvider(IProjectInfoProvider projectInfoProvider, IDexterInfoProvider dexterInfoProvider)
        {
            this.projectInfoProvider = projectInfoProvider;
            this.dexterInfoProvider = dexterInfoProvider;
        }

        /// <summary>
        /// Loads new configuration 
        /// </summary>
        /// <returns>new configuration</returns>
        public Configuration Load()
        {   
            return new Configuration(LoadProjectInfo(), LoadDexterInfo());
        }

        /// <summary>
        /// Loads new Dexter info from dexterInfoProvider
        /// </summary>
        /// <returns>new DexterInfo</returns>
        public DexterInfo LoadDexterInfo()
        {
            return dexterInfoProvider.Load();
        }

        /// <summary>
        /// Loads new Project info from projectInfoProvider
        /// </summary>
        /// <returns>new ProjectInfo</returns>
        public ProjectInfo LoadProjectInfo()
        {
            return projectInfoProvider.Load();
        }
    }
}
