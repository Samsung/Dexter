using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using DexterCS;
using System.IO;
using DexterCRC;
using System.Text;
using DexterDepend;

namespace DexterCSTest
{
    [TestClass]
    public class CRCPluginTest
    {
        DexterCRCPlugin dexterCrcPlugin;
        DexterConfigFile dexterConfigFile;
        DexterDependPlugin dexterDependPlugin;
        static string currentSolutionPath = Directory.GetParent(Environment.CurrentDirectory).Parent.FullName;
        readonly string checkerConfig = File.ReadAllText(currentSolutionPath + "/resource/dexter_cfg.json", Encoding.UTF8);

        public void Init()
        {
            dexterConfigFile = new DexterConfigFile();
            dexterCrcPlugin = new DexterCRCPlugin();
            dexterDependPlugin = new DexterDependPlugin();
        }

        [TestMethod]
        public void InitPlugin_ShouldSuccess()
        {
            Init();
            //given

            //when

            //then
        }
    }
}
