using NUnit.Framework;
using System;

namespace dexter_vs.Analysis.Config
{
    [TestFixture]
    public class ConfigurationTest
    {
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
    }
}
