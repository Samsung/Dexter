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

        public override bool Equals(object obj)
        {
            if (obj is PeerReviewComment)
            {
                var comment = obj as PeerReviewComment;

                return  StartLine == comment.StartLine &&
                    EndLine == comment.EndLine &&
                    string.Equals(Serverity, comment.Serverity) &&
                    string.Equals(Message, comment.Message) &&
                    string.Equals(FilePath, comment.FilePath) &&
                    Equals(Span, comment.Span);
            } else
            {
                return false;
            }
        }

        public override int GetHashCode()
        {
            int hashCode = StartLine ^ EndLine;

            if (Serverity != null) hashCode ^= Serverity.GetHashCode();
            if (Message != null) hashCode ^= Message.GetHashCode();
            if (FilePath != null) hashCode ^= FilePath.GetHashCode();
            if (Span != null) hashCode ^= Span.GetHashCode();

            return hashCode;
        }
    }
}
