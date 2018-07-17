using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using DexterCRC;
using DexterCRC.Src.CheckerLogic;

namespace DexterCSTest.Src.CRCLogic
{
    [TestClass]
    class CommentRulesTest
    {
        CommentRules rules;
        public void Init()
        {
            rules = new CommentRules();
        }

        [TestMethod]
        public void HasDefect_Should_True_without_Slashes()
        {
            Init();
            //given
            string statement = @" Write the description for the Class/Interface ";
            //when
            bool result = rules.HasDefect(statement);
            //then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasDefect_Should_False_with_Slashes()
        {
            Init();
            //given
            string statement = @"/// <summary>
                        /// Write the description for the Class/Interface.
                    /// </summary>
                        ";
            //when
            bool result = rules.HasDefect(statement);
            //then
            Assert.IsFalse(result);
        }
    }
}
