using NUnit.Framework;
using Dexter.Common.Utils;

namespace Dexter.Common.Tests.Utils
{

    [TestFixture]
    public class EnumerableExtensionsTest
    {
        /// <summary>
        /// Enumerable should return empty collection instead of null
        /// </summary>
        [Test]
        public void TestOrEmptyIfNull()
        {
            object[] array = null;

            Assert.IsNull(array);
            Assert.IsNotNull(array.OrEmptyIfNull());  
        }
    }
}
