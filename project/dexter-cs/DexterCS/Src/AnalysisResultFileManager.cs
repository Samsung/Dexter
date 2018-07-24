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
            FileInfo resultFile = DexterUtil.CreateEmptyFileIfNoyExist(path);
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
