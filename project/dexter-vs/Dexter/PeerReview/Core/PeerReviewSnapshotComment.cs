using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.VisualStudio.Text;
using Dexter.PeerReview.Utils;

namespace Dexter.PeerReview
{
    /// <summary>
    /// Provides review comment in SnapshotSapn
    /// </summary>
    public class PeerReviewSnapshotComment : PeerReviewComment
    {
        SnapshotSpan snapshotSpan;
        
        /// <summary>
        /// SnapshotSpan contains review comment
        /// </summary>
        public SnapshotSpan SnapShotSpan
        {
            get { return snapshotSpan; }
        }

        /// <summary>
        /// Creates review comment in SnapshotSpan and sets all base properties
        /// </summary>
        /// <param name="reviewService">Peer review service for interface with snapshotSpan</param>
        /// <param name="snapshotSpan">SnapshotSpan contains review comment</param>
        public PeerReviewSnapshotComment(IPeerReviewService reviewService, SnapshotSpan snapshotSpan)
        {
            this.snapshotSpan = snapshotSpan;
            Span = snapshotSpan;
            StartLine = reviewService.getStartLineNumber(snapshotSpan);
            EndLine = reviewService.getEndLineNumber(snapshotSpan);
            Serverity = reviewService.getServerity(snapshotSpan);
            Message = reviewService.getCommentMessage(snapshotSpan);
        }
    }
}
