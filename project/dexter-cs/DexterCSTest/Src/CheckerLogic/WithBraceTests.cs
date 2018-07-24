using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace DexterCRC.Tests
{
    [TestClass()]
    public class WithBraceTests
    {
        WithBrace brace;
        public void Init()
        {
            brace = new WithBrace();
        }

        [TestMethod]
        public void HasDefectTest_WithoutBrace_ReturnsTrue()
        {
            Init();
            // Given
            string statement = @" int a = 10; ";
            // When
            bool result = brace.HasDefect(statement);
            // Then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasDefectTest_With_Brace_ReturnsFalse()
        {
            Init();
            // Given
            string statement = @"{
                                    int a = 10; 
                                    int b = 20;
                                  }";
            // When
            bool result = brace.HasDefect(statement);
            // Then
            Assert.IsFalse(result);
        }
    }
}