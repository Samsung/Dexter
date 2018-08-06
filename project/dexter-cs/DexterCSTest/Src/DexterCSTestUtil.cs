using System;
using System.IO;
using System.Linq;

namespace DexterCSTest.Src
{
    public static class DexterCSTestUtil
    {
        private static Random random = new Random();

        public static string RandomString(int length = 10)
        {
            const string chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            return new string(Enumerable.Repeat(chars, length)
              .Select(s => s[random.Next(s.Length)]).ToArray());
        }

        public static string TestingDirectory
        {
            get
            {
                return Path.GetFullPath(@"..\..\TestingDirectory");
            }
        }

        public static void ClearTestingDirectory()
        {
            DirectoryInfo di = new DirectoryInfo(TestingDirectory);

            foreach (FileInfo file in di.GetFiles())
            {
                file.Delete();
            }
            foreach (DirectoryInfo dir in di.GetDirectories())
            {
                dir.Delete(true);
            }
        }
    }
}
