using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace DexterCRC.Tests
{
    [TestClass()]
    public class DexterCRCUtilTests
    {
        [TestMethod()]
        public void HasSuffixTest_StringWithSuffix_ReturnsTrue()
        {
            // Given
            string name = "Namesuffix1";
            string suffix = "suffix1";

            // When
            bool result = DexterCRCUtil.HasSuffix(name, suffix);

            // Then
            Assert.IsTrue(result);
        }

        [TestMethod()]
        public void HasSuffixTest_StringWithoutSuffix_ReturnsTrue()
        {
            // Given
            string name = "Name";
            string suffix = "suffix1";

            // When
            bool result = DexterCRCUtil.HasSuffix(name, suffix);

            // Then
            Assert.IsFalse(result);
        }
    }
}