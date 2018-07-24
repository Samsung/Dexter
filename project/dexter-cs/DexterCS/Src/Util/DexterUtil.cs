using log4net;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Globalization;
using System.IO;
using System.Linq;
using System.Text;

namespace DexterCS
{
    public class DexterUtil
    {
        private static ILog CliLog = LogManager.GetLogger(typeof(DexterUtil));
        public enum STATUS_CODE
        {
            SUCCESS, ERROR
        }
        public enum OS
        {
            WINDOWS, LINUX, MAC, UNKNOWN
        };

        public enum BIT
        {
            _32, _64, _128, UNKNOWN
        };

        public enum OS_BIT
        {
            WIN32, WIN64, WIN128, LINUX32, LINUX64, LINUX128, UNKNOWN
        };



        public static string FILE_SEPARATOR
        {
            get
            {
                return Path.AltDirectorySeparatorChar.ToString();
            }
        }

        private static string dateFormat = "yyyyMMddHHmmss";
        private static DateTimeFormatInfo dti = new DateTimeFormatInfo();
        public static readonly string JSON_EXTENSION = ".json";

        internal static bool IsDirectory(FileInfo fi)
        {
            FileAttributes attr = File.GetAttributes(fi.FullName);
            return ((attr & FileAttributes.Directory) == FileAttributes.Directory);
        }

        internal static string GetSourcecodeFromFile(string filePath)
        {
            CheckFileExistence(filePath);
            try
            {
                FileInfo fi = new FileInfo(filePath);
                if (IsDirectory(fi) || fi.Length > DexterConfig.SOURCE_FILE_SIZE_LIMIT)
                {
                    CliLog.Error("Dexter can't analyze:" + fi.FullName);
                    return "";
                }
                return File.ReadAllText(fi.FullName, Encoding.UTF8);
            }
            catch (IOException e)
            {
                throw new DexterRuntimeException(e.Message);
            }
        }

        public static string GetCurrentMethodName()
        {
            StackTrace st = new StackTrace();
            StackFrame sf = st.GetFrame(1);

            return sf.GetMethod().Name;
        }

        public static string currentDateTime()
        {
            DateTime dateTime = DateTime.Now;
            return ConvertDateTimeToFormatedString(dateTime);
        }

        internal static void CreateFolderIfNotExist(string resultFolderStr)
        {
            if (!Directory.Exists(resultFolderStr))
            {
                return;
            }
            try
            {
                Directory.CreateDirectory(resultFolderStr);
            }
            catch (Exception e)
            {
                CliLog.Error(e.StackTrace);
            }
        }

        internal static string GetBase64CharSequence(string sourcecode)
        {
            try
            {
                return Convert.ToBase64String(Encoding.UTF8.GetBytes(sourcecode));
            }
            catch (Exception e)
            {
                CliLog.Error(e.StackTrace);
                return "";
            }
        }

        internal static void WriteFilecontents(string contents, FileInfo file)
        {
            try
            {
                using (StreamWriter sw = file.AppendText())
                {
                    sw.Write(contents);
                }
            }
            catch (Exception e)
            {
                CliLog.Error(e.StackTrace);
            }
        }

        public static void CheckFileExistence(string filePath)
        {
            try
            {
                if (!File.Exists(filePath))
                    throw new DexterRuntimeException("There is no file to read " + filePath);
            }
            catch (DexterRuntimeException e)
            {
                CliLog.Error(e.Message);
                Environment.Exit(0);
            }
        }

        internal static FileInfo CreateEmptyFileIfNoyExist(string path)
        {
            FileInfo fi = new FileInfo(path);
            if (!fi.Exists)
            {
                try
                {
                    using (FileStream fs = fi.Create())
                    {
                        Byte[] info = new UTF8Encoding(true).GetBytes("");
                        fs.Write(info, 0, info.Length);
                    }
                }
                catch (Exception e)
                {
                    CliLog.Error(e.StackTrace);
                }
            }
            return fi;
        }

        internal static void ThrowExceptionWhenFileNotExist(string filePath)
        {
            try
            {
                if (!File.Exists(filePath))
                {
                    throw (new DexterRuntimeException("There is no file : " + filePath));
                }
            }
            catch (DexterRuntimeException e)
            {
                CliLog.Error(e.Message);
                Environment.Exit(0);
            }
        }

        internal static string GetCurrentDateTimeMillis()
        {
            return DateTime.UtcNow.ToString("yyyyMMddHHmmssfff", CultureInfo.InvariantCulture);
        }

        internal static string GetContentsFromFile(string cfgFilePath)
        {
            CheckFileExistence(cfgFilePath);
            return File.ReadAllText(cfgFilePath, Encoding.UTF8);
        }

        public static void CreateEmptyFileIfNotExist(string cfgFilePath)
        {
            if (!File.Exists(cfgFilePath))
            {
                try
                {
                    File.Create(cfgFilePath);
                }
                catch (Exception e)
                {
                    CliLog.Error(e.StackTrace);
                }
            }
        }

        private static string ConvertDateTimeToFormatedString(DateTime dateTime)
        {
            return dateTime.ToString(dateFormat, dti);
        }
        //convert Formatted String into DateTime
        public static DateTime convertStringToDateTime(string dateTimeInString)
        {
            dti.LongTimePattern = dateFormat;
            return DateTime.ParseExact(dateTimeInString, "T", dti);
        }

        public static void CheckFolderExistence(dynamic dir)
        {
            if (Object.ReferenceEquals(null, dir))
            {
                return;
            }
            if (!new DirectoryInfo(dir.ToString()).Exists)
            {
                throw new DexterRuntimeException("Folder(Directory) is not exist : " + dir);
            }
        }

        public static string RefinePath(string path)
        {
            if (string.IsNullOrEmpty(path))
            {
                return "";
            }
            string _path = path.Replace("//", "/").Replace("\\", "/").Replace(DexterUtil.FILE_SEPARATOR, "/");
            return _path;
        }

        public static void CreateFolderWithParents(string dir)
        {
            try
            {
                if (!Directory.Exists(dir))
                {
                    Directory.CreateDirectory(dir);
                }
            }
            catch (Exception)
            {
                CliLog.Error("Can not create");
            }
        }

        public static List<string> getSubFileNames(string baseDir)
        {
            if (string.IsNullOrEmpty(baseDir))
            {
                return new List<string>();
            }

            try
            {
                DirectoryInfo baseDirInfo = new DirectoryInfo(baseDir);
                var fileNames = baseDirInfo.GetFiles().Select(fileName => fileName.Name).ToList();
                return fileNames;
            }
            catch (Exception)
            {
                CliLog.Error("Can get Sub File Names");
                return new List<string>();
            }
        }

        public static IList<FileInfo> GetSubFileNamesByPrefix(string baseDir, string fileNamePrefix)
        {
            List<FileInfo> fileNames = null;
            if (string.IsNullOrEmpty(baseDir))
            {
                return new List<FileInfo>();
            }

            try
            {
                DirectoryInfo baseDirInfo = new DirectoryInfo(baseDir);
                return baseDirInfo.GetFiles(fileNamePrefix + "*").ToList();
            }
            catch (Exception)
            {
                CliLog.Error("Can get Sub File Names");
            }
            return fileNames;
        }

        public static string getFileName(string filePath)
        {
            return Path.GetFileName(filePath);
        }

        public static bool HasOption(object option)
        {
            return !(Object.ReferenceEquals(null, option));
        }

        public static IList<string> DirectorySearch(string dir)
        {
            IList<string> subDirList = new List<string>();
            try
            {
                foreach (string f in Directory.GetFiles(dir))
                {
                    subDirList.Add(Path.GetFullPath(f));
                }
                foreach (string d in Directory.GetDirectories(dir))
                {
                    subDirList.Add(Path.GetFullPath(d));
                    DirectorySearch(d);
                }
            }
            catch (Exception e)
            {
                CliLog.Error(e.Message);
            }
            return subDirList;
        }

    }
};