using log4net;
using System;
using System.Collections.Generic;
using System.IO;
using System.Reflection;

namespace DexterCS
{
    public class CLIPluginInitializer : IDexterPluginInitializer
    {
        private ILog CliLog { get; set; }

        public CLIPluginInitializer(ILog cliLog)
        {
            CliLog = cliLog;
        }

        public void Init(List<IDexterPlugin> pluginHandlerList)
        {
            string pluginBasePath = DexterConfig.Instance.DexterHome + "/plugin";
            DirectoryInfo pluginBaseDir = new DirectoryInfo(pluginBasePath);
            if (pluginBaseDir.Exists == false)
            {
                throw new Exception("There is no exist Dexter Home.");
            }
            foreach (string fileOn in Directory.GetFiles(pluginBasePath))
            {
                FileInfo file = new FileInfo(fileOn);
                if (file.Extension.Equals(".dll"))
                {
                    AddPlugin(pluginHandlerList, fileOn);
                }
            }

            InitAllHandler(pluginHandlerList);
        }

        private void InitAllHandler(List<IDexterPlugin> pluginHandlerList)
        {
            //FEEDBACK 역할
            if (pluginHandlerList.Count == 0)
            {
                throw new Exception("There are no dexter plug-ins to add");
            }

            foreach(var plugin in pluginHandlerList)
            {
                plugin.Init();
                CliLog.Info(plugin.PLUGIN_DESCRIPTION +" :v" + plugin.VERSION + " loaded.");
            }
        }

        private void AddPlugin(List<IDexterPlugin> pluginHandlerList, string dllFileName)
        {
            try
            {
                Assembly pluginAssembly = Assembly.LoadFrom(dllFileName);
                foreach (Type pluginType in pluginAssembly.GetTypes())
                {
                    if (pluginType.IsPublic)
                    {
                        if (!pluginType.IsAbstract)
                        {
                            Type typeInterface = pluginType.GetInterface("IDexterPlugin", true);
                            if (typeInterface != null)
                            {
                                AvailablePlugin newPlugin = new AvailablePlugin();
                                newPlugin.Instance = (IDexterPlugin)Activator.CreateInstance(pluginAssembly.GetType(pluginType.ToString()));
                                pluginHandlerList.Add(newPlugin.Instance);
                            }
                        }
                    }
                }
            }catch(Exception e)
            {
                CliLog.Error("Plugin Loading failed:" + e.StackTrace);
            }
        }
    }
}