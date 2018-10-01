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
using DexterCSTest.Src;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.IO;
using System.Linq;
using System.Text.RegularExpressions;

namespace DexterCS.Tests
{
    [TestClass()]
    public class DexterUtilTests
    {

        [TestMethod()]
        public void SplitTest_CamelCasingWord_ReturnsSplitWord()
        {
            // Given
            string camelCasingWord = "camelCasingWord";

            // When
            string[] splitcamelCasingWord = DexterUtil.Split(camelCasingWord);

            // Then
            Assert.IsTrue(splitcamelCasingWord.SequenceEqual(new string[] { "camel", "Casing", "Word" }));
        }

        [TestMethod()]
        public void SplitTest_PascalCasingWord_ReturnsSplitWord()
        {
            // Given
            string pascalCasingWord = "PascalCasingWord";

            // When
            string[] splitPascalCasingWord = DexterUtil.Split(pascalCasingWord);

            // Then
            Assert.IsTrue(splitPascalCasingWord.SequenceEqual(new string[] { "Pascal", "Casing", "Word" }));
        }

        [TestMethod()]
        public void SplitTest_SnakeCasingWord_ReturnsSplitWord()
        {
            // Given
            string snakeCasingWord = "snake_casing_word";

            // When
            string[] splitSnakeCasingWord = DexterUtil.Split(snakeCasingWord);

            // Then
            foreach (string x in splitSnakeCasingWord)
            {
                Console.WriteLine("\"" + x + "\"");
            }
            Assert.IsTrue(splitSnakeCasingWord.SequenceEqual(new string[] { "snake", "casing", "word" }));
        }

        [TestMethod()]
        public void SplitTest_CamelAndSnakeCasingWord_ReturnsSplitWord()
        {
            // Given
            string camelAndSnakeCasingWord = "camelAndSnake_Casing_Word";

            // When
            string[] splitCamelAndSnakeCasingWord = DexterUtil.Split(camelAndSnakeCasingWord);

            // Then
            foreach (string x in splitCamelAndSnakeCasingWord)
            {
                Console.WriteLine("\"" + x + "\"");
            }
            Assert.IsTrue(splitCamelAndSnakeCasingWord.SequenceEqual(new string[] { "camel", "And", "Snake", "Casing", "Word" }));
        }

        [TestMethod()]
        public void SplitTest_PascalAndSnakeCasingWord_ReturnsSplitWord()
        {
            // Given
            string pascalAndSnakeCasingWord = "PascalAndSnake_Casing_Word";

            // When
            string[] splitPascalAndSnakeCasingWord = DexterUtil.Split(pascalAndSnakeCasingWord);

            // Then
            foreach (string x in splitPascalAndSnakeCasingWord)
            {
                Console.WriteLine("\"" + x + "\"");
            }
            Assert.IsTrue(splitPascalAndSnakeCasingWord.SequenceEqual(new string[] { "Pascal", "And", "Snake", "Casing", "Word" }));
        }

        [TestMethod()]
        public void IsDirectoryTest_ExistingDirectory_ReturnsTrue()
        {
            try
            {
                // Given
                string directoryPath = DexterCSTestUtil.TestingDirectory + @"\" + DexterCSTestUtil.RandomString();
                Directory.CreateDirectory(directoryPath);
                FileInfo fileInfo = new FileInfo(directoryPath);

                // When
                bool result = DexterUtil.IsDirectory(fileInfo);

                // Then
                Assert.IsTrue(result);
            }
            finally
            {
                DexterCSTestUtil.ClearTestingDirectory();
            }
        }

        [TestMethod()]
        public void IsDirectoryTest_ExistingFile_ReturnsFalse()
        {
            try
            {
                // Given
                string filePath = DexterCSTestUtil.TestingDirectory + @"\" + DexterCSTestUtil.RandomString();
                File.Create(filePath).Close();
                FileInfo fileInfo = new FileInfo(filePath);

                // When
                bool result = DexterUtil.IsDirectory(fileInfo);

                // Then
                Assert.IsFalse(result);
            }
            finally
            {
                DexterCSTestUtil.ClearTestingDirectory();
            }
        }

        [TestMethod()]
        [ExpectedException(typeof(FileNotFoundException))]
        public void IsDirectoryTest_NonExistantDirectory_ThrowsException()
        {
            // Given
            string directoryPath = DexterCSTestUtil.TestingDirectory + @"\" + DexterCSTestUtil.RandomString();
            FileInfo fileInfo = new FileInfo(directoryPath);

            // When
            bool result = DexterUtil.IsDirectory(fileInfo);

            // Then
            // FileNotFoundException is thrown
        }

        [TestMethod()]
        public void GetSourcecodeFromFileTest_SimpleFileContent_ReturnsFileContent()
        {
            try
            {
                // Given
                string filePath = DexterCSTestUtil.TestingDirectory + @"\" + DexterCSTestUtil.RandomString();
                string fileContent = DexterCSTestUtil.RandomString();
                StreamWriter streamWriter = File.CreateText(filePath);
                streamWriter.Write(fileContent);
                streamWriter.Close();
                FileInfo fileInfo = new FileInfo(filePath);

                // When
                string result = DexterUtil.GetSourcecodeFromFile(filePath);

                // Then
                Assert.AreEqual(fileContent, result);
            }
            finally
            {
                DexterCSTestUtil.ClearTestingDirectory();
            }
        }

        [TestMethod()]
        public void GetSourcecodeFromFileTest_ComplexAndBigFileContent_ReturnsFileContent()
        {
            try
            {
                // Given
                string filePath = DexterCSTestUtil.TestingDirectory + @"\" + DexterCSTestUtil.RandomString();
                string fileContent = DexterCSTestUtil.RandomString(10000);
                StreamWriter streamWriter = File.CreateText(filePath);
                streamWriter.Write(fileContent);
                streamWriter.Close();
                FileInfo fileInfo = new FileInfo(filePath);

                // When
                string result = DexterUtil.GetSourcecodeFromFile(filePath);

                // Then
                Assert.AreEqual(fileContent, result);
            }
            finally
            {
                DexterCSTestUtil.ClearTestingDirectory();
            }
        }

        [TestMethod()]
        public void GetCurrentMethodNameTest_ReturnsCorrectMethodName()
        {
            // Given

            // When
            string methodName = DexterUtil.GetCurrentMethodName();

            // Then
            Assert.AreEqual("GetCurrentMethodNameTest_ReturnsCorrectMethodName", methodName);
        }

        [TestMethod()]
        public void GetBase64CharSequenceTest_SimpleString_ReturnsStringOfLengthDivisableByFour()
        {
            // Given
            string simpleString = "Simple String Content";

            // When
            string base64String = DexterUtil.GetBase64CharSequence(simpleString);

            // Then
            base64String = base64String.Trim();
            Assert.IsTrue((base64String.Length % 4 == 0));
        }

        [TestMethod()]
        public void GetBase64CharSequenceTest_SimpleString_ReturnsStringContainingOnlyBase64Characters()
        {
            // Given
            string simpleString = "Simple String Content";

            // When
            string base64String = DexterUtil.GetBase64CharSequence(simpleString);

            // Then
            base64String = base64String.Trim();
            Assert.IsTrue(Regex.IsMatch(base64String, @"^[a-zA-Z0-9\+/]*={0,3}$", RegexOptions.None));
        }

        [TestMethod]
        public void RefinePathTest_RefinesFilePath()
        {
            // Given
            string tempPath = @":/DEV//temp\DexterCS-cli_#.#.#_64";
            string expectedPath = @":/DEV/temp/DexterCS-cli_#.#.#_64";

            // When
            string result = DexterUtil.RefinePath(tempPath);

            // Then
            Assert.AreEqual(expectedPath, result);
        }
    }
}