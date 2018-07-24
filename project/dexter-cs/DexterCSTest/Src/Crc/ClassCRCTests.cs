using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace DexterCRC.Tests
{
    [TestClass()]
    public class ClassCRCTests
    {
        ClassCRC classCRC;

        void Init()
        {
            classCRC = new ClassCRC();
        }

        [TestMethod]
        public void CheckEventNamingTest_ReturnsTrue_Invalid_Class_Name()
        {
            Init();
            // Given
            string className = @"MyEvent";
            string baseName = @"EventArgs";
            // When
            bool result = classCRC.CheckEventNaming(className, baseName);
            // Then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void CheckEventNamingTest_ValidClassName_ReturnsFalse()
        {
            Init();
            // Given
            string className = @"MyEventArgs";
            string baseName = @"EventArgs";
            // When
            bool result = classCRC.CheckEventNaming(className, baseName);
            // Then
            Assert.IsFalse(result);
        }

        [TestMethod]
        public void CheckAttributeNamingTest_InvalidClassName_ReturnsTrue()
        {
            Init();
            // Given
            string className = @"User";
            string baseName = @"Attribute";
            // When
            bool result = classCRC.CheckAttributeNaming(className, baseName);
            // Then
            Assert.IsTrue(result);
        }
        [TestMethod]
        public void CheckAttributeNamingTest_ValidClassName_ReturnsFalse()
        {
            Init();
            // Given
            string className = @"HelpAttribute";
            string baseName = @"Attribute";
            // When
            bool result = classCRC.CheckAttributeNaming(className, baseName);
            // Then
            Assert.IsFalse(result);
        }
    }
}