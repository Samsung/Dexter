#region Copyright notice
/**
 * Copyright (c) 2018 Samsung Electronics, Inc.,
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
#endregion
using log4net;
using Microsoft.CodeAnalysis.CSharp.Syntax;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Globalization;
using System.IO;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;

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

        public static string[] Split(string input)
        {
            input = Regex.Replace(input, "_", " ", RegexOptions.CultureInvariant);
            input = Regex.Replace(input, "([A-Z])", " $1", RegexOptions.CultureInvariant);
            input = Regex.Replace(input, @"\s+", " ");
            return input.Trim().Split(' '); ;
        }

        public static string FILE_SEPARATOR
        {
            get
            {
                return Path.AltDirectorySeparatorChar.ToString();
            }
        }

        public static bool IsPropertyDeclarationBoolean(PropertyDeclarationSyntax propertyRaw)
        {
            return propertyRaw.Type.ToString() == "bool" || propertyRaw.Type.ToString() == "boolean";
        }

        private static string dateFormat = "yyyyMMddHHmmss";
        private static DateTimeFormatInfo dti = new DateTimeFormatInfo();
        public static readonly string JSON_EXTENSION = ".json";

        public static bool IsDirectory(FileInfo fi)
        {
            FileAttributes attr = File.GetAttributes(fi.FullName);
            return ((attr & FileAttributes.Directory) == FileAttributes.Directory);
        }

        public static string GetSourcecodeFromFile(string filePath)
        {
            LogErrorAndExitIfFileDoesNotExist(filePath);
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

        public static string GetBase64CharSequence(string sourcecode)
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

        internal static FileInfo CreateEmptyFileIfDoesNotExist(string path)
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

        internal static void LogErrorAndExitIfFileDoesNotExist(string filePath)
        {
            try
            {
                if (!File.Exists(filePath))
                {
                    throw (new DexterRuntimeException("File does not exist: " + filePath));
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
            LogErrorAndExitIfFileDoesNotExist(cfgFilePath);
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
                throw new DexterRuntimeException("Directory does not exist: " + dir);
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

        public static void CreateFolderIfDoesNotExist(string dir)
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
                CliLog.Error("Cannot create directory: " + dir);
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
                CliLog.Error("Cannot get file names in directory: " + baseDir);
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
                CliLog.Error("Cannot get file names in directory: " + baseDir);
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

        public static IList<string> GetAllFilesAndDirectoriesInDirectory(string dir)
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
                    GetAllFilesAndDirectoriesInDirectory(d);
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