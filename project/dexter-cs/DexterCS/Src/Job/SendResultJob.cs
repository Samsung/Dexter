using DexterCS.Client;
using log4net;
using System;
using System.Collections.Generic;
using System.IO;
using System.Text;

namespace DexterCS.Job
{
    public class SendResultJob
    {
        private static ILog CliLog = LogManager.GetLogger(typeof(SendResultJob));
        private IDexterClient client;
        public SendResultJob(IDexterClient client)
        {
            this.client = client;
        }

        internal static void SendResultFileThenDelete(IDexterClient client, string resultFilePrefix)
        {
            string resultFolderStr = DexterConfig.Instance.DexterHome + "/" + DexterConfig.RESULT_FOLDER_NAME;
            IList<FileInfo> resultFiles = DexterUtil.GetSubFileNamesByPrefix(resultFolderStr, resultFilePrefix);

            foreach (var resultFile in resultFiles)
            {
                SendResult(resultFile, client);
                MmoveResultFileToOldFolder(resultFile);
            }
        }

        private static void MmoveResultFileToOldFolder(FileInfo resultFile)
        {
            try
            {
                string oldResultPath = DexterConfig.Instance.OldFilePath;
                File.Move(resultFile.FullName, oldResultPath + "/" + resultFile.Name);
            }
            catch (Exception e)
            {
                throw new DexterRuntimeException(e.Message);
            }
        }

        private static void SendResult(FileInfo resultFile, IDexterClient client)
        {
            if (DexterUtil.IsDirectory(resultFile) || false.Equals(resultFile.Exists) || true.Equals(resultFile.IsReadOnly))
            {
                throw new DexterRuntimeException("Invalid resultFile parameter:" + resultFile);
            }

            if (!DexterUtil.JSON_EXTENSION.Equals(resultFile.Extension)
                || !resultFile.ToString().StartsWith("result_", StringComparison.Ordinal))
            {
                return;
            }
            try
            {
                client.SendAnalysisResult(File.ReadAllText(resultFile.FullName, Encoding.UTF8)).Wait();
            }
            catch (Exception e)
            {
                CliLog.Error(e.StackTrace);
            }
        }


    }
}