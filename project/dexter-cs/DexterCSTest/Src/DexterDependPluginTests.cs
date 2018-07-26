using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.IO;
using System.Text;

namespace DexterDepend.Tests
{
    [TestClass()]
    public class DexterDependPluginTests
    {
        DexterDependPlugin dexterDependPlugin;
        static string currentSolutionPath = Directory.GetParent(Environment.CurrentDirectory).Parent.FullName;
        readonly string checkerConfig = File.ReadAllText(currentSolutionPath + "/resource/dexter_cfg.json", Encoding.UTF8);

        [TestMethod]
        public void DexterDependPluginTest_InitiatesDexterDependPlugin()
        {
            // Given
            dexterDependPlugin = new DexterDependPlugin();
            // When

            // Then
            Assert.IsNotNull(dexterDependPlugin);
        }
    }
}