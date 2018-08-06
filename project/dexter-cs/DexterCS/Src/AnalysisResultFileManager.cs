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
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.IO;
using System.Text;

namespace DexterCS
{
    public class AnalysisResultFileManager
    {
        private static ILog CliLog = LogManager.GetLogger(typeof(AnalysisResultFileManager));
        private static AnalysisResultFileManager instance = null;
        private static readonly object padlock = new object();

        public static AnalysisResultFileManager Instance
        {
            get
            {
                lock (padlock)
                {
                    if (instance == null)
                    {
                        instance = new AnalysisResultFileManager();
                    }
                    return instance;
                }
            }
        }

        internal void WriteJson(List<AnalysisResult> resultList)
        {
            if (resultList.Count == 0)
            {
                return;
            }

            string resultFolderStr = DexterConfig.Instance.DexterHome + "/" + DexterConfig.RESULT_FOLDER_NAME;
            DexterUtil.CreateFolderIfNotExist(resultFolderStr);

            IAnalysisEntityFactory factory = new AnalysisEntityFactory();
            AnalysisResult baseResult = factory.CreateAnalysisResultList(resultList);
            RemoveOldResultFile(baseResult, resultFolderStr);
            WriteJsonResult(baseResult, resultFolderStr);

        }

        private void RemoveOldResultFile(AnalysisResult result, string resultFolderStr)
        {
            DirectoryInfo di = new DirectoryInfo(resultFolderStr);
            string resultFileName = GetResultFilePrefixName(result.ModulePath, result.FileName) + "_";
            FileInfo[] fiList = di.GetFiles(resultFileName + "*");
            foreach (var fi in fiList)
            {
                try
                {
                    fi.Delete();
                }
                catch (Exception e)
                {
                    CliLog.Error(e.StackTrace);
                }

            }
        }

        private void WriteJsonResult(AnalysisResult result, string resultFolderStr)
        {
            StringBuilder contents = CreateJsonFormat(result);
            FileInfo resultFile = GetResultFilePath(result, resultFolderStr);
            DexterUtil.WriteFilecontents(contents.ToString(), resultFile);
        }

        private FileInfo GetResultFilePath(AnalysisResult result, string resultFolderStr)
        {
            string path = resultFolderStr + "/" + GetResultFilePrefixName(result.ModulePath, result.FileName) +
                "_" + DexterUtil.GetCurrentDateTimeMillis() + ResultFileConstant.RESULT_FILE_EXTENSION;
            FileInfo resultFile = DexterUtil.CreateEmptyFileIfDoesNotExist(path);
            return resultFile;
        }

        public string GetResultFilePrefixName(string modulePath, string fileName)
        {
            return ResultFileConstant.RESULT_FILE_PREFIX + fileName + "_" + modulePath.GetHashCode();
        }

        private StringBuilder CreateJsonFormat(AnalysisResult result)
        {
            StringBuilder contents = new StringBuilder(1024);

            AddGeneralContent(result, contents);
            AddDefectContent(result, contents);
            contents.Append(Environment.NewLine).Remove(contents.Length - 1, 1);

            return contents;
        }

        private void AddDefectContent(AnalysisResult result, StringBuilder contents)
        {
            contents.Append(",\"").Append(ResultFileConstant.DEFECT_LIST).Append("\":[");
            int i = 0;
            foreach (var defect in result.DefectList)
            {
                if (i != 0)
                {
                    contents.Append(",");
                }

                contents.Append(JsonConvert.SerializeObject(defect));
                i++;
            }
            contents.Append("]}");
        }

        private void AddGeneralContent(AnalysisResult result, StringBuilder contents)
        {
            contents.Append("{\"").Append(ResultFileConstant.SNAPSHOT_ID).Append("\":\"").Append(result.SnapshotId).Append("\"");

            AddOptionalContent(contents, ResultFileConstant.MODULE_PATH, result.ModulePath);
            AddOptionalContent(contents, ResultFileConstant.FILE_NAME, result.FileName);
            AddOptionalContent(contents, ResultFileConstant.FULL_FILE_PATH, result.SourceFileFullPath);
            AddOptionalContent(contents, ResultFileConstant.PROJECT_NAME, result.ProjectName);

            contents.Append(",\"").Append(ResultFileConstant.GROUP_ID).Append("\":\"").Append(ResultFileConstant.DEFAULT_GROUPT_IP)
               .Append("\"").Append(",\"").Append(ResultFileConstant.DEFECT_COUNT).Append("\":\"")
               .Append(result.DefectList.Count).Append("\"");
        }

        private void AddOptionalContent(StringBuilder contents, string key, string value)
        {

            if (String.IsNullOrEmpty(value))
            {
                return;
            }
            contents.Append(",\"").Append(key).Append("\":\"").Append(value).Append("\"");
        }
    }
}
