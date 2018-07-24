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

