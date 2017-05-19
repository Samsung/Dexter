using Dexter.Common.Config;

namespace Dexter.Config.Providers
{
    /// <summary>
    /// Provides DexterInfo 
    /// </summary>
    public interface IDexterInfoProvider
    {
        /// <summary>
        /// Loads new DexterInfo
        /// </summary>
        /// <returns>new DexterInfo</returns>
        DexterInfo Load();

        /// <summary>
        /// Saves DexterInfo
        /// </summary>
        /// <param name="dexterInfo">DexterInfo to save</param>
        void Save(DexterInfo dexterInfo); 
    }
}
