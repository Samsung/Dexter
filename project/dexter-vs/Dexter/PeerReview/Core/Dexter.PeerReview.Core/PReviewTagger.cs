using System;
using System.Diagnostics;
using System.Collections.Generic;
using System.ComponentModel.Composition;
using Microsoft.VisualStudio.Text;
using Microsoft.VisualStudio.Text.Tagging;
using Microsoft.VisualStudio.Text.Editor;
using Microsoft.VisualStudio.Text.Classification;
using Microsoft.VisualStudio.Utilities;

namespace Dexter.PeerReview
{
    public interface ICommentsOwner<T>
    {
        IList<T> Comments { get; }
    }
    public class PReviewTag : IGlyphTag
    {
    }

    public class PReviewTagger : ITagger<PReviewTag>, ICommentsOwner<PReviewComment>
    {
        private ITextBuffer textBuffer;
        private IList<PReviewComment> comments;
        private const string COMMENT_DELIMITER = "dpr:";

        IList<PReviewComment> ICommentsOwner<PReviewComment>.Comments
        {
            get
            {
                return comments;
            }
        }

        public PReviewTagger(ITextBuffer textBuffer)
        {
            this.textBuffer = textBuffer;
            this.textBuffer.Changed += TextBufferChanged;
            this.textBuffer.Properties.AddProperty(PReviewConstants.COMMENT_OWNER, this);

            ParsePReviewComments();
        }

        private void TextBufferChanged(object sender, TextContentChangedEventArgs e)
        {
            ParsePReviewComments();
        }

        private void ParsePReviewComments()
        {
            comments = new List<PReviewComment>();
            
            foreach (var line in textBuffer.CurrentSnapshot.Lines)
            {
                string text = line.GetText().ToLower();
                int commentStart = text.IndexOf(COMMENT_DELIMITER);

                if (commentStart >= 0)
                {
                    comments.Add(new PReviewComment()
                    {
                        Span = new SnapshotSpan(line.Start + commentStart, line.End)
                    });
                }
            } 

        }

        IEnumerable<ITagSpan<PReviewTag>> ITagger<PReviewTag>.GetTags(NormalizedSnapshotSpanCollection spans)
        {
            var startPoint = spans[0].Start;
            var endPoint = spans[spans.Count - 1].End;

            foreach (var comment in comments)
            {
                if (comment.Span.Start >= startPoint && comment.Span.End <= endPoint)
                {
                    yield return new TagSpan<PReviewTag>(comment.Span, new PReviewTag());
                }
            }
        }

        public event EventHandler<SnapshotSpanEventArgs> TagsChanged;
    }
}
