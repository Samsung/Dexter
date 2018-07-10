using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using DexterDepend;
using System.Collections.Generic;

namespace DexterCSTest.Src.DependLogic
{
    [TestClass]
    public class VconfETCTest
    {
        VconfMethod vconfMethod;
        List<string> subMethodList;

        public void Init()
        {
            vconfMethod = new VconfMethod();
            subMethodList = new List<string>();
            subMethodList.AddRange(new string[] { "Vconf.NotifyKeyChanged" });
        }
        [TestMethod]
        public void HasVConfMethod_Should_True_vconf_etc()
        {
            //given
            Init();
            string methodName = "Vconf.NotifyKeyChanged(IntKey, Outkey, out GetValue)";

            //when
            bool result = vconfMethod.HasVconfMethod(subMethodList, methodName);

            //then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasVConfMethod_Should_False_other_method()
        {
            //given
            Init();
            string methodName = "Vconf.NotifyKeysTest(IntKey, out GetValue)";

            //when
            bool result = vconfMethod.HasVconfMethod(subMethodList, methodName);

            //then
            Assert.IsFalse(result);
        }

        [TestMethod]
        public void HasVConfMethod_Should_True_vconf_get_int()
        {
            //given
            Init();
            string methodName = "Vconf.NotifyKeyChanged(IntKey, outKey, out GetValue)";

            //when
            bool result = vconfMethod.HasVconfMethod(subMethodList, methodName);

            //then
            Assert.IsTrue(result);
        }
    }
}
