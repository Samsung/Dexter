#region Copyright notice
/**
 * Copyright (c) 2018 Samsung Electronics, Inc.,
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
#endregion
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
