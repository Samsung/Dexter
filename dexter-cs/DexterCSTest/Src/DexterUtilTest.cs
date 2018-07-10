using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using DexterCS;

namespace DexterCSTest.src
{
    [TestClass]
    public class DexterUtilTest
    {
        [TestMethod]
        public void RefinePath_ShouldRefineFilePath() 
        {
            //given
            string tempPath = @":/DEV//temp\DexterCS-cli_#.#.#_64";
            string expectedPath = @":/DEV/temp/DexterCS-cli_#.#.#_64";

            //when
            string result = DexterUtil.RefinePath(tempPath);

            //then
            Assert.AreEqual(expectedPath, result);
        }
    }
}
