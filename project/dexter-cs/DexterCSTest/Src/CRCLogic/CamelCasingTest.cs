using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using DexterCRC;

namespace DexterCSTest.checkerLogic
{

    [TestClass]
    public class CamelCasingTest
    {
        CamelCasing camelCasing;
        void Init() {
            camelCasing = new CamelCasing();
        }

        [TestMethod]
        public void HasDefect_Should_True_without_Calmel_Casing()
        {
            Init();
            //given
            string typeName = @"Name";
            //when
            bool result = camelCasing.HasDefect(typeName);
            //then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasDefect_Should_False_with_Calmel_Casing()
        {
            Init();
            //given
            string typeName = @"pName";
            //when
            bool result = camelCasing.HasDefect(typeName);
            //then
            Assert.IsFalse(result);
        }

        [TestMethod]
        public void HasDefect_Should_False_with_Lambda_Parameter()
        {
            Init();
            //given
            string parameterName = @"_";
            //when
            bool result = camelCasing.HasDefect(parameterName);
            //then
            Assert.IsFalse(result);
        }

        [TestMethod]
        public void HasDefect_Should_True_with_Lambda_Parameter()
        {
            Init();
            //given
            string parameterName = @"_pName";
            //when
            bool result = camelCasing.HasDefect(parameterName);
            //then
            Assert.IsTrue(result);
        }
    }
}
