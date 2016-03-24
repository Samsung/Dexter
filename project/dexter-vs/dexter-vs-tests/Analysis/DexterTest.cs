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

            Dexter invalidDexter = new Dexter("");

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

    }
}
