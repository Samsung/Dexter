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