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
            dexter = new Dexter();
        }

        /// <summary>
        /// Analysis should gather list of defects 
        /// </summary>
        [Test]
        public void TestAnalysis()
        {
            List<Defect> defects = dexter.Analyse();

            Assert.IsNotNull(defects);
        }
    }
}
