using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.IO;
using System.Text;

namespace DexterCRC.Tests
{
    [TestClass()]
    public class DexterCRCPluginTests
    {
        DexterCRCPlugin dexterCrcPlugin;
        static string currentSolutionPath = Directory.GetParent(Environment.CurrentDirectory).Parent.FullName;
        readonly string checkerConfig = File.ReadAllText(currentSolutionPath + "/resource/dexter_cfg.json", Encoding.UTF8);

        [TestMethod]
        public void DexterCRCPluginTest_InitiatesDexterCRCPlugin()
        {
            // Given
            dexterCrcPlugin = new DexterCRCPlugin();
            // When

            // Then
            Assert.IsNotNull(dexterCrcPlugin);
        }
    }
}