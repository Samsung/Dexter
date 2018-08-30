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

