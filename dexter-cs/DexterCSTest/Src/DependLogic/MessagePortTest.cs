using System.Collections.Generic;
using DexterDepend;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace DexterCSTest
{
    [TestClass]
    public class MessagePortTest
    {
        MessageMethod messageMethod;
        List<string> subMethodList;

        public void Init()
        {
            messageMethod = new MessageMethod();
            subMethodList = new List<string>();
            subMethodList.AddRange(new string[] { "new MessagePort" });
        }
        [TestMethod]
        public void HasMessageMethod_Should_True_MessagePort_Usage()
        {
            //given
            Init();
            string methodName = "new MessagePort(senderPort, false);";

            //when
            bool result = messageMethod.HasVconfMethod(subMethodList, methodName);

            //then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasMessageMethod_Should_False_other_method()
        {
            //given
            Init();
            string methodName = "MessagePort(\"weatherMsgPort\", false);";

            //when
            bool result = messageMethod.HasVconfMethod(subMethodList, methodName);

            //then
            Assert.IsFalse(result);
        }
    }
}
