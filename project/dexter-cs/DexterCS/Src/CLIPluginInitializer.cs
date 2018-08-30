#region Copyright notice
/**
 * Copyright (c) 2018 Samsung Electronics, Inc.,
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
#endregion
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
                throw new Exception("Dexter Home does not exist");
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
                throw new Exception("There are no Dexter plug-ins to load");
            }

            foreach (var plugin in pluginHandlerList)
            {
                plugin.Init();
                CliLog.Info(plugin.PLUGIN_DESCRIPTION + " :v" + plugin.VERSION + " loaded.");
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
            }
            catch (Exception e)
            {
                CliLog.Error("Plugin Loading failed:" + e.StackTrace);
            }
        }
    }
}