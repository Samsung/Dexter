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

namespace DexterCRC.Tests
{
    [TestClass()]
    public class FieldCRCTests
    {
        FieldCRC filedCRC;

        void Init()
        {
            filedCRC = new FieldCRC();
        }

        [TestMethod]
        public void HasInvalidModifierTest_HasInvalidProtectedModifier_ReturnsTrue()
        {
            Init();
            // Given
            string modifier = @" protected static";
            // When
            bool result = filedCRC.HasInvalidModifier(modifier);
            // Then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasInvalidModifierTest_HasInvalidStaticModifier_ReturnsTrue()
        {
            Init();
            // Given
            string modifier = @"public static";
            // When
            bool result = filedCRC.HasInvalidModifier(modifier);
            // Then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasInvalidModifierTest_HasInvalidStaticModifierWithBlank_ReturnsTrue()
        {
            Init();
            // Given
            string modifier = @" public static ";
            // When
            bool result = filedCRC.HasInvalidModifier(modifier);
            // Then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasInvalidModifierTest_HasValidPublicModifier_ReturnsFalse()
        {
            Init();
            // Given
            string modifier = @"public";
            // When
            bool result = filedCRC.HasInvalidModifier(modifier);
            // Then
            Assert.IsFalse(result);
        }

        [TestMethod]
        public void HasInvalidModifierTest_HasValidPrivateModifier_ReturnsFalse()
        {
            Init();
            // Given
            string modifier = @"private  ";
            // When
            bool result = filedCRC.HasInvalidModifier(modifier);
            // Then
            Assert.IsFalse(result);
        }
    }
}