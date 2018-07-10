using System.Linq;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using DexterDepend;
using System.Collections.Generic;

namespace DexterCSTest
{
    [TestClass]
    public class VconfSetTest
    {
        VconfMethod vconfMethod;
        List<string> subMethodList;

        public void Init()
        {
            vconfMethod = new VconfMethod();
            subMethodList = new List<string>();
            subMethodList.AddRange(new string[] { "Vconf.SetString", "Vconf.SetInt"});
        }
        [TestMethod]
        public void HasVConfMethod_Should_True_vconf_set()
        {
            //given
            Init();
            string methodName = "Vconf.SetString(\"db/cloud_voice_assistant/location/latitude\", latitude);";

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
            string methodName = "Vconf.SetTest(\"db/cloud_voice/location\", \"suwon\");";

            //when
            bool result = vconfMethod.HasVconfMethod(subMethodList, methodName);

            //then
            Assert.IsFalse(result);
        }

        [TestMethod]
        public void HasVConfMethod_Should_True_vconf_set_int()
        {
            //given
            Init();
            string methodName = "Vconf.SetInt(\"db/cloud_voice\", \"test\");";

            //when
            bool result = vconfMethod.HasVconfMethod(subMethodList, methodName);

            //then
            Assert.IsTrue(result);
        }
    }
}
