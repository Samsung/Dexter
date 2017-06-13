using NUnit.Framework;
using Dexter.Common.Utils;
using Dexter.PeerReview.Utils;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Moq;
using Microsoft.VisualStudio.Shell;

namespace Dexter.PeerReview.Tests.Utils
{
    [TestFixture()]
    public class PeerReviewCommentManagerTests
    {
        Mock<IDexterFileService> fileServiceMock;
        Mock<IPeerReviewService> reviewServiceMock;
        Mock<IDexterSolutionManager> solutionManagerMock;
        Mock<IPeerReviewTaskProviderWrapper> taskProviderMock;
        Mock<IPeerReviewTaskCollectionWrapper> taskCollectionMock;
        PeerReviewCommentManager manager;

        [SetUp]
        public void SetUp()
        {
            fileServiceMock = new Mock<IDexterFileService>();
            reviewServiceMock = new Mock<IPeerReviewService>();
            solutionManagerMock = new Mock<IDexterSolutionManager>();
            taskProviderMock = new Mock<IPeerReviewTaskProviderWrapper>();
            taskCollectionMock = new Mock<IPeerReviewTaskCollectionWrapper>();
            taskProviderMock.Setup(provider => provider.Tasks).Returns(taskCollectionMock.Object);

            manager = new PeerReviewCommentManager(fileServiceMock.Object, reviewServiceMock.Object, solutionManagerMock.Object, taskProviderMock.Object);
        }

        [Test()]
        public void UpdateReviewComments_matchCountOfComments_GivenOneFile()
        {
            // given
            var testFilePaths = new List<string>() { "c:\\test1.c" };
            var testFileContent = createTestFileContent();
            fileServiceMock.Setup(service => service.ReadTextAsync("c:\\test1.c")).ReturnsAsync(testFileContent);

            // when
            manager.UpdateReviewComments(testFilePaths, true).Wait();

            // then
            Assert.AreEqual(2, manager.Comments.Count);
        }

        [Test()]
        public void UpdateReviewComments_matchCountOfComments_GivenTwoFile()
        {
            // given
            var testFilePaths = new List<string>() { "c:\\test1.c", "c:\\test2.c" };
            var testFileContent = createTestFileContent();
            fileServiceMock.Setup(service => service.ReadTextAsync(It.IsAny<string>())).ReturnsAsync(testFileContent);

            // when
            manager.UpdateReviewComments(testFilePaths, true).Wait();

            // then
            Assert.AreEqual(4, manager.Comments.Count);
        }

        [Test()]
        public void UpdateReviewComments_matchCountOfComments_GivenTwoFileAndThenOneFileDeleted()
        {
            // given
            var addedFilePaths = new List<string>() { "c:\\test1.c", "c:\\test2.c" };
            var deletedFilePaths = new List<string>() { "c:\\test1.c" };
            var testFileContent = createTestFileContent();
            fileServiceMock.Setup(service => service.ReadTextAsync(It.IsAny<string>())).ReturnsAsync(testFileContent);

            // when
            manager.UpdateReviewComments(addedFilePaths, true).Wait();
            manager.UpdateReviewComments(deletedFilePaths, false).Wait();

            // then
            Assert.AreEqual(2, manager.Comments.Count);
        }

        [Test()]
        public void UpdateReviewComments_matchLineNumbersOfComments_GivenOneFile()
        {
            // given
            var testFilePaths = new List<string>() { "c:\\test1.c" };
            var testFileContent = createTestFileContent();
            fileServiceMock.Setup(service => service.ReadTextAsync("c:\\test1.c")).ReturnsAsync(testFileContent);

            // when
            manager.UpdateReviewComments(testFilePaths, true).Wait();

            // then
            Assert.AreEqual(4, manager.Comments[0].StartLine);
            Assert.AreEqual(4, manager.Comments[0].EndLine);
            Assert.AreEqual(8, manager.Comments[1].StartLine);
            Assert.AreEqual(8, manager.Comments[1].EndLine);
        }

        [Test()]
        public void UpdateReviewComments_matchFilePathOfComments_GivenOneFile()
        {
            // given
            var testFilePaths = new List<string>() { "c:\\test1.c" };
            var testFileContent = createTestFileContent();
            fileServiceMock.Setup(service => service.ReadTextAsync("c:\\test1.c")).ReturnsAsync(testFileContent);

            // when
            manager.UpdateReviewComments(testFilePaths, true).Wait();

            // then
            Assert.AreEqual("c:\\test1.c", manager.Comments[0].FilePath);
        }

        [Test()]
        public void UpdateReviewComments_matchCountOfTasks_GivenOneFile()
        {
            // given
            var testFilePaths = new List<string>() { "c:\\test1.c" };
            var testFileContent = createTestFileContent();
            fileServiceMock.Setup(service => service.ReadTextAsync("c:\\test1.c")).ReturnsAsync(testFileContent);
            var countOfTasks = 0;
            taskCollectionMock.Setup(tasks => tasks.Add(It.IsAny<Microsoft.VisualStudio.Shell.Task>()))
                .Callback(() => countOfTasks++);

            // when
            manager.UpdateReviewComments(testFilePaths, true).Wait();

            // then
            Assert.AreEqual(2, countOfTasks);
        }

        [Test()]
        public void UpdateReviewComments_TaskHasValidInfo_GivenOneFile()
        {
            // given
            var testFilePaths = new List<string>() { "c:\\test1.c" };
            var testFileContent = createTestFileContent();
            fileServiceMock.Setup(service => service.ReadTextAsync("c:\\test1.c")).ReturnsAsync(testFileContent);
            IList<Microsoft.VisualStudio.Shell.Task> resultTasks = new List<Microsoft.VisualStudio.Shell.Task>();
            taskCollectionMock.Setup(tasks => tasks.Add(It.IsAny<Microsoft.VisualStudio.Shell.Task>()))
                .Callback((Microsoft.VisualStudio.Shell.Task task) => resultTasks.Add(task));

            // when
            manager.UpdateReviewComments(testFilePaths, true).Wait();

            // then
            Assert.AreEqual("c:\\test1.c", resultTasks[0].Document);
            Assert.AreEqual(4, resultTasks[0].Line);
            Assert.AreEqual(37, resultTasks[0].Column);
        }

        private string createTestFileContent()
        {
            return @"void main() 
{
    var sum = 0;
    for (int i=0 ; i < 100 ; i++) { // DPR: [CRI] test message1
        sum += i; 
    }

    PrintSum(sum); // DPR: test message2
}";
        }
    }
}