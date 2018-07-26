using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace DexterCRC.Tests
{
    [TestClass()]
    public class FieldCRCTests
    {
        FieldCRC filedCRC;

        void Init()
        {
            filedCRC = new FieldCRC();
        }

        [TestMethod]
        public void HasInvalidModifierTest_HasInvalidProtectedModifier_ReturnsTrue()
        {
            Init();
            // Given
            string modifier = @" protected static";
            // When
            bool result = filedCRC.HasInvalidModifier(modifier);
            // Then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasInvalidModifierTest_HasInvalidStaticModifier_ReturnsTrue()
        {
            Init();
            // Given
            string modifier = @"public static";
            // When
            bool result = filedCRC.HasInvalidModifier(modifier);
            // Then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasInvalidModifierTest_HasInvalidStaticModifierWithBlank_ReturnsTrue()
        {
            Init();
            // Given
            string modifier = @" public static ";
            // When
            bool result = filedCRC.HasInvalidModifier(modifier);
            // Then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasInvalidModifierTest_HasValidPublicModifier_ReturnsFalse()
        {
            Init();
            // Given
            string modifier = @"public";
            // When
            bool result = filedCRC.HasInvalidModifier(modifier);
            // Then
            Assert.IsFalse(result);
        }

        [TestMethod]
        public void HasInvalidModifierTest_HasValidPrivateModifier_ReturnsFalse()
        {
            Init();
            // Given
            string modifier = @"private  ";
            // When
            bool result = filedCRC.HasInvalidModifier(modifier);
            // Then
            Assert.IsFalse(result);
        }
    }
}