using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace DexterCRC.Tests
{
    [TestClass()]
    public class WithoutUnderscoreTests
    {
        [TestMethod]
        public void HasDefectTest_WithUnderscore_ReturnsFalse()
        {
            // Given
            string interfaceName = @"Dexter_CS_Test";

            // When
            bool result = (new WithoutUnderscore()).HasDefect(interfaceName);

            // Then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasDefectTest_CorrectInterfaceName_ReturnsFalse()
        {
            // Given
            string interfaceName = @"IDexterCSTest";

            // When
            bool result = (new WithoutUnderscore()).HasDefect(interfaceName);

            // Then
            Assert.IsFalse(result);
        }
    }
}