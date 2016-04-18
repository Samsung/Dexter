namespace dexter_vs.Config.Providers
{
    /// <summary>
    /// Provides DexterInfo 
    /// </summary>
    internal interface IDexterInfoProvider
    {
        /// <summary>
        /// Creates new DexterInfo
        /// </summary>
        /// <returns>new DexterInfo</returns>
        DexterInfo Create();
    }
}
