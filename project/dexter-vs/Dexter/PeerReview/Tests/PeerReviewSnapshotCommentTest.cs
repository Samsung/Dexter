using NUnit.Framework;
using Dexter.PeerReview;
using Dexter.PeerReview.Utils;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.VisualStudio.Text;
using Moq;

namespace Dexter.PeerReview.Tests
{
    [TestFixture()]
    public class PeerReviewSnapshotCommentTest
    {
        Mock<IPeerReviewService> reviewServiceMock;
        Mock<ITextSnapshot> textSnapshotMock;
        string testFilePath;

        [SetUp]
        public void SetUp() {
            testFilePath = "c:\\test.cs";
            reviewServiceMock = new Mock<IPeerReviewService>();
            textSnapshotMock = new Mock<ITextSnapshot>(MockBehavior.Strict);
            textSnapshotMock.Setup(snapshot => snapshot.Length).Returns(30);

        }
   
        [Test()]
        public void PeerReviewSnapshotComment_setValidSnapshotSpan_GivenSnapshotSpan()
        {
            // given
            var span = new Span();
            var snapshotSpan = new SnapshotSpan(textSnapshotMock.Object, span);

            // when
            var comment = new PeerReviewSnapshotComment(reviewServiceMock.Object, snapshotSpan, testFilePath);

            // then
            Assert.AreEqual(snapshotSpan, comment.SnapShotSpan);
        }

        [Test()]
        public void PeerReviewSnapshotComment_setValidSpan_GivenSnapshotSpan()
        {
            // given
            var span = new Span(5, 20);
            var snapshotSpan = new SnapshotSpan(textSnapshotMock.Object, span);
            reviewServiceMock.Setup(service => service.GetLineSpan(It.IsAny<SnapshotSpan>())).Returns(span);

            // when
            var comment = new PeerReviewSnapshotComment(reviewServiceMock.Object, snapshotSpan, testFilePath);

            // then
            Assert.AreEqual(span, comment.Span);
        }

        [Test()]
        public void PeerReviewSnapshotComment_setValidLines_GivenSnapshotSpan()
        {
            // given
            var span = new Span();
            var snapshotSpan = new SnapshotSpan(textSnapshotMock.Object, span);
            reviewServiceMock.Setup(service => service.GetStartLineNumber(It.IsAny<SnapshotSpan>())).Returns(5);
            reviewServiceMock.Setup(service => service.GetEndLineNumber(It.IsAny<SnapshotSpan>())).Returns(10);

            // when
            var comment = new PeerReviewSnapshotComment(reviewServiceMock.Object, snapshotSpan, testFilePath);

            // then
            Assert.AreEqual(5, comment.StartLine);
            Assert.AreEqual(10, comment.EndLine);
        }

        [Test()]
        public void PeerReviewSnapshotComment_setValidServerity_GivenSnapshotSpan()
        {
            // given
            var span = new Span();
            var snapshotSpan = new SnapshotSpan(textSnapshotMock.Object, span);
            reviewServiceMock.Setup(service => service.GetServerity(It.IsAny<SnapshotSpan>())).Returns("MAJ");

            // when
            var comment = new PeerReviewSnapshotComment(reviewServiceMock.Object, snapshotSpan, testFilePath);

            // then
            Assert.AreEqual("MAJ", comment.Serverity);
        }

        [Test()]
        public void PeerReviewSnapshotComment_setValidMessage_GivenSnapshotSpan()
        {
            // given
            var span = new Span();
            var snapshotSpan = new SnapshotSpan(textSnapshotMock.Object, span);
            reviewServiceMock.Setup(service => service.GetCommentMessage(It.IsAny<SnapshotSpan>())).Returns("test message");

            // when
            var comment = new PeerReviewSnapshotComment(reviewServiceMock.Object, snapshotSpan, testFilePath);

            // then
            Assert.AreEqual("test message", comment.Message);
        }

        [Test()]
        public void PeerReviewSnapshotComment_setValidFilePath_GivenFilePath()
        {
            // given
            var span = new Span();
            var snapshotSpan = new SnapshotSpan(textSnapshotMock.Object, span);
            reviewServiceMock.Setup(service => service.GetCommentMessage(It.IsAny<SnapshotSpan>())).Returns("test message");

            // when
            var comment = new PeerReviewSnapshotComment(reviewServiceMock.Object, snapshotSpan, testFilePath);

            // then
            Assert.AreEqual(testFilePath, comment.FilePath);
        }

        [Test()]
        public void Equals_returnTrue_GivenCommentWithSameValues()
        {
            // given
            var span = new Span();
            var snapshotSpan = new SnapshotSpan(textSnapshotMock.Object, span);
            reviewServiceMock.Setup(service => service.GetCommentMessage(It.IsAny<SnapshotSpan>())).Returns("test message");

            var commentA = new PeerReviewSnapshotComment(reviewServiceMock.Object, snapshotSpan, testFilePath);
            var commentB = new PeerReviewSnapshotComment(reviewServiceMock.Object, snapshotSpan, testFilePath);

            // when & then
            Assert.True(commentA.Equals(commentB));
        }
    }
}