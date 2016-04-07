using NUnit.Framework;

namespace dexter_vs.Utils
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

            foreach (var element in array.OrEmptyIfNull())
            {
                Assert.IsNotNull(array);
            }
        }
    }
}
