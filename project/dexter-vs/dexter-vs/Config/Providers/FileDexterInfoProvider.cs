using System;
using System.IO;

namespace dexter_vs.Config.Providers
{
    /// <summary>
    /// Provides DexterInfo from given configuration file
    /// </summary>
    internal class FileDexterInfoProvider : IDexterInfoProvider
    {
        /// <summary>
        /// Path to configuration file
        /// </summary>
        public string Path { get; set; }

        /// <summary>
        /// Creates new FileDexterInfoProvider
        /// </summary>
        /// <param name="path">path to configuration file</param>
        public FileDexterInfoProvider(string path)
        {
            Path = path;
        }

        public DexterInfo Load()
        {
            return File.Exists(Path) ? DexterInfo.Load(Path) : new DexterInfo();
        }

        public void Save(DexterInfo dexterInfo)
        {
            Configuration configuration = new Configuration(new ProjectInfo(), dexterInfo);
            configuration.Save(Path);
        }
    }
}
