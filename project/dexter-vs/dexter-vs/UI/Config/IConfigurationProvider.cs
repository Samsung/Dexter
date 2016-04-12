using dexter_vs.Analysis;

namespace dexter_vs.UI.Config
{
    /// <summary>
    /// Provider Dexter configuration
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
