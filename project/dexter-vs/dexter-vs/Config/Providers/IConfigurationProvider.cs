namespace dexter_vs.Config.Providers
{
    /// <summary>
    /// Provides Dexter configuration
    /// </summary>
    internal interface IConfigurationProvider
    {
        /// <summary>
        /// Creates new Configuration object
        /// </summary>
        /// <returns>new Configuration</returns>
        Configuration Create();
    }
}
