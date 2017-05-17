using NUnit.Framework;
using System.IO;

namespace Dexter.Utils
{
    [TestFixture]
    class PathUtilsTest
    {
        /// <summary>
        /// Directory in AppData should exist
        /// </summary>
        [Test]
        public void TestAppDataPath()
        {
            string path = PathUtils.GetAppDataPath("test.txt");

            string dirPath = Path.GetDirectoryName(path);

            Assert.IsTrue(Directory.Exists(dirPath));
        }
    }
}
