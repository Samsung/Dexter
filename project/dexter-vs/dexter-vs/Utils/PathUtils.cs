using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace dexter_vs.Utils
{
    /// <summary>
    /// Path utilities
    /// </summary>
    public sealed class PathUtils
    {
        /// <summary>
        /// Default folder for Dexter Application Data
        /// </summary>
        public const string DexterAppDataFolder = "Dexter";

        /// <summary>
        /// Returns absolute path to a file in AppData directory.
        /// </summary>
        /// <param name="file">file in AppData to get path to</param>
        /// <returns>absolute path to given file</returns>
        public static string GetAppDataPath(string file)
        {
            var basePath = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData);
            var folderPath = Path.Combine(basePath, DexterAppDataFolder);

            if (!Directory.Exists(folderPath))
            {
                Directory.CreateDirectory(folderPath);
            }

            var filePath = Path.Combine(folderPath, file);

            return filePath;
        }

    }
}
