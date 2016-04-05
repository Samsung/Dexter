using System;
using NUnit.Framework;
using System.Collections.Generic;

namespace dexter_vs.Analysis
{
    [TestFixture]
    public class DexterTest
    {
        private Dexter dexter;
        private Dexter invalidDexter; 

        [SetUp]
        public void Init()
        {
            dexter = new Dexter("D:/Applications/dexter/0.9.2/dexter-cli_0.9.2_32/bin/dexter-executor.jar");
            invalidDexter = new Dexter("D:/");
        }

        /// <summary>
        /// Dexter should be found in proper path
        /// </summary>
        [Test]
        public void TestDexterFound()
        {
            Assert.IsTrue(dexter.IsDexterFound);
            Assert.IsFalse(invalidDexter.IsDexterFound);
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
            dexter.OutputDataReceived += (s, e) => dataReceived = true;
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
            invalidDexter.ErrorDataReceived += (s, e) => dataReceived = true;
            invalidDexter.Analyse();
            Assert.IsTrue(dataReceived);
        }


    }
}
