using NUnit.Framework;
using Dexter.PeerReview;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Dexter.PeerReview.Tests
{
    [TestFixture()]
    public class PeerReviewCommentTests
    {
        PeerReviewComment commentA;
        PeerReviewComment commentB;

        [SetUp]
        public void SetUp()
        {
            commentA = CreateTestComment();
            commentB = CreateTestComment();
        }

        private PeerReviewComment CreateTestComment()
        {
            return new PeerReviewComment()
            {
                StartLine = 1,
                EndLine = 1,
                Serverity = "MAJ",
                Message = "test message",
                FilePath = "c:\\test.cs",
                Span = new Microsoft.VisualStudio.Text.Span(0, 10)
            };
        }

        [Test()]
        public void Equals_returnTrue_GivenCommentsWithSameValues()
        {
            // when
            var isEqual = commentA.Equals(commentB);

            // then
            Assert.AreEqual(true, isEqual);
        }

        [Test()]
        public void Equals_returnFalse_GivenCommentsWithDifferentStartLine()
        {
            // given
            commentA.StartLine = 2;

            // when
            var isEqual = commentA.Equals(commentB);

            // then
            Assert.AreEqual(false, isEqual);
        }

        [Test()]
        public void Equals_returnFalse_GivenCommentsWithDifferentEndLine()
        {
            // given
            commentA.EndLine = 2;

            // when
            var isEqual = commentA.Equals(commentB);

            // then
            Assert.AreEqual(false, isEqual);
        }

        [Test()]
        public void Equals_returnFalse_GivenCommentsWithDifferentServerity()
        {
            // given
            commentA.Serverity = "CRI";

            // when
            var isEqual = commentA.Equals(commentB);

            // then
            Assert.AreEqual(false, isEqual);
        }

        [Test()]
        public void Equals_returnFalse_GivenCommentsWithDifferentMessage()
        {
            // given
            commentA.Message = "Hello";

            // when
            var isEqual = commentA.Equals(commentB);

            // then
            Assert.AreEqual(false, isEqual);
        }

        [Test()]
        public void Equals_returnFalse_GivenCommentsWithDifferentFilePath()
        {
            // given
            commentA.FilePath = "c:\\testA.cs";

            // when
            var isEqual = commentA.Equals(commentB);

            // then
            Assert.AreEqual(false, isEqual);
        }

        [Test()]
        public void Equals_returnFalse_GivenCommentsWithDifferentSpan()
        {
            // given
            commentA.Span = new Microsoft.VisualStudio.Text.Span(0, 20);

            // when
            var isEqual = commentA.Equals(commentB);

            // then
            Assert.AreEqual(false, isEqual);
        }

        [Test()]
        public void GetHashCode_HashCodesAreSame_GivenCommentsWithSameValues()
        {
            // when & then
            Assert.AreEqual(commentA.GetHashCode(), commentB.GetHashCode());
        }

        [Test()]
        public void GetHashCode_HashCodesAreDifferent_GivenCommentsWithDifferentStartLine()
        {
            // given
            commentA.StartLine = 2;

            // when & then
            Assert.AreNotEqual(commentA.GetHashCode(), commentB.GetHashCode());
        }

        [Test()]
        public void GetHashCode_HashCodesAreDifferent_GivenCommentsWithDifferentEndLine()
        {
            // given
            commentA.EndLine = 2;

            // when & then
            Assert.AreNotEqual(commentA.GetHashCode(), commentB.GetHashCode());
        }

        [Test()]
        public void GetHashCode_HashCodesAreDifferent_GivenCommentsWithDifferentServerity()
        {
            // given
            commentA.Serverity = "CRI";

            // when & then
            Assert.AreNotEqual(commentA.GetHashCode(), commentB.GetHashCode());
        }

        [Test()]
        public void GetHashCode_HashCodesAreDifferent_GivenCommentsWithDifferentMessage()
        {
            // given
            commentA.Message = "Hello";

            // when & then
            Assert.AreNotEqual(commentA.GetHashCode(), commentB.GetHashCode());
        }

        [Test()]
        public void GetHashCode_HashCodesAreDifferent_GivenCommentsWithDifferentFilePath()
        {
            // given
            commentA.FilePath = "c:\\testA.cs";

            // when & then
            Assert.AreNotEqual(commentA.GetHashCode(), commentB.GetHashCode());
        }

        [Test()]
        public void GetHashCode_HashCodesAreDifferent_GivenCommentsWithDifferentSpan()
        {
            // given
            commentA.Span = new Microsoft.VisualStudio.Text.Span(0, 20);

            // when & then
            Assert.AreNotEqual(commentA.GetHashCode(), commentB.GetHashCode());
        }
    }
}