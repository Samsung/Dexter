using Microsoft.VisualStudio.TestTools.UnitTesting;
using DexterCRC;

namespace DexterCSTest.checkerLogic
{
    [TestClass]
    public class PascalCasingTest
    {
        PascalCasing pascalCasing;

        void Init()
        {
            pascalCasing = new PascalCasing();
        }

        [TestMethod]
        public void HasDefect_Should_True_with_Pascal_Casing()
        {
            Init();
            //given
            string typeName = @"textReader";
            //when
            bool result = pascalCasing.HasDefect(typeName);
            //then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasDefect_Should_False_with_Pascal_Casing()
        {
            Init();
            //given
            string typeName = @"TestReader";
            //when
            bool result = pascalCasing.HasDefect(typeName);
            //then
            Assert.IsFalse(result);

        }

        [TestMethod]
        public void HasDefect_Should_False_with_Pascal_Casing_Whti_Integer()
        {
            Init();
            //given
            string typeName = @"TestReader1301";
            //when
            bool result = pascalCasing.HasDefect(typeName);
            //then
            Assert.IsFalse(result);
        }
    }
}
