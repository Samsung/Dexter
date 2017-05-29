using NUnit.Framework;
using System;
using System.Collections.Generic;
using System.Linq;
using Microsoft.VisualStudio.Text;
using Microsoft.VisualStudio.Text.Tagging;
using Microsoft.VisualStudio.Utilities;
using Moq;
using Dexter.Common.Client;
using Dexter.PeerReview.Utils;
using Dexter.Common.Defect;

namespace Dexter.PeerReview.Tests
{
    [TestFixture]
    public class PReviewTaggerTest
    {
        Mock<ITextBuffer> textBufferMock;
        Mock<ITextSnapshot> textSnapshotMock;
        Mock<ITextSnapshotLine> textSnapshotLineMock;
        Mock<ITextDocument> textDocumentMock;
        Mock<IPReviewService> reviewServiceMock;
        Mock<IDexterClient> dexterClientMock;
        PropertyCollection properties;
        PReviewTagger tagger;

        const string VALID_COMMENT = "// DPR: test";

        [SetUp]
        public void SetUp()
        {
            textBufferMock = new Mock<ITextBuffer>(MockBehavior.Strict);
            textSnapshotMock = new Mock<ITextSnapshot>(MockBehavior.Strict);
            textSnapshotLineMock = new Mock<ITextSnapshotLine>(MockBehavior.Strict);
            textDocumentMock = new Mock<ITextDocument>(MockBehavior.Strict);
            reviewServiceMock = new Mock<IPReviewService>(MockBehavior.Strict);
            dexterClientMock = new Mock<IDexterClient>();
            properties = new PropertyCollection();

            textBufferMock.Setup(buffer => buffer.Properties).Returns(properties);
            textBufferMock.Setup(buffer => buffer.CurrentSnapshot).Returns(textSnapshotMock.Object);
            textSnapshotMock.Setup(snapshot => snapshot.Length).Returns(10);
            textSnapshotMock.Setup(snapshot => snapshot.Lines).Returns(createTestSnapshotLines());
            tagger = new PReviewTagger(textBufferMock.Object, textDocumentMock.Object, 
                dexterClientMock.Object, reviewServiceMock.Object);
        }

        private IEnumerable<ITextSnapshotLine> createTestSnapshotLines()
        {
            textSnapshotLineMock.Setup(line => line.GetText()).Returns(VALID_COMMENT);
            textSnapshotLineMock.Setup(line => line.Start).Returns(new SnapshotPoint(textSnapshotMock.Object, 0));
            textSnapshotLineMock.Setup(line => line.End).Returns(new SnapshotPoint(textSnapshotMock.Object, 8));
            yield return textSnapshotLineMock.Object;
        }

        [Test]
        public void GetTags_matchPReviewTagsCount_IfVaildReviewComment()
        {
            // given
            var spans = createTestSnapshotSpansWithOnelineComment(VALID_COMMENT);

            // when
            var tags = ((ITagger<PReviewTag>)tagger).GetTags(spans).ToList();

            // then
            Assert.AreEqual(1, tags.Count);
        }

        [Test]
        public void Constructor_AddCommentOwnerToTextBufferProperties()
        {
            // then
            Assert.AreEqual(tagger, properties.GetProperty(PReviewConstants.COMMENT_OWNER));
        }

        [Test]
        public void FileActionOccurred_sendReviewCommentsToServer_OnFileSavedEvent()
        {
            // given 
            var comments = new List<PReviewComment>();
            comments.Add(new PReviewComment());
            reviewServiceMock.Setup(service => service.ConvertToDexterResult(
                It.IsAny<ITextDocument>(), It.IsAny<IList<PReviewComment>>())).Returns(new DexterResult());

            // when
            textDocumentMock.Raise(doc => doc.FileActionOccurred += null,
                new TextDocumentFileActionEventArgs(@"c:\test.cs", new DateTime(), FileActionTypes.ContentSavedToDisk));

            // then
            dexterClientMock.Verify(client => client.SendAnalysisResult(It.IsAny<DexterResult>()));

        }

        private NormalizedSnapshotSpanCollection createTestSnapshotSpansWithOnelineComment(string comment)
        {
            textSnapshotMock.Setup(snapshot => snapshot.Length).Returns(comment.Length);
            textSnapshotMock.Setup(snapshot => snapshot.GetText(It.IsAny<Span>())).Returns(comment);

            var span = new Span(0, comment.Length);
            var snapshotSpan = new SnapshotSpan(textSnapshotMock.Object, span);
            var spans = new NormalizedSnapshotSpanCollection(snapshotSpan);

            return spans;
        }
    }
}
