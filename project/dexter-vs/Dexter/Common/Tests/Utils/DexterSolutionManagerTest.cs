using NUnit.Framework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.VisualStudio;
using Dexter.Common.Utils;
using Moq;

namespace Dexter.Common.Tests.Utils
{
    [TestFixture]
    public class DexterSolutionManagerTest
    {
        Mock<IDexterHierarchyService> hierarchyServiceMock; 
        DexterSolutionManager manager;
        IList<string> testFilePaths;

        [SetUp]
        public void Setup()
        {
            hierarchyServiceMock = new Mock<IDexterHierarchyService>();
            manager = new DexterSolutionManager(hierarchyServiceMock.Object);

            testFilePaths = new List<string>()
            {
                "c:\\test1.cs", "c:\\test2.cs"
            };
            hierarchyServiceMock.Setup(service => service.getAllSourceFilePaths(null)).Returns(testFilePaths);
        }

        [Test]
        public void Instance_ReturnValidInstance()
        {
            // given
            DexterSolutionManager.Instance = manager;

            // when
            var returnedManager = DexterSolutionManager.Instance;

            // then
            Assert.AreEqual(manager, returnedManager);
        }

        [Test]
        public void Instance_ThrowException_IfInstanceIsNull()
        {
            // given
            DexterSolutionManager.Instance = null;

            // when &  then
            Assert.Throws(typeof(ArgumentNullException), 
                delegate { var result = DexterSolutionManager.Instance; });
        }

        [Test]
        public void OnAfterCloseSolution_returnOK()
        {
            // when
            int result = manager.OnAfterCloseSolution(null);

            // then
            Assert.AreEqual(VSConstants.S_OK, result);
        }

        [Test]
        public void OnAfterLoadProject_returnOK()
        {
            // when
            int result = manager.OnAfterLoadProject(null, null);

            // then
            Assert.AreEqual(VSConstants.S_OK, result);
        }

        [Test]
        public void OnAfterOpenProject_returnOK()
        {
            // when
            int result = manager.OnAfterOpenProject(null, 0);

            // then
            Assert.AreEqual(VSConstants.S_OK, result);
        }

        [Test]
        public void OnAfterOpenProject_raiseSourceFileEvent()
        {
            // given 
            var isEventRaised = false;
            manager.SourceFilesChanged += (s, e) => isEventRaised = true;

            // when
            int result = manager.OnAfterOpenProject(null, 0);

            // then
            Assert.AreEqual(true, isEventRaised);
        }

        [Test]
        public void OnAfterOpenProject_raiseSourceFileEvent_WithValidFilePaths()
        {
            // given 
            IList<string> passedFilePaths = null;
            manager.SourceFilesChanged += (s, e) => passedFilePaths = e.FilePaths;

            // when
            int result = manager.OnAfterOpenProject(null, 0);

            // then
            Assert.AreEqual(testFilePaths, passedFilePaths);
        }

        [Test]
        public void OnAfterOpenProject_raiseSourceFileEvent_WithTrueAddedFlag()
        {
            // given 
            bool isAddedFlag = false;
            manager.SourceFilesChanged += (s, e) => isAddedFlag = e.IsAdded;

            // when
            int result = manager.OnAfterOpenProject(null, 0);

            // then
            Assert.AreEqual(true, isAddedFlag);
        }

        [Test]
        public void OnAfterOpenProject_doNotRaiseSourceFileEvent_GivenNoSourceFile()
        {
            // given 
            IList<string> emptyFilePaths = new List<string>();
            hierarchyServiceMock.Setup(service => service.getAllSourceFilePaths(null)).Returns(emptyFilePaths);
            var isEventRaised = false;
            manager.SourceFilesChanged += (s, e) => isEventRaised = true;

            // when
            int result = manager.OnAfterOpenProject(null, 0);

            // then
            Assert.AreEqual(false, isEventRaised);
        }

        [Test]
        public void OnAfterOpenSolution_returnOK()
        {
            // when
            int result = manager.OnAfterOpenSolution(null, 0);

            // then
            Assert.AreEqual(VSConstants.S_OK, result);
        }

        [Test]
        public void OnBeforeCloseProject_returnOK()
        {
            // when
            int result = manager.OnBeforeCloseProject(null, 0);

            // then
            Assert.AreEqual(VSConstants.S_OK, result);
        }

        [Test]
        public void OnBeforeCloseSolution_returnOK()
        {
            // when
            int result = manager.OnBeforeCloseSolution(null);

            // then
            Assert.AreEqual(VSConstants.S_OK, result);
        }

        [Test]
        public void OnBeforeUnloadProject_returnOK()
        {
            // when
            int result = manager.OnBeforeUnloadProject(null, null);

            // then
            Assert.AreEqual(VSConstants.S_OK, result);
        }

        [Test]
        public void OnQueryCloseProject_returnOK()
        {
            // given
            int pfCancel = DexterConstants.FALSE;

            // when
            int result = manager.OnQueryCloseProject(null, 0, ref pfCancel);

            // then
            Assert.AreEqual(VSConstants.S_OK, result);
        }

        [Test]
        public void OnQueryCloseProject_setCancelVariableToFalse()
        {
            // given
            int pfCancel = DexterConstants.FALSE;

            // when
            int result = manager.OnQueryCloseProject(null, 0, ref pfCancel);

            // then
            Assert.AreEqual(DexterConstants.FALSE, pfCancel);
        }

        [Test]
        public void OnQueryCloseSolution_returnOK()
        {
            // given
            int temp = DexterConstants.FALSE;

            // when
            int result = manager.OnQueryCloseSolution(null, ref temp);

            // then
            Assert.AreEqual(VSConstants.S_OK, result);
        }

        [Test]
        public void OnQueryCloseSolution_setCancelVariableToFalse()
        {
            // given
            int pfCancel = DexterConstants.FALSE;

            // when
            int result = manager.OnQueryCloseSolution(null, ref pfCancel);

            // then
            Assert.AreEqual(DexterConstants.FALSE, pfCancel);
        }

        [Test]
        public void OnQueryUnloadProject_returnOK()
        {
            // given
            int temp = DexterConstants.FALSE;

            // when
            int result = manager.OnQueryUnloadProject(null, ref temp);

            // then
            Assert.AreEqual(VSConstants.S_OK, result);
        }

        [Test]
        public void OnQueryUnloadProject_setCancelVariableToFalse()
        {
            // given
            int pfCancel = DexterConstants.FALSE;

            // when
            int result = manager.OnQueryUnloadProject(null, ref pfCancel);

            // then
            Assert.AreEqual(DexterConstants.FALSE, pfCancel);
        }
    }
}
