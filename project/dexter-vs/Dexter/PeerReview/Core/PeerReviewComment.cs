using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.VisualStudio.Text;
using Dexter.Common.Utils;

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
        public Span Span { get; set; }

        /// <summary>
        /// Start line of review comment
        /// </summary>
        public int StartLine { get; set; } 

        /// <summary>
        /// End line of review comment
        /// </summary>
        public int EndLine { get; set; } 

        /// <summary>
        /// Serverity of review comment (MAJ/CRI/CRC)
        /// </summary>
        public string Serverity { get; set; }

        /// <summary>
        /// Text message of review comment 
        /// </summary>
        public string Message { get; set; }

        /// <summary>
        /// File path contains review comment
        /// </summary>
        public string FilePath { get; set; }
    }
}
