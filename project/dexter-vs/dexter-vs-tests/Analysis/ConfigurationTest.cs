using NUnit.Framework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace dexter_vs.Analysis
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
