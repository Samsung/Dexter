﻿using NUnit.Framework;
using System;
using Dexter.Common.Config;

namespace Dexter.Common.Tests.Config
{
    [TestFixture]
    public class ConfigurationTest
    {

        private Configuration configuration;

        [SetUp]
        public void Init()
        {
            configuration = new Configuration()
            {
                dexterHome = "D:/Applications/dexter/0.9.2/dexter-cli_0.9.2_32/",
                projectName = "TestData",
                type = "PROJECT",
                sourceDir = { AppDomain.CurrentDomain.BaseDirectory + "../../TestData/SampleCppProject/" },
                headerDir = { AppDomain.CurrentDomain.BaseDirectory + "../../TestData/SampleCppProject/" },
                projectFullPath = AppDomain.CurrentDomain.BaseDirectory + "../../TestData/SampleCppProject/",
                dexterServerPort = "0",
                dexterServerIp = "dexter-server"
            };
        }

        /// <summary>
        /// Configuration should be properly saved and loaded from file
        /// </summary>
        [Test]
        public void Load_configurationIsLoadedProperly()
        {
            Configuration config = new Configuration()
            {
                dexterHome = "TestPath"
            };

            string configPath = AppDomain.CurrentDomain.BaseDirectory + "../../TestData/test-config.json";

            config.Save(configPath);

            Configuration loadedConfig = Configuration.Load(configPath);

            Assert.AreEqual("TestPath", loadedConfig.dexterHome); 
        }
    }
}
