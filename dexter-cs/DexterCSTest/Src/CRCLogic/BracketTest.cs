using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using DexterCRC;

namespace DexterCSTest.checkerLogic
{
    [TestClass]
    public class WithBraceTest
    {
        WithBrace brace;
        public void Init()
        {
            brace = new WithBrace();
        }

        [TestMethod]
        public void HasDefect_Should_True_without_Brace()
        {
            Init();
            //given
            string statement = @" int a = 10; ";
            //when
            bool result = brace.HasDefect(statement);
            //then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasDefect_Should_False_with_Brace()
        {
            Init();
            //given
            string statement = @"{
                                    int a = 10; 
                                    int b = 20;
                                  }";
            //when
            bool result = brace.HasDefect(statement);
            //then
            Assert.IsFalse(result);
        }
    }
}
