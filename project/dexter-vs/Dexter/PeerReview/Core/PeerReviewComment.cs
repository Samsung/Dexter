using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.VisualStudio.Text;

namespace Dexter.PeerReview
{
    /// <summary>
    /// Presents a review comment
    /// </summary>
    public class PeerReviewComment
    {
        /// <summary>
        /// Span contains a review comment
        /// </summary>
        public SnapshotSpan Span { get; set; }
    }
}
