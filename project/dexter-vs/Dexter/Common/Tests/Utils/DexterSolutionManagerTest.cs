using NUnit.Framework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.VisualStudio;
using Dexter.Common.Utils;

namespace Dexter.Common.Tests.Utils
{
    [TestFixture]
    public class DexterSolutionManagerTest
    {
        DexterSolutionManager manager;

        [SetUp]
        public void Setup()
        {
            manager = new DexterSolutionManager();
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
