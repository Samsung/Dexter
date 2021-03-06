﻿#region Copyright notice
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
    public class WithBraceTests
    {
        private WithBrace brace;

        public void Init()
        {
            brace = new WithBrace();
        }

        [TestMethod]
        public void HasDefectTest_WithoutBrace_ReturnsTrue()
        {
            Init();
            // Given
            string statement = @" int a = 10; ";
            // When
            bool result = brace.HasDefect(statement);
            // Then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasDefectTest_WithBrace_ReturnsFalse()
        {
            Init();
            // Given
            string statement = @"{
                                    int a = 10; 
                                    int b = 20;
                                  }";
            // When
            bool result = brace.HasDefect(statement);
            // Then
            Assert.IsFalse(result);
        }

        [TestMethod]
        public void HasDefectTest_WithStartingBraceWithoutEndingBrace_ReturnsTrue()
        {
            Init();
            // Given
            string statement = @"{
                                    int a = 10; 
                                    int b = 20;
                                  ";
            // When
            bool result = brace.HasDefect(statement);
            // Then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasDefectTest_WithoutStartingBraceWithEndingBrace_ReturnsTrue()
        {
            Init();
            // Given
            string statement = @"
                                    int a = 10; 
                                    int b = 20;
                                  }";
            // When
            bool result = brace.HasDefect(statement);
            // Then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasDefectTest_WithBracesEmptyContent_ReturnsFalse()
        {
            Init();
            // Given
            string statement = @"{}";
            // When
            bool result = brace.HasDefect(statement);
            // Then
            Assert.IsFalse(result);
        }

        [TestMethod]
        public void HasDefectTest_WithParenthesis_ReturnsTrue()
        {
            Init();
            // Given
            string statement = @"(
                                    int a = 10; 
                                    int b = 20;
                                  )";
            // When
            bool result = brace.HasDefect(statement);
            // Then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasDefectTest_NestedBrackets_ReturnsFalse()
        {
            Init();
            // Given
            string statement = @"{
                                    {
                                        int a = 10;
                                        int b = 20;
                                    }
                                }";
            // When
            bool result = brace.HasDefect(statement);
            // Then
            Assert.IsFalse(result);
        }
    }
}