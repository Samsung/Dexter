using System;
using NUnit.Framework;

namespace Dexter.PeerReview.Tests
{
    [TestFixture]
    public class PeerReviewTaggerProviderTest
    {
        PeerReviewTaggerProvider taggerProvider;
                
        [SetUp]
        public void SetUp()
        {
            taggerProvider = new PeerReviewTaggerProvider();
        }

        [Test]
        public void CreateTagger_ThrowsArgumentNullException()
        {
            Assert.Throws<ArgumentNullException>( delegate { taggerProvider.CreateTagger<PReviewTag>(null);} );
        }
    }

}
