using NUnit.Framework;
using System;

namespace dexter_vs.Analysis.Config
{
    [TestFixture]
    public class ConfigurationTest
    {

        private Configuration configuration;

        [SetUp]
        public void Init()
        {
            var configuration = new Configuration()
            {
                dexterHome = "D:/Applications/dexter/0.9.2/dexter-cli_0.9.2_32/",
                projectName = "TestData",
                type = "PROJECT",
                sourceDir = { AppDomain.CurrentDomain.BaseDirectory + "../../TestData/SampleCppProject/" },
                headerDir = { AppDomain.CurrentDomain.BaseDirectory + "../../TestData/SampleCppProject/" },
                projectFullPath = AppDomain.CurrentDomain.BaseDirectory + "../../TestData/SampleCppProject/",
                dexterServerPort = "0000",
                dexterServerIp = "dexter-server"
            };
        }

        /// <summary>
        /// Configuration should be properly saved and loaded from file
        /// </summary>
        [Test]
        public void TestSaveLoad()
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

        /// <summary>
        /// Dexter should be found in proper path
        /// </summary>
        [Test]
        public void TestDexterFound()
        {
            Assert.IsTrue(configuration.IsDexterFound);
        }
    }
}
