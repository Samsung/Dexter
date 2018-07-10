using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using DexterCRC;

namespace DexterCSTest.checkerLogic
{
    [TestClass]
    public class NamingTest
    {
        PrefixNaming prefixNaming;

        void Init() {
            prefixNaming = new PrefixNaming();
        }

        [TestMethod]
        public void HasDefect_Should_True_without_an_I()
        {
            //given
            Init();
            string interfaceName = @"DexterInterfaceNameTest";
            NamingSet namingSet = new NamingSet {
                currentName = interfaceName,
                basicWord = "I"
            };
            //when
            bool result = prefixNaming.HasDefect(namingSet);
            //then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasDefect_Should_False_with_an_I()
        {
            Init();
            //given
            string interfaceName = @"IDexterInterfaceNameTest";
            NamingSet namingSet = new NamingSet
            {
                currentName = interfaceName,
                basicWord = "I"
            };
            //when
            bool result = prefixNaming.HasDefect(namingSet);
            //then
            Assert.IsFalse(result);
        }
    }
}
