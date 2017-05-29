using Dexter.Common.Config;

namespace Dexter.Common.Config.Providers
{
    /// <summary>
    /// Provides ProjectInfo 
    /// </summary>
    public interface IProjectInfoProvider
    {
        /// <summary>
        /// Loads new ProjectInfo
        /// </summary>
        /// <returns>new ProjectInfo</returns>
        ProjectInfo Load();
    }
}
