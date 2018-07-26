using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Collections.Generic;

namespace DexterDepend.Tests
{
    [TestClass()]
    public class VconfSetTests
    {
        VconfMethod vconfMethod;
        List<string> subMethodList;

        public void Init()
        {
            vconfMethod = new VconfMethod();
            subMethodList = new List<string>();
            subMethodList.AddRange(new string[] { "Vconf.SetString", "Vconf.SetInt" });
        }
        [TestMethod]
        public void HasVConfMethodTest_VconfSet_ReturnsTrue()
        {
            // Given
            Init();
            string methodName = "Vconf.SetString(\"db/cloud_voice_assistant/location/latitude\", latitude);";

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
            string methodName = "Vconf.SetTest(\"db/cloud_voice/location\", \"suwon\");";

            // When
            bool result = vconfMethod.HasVconfMethod(subMethodList, methodName);

            // Then
            Assert.IsFalse(result);
        }

        [TestMethod]
        public void HasVConfMethodTest_VconfSetInt_ReturnsTrue()
        {
            // Given
            Init();
            string methodName = "Vconf.SetInt(\"db/cloud_voice\", \"test\");";

            // When
            bool result = vconfMethod.HasVconfMethod(subMethodList, methodName);

            // Then
            Assert.IsTrue(result);
        }
    }
}
