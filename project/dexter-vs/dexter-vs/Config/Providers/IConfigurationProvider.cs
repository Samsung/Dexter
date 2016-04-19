namespace dexter_vs.Config.Providers
{
    /// <summary>
    /// Provides Dexter configuration
    /// </summary>
    internal interface IConfigurationProvider
    {
        /// <summary>
        /// Loads new Configuration object
        /// </summary>
        /// <returns>new Configuration</returns>
        Configuration Load();
    }
}
