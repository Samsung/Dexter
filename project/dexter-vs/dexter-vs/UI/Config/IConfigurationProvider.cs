using dexter_vs.Analysis.Config;

namespace dexter_vs.UI.Config
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
