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
using System.Net.Http;
using System.Threading.Tasks;

namespace DexterCS.Client
{
    public class DexterClient : IDexterClient
    {
        private static readonly ILog CliLog = LogManager.GetLogger(typeof(DexterClient));
        private IHttpClient httpClient;

        public string ServerAddress { get; set; }

        public DexterClient(IHttpClient httpClient)
        {
            this.httpClient = httpClient;
        }

        public void InitHttpClient(string serverUri)
        {
            ServerAddress = serverUri;
            httpClient.Init(ServerAddress);
        }

        public string DexterWebUrl { get; set; }

        public string ServerHost { get; set; }

        public int ServerPort { get; set; }

        public string SourceCode(string modulePath, string fileName)
        {
            return @"";
        }

        public async Task SendAnalysisResult(string resultJson)
        {
            if (string.IsNullOrEmpty(resultJson))
            {
                throw new DexterRuntimeException("The result file has no content to send");
            }

            if (DexterConfig.Run_Mode.CLI == DexterConfig.Instance.RunMode)
            {
                try
                {
                    HttpResponseMessage response = await httpClient.PostAsJsonAsync(DexterConfig.POST_ANALYSIS_RESULT_V3,
                       new ResultJsonFormat { Result = JsonConvert.DeserializeObject<ResultJsonParsing>(resultJson) });

                    response.EnsureSuccessStatusCode();
                }
                catch (Exception e)
                {
                    CliLog.Error(e.Message);
                    CliLog.Error(e.StackTrace);
                }
            }
        }

        public async Task StoreSourceCodeCharSequence(long snapshotId, long groupId, string modulePath, string fileName, string sourcecode)
        {
            try
            {
                HttpResponseMessage response = await httpClient.PostAsJsonAsync(DexterConfig.POST_SNAPSHOT_SOURCECODE,
                   new SourceCodeJsonFormat
                   {
                       SnapshotId = snapshotId,
                       GroupId = groupId,
                       ModulePath = modulePath,
                       FileName = fileName,
                       SourceCode = DexterUtil.GetBase64CharSequence(sourcecode)
                   });

                response.EnsureSuccessStatusCode();
            }
            catch (Exception e)
            {
                CliLog.Error(e.StackTrace);
            }
        }
    }

    public class ResultJsonFormat
    {
        [JsonProperty("result")]
        public ResultJsonParsing Result { get; set; }
    }
    public class SourceCodeJsonFormat
    {
        [JsonProperty("snapshotId")]
        public long SnapshotId { get; set; }
        [JsonProperty("gouprId")]
        public long GroupId { get; set; }
        [JsonProperty("modulePath")]
        public string ModulePath { get; set; }
        [JsonProperty("fileName")]
        public string FileName { get; set; }
        [JsonProperty("sourceCode")]
        public string SourceCode { get; set; }
    }
}

