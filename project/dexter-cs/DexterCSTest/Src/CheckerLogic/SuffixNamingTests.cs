using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace DexterCRC.Tests
{
    [TestClass()]
    public class SuffixNamingTests
    {
        SuffixNaming suffixNaming;

        void Init()
        {
            suffixNaming = new SuffixNaming();
        }

        [TestMethod]
        public void HasDefectTest_WithoutAnI_ReturnsTrue()
        {
            // Given
            Init();
            string interfaceName = @"ClassWork";
            NamingSet namingSet = new NamingSet
            {
                currentName = interfaceName,
                basicWord = "er"
            };
            // When
            bool result = suffixNaming.HasDefect(namingSet);
            // Then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasDefectTest_WithAnI_ReturnsFalse()
        {
            Init();
            // Given
            string interfaceName = @"ClassWorker";
            NamingSet namingSet = new NamingSet
            {
                currentName = interfaceName,
                basicWord = "er"
            };
            // When
            bool result = suffixNaming.HasDefect(namingSet);
            // Then
            Assert.IsFalse(result);
        }
    }
}