using Microsoft.VisualStudio.Text;
using Dexter.PeerReview.Utils;

namespace Dexter.PeerReview
{
    /// <summary>
    /// Provides review comment in SnapshotSapn
    /// </summary>
    public class PeerReviewSnapshotComment : PeerReviewComment
    {
        /// <summary>
        /// SnapshotSpan contains review comment
        /// </summary>
        public SnapshotSpan SnapShotSpan
        {
            get;
            private set;
        }

        /// <summary>
        /// Creates review comment in SnapshotSpan and sets all base properties
        /// </summary>
        /// <param name="reviewService">Peer review service for interface with snapshotSpan</param>
        /// <param name="snapshotSpan">SnapshotSpan contains review comment</param>
        /// <param name="filePath">Path of file contains review comment</param>
        public PeerReviewSnapshotComment(IPeerReviewService reviewService, SnapshotSpan snapshotSpan, string filePath)
        {
            SnapShotSpan = snapshotSpan;
            Span = reviewService.GetLineSpan(snapshotSpan);
            StartLine = reviewService.GetStartLineNumber(snapshotSpan);
            EndLine = reviewService.GetEndLineNumber(snapshotSpan);
            Serverity = reviewService.GetServerity(snapshotSpan);
            Message = reviewService.GetCommentMessage(snapshotSpan);
            FilePath = filePath;
        }
    }
}
