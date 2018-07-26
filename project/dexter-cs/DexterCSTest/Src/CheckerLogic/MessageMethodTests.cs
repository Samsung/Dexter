using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Collections.Generic;

namespace DexterDepend.Tests
{
    [TestClass()]
    public class MessageMehodTests
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
        public void HasMessageMethodTest_MessagePortUsage_ReturnsTrue()
        {
            // Given
            Init();
            string methodName = "new MessagePort(senderPort, false);";

            // When
            bool result = messageMethod.HasVconfMethod(subMethodList, methodName);

            // Then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasMessageMethodTest_OtherMethod_ReturnsFalse()
        {
            // Given
            Init();
            string methodName = "MessagePort(\"weatherMsgPort\", false);";

            // When
            bool result = messageMethod.HasVconfMethod(subMethodList, methodName);

            // Then
            Assert.IsFalse(result);
        }
    }
}