using NUnit.Framework;

namespace Dexter.Utils
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
