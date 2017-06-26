using System;
using NUnit.Framework;
using Dexter.Common.Config.Providers;
using System.ComponentModel.Composition.Hosting;
using System.ComponentModel.Composition;
using Dexter.Common.Config;
using Microsoft.VisualStudio.Text.Classification;
using Dexter.Common.Client;
using Microsoft.VisualStudio.Text;

namespace Dexter.PeerReview.Tests
{
    [TestFixture]
    public class PeerReviewTaggerProviderTest
    {
        PeerReviewTaggerProvider taggerProvider = new PeerReviewTaggerProvider();

        [SetUp]
        public void SetUp()
        {
            IDexterInfoProvider dexterInfoProvider = new DexterInfoProviderMock();
            IDexterClient dexterClient = new DexterClient(new DexterHttpClientWrapper(dexterInfoProvider));
            IClassifierAggregatorService aggregatorService = new ClassifierAggregatorServiceMock();

            var batch = new CompositionBatch();
            batch.AddPart(AttributedModelServices.CreatePart(taggerProvider));
            batch.AddPart(AttributedModelServices.CreatePart(dexterInfoProvider));
            batch.AddPart(AttributedModelServices.CreatePart(dexterClient));
            batch.AddPart(AttributedModelServices.CreatePart(aggregatorService));

            var container = new CompositionContainer();
            container.Compose(batch);
        }

        [Test]
        public void DexterInfoProvider_isNotNull()
        {
            Assert.NotNull(taggerProvider.dexterInfoProvider);
        }

        [Test]
        public void DexterClient_isNotNull()
        {
            Assert.NotNull(taggerProvider.dexterClient);
        }
    }

    [Export(typeof(IDexterInfoProvider))]
    class DexterInfoProviderMock : IDexterInfoProvider
    {
        public DexterInfo Load()
        {
            return new DexterInfo() { dexterServerIp="http://dexter-server"};
        }

        public void Save(DexterInfo dexterInfo)
        {
        }
    }

    [Export(typeof(IClassifierAggregatorService))]
    class ClassifierAggregatorServiceMock : IClassifierAggregatorService
    {
        public IClassifier GetClassifier(ITextBuffer textBuffer)
        {
            throw new NotImplementedException();
        }
    }
}
