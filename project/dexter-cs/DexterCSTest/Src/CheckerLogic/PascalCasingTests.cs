using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace DexterCRC.Tests
{
    [TestClass()]
    public class PascalCasingTests
    {
        PascalCasing pascalCasing;

        void Init()
        {
            pascalCasing = new PascalCasing();
        }

        [TestMethod]
        public void HasDefectTest_WithPascalCasing_ReturnsTrue()
        {
            Init();
            // Given
            string typeName = @"textReader";
            // When
            bool result = pascalCasing.HasDefect(typeName);
            // Then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasDefectTest_WithPascalCasing_ReturnsFalse()
        {
            Init();
            // Given
            string typeName = @"TestReader";
            // When
            bool result = pascalCasing.HasDefect(typeName);
            // Then
            Assert.IsFalse(result);

        }

        [TestMethod]
        public void HasDefectTest_WithPascalCasingWithInteger_ReturnsFalse()
        {
            Init();
            // Given
            string typeName = @"TestReader1301";
            // When
            bool result = pascalCasing.HasDefect(typeName);
            // Then
            Assert.IsFalse(result);
        }
    }
}