using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using DexterCS;
using System.IO;
using System.Reflection;
using Newtonsoft.Json.Linq;
using System.Collections.Generic;
using System.Text;

namespace DexterCSTest.src
{
    [TestClass]
    public class DexterConfigFileTest
    {
        DexterConfigFile dexterConfigFile;
        static string currentSolutionPath = Directory.GetParent(Environment.CurrentDirectory).Parent.FullName;
        readonly string dexterConfig = File.ReadAllText(currentSolutionPath + "/resource/dexter_cfg.json", Encoding.UTF8);
        readonly string dexterSnapshotConfig = File.ReadAllText(currentSolutionPath + "/resource/dexter_cfg-snapshot.json", Encoding.UTF8);

        public void DexterConfigFileInit()
        {
            dexterConfigFile = new DexterConfigFile();
        }
        [TestMethod]
        public void GetListFromDictionary_ShouldSuccess()
        {
            DexterConfigFileInit();

            //given
            JObject configMetadata = JObject.Parse(dexterConfig);
            List<string> expected = new List<string>();
            expected.Add(":/TEST//TestSI/TestDir4");
            expected.Add(":/TEST//TestSI/TestDir5");

            //when
            List<string> result = dexterConfigFile.GetListFromDictionary(configMetadata["sourceDir"]);

            //then
            Assert.AreEqual(expected[0], result[0]);
            Assert.AreEqual(expected[1], result[1]);
        }

        [TestMethod]
        public void GetSnapshotIdFromConfigFile_ShouldSuccess()
        {
            DexterConfigFileInit();
            //given
            JObject configMetadata = JObject.Parse(dexterSnapshotConfig);
            long expected = 155556;

            //when
            long result = Convert.ToInt64((string)configMetadata["snapshotId"]);

            //then
            Assert.AreEqual(expected, result);
        }
    }
}

