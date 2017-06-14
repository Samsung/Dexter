using System;
using System.Collections.Generic;
using System.ComponentModel.Composition;
using Microsoft.VisualStudio.Text;
using Microsoft.VisualStudio.Text.Tagging;
using Microsoft.VisualStudio.Text.Editor;
using Microsoft.VisualStudio.Text.Classification;
using Microsoft.VisualStudio.Utilities;
using Dexter.Common.Client;
using Dexter.PeerReview.Utils;
using Dexter.Common.Config.Providers;

namespace Dexter.PeerReview
{
    /// <summary>
    /// Creates a instance of PeerReviewTagger for given text buffer
    /// </summary>
    [Export(typeof(ITaggerProvider))]
    [ContentType("code")]
    [TagType(typeof(PReviewTag))]
    public class PeerReviewTaggerProvider : ITaggerProvider
    {
        [Import]
        public IClassifierAggregatorService AggregatorService;

        [Import]
        IDexterClient dexterClient;

        [Import]
        IDexterInfoProvider dexterInfoProvider;

        public ITagger<T> CreateTagger<T>(ITextBuffer buffer) where T : ITag
        {
            if (buffer == null)
            {
                throw new ArgumentNullException("buffer");
            }

            Func<ITagger<T>> sc = delegate () {
                ITextDocument document = null;
                buffer.Properties.TryGetProperty(typeof(ITextDocument), out document);

                return new PeerReviewTagger(buffer, document, dexterClient, PeerReviewService.Instance, dexterInfoProvider) as ITagger<T>;
            };
            return buffer.Properties.GetOrCreateSingletonProperty<ITagger<T>>(sc);
        }
    }
}