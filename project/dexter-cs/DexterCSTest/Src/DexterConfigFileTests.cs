using Microsoft.VisualStudio.TestTools.UnitTesting;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.IO;
using System.Text;

namespace DexterCS.Tests
{
    [TestClass()]
    public class DexterConfigFileTests
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
        public void DexterConfigFileTest_InitiatesDexterConfigFile()
        {
            // Given
            dexterConfigFile = new DexterConfigFile();
            // When

            // Then
            Assert.IsNotNull(dexterConfigFile);
        }

        [TestMethod]
        public void GetListFromDictionaryTest_TwoElementDictionary_ShouldReturnCorrectList()
        {
            DexterConfigFileInit();

            // Given
            JObject configMetadata = JObject.Parse(dexterConfig);
            List<string> expected = new List<string>();
            expected.Add(":/TEST//TestSI/TestDir4");
            expected.Add(":/TEST//TestSI/TestDir5");

            // When
            List<string> result = dexterConfigFile.GetListFromDictionary(configMetadata["sourceDir"]);

            // Then
            Assert.AreEqual(expected[0], result[0]);
            Assert.AreEqual(expected[1], result[1]);
        }

        [TestMethod]
        public void GetSnapshotIdFromConfigFileTest_SnapshotIDProvided_ReturnCorrectSnaphshotId()
        {
            DexterConfigFileInit();
            // Given
            JObject configMetadata = JObject.Parse(dexterSnapshotConfig);
            long expected = 155556;

            // When
            long result = Convert.ToInt64((string)configMetadata["snapshotId"]);

            // Then
            Assert.AreEqual(expected, result);
        }
    }
}

