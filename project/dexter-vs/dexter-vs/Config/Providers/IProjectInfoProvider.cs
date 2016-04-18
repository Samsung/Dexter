namespace dexter_vs.Config.Providers
{
    /// <summary>
    /// Provides ProjectInfo 
    /// </summary>
    internal interface IProjectInfoProvider
    {
        /// <summary>
        /// Creates new ProjectInfo
        /// </summary>
        /// <returns>new ProjectInfo</returns>
        ProjectInfo Create();
    }
}
