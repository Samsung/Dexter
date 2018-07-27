using DexterCS;
using DexterCSTest.Src;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.IO;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;

namespace DexterCS.Tests
{
    [TestClass()]
    public class DexterUtilTests
    {

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