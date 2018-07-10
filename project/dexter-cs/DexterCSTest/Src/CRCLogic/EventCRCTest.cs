using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using DexterCRC;

namespace DexterCSTest
{
    [TestClass]
    public class EventCRCTest
    {
        SuffixNaming suffixNaming;
        void Init()
        {
            suffixNaming = new SuffixNaming();
        }

        [TestMethod]
        public void HasDefect_Should_True_with_InValid_Event_Type_Name()
        {
            Init();
            //given
            string eventTypeName = @"NameChangedEvent";
            NamingSet namingSet = new NamingSet
            {
                currentName = eventTypeName,
                basicWord = DexterCRCUtil.EVENT_TYPE_SUFFIX
            };
            //when
            bool result = suffixNaming.HasDefect(namingSet);
            //then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasDefect_Should_False_with_Valid_Event_Type_Name()
        {
            Init();
            //given
            string eventTypeName = @"NameChangedEventHandler";
            NamingSet namingSet = new NamingSet
            {
                currentName = eventTypeName,
                basicWord = DexterCRCUtil.EVENT_TYPE_SUFFIX
            };
            //when
            bool result = suffixNaming.HasDefect(namingSet);
            //then
            Assert.IsFalse(result);
        }
    }
}
