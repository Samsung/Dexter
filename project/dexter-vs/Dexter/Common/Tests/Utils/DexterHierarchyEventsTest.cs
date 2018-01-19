using NUnit.Framework;
using Dexter.Common.Utils;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.VisualStudio;

namespace Dexter.Common.Tests.Utils
{
    [TestFixture()]
    public class DexterHierarchyEventsTest
    {
        DexterHierarchyEvents hierarchyEvents;

        [SetUp]
        public void SetUp()
        {
            hierarchyEvents = new DexterHierarchyEvents();
        }

        [Test()]
        public void OnInvalidateIcon_returnOK()
        {
            // when
            int result = hierarchyEvents.OnInvalidateIcon(IntPtr.Zero);

            // then
            Assert.AreEqual(VSConstants.S_OK, result);
        }

        [Test()]
        public void OnInvalidateItems_returnOK()
        {
            // when
            int result = hierarchyEvents.OnInvalidateItems(0);

            // then
            Assert.AreEqual(VSConstants.S_OK, result);
        }

        [Test()]
        public void OnItemAdded_returnOK()
        {
            // when
            int result = hierarchyEvents.OnItemAdded(0, 0, 0);

            // then
            Assert.AreEqual(VSConstants.S_OK, result);
        }

        [Test()]
        public void OnItemDeleted_returnOK()
        {
            // when
            int result = hierarchyEvents.OnItemDeleted(0);

            // then
            Assert.AreEqual(VSConstants.S_OK, result);
        }

        [Test()]
        public void OnItemsAppended_returnOK()
        {
            // when
            int result = hierarchyEvents.OnItemsAppended(0);

            // then
            Assert.AreEqual(VSConstants.S_OK, result);
        }

        [Test()]
        public void OnPropertyChanged_returnOK()
        {
            // when
            int result = hierarchyEvents.OnPropertyChanged(0, 0, 0);

            // then
            Assert.AreEqual(VSConstants.S_OK, result);
        }
    }
}