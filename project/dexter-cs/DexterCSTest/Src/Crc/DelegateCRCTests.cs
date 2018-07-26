using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace DexterCRC.Tests
{
    [TestClass()]
    public class DelegateCRCTests
    {
        SuffixNaming suffixNaming;

        void Init()
        {
            suffixNaming = new SuffixNaming();
        }

        [TestMethod]
        public void HasDefectTest_WithInvalidDelegateName_Should_True()
        {
            Init();
            // Given
            string delegateName = @"NameChangedDelegate";
            NamingSet namingSet = new NamingSet
            {
                currentName = delegateName,
                basicWord = DexterCRCUtil.DELEGATE_SUFFIX
            };
            // When
            bool result = !suffixNaming.HasDefect(namingSet);
            // Then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasDefectTest_WithValidDelegateName_ReturnsFalse()
        {
            Init();
            // Given
            string delegateName = @"NameChangedDele";
            NamingSet namingSet = new NamingSet
            {
                currentName = delegateName,
                basicWord = DexterCRCUtil.DELEGATE_SUFFIX
            };
            // When
            bool result = !suffixNaming.HasDefect(namingSet);
            // Then
            Assert.IsFalse(result);
        }
    }
}