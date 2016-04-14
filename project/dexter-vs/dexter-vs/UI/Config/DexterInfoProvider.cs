using dexter_vs.Analysis.Config;
using EnvDTE;
using System;

namespace dexter_vs.UI.Config
{
    /// <summary>
    /// Provides Dexter info based on user preferences.
    /// </summary>
    internal class DexterInfoProvider : IDexterInfoProvider
    {
        /// <summary>
        /// DTE object
        /// </summary>
        protected readonly DTE dte;

        /// <summary>
        /// Creates new DexterInfoProvider
        /// </summary>
        /// <param name="serviceProvider"> service provider from the owner package</param>
        public DexterInfoProvider(IServiceProvider serviceProvider)
        {
            dte = (DTE)serviceProvider.GetService(typeof(DTE));
        }

        /// <summary>
        /// Creates new DexterInfo based on user preferences 
        /// </summary>
        /// <returns>new DexterInfo</returns>
        public virtual DexterInfo Create()
        {   
            return new DexterInfo()
            {
                //TODO: Add configuration of these properties
                dexterHome = "D:/Applications/dexter/0.9.2/dexter-cli_0.9.2_32/",
                dexterServerIp = "dexter-server",
                dexterServerPort = "0000"
            };
        }
    }
}
