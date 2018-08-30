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

namespace DexterCS.Tests
{
    [TestClass()]
    public class DexterConfigTests
    {
        [TestMethod()]
        public void CreateInitialFolderAndFilesTest_ProvidedDexterHomePath_CreatesNonEmptyDirectory()
        {
            try
            {
                // Given
                DexterConfig dexterConfig = new DexterConfig();
                string dexterHomePath = DexterCSTestUtil.TestingDirectory + @"\" + "DexterHome";
                dexterConfig.DexterHome = dexterHomePath;

                // When
                dexterConfig.CreateInitialFolderAndFiles();

                // Then
                Assert.IsTrue(Directory.GetFileSystemEntries(dexterHomePath).Any());
            }
            finally
            {
                DexterCSTestUtil.ClearTestingDirectory();
            }
        }

        [TestMethod()]
        public void CreateInitialFolderAndFilesTest_ProvidedDexterHomePath_CreatesBinDirectoryInDexterHome()
        {
            try
            {
                // Given
                DexterConfig dexterConfig = new DexterConfig();
                string dexterHomePath = DexterCSTestUtil.TestingDirectory + @"\" + "DexterHome";
                dexterConfig.DexterHome = dexterHomePath;

                // When
                dexterConfig.CreateInitialFolderAndFiles();

                foreach (string s in Directory.GetDirectories(dexterHomePath))
                {
                    Console.WriteLine(s);
                }

                // Then
                Assert.IsTrue(Directory.GetDirectories(dexterHomePath)
                    .Any(x => new DirectoryInfo(x).Name == "bin"));
            }
            finally
            {
                DexterCSTestUtil.ClearTestingDirectory();
            }
        }

        [TestMethod()]
        public void ChangeDexterHomeTest_SimplePath_ChangesDexterHomePath()
        {
            // Given
            string dexterHomePath = @"C:\NewDexterHomePath";
            DexterConfig dexterConfig = new DexterConfig();

            // When
            dexterConfig.ChangeDexterHome(dexterHomePath);

            // Then
            Assert.AreEqual(
                Path.GetFullPath(dexterHomePath),
                Path.GetFullPath(dexterConfig.DexterHome));
        }

        [TestMethod()]
        public void ChangeDexterHomeTest_ComplexPath_ChangesDexterHomePath()
        {
            // Given
            string dexterHomePath = @"C:\Directory1\\Directory2\..\NewDexterHomePath";
            DexterConfig dexterConfig = new DexterConfig();

            // When
            dexterConfig.ChangeDexterHome(dexterHomePath);

            // Then
            Assert.AreEqual(
                Path.GetFullPath(dexterHomePath),
                Path.GetFullPath(dexterConfig.DexterHome));
        }

        [TestMethod()]
        public void IsAnalysisAllowedFileTest_OneExtensionSupported_FileWithSupportedExtensions_ReturnsTrue()
        {
            // Given
            DexterConfig dexterConfig = new DexterConfig();
            dexterConfig.AddSupportedFileExtensions(new string[] { "cpp" });

            // When
            bool result = dexterConfig.IsFileSupportedForAnalysis("file.cpp");

            // Then
            Assert.IsTrue(result);
        }

        [TestMethod()]
        public void IsAnalysisAllowedFileTest_OneExtensionSupported_FileWithNotSupportedExtensions_ReturnsFalse()
        {
            // Given
            DexterConfig dexterConfig = new DexterConfig();
            dexterConfig.AddSupportedFileExtensions(new string[] { "cpp" });

            // When
            bool result = dexterConfig.IsFileSupportedForAnalysis("file.txt");

            // Then
            Assert.IsFalse(result);
        }

        [TestMethod()]
        public void IsAnalysisAllowedFileTest_MultipleExtensionsSupported_FileWithSupportedExtension_ReturnsTrue()
        {
            // Given
            DexterConfig dexterConfig = new DexterConfig();
            dexterConfig.AddSupportedFileExtensions(new string[] { "c", "cpp", "java" });

            // When
            bool result = dexterConfig.IsFileSupportedForAnalysis("file.c");

            // Then
            Assert.IsTrue(result);
        }

        [TestMethod()]
        public void IsAnalysisAllowedFileTest_MultipleExtensionsSupported_FileWithNotSupportedExtension_ReturnsFalse()
        {
            // Given
            DexterConfig dexterConfig = new DexterConfig();
            dexterConfig.AddSupportedFileExtensions(new string[] { "c", "cpp", "java" });

            // When
            bool result = dexterConfig.IsFileSupportedForAnalysis("file.cs");

            // Then
            Assert.IsFalse(result);
        }
    }
}