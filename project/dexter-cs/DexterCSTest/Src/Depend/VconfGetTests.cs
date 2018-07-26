using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Collections.Generic;

namespace DexterDepend.Tests
{
    [TestClass()]
    public class VconfGetTests
    {
        VconfMethod vconfMethod;
        List<string> subMethodList;

        public void Init()
        {
            vconfMethod = new VconfMethod();
            subMethodList = new List<string>();
            subMethodList.AddRange(new string[] { "Vconf.GetString", "Vconf.GetInt" });
        }
        [TestMethod]
        public void HasVConfMethodTest_VconfGet_ReturnsTrue()
        {
            // Given
            Init();
            string methodName = "Vconf.GetInt(IntKey, out GetValue)";

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
            string methodName = "Vconf.GetTest(IntKey, out GetValue)";

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
            string methodName = "Vconf.GetInt(IntKey, out GetValue)";

            // When
            bool result = vconfMethod.HasVconfMethod(subMethodList, methodName);

            // Then
            Assert.IsTrue(result);
        }
    }
}
