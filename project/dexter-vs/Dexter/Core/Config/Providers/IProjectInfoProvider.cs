namespace Dexter.Config.Providers
{
    /// <summary>
    /// Provides ProjectInfo 
    /// </summary>
    internal interface IProjectInfoProvider
    {
        /// <summary>
        /// Loads new ProjectInfo
        /// </summary>
        /// <returns>new ProjectInfo</returns>
        ProjectInfo Load();
    }
}
