using System;
using System.Collections.Generic;
using System.ComponentModel.Composition;
using Microsoft.VisualStudio.Text;
using Microsoft.VisualStudio.Text.Tagging;
using Microsoft.VisualStudio.Text.Editor;
using Microsoft.VisualStudio.Text.Classification;
using Microsoft.VisualStudio.Utilities;

namespace Dexter.PeerReview
{
    [Export(typeof(ITaggerProvider))]
    [ContentType("code")]
    [TagType(typeof(PReviewTag))]
    public class PReviewTaggerProvider : ITaggerProvider
    {
        [Import]
        public IClassifierAggregatorService AggregatorService;

        public ITagger<T> CreateTagger<T>(ITextBuffer buffer) where T : ITag
        {
            if (buffer == null)
            {
                throw new ArgumentNullException("buffer");
            }

            Func<ITagger<T>> sc = delegate () {
                return new PReviewTagger(buffer) as ITagger<T>;
            };
            return buffer.Properties.GetOrCreateSingletonProperty<ITagger<T>>(sc);
        }
    }
}