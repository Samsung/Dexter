using System;
using NUnit.Framework;
using Dexter.Defects;
using Dexter.Common.Config;
using Dexter.Analysis;
using System.Net;
using System.IO.Compression;
using System.IO;

namespace Dexter.Tests.Analysis
{
    [TestFixture]
    public class DexterTest
    {
        private DexterLegacyAnalyzer dexter;

        [SetUp]
        public void Init()
        {  
            var config = new Configuration()
            {
                dexterHome = AppDomain.CurrentDomain.BaseDirectory + "../../TestData/Dexter",
                projectName = "TestData",
                type = "PROJECT",
                sourceDir = { AppDomain.CurrentDomain.BaseDirectory + "../../TestData/SampleCppProject/" },
                headerDir = { AppDomain.CurrentDomain.BaseDirectory + "../../TestData/SampleCppProject/" },
                projectFullPath = AppDomain.CurrentDomain.BaseDirectory + "../../TestData/SampleCppProject/",
            };

            // If there are no Dexter binaries in TestData, we need to download them. It may take some time.
            if (!config.IsDexterFound)
            {
                var dexterDownloadUrl = "https://dexter.atlassian.net/wiki/download/attachments/6258746/dexter-cli_0.10.6_32.zip";
                var dexterZipPath = AppDomain.CurrentDomain.BaseDirectory + "../../TestData/Dexter.zip";
        
                if (!File.Exists(dexterZipPath))
                {
                    using (var client = new WebClient())
                    {
                        client.DownloadFile(dexterDownloadUrl, dexterZipPath);
                    }
                }
                ZipFile.ExtractToDirectory(dexterZipPath, config.dexterHome);
            }

            dexter = new DexterLegacyAnalyzer(config);
        }

        /// <summary>
        /// Analysis should gather list of defects 
        /// </summary>
        [Test]
        public void Analyse_returnNotEmpty()
        {
            Result result = dexter.Analyse();
            Assert.IsNotNull(result);
            Assert.IsNotNull(result.FileDefects);
            Assert.IsNotEmpty(result.FileDefects);
        }

        /// <summary>
        /// Dexter should inform about produced output
        /// </summary>
        [Test]
        public void Analyse_callOutputDataReceived()
        {
            var dataReceived = false;
            dexter.OutputDataReceived += (s, e) => { Console.WriteLine(e.Data); dataReceived = true; };
            dexter.Analyse();
            Assert.IsTrue(dataReceived);
        }

        /// <summary>
        /// Dexter should inform about produced errors
        /// </summary>
        [Test]
        public void Analyse_callErrorDataReceived()
        {
            var dataReceived = false;
            dexter.ErrorDataReceived += (s, e) => { Console.WriteLine(e.Data); dataReceived = true; };
            dexter.Analyse();
            Assert.IsTrue(dataReceived);
        }


    }
}
