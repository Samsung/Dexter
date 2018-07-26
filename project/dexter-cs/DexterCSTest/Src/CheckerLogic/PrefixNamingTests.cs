using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace DexterCRC.Tests
{
    [TestClass()]
    public class PrefixNamingTests
    {
        PrefixNaming prefixNaming;

        void Init()
        {
            prefixNaming = new PrefixNaming();
        }

        [TestMethod]
        public void HasDefectTest_WithoutAnI_ReturnsTrue()
        {
            // Given
            Init();
            string interfaceName = @"DexterInterfaceNameTest";
            NamingSet namingSet = new NamingSet
            {
                currentName = interfaceName,
                basicWord = "I"
            };
            // When
            bool result = prefixNaming.HasDefect(namingSet);
            // Then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasDefectTest_WithAnI_ReturnsFalse()
        {
            Init();
            // Given
            string interfaceName = @"IDexterInterfaceNameTest";
            NamingSet namingSet = new NamingSet
            {
                currentName = interfaceName,
                basicWord = "I"
            };
            // When
            bool result = prefixNaming.HasDefect(namingSet);
            // Then
            Assert.IsFalse(result);
        }
    }
}