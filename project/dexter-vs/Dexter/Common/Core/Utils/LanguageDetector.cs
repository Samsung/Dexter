using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;

namespace Dexter.Common.Utils
{
    public static class LanguageDetector
    {
        private static readonly Dictionary<string, int> FileExtensionsCount = new Dictionary<string, int>();

        private static void WalkDirectoryTree(DirectoryInfo root)
        {
            FileInfo[] files = null;
            DirectoryInfo[] subDirs = null;

            try
            {
                files = root.GetFiles("*.*");
            }
            catch (UnauthorizedAccessException e)
            {
                Console.Error.WriteLine(e.Message);
            }
            catch (DirectoryNotFoundException e)
            {
                Console.Error.WriteLine(e.Message);

            }

            if (files != null)
            {
                foreach (FileInfo fi in files)
                {
                    if (FileExtensionsCount.ContainsKey(fi.Extension))
                    {
                        FileExtensionsCount[(fi.Extension)]++;
                    }
                    else
                    {
                        FileExtensionsCount.Add(fi.Extension, 1);
                    }
                }

                subDirs = root.GetDirectories();

                foreach (DirectoryInfo dirInfo in subDirs)
                {
                    WalkDirectoryTree(dirInfo);
                }
            }
        }

        public static bool IsMostCommonLanguageCSharp(string projectFullPath)
        {
            WalkDirectoryTree(new DirectoryInfo(projectFullPath));

            if (".cs" == FileExtensionsCount.OrderByDescending(entry => entry.Value).First().Key)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }
}
