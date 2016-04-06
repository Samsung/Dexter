using System;
using NUnit.Framework;
using System.Collections.Generic;

namespace dexter_vs.Analysis
{
    [TestFixture]
    public class DexterTest
    {
        private Dexter dexter;

        [SetUp]
        public void Init()
        {
            dexter = new Dexter("D:/Applications/dexter/0.9.2/dexter-cli_0.9.2_32/bin/dexter-executor.jar");
        }

        /// <summary>
        /// Dexter should be found in proper path
        /// </summary>
        [Test]
        public void TestDexterFound()
        {
            Assert.IsTrue(dexter.IsDexterFound);
        }

        /// <summary>
        /// Analysis should gather list of defects 
        /// </summary>
        [Test]
        public void TestAnalysis()
        {
            List<Defect> defects = dexter.Analyse();
            Assert.IsNotNull(defects);
            Assert.IsNotEmpty(defects);
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
            dexter.Analyse("");
            Assert.IsTrue(dataReceived);
        }


    }
}
