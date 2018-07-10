using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using DexterCRC;

namespace DexterCSTest
{
    [TestClass]
    public class DelegateCRCTest
    {
        SuffixNaming suffixNaming;

        void Init()
        {
            suffixNaming = new SuffixNaming();
        }

        [TestMethod]
        public void HasDefect_Should_True_with_Invalid_Delegate_Name()
        {
            Init();
            //given
            string delegateName = @"NameChangedDelegate";
            NamingSet namingSet = new NamingSet {
                currentName = delegateName,
                basicWord = DexterCRCUtil.DELEGATE_SUFFIX
            };
            //when
            bool result = !suffixNaming.HasDefect(namingSet);
            //then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasDefect_Should_False_with_Valid_Delegate_Name()
        {
            Init();
            //given
            string delegateName = @"NameChangedDele";
            NamingSet namingSet = new NamingSet
            {
                currentName = delegateName,
                basicWord = DexterCRCUtil.DELEGATE_SUFFIX
            };
            //when
            bool result = !suffixNaming.HasDefect(namingSet);
            //then
            Assert.IsFalse(result);
        }
    }
}
