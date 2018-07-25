using Microsoft.VisualStudio.TestTools.UnitTesting;
using DexterCRC;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DexterCRC.Tests
{
    [TestClass()]
    public class NounNamingTests
    {
        private NounNaming nounNaming;

        private void Initialize()
        {
            nounNaming = new NounNaming();
        }

        [TestMethod()]
        public void SplitCamelCaseTest()
        {
            Initialize();

            // Given
            string camelCaseWord = "camelCaseWord";

            // When
            string[] splitCamelCaseWord = nounNaming.SplitOnCamelCase(camelCaseWord);

            // Then
            Assert.IsTrue(splitCamelCaseWord.SequenceEqual(new string[] { "camel", "Case", "Word" }));
        }
    }
}