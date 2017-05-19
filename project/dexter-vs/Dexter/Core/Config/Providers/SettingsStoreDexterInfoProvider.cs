using Microsoft.VisualStudio.Settings;
using Microsoft.VisualStudio.Shell.Settings;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Dexter.Common.Config;

namespace Dexter.Config.Providers
{
    /// <summary>
    /// Uses Visual Studio Settings Store for storing dexter info 
    /// </summary>
    internal class SettingsStoreDexterInfoProvider : IDexterInfoProvider
    {
        /// <summary>
        /// Settigns store
        /// </summary>
        private WritableSettingsStore settingsStore;

        public const string DexterStoreName = "Dexter";

        /// <summary>
        /// Creates new SettingsStoreDexterInfoProvider
        /// </summary>
        /// <param name="serviceProvider"> service provider from the owner package</param>
        public SettingsStoreDexterInfoProvider(IServiceProvider serviceProvider)
        {
            SettingsManager settingsManager = new ShellSettingsManager(serviceProvider);
            settingsStore = settingsManager.GetWritableSettingsStore(SettingsScope.UserSettings);
        }

        /// <summary>
        /// Loads DexterInfo from Settings Store
        /// </summary>
        /// <returns>loaded DexterInfo</returns>
        public DexterInfo Load()
        {
            if (!settingsStore.CollectionExists(DexterStoreName))
            {
                return new DexterInfo();
            }
            else
            {
                return new DexterInfo()
                {
                    dexterHome = settingsStore.GetString(DexterStoreName, "dexterHome"),
                    dexterServerIp = settingsStore.GetString(DexterStoreName, "dexterServerIp"),
                    dexterServerPort = settingsStore.GetInt32(DexterStoreName, "dexterServerPort"),
                    userName = settingsStore.GetString(DexterStoreName, "userName"),
                    userPassword = settingsStore.GetString(DexterStoreName, "userPassword"),
                    standalone = settingsStore.GetBoolean(DexterStoreName, "standalone")
                };
            }
            
        }

        /// <summary>
        /// Saves DexterInfo to Settings Store
        /// </summary>
        /// <param name="dexterInfo">DexterInfo to save</param>
        public void Save(DexterInfo dexterInfo)
        {
            if (!settingsStore.CollectionExists(DexterStoreName))
            {
                settingsStore.CreateCollection(DexterStoreName);
            }

            settingsStore.SetString(DexterStoreName, "dexterHome", dexterInfo.dexterHome);
            settingsStore.SetString(DexterStoreName, "dexterServerIp", dexterInfo.dexterServerIp);
            settingsStore.SetInt32(DexterStoreName, "dexterServerPort", dexterInfo.dexterServerPort);
            settingsStore.SetString(DexterStoreName, "userName", dexterInfo.userName);
            settingsStore.SetString(DexterStoreName, "userPassword", dexterInfo.userPassword);
            settingsStore.SetBoolean(DexterStoreName, "standalone", dexterInfo.standalone);
        }
    }
}
