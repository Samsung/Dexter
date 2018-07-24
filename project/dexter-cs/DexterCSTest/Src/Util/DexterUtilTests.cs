using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;

namespace DexterCS.Tests
{
    [TestClass()]
    public class DexterUtilTests
    {
        [TestMethod]
        public void RefinePathTest_RefinesFilePath()
        {
            // Given
            string tempPath = @":/DEV//temp\DexterCS-cli_#.#.#_64";
            string expectedPath = @":/DEV/temp/DexterCS-cli_#.#.#_64";

            // When
            string result = DexterUtil.RefinePath(tempPath);

            // Then
            Assert.AreEqual(expectedPath, result);
        }
    }
}