using System;
using NUnit.Framework;
using Dexter.Defects;
using Dexter.Config;
using Dexter.Analysis;

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
                dexterHome = "D:/Applications/dexter/0.9.2/dexter-cli_0.9.2_32/",
                projectName = "TestData",
                type = "PROJECT",
                sourceDir = { AppDomain.CurrentDomain.BaseDirectory + "../../TestData/SampleCppProject/" },
                headerDir = { AppDomain.CurrentDomain.BaseDirectory + "../../TestData/SampleCppProject/" },
                projectFullPath = AppDomain.CurrentDomain.BaseDirectory + "../../TestData/SampleCppProject/",
                dexterServerPort = "0", 
                dexterServerIp = "dexter-server"
            };

            dexter = new DexterLegacyAnalyzer((Configuration)config);
        }

        /// <summary>
        /// Analysis should gather list of defects 
        /// </summary>
        [Test]
        public void TestAnalysis()
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
        public void TestStandardOuputput()
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
        public void TestErrorOuputput()
        {
            var dataReceived = false;
            dexter.ErrorDataReceived += (s, e) => { Console.WriteLine(e.Data); dataReceived = true; };
            dexter.Analyse();
            Assert.IsTrue(dataReceived);
        }


    }
}
