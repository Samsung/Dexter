using NUnit.Framework;
using System.IO;
using Dexter.Common.Utils;

namespace Dexter.Common.Tests.Utils
{
    [TestFixture]
    class PathUtilsTest
    {
        /// <summary>
        /// Directory in AppData should exist
        /// </summary>
        [Test]       
        public void GetAppDataPath_DirectoryExists()
        {
            string path = PathUtils.GetAppDataPath("test.txt");

            string dirPath = Path.GetDirectoryName(path);

            Assert.IsTrue(Directory.Exists(dirPath));
        }
    }
}
