using System;
using System.Diagnostics;
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
using System.Linq;

namespace Dexter.PeerReview
{
    /// <summary>
    /// Provides peer review comments list
    /// </summary>
    /// <typeparam name="T"></typeparam>
    public interface ICommentsOwner<T>
    {
        IList<T> Comments { get; }
    }

    public class PReviewTag : IGlyphTag
    {
    }


    /// <summary>
    /// Tags peer reivew comments as the type PeerReivewTag
    /// </summary>
    public class PeerReviewTagger : ITagger<PReviewTag>, ICommentsOwner<PeerReviewSnapshotComment>
    {
        private ITextBuffer textBuffer;
        private ITextDocument textDocument;
        private IList<PeerReviewSnapshotComment> comments;
        private IDexterClient dexterClient;
        private IDexterInfoProvider dexterInfoProvider;
        private IPeerReviewService reviewService;
        private IPeerReviewCommentManager commentManager;
        private bool isCommentDirty;

        IList<PeerReviewSnapshotComment> ICommentsOwner<PeerReviewSnapshotComment>.Comments
        {
            get
            {
                return comments; 
            }
        }

        /// <summary>
        /// Initializes variables and parses peer review comments for the text buffer
        /// </summary>
        public PeerReviewTagger(ITextBuffer textBuffer, ITextDocument document, IDexterClient dexterClient, 
            IPeerReviewService reviewService, IDexterInfoProvider dexterInfoProvider, 
            IPeerReviewCommentManager commentManager)
        {
            this.commentManager = commentManager;
            this.reviewService = reviewService;
            this.dexterClient = dexterClient;
            this.textBuffer = textBuffer;
            this.dexterInfoProvider = dexterInfoProvider;
            this.textBuffer.Changed += TextBufferChanged;
            this.textBuffer.Properties.AddProperty(PeerReviewConstants.COMMENT_OWNER, this);

            textDocument = document;
            textDocument.FileActionOccurred += FileActionOccurred;

            isCommentDirty = false;
            ParsePReviewComments();
        }

        private void FileActionOccurred(object sender, TextDocumentFileActionEventArgs e)
        {
            if (e.FileActionType == FileActionTypes.ContentSavedToDisk)
            {
                if (dexterInfoProvider.Load().standalone)
                    return;

                if (isCommentDirty)
                {
                    dexterClient.SendAnalysisResult(reviewService.ConvertToDexterResult(textDocument, comments));
                    dexterClient.SendSourceCode(reviewService.ConverToSourceCodeJsonFormat(
                        textDocument.FilePath, textBuffer.CurrentSnapshot.GetText()));
                    isCommentDirty = false;
                }
            }
        }

        private void TextBufferChanged(object sender, TextContentChangedEventArgs e)
        {
             ParsePReviewComments();
        }

        private void ParsePReviewComments()
        {
            var previousComments = comments;
            comments = new List<PeerReviewSnapshotComment>();

            foreach (var line in textBuffer.CurrentSnapshot.Lines)
            {
                string text = line.GetText().ToLower();
                int commentStart = text.IndexOf(PeerReviewConstants.COMMENT_DELIMITER);

                if (commentStart >= 0)
                {
                    comments.Add(new PeerReviewSnapshotComment(
                        reviewService, new SnapshotSpan(line.Start + commentStart, line.End), textDocument.FilePath));
                }
            } 

            if (IsPReviewCommentsChanged(previousComments, comments))
            {
                Debug.WriteLine("Review comments changed");
                var baseComments = comments.Cast<PeerReviewComment>().ToList();

                commentManager.UpdateReviewCommentOfOneDocument(textDocument.FilePath, baseComments);
                isCommentDirty = true;
            }
        }

        private bool IsPReviewCommentsChanged(IList<PeerReviewSnapshotComment> previousComments, IList<PeerReviewSnapshotComment> currentComments)
        {
            if (previousComments == null)
                return false;

            if (previousComments.Count != currentComments.Count)
                return true;

            for (int i = 0; i < previousComments.Count; i++)
            {
                if (!previousComments[i].Equals(currentComments[i]))
                    return true;
            }

            return false;
        }

        IEnumerable<ITagSpan<PReviewTag>> ITagger<PReviewTag>.GetTags(NormalizedSnapshotSpanCollection spans)
        {
            var startPoint = spans[0].Start;
            var endPoint = spans[spans.Count - 1].End;

            foreach (var comment in comments)
            {
                if (comment.SnapShotSpan.Start >= startPoint && comment.SnapShotSpan.End <= endPoint)
                {
                    yield return new TagSpan<PReviewTag>(comment.SnapShotSpan, new PReviewTag());
                }
            }
        }

        public event EventHandler<SnapshotSpanEventArgs> TagsChanged;
    }
}
