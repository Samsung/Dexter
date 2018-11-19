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
using DexterCS.Client;
using DexterCS.Client;
using DexterCS.Job;
using log4net;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;

namespace DexterCS
{
    public class CLIAnalysisResultHandler : IAnalysisResultHandler
    {
        private ILog cliLog;
        private IDexterCLIOption cliOption;
        private string dexterWebUrl;

        private ICLIResultFile cliResultFile = new CLIResultFile();
        private int totalCnt = 0;
        private int totalOccurenceCnt = 0;
        private int criticalCnt = 0;
        private int majorCnt = 0;
        private int minorCnt = 0;
        private int etcCnt = 0;
        private int crcCnt = 0;

        public CLIAnalysisResultHandler(string dexterWebUrl, IDexterCLIOption cliOption, ILog cliLog)
        {
            this.dexterWebUrl = dexterWebUrl;
            this.cliOption = cliOption;
            this.cliLog = cliLog;
        }

        public void HandleBeginningOfResultFile()
        {
            try
            {
                if (cliOption.IsJsonResultFile)
                {
                    cliResultFile.WriteJsonResultFilePrefix(cliOption.JsonResultFile);
                }
                if (cliOption.IsXmlResultFile)
                {
                    cliResultFile.WriteXmlResultFilePrefix(cliOption.XmlResultFile);
                }
                if (cliOption.IsXml2ResultFile)
                {
                    cliResultFile.WriteXml2ResultFilePrefix(cliOption.Xml2ResultFile);
                }
            }
            catch (IOException e)
            {
                cliLog.Error(e.Message, e);
            }
        }

        public void HandleEndOfResultFile()
        {
            try
            {
                if (cliOption.IsXmlResultFile)
                {
                    cliResultFile.WriteXmlResultFilePostfix(cliOption.XmlResultFile);
                }
                else if (cliOption.IsJsonResultFile)
                {
                    cliResultFile.WriteJsonResultFilePostfix(cliOption.JsonResultFile);
                }
            }
            catch (Exception e)
            {
                cliLog.Error(e.StackTrace);
            }
        }

        public void PrintLogAfterAnalyze()
        {
            cliLog.Info("");
            cliLog.Info("====================================================");
            cliLog.Info("- Total Defects: " + totalCnt);
            cliLog.Info("- Critical Defects: " + criticalCnt);
            cliLog.Info("- Major Defects: " + majorCnt);
            cliLog.Info("- Minor Defects: " + minorCnt);
            cliLog.Info("- CRC Defects: " + crcCnt);
            cliLog.Info("- Etc. Defects: " + etcCnt);
            cliLog.Info("- Total Occurences: " + totalOccurenceCnt);
            cliLog.Info("====================================================");
            cliLog.Info("");
        }

        public void HandleAnalysisResult(List<AnalysisResult> resultList, IDexterClient client)
        {
            if (resultList.Count == 0)
            {
                cliLog.Warn("No defect result");
                return;
            }

            List<Defect> allDefectList = DexterAnalyzer.AllDefectList(resultList);
            AnalysisResult firstAnalysisResult = resultList[0];
            string sourceFileFullPath = firstAnalysisResult.SourceFileFullPath;

            try
            {
                WriteResultFile(allDefectList, sourceFileFullPath);
                string resultFilePrefixName = AnalysisResultFileManager.Instance.
                    GetResultFilePrefixName(firstAnalysisResult.ModulePath, firstAnalysisResult.FileName);
                SendResultJob.SendResultFileThenDelete(client, resultFilePrefixName);
            }
            catch (IOException e)
            {
                cliLog.Error(e.StackTrace);
            }

            cliLog.Info(" - " + sourceFileFullPath);
            if (allDefectList.Count == 0)
            {
                cliLog.Info("    > No Defect");
                return;
            }
            else
            {
                cliLog.Info("    > Total Defects: " + allDefectList.Count);
            }
            PrintDefect(allDefectList);
        }

        private void PrintDefect(List<Defect> allDefectList)
        {
            foreach (var defect in allDefectList)
            {
                switch (defect.SeverityCode)
                {
                    case "CRI":
                        criticalCnt++;
                        break;
                    case "MAJ":
                        majorCnt++;
                        break;
                    case "MIN":
                        minorCnt++;
                        break;
                    case "CRC":
                        crcCnt++;
                        break;
                    case "ETC":
                        etcCnt++;
                        break;
                    default:
                        defect.SeverityCode = "ETC";
                        etcCnt++;
                        break;
                }
                totalCnt++;
                totalOccurenceCnt += defect.Occurences.Count;

                cliLog.Info("    > " + defect.CheckerCode + " / " + defect.SeverityCode + " / "
                    + defect.Occurences.Count + " / " + defect.MethodName + " / " + defect.ClassName);

                PrintOccurences(defect.Occurences.ToArray());
            }
        }

        private void PrintOccurences(Occurence[] occurence)
        {
            int i = 0;
            foreach (var occ in occurence)
            {
                i += 1;
                if (i == occurence.Count())
                {
                    cliLog.Info("       + " + occ.StartLine + " " + occ.Message + " / " +
                        occ.VariableName + " / " + occ.StringValue);
                }
                else
                {
                    cliLog.Info("       + " + occ.StartLine + " " + occ.Message + " / " +
                        occ.VariableName + " / " + occ.StringValue);
                }
            }
        }

        private void WriteResultFile(List<Defect> allDefectList, string sourceFileFullPath)
        {
            if (cliOption.IsJsonResultFile)
            {
                cliResultFile.WriteJsonResultFileBody(cliOption.JsonResultFile, allDefectList);
            }

            if (cliOption.IsXmlResultFile)
            {
                cliResultFile.WriteXmlResultFileBody(cliOption.XmlResultFile, allDefectList, sourceFileFullPath);
            }

            if (cliOption.IsXml2ResultFile)
            {
                cliResultFile.WriteXml2ResultFileBody(cliOption.Xml2ResultFile, allDefectList, sourceFileFullPath);
            }
        }
    }
}
