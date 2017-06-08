using NUnit.Framework;
using Dexter.PeerReview.Utils;
using Dexter.PeerReview;
using Dexter.Common.Defect;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.VisualStudio.Text;
using Moq;
using Dexter.Common.Utils;

namespace Dexter.PeerReview.Tests.Utils
{
    [TestFixture()]
    public class PReviewServiceTest
    {
        PeerReviewService reviewService;
        Mock<ITextDocument> textDocumentMock;
        Mock<IDexterTextService> textServiceMock;

        [SetUp]
        public void SetUp()
        {
            textServiceMock = new Mock<IDexterTextService>(MockBehavior.Strict);
            textServiceMock.Setup(service => service.getText(It.IsAny<SnapshotSpan>())).Returns("");
            textServiceMock.Setup(service => service.getStartLineNumber(It.IsAny<SnapshotSpan>())).Returns(1);
            textServiceMock.Setup(service => service.getEndLineNumber(It.IsAny<SnapshotSpan>())).Returns(1);
            textDocumentMock = new Mock<ITextDocument>(MockBehavior.Strict);
            reviewService = new PeerReviewService(textServiceMock.Object);
        }

        [Test()]
        public void ConvertToDexterResult_setValidFileName()
        {
            // given
            var fileName = "testFile.cs";
            var filePath = "c:\\test\\" + fileName;
            textDocumentMock.Setup(document => document.FilePath).Returns(filePath);
            var comments = createTestComments();

            // when
            var result = reviewService.ConvertToDexterResult(textDocumentMock.Object, comments);

            // then
            Assert.AreEqual(fileName, result.FileName);
        }

        [Test()]
        public void ConvertToDexterResult_BackSlashsShouldbeConvertedToSlashs_GivenFileFullPath()
        {
            // given
            var fileName = "testFile.cs";
            var filePath = "c:\\test\\" + fileName;
            var convertedFilePath = "c:/test/" + fileName;
            textDocumentMock.Setup(document => document.FilePath).Returns(filePath);
            var comments = createTestComments();

            // when
            var result = reviewService.ConvertToDexterResult(textDocumentMock.Object, comments);

            // then
            Assert.AreEqual(convertedFilePath, result.FullFilePath);
        }

        [Test()]
        public void ConvertToDexterResult_setValidDefectCount()
        {
            // given
            textDocumentMock.Setup(document => document.FilePath).Returns("c://test.cs");
            var comments = createTestComments();

            // when
            var result = reviewService.ConvertToDexterResult(textDocumentMock.Object, comments);

            // then
            Assert.AreEqual(2, result.DefectCount);
        }

        [Test()]
        public void ConvertToDexterResult_setValidDefectList_GivenCommentsWithSameCheckerCodeAndFileName()
        {
            // given
            textDocumentMock.Setup(document => document.FilePath).Returns("c://test.cs");
            var comments = createTestComments();

            // when
            var result = reviewService.ConvertToDexterResult(textDocumentMock.Object, comments);

            // then
            Assert.AreEqual(1, result.DefectList.Count);
        }

        [Test()]
        public void ConvertToDexterResult_setValidOccurenceCount_GivenCommentsWithSameCheckerCodeAndFileName()
        {
            // given
            textDocumentMock.Setup(document => document.FilePath).Returns("c://test.cs");
            var comments = createTestComments();

            // when
            var result = reviewService.ConvertToDexterResult(textDocumentMock.Object, comments);

            // then
            var occurences = result.DefectList[0].Occurences;
            Assert.AreEqual(2, occurences.Count);
        }

        [Test()]
        public void ConvertToDexterResult_setValidDefectVariables()
        {
            // given
            textDocumentMock.Setup(document => document.FilePath).Returns("c://test.cs");
            var comments = createTestComments();

            // when
            var result = reviewService.ConvertToDexterResult(textDocumentMock.Object, comments);

            // then
            var defect = result.DefectList[0];
            Assert.AreEqual("PeerReview", defect.CategoryName);
            Assert.AreEqual("FILE", defect.AnalysisType);
            Assert.AreEqual("C_SHARP", defect.Language);
            Assert.AreEqual("dexter-peerreview", defect.ToolName);
            Assert.AreEqual("test.cs", defect.FileName);
        }

        [Test()]
        public void ConvertToDexterResult_setValidSererity_IfSererityIsMAJ()
        {
            // given
            textDocumentMock.Setup(document => document.FilePath).Returns("c://test.cs");
            textServiceMock.Setup(service => service.getText(It.IsAny<SnapshotSpan>()))
                .Returns("// DPR: [MAJ] test message");
            var comments = createTestComments();

            // when
            var result = reviewService.ConvertToDexterResult(textDocumentMock.Object, comments);

            // then
            var defect = result.DefectList[0];
            Assert.AreEqual("MAJ", defect.SeverityCode);
            Assert.AreEqual("DPR_MAJ", defect.CheckerCode);
        }

        [Test()]
        public void ConvertToDexterResult_setValidSererity_IfSererityIsCRI()
        {
            // given
            textDocumentMock.Setup(document => document.FilePath).Returns("c://test.cs");
            textServiceMock.Setup(service => service.getText(It.IsAny<SnapshotSpan>()))
                .Returns("// DPR: [CRI] test message");
            var comments = createTestComments();

            // when
            var result = reviewService.ConvertToDexterResult(textDocumentMock.Object, comments);

            // then
            var defect = result.DefectList[0];
            Assert.AreEqual("CRI", defect.SeverityCode);
            Assert.AreEqual("DPR_CRI", defect.CheckerCode);
        }

        [Test()]
        public void ConvertToDexterResult_setValidSererity_IfSererityIsCRC()
        {
            // given
            textDocumentMock.Setup(document => document.FilePath).Returns("c://test.cs");
            textServiceMock.Setup(service => service.getText(It.IsAny<SnapshotSpan>()))
                .Returns("// DPR: [CRC] test message");
            var comments = createTestComments();

            // when
            var result = reviewService.ConvertToDexterResult(textDocumentMock.Object, comments);

            // then
            var defect = result.DefectList[0];
            Assert.AreEqual("CRC", defect.SeverityCode);
            Assert.AreEqual("DPR_CRC", defect.CheckerCode);
        }

        [Test()]
        public void ConvertToDexterResult_setMAJSererity_IfSererityIsOmitted()
        {
            // given
            textDocumentMock.Setup(document => document.FilePath).Returns("c://test.cs");
            textServiceMock.Setup(service => service.getText(It.IsAny<SnapshotSpan>()))
                .Returns("// DPR: test message");
            var comments = createTestComments();

            // when
            var result = reviewService.ConvertToDexterResult(textDocumentMock.Object, comments);

            // then
            var defect = result.DefectList[0];
            Assert.AreEqual("MAJ", defect.SeverityCode);
            Assert.AreEqual("DPR_MAJ", defect.CheckerCode);
        }

        [Test()]
        public void ConvertToDexterResult_setEmptyDefectMessage()
        {
            // given
            textDocumentMock.Setup(document => document.FilePath).Returns("c://test.cs");
            textServiceMock.Setup(service => service.getText(It.IsAny<SnapshotSpan>()))
                .Returns("// DPR: [CRC] test message");
            var comments = createTestComments();

            // when
            var result = reviewService.ConvertToDexterResult(textDocumentMock.Object, comments);

            // then
            var defect = result.DefectList[0];
            Assert.AreEqual("", defect.Message);
        }

        [Test()]
        public void ConvertToDexterResult_DefectHasOccurences_IfFilePathAndCheckerCodeAreSame()
        {
            // given
            textDocumentMock.Setup(document => document.FilePath).Returns("c://test.cs");
            textServiceMock.Setup(service => service.getText(It.IsAny<SnapshotSpan>()))
                .Returns("// DPR: [CRC] test message");
            var comments = createTestComments();

            // when
            var result = reviewService.ConvertToDexterResult(textDocumentMock.Object, comments);

            // then
            var ocurrences = result.DefectList[0].Occurences;
            Assert.NotNull(ocurrences);
            Assert.AreEqual(2, ocurrences.Count);
        }

        [Test()]
        public void ConvertToDexterResult_OccurenceHasValidTexts()
        {
            // given
            textDocumentMock.Setup(document => document.FilePath).Returns("c://test.cs");
            textServiceMock.Setup(service => service.getText(It.IsAny<SnapshotSpan>()))
                .Returns("// DPR: [CRC] test message");
            var comments = createTestComments();

            // when
            var result = reviewService.ConvertToDexterResult(textDocumentMock.Object, comments);

            // then
            var ocurrence = result.DefectList[0].Occurences[0];
            Assert.AreEqual("DPR", ocurrence.StringValue);
            Assert.AreEqual("test message", ocurrence.Message);
        }

        [Test()]
        public void ConvertToDexterResult_OccurenceHasValidLines()
        {
            // given
            textDocumentMock.Setup(document => document.FilePath).Returns("c://test.cs");
            textServiceMock.Setup(service => service.getText(It.IsAny<SnapshotSpan>()))
                .Returns("// DPR: [CRC] test message");
            var comments = createTestComments();

            // when
            var result = reviewService.ConvertToDexterResult(textDocumentMock.Object, comments);

            // then
            var ocurrence = result.DefectList[0].Occurences[0];
            Assert.AreEqual(1, ocurrence.StartLine);
            Assert.AreEqual(1, ocurrence.EndLine);
        }

        [Test]
        public void getServerity_returnValidServerity_GivenSnapshotSpan()
        {
            // given
            var span = new SnapshotSpan();
            textServiceMock.Setup(service => service.getText(It.IsAny<SnapshotSpan>()))
                .Returns("// DPR: [CRI] test message");

            // when
            var serverity = reviewService.getServerity(span);

            // then
            Assert.AreEqual("CRI", serverity);
        }

        [Test]
        public void getCommentMessage_returnValidMessage_GivenSnapshotSpan()
        {
            // given
            var span = new SnapshotSpan();
            textServiceMock.Setup(service => service.getText(It.IsAny<SnapshotSpan>()))
                .Returns("// DPR: [CRI] test message");

            // when
            var message = reviewService.getCommentMessage(span);

            // then
            Assert.AreEqual("test message", message);
        }

        private IList<PeerReviewSnapshotComment> createTestComments()
        {
            var comments = new List<PeerReviewSnapshotComment>();

            comments.Add(createTestOneComment());
            comments.Add(createTestOneComment());
            return comments;
        }

        private PeerReviewSnapshotComment createTestOneComment()
        {
            return new PeerReviewSnapshotComment(reviewService, new SnapshotSpan());
        }
    }
}