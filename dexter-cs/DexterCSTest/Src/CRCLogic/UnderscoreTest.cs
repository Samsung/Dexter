using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using DexterCRC;

namespace DexterCSTest
{
    [TestClass]
    public class UnderscoreNamingTest
    {
        [TestMethod]
        public void HasUnderscoreDefect_Should_True_with_Underscore()
        {
            //givin
            string interfaceName = @"Dexter_CS_Test";

            //when
             bool result = (new WithoutUnderscore()).HasDefect(interfaceName);

            //then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasUnderscoreDefect_Should_False_Correct_ClassName()
        {
            //givin
            string interfaceName = @"IDexterCSTest";

            //when
            bool result = (new WithoutUnderscore()).HasDefect(interfaceName);

            //then
            Assert.IsFalse(result);
        }
    }
}
