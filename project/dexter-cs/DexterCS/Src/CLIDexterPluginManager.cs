using DexterCS.Client;
using log4net;
using System.Collections.Generic;

namespace DexterCS
{
    public class CLIDexterPluginManager : BaseDexterPluginManager
    {
        private ILog cliLog;
        private IDexterCLIOption cliOption;

        public CLIDexterPluginManager(IDexterPluginInitializer initializer, IDexterClient client,
            ILog cliLog, IDexterCLIOption cliOption)
            : base(initializer, client)
        {
            this.cliLog = cliLog;
            this.cliOption = cliOption;
        }

        public new void InitDexterPlugins()
        {
            pluginList = new List<IDexterPlugin>(0);
            initializer.Init(pluginList);
        }
    }
}