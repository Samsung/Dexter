using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Collections.Generic;

namespace DexterDepend.Tests
{
    [TestClass()]
    public class VconfETCTests
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
        public void HasVConfMethodTest_VconfEtc_ReturnsTrue()
        {
            // Given
            Init();
            string methodName = "Vconf.NotifyKeyChanged(IntKey, Outkey, out GetValue)";

            // When
            bool result = vconfMethod.HasVconfMethod(subMethodList, methodName);

            // Then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasVConfMethodTest_OtherMethod_ReturnsFalse()
        {
            // Given
            Init();
            string methodName = "Vconf.NotifyKeysTest(IntKey, out GetValue)";

            // When
            bool result = vconfMethod.HasVconfMethod(subMethodList, methodName);

            // Then
            Assert.IsFalse(result);
        }

        [TestMethod]
        public void HasVConfMethodTest_VconfGetInt_ReturnsTrue()
        {
            // Given
            Init();
            string methodName = "Vconf.NotifyKeyChanged(IntKey, outKey, out GetValue)";

            // When
            bool result = vconfMethod.HasVconfMethod(subMethodList, methodName);

            // Then
            Assert.IsTrue(result);
        }
    }
}