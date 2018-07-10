using System.Linq;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using DexterDepend;
using System.Collections.Generic;

namespace DexterCSTest
{
    [TestClass]
    public class VconfGetTest
    {
        VconfMethod vconfMethod;
        List<string> subMethodList;

        public void Init()
        {
            vconfMethod = new VconfMethod();
            subMethodList = new List<string>();
            subMethodList.AddRange(new string[] { "Vconf.GetString", "Vconf.GetInt"});
        }
        [TestMethod]
        public void HasVConfMethod_Should_True_vconf_get()
        {
            //given
            Init();
            string methodName = "Vconf.GetInt(IntKey, out GetValue)";

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
            string methodName = "Vconf.GetTest(IntKey, out GetValue)";

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
            string methodName = "Vconf.GetInt(IntKey, out GetValue)";

            //when
            bool result = vconfMethod.HasVconfMethod(subMethodList, methodName);

            //then
            Assert.IsTrue(result);
        }
    }
}
