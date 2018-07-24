using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace DexterCRC.Tests
{
    [TestClass()]
    public class CamelCasingTests
    {
        CamelCasing camelCasing;
        void Init()
        {
            camelCasing = new CamelCasing();
        }

        [TestMethod]
        public void HasDefectTest_WithoutCamelCasing_ReturnsTrue()
        {
            Init();
            // Given
            string typeName = @"Name";
            // When
            bool result = camelCasing.HasDefect(typeName);
            // Then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasDefectTest_WithCamelCasing_ReturnsFalse()
        {
            Init();
            // Given
            string typeName = @"pName";
            // When
            bool result = camelCasing.HasDefect(typeName);
            // Then
            Assert.IsFalse(result);
        }

        [TestMethod]
        public void HasDefectTest_WithLambdaParameter_ReturnsFalse()
        {
            Init();
            // Given
            string parameterName = @"_";
            // When
            bool result = camelCasing.HasDefect(parameterName);
            // Then
            Assert.IsFalse(result);
        }

        [TestMethod]
        public void HasDefectTest_WithLambdaParameter_ReturnsTrue()
        {
            Init();
            // Given
            string parameterName = @"_pName";
            // When
            bool result = camelCasing.HasDefect(parameterName);
            // Then
            Assert.IsTrue(result);
        }
    }
}