using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using System.Diagnostics;
using Dexter.Common.Defect;

namespace Dexter.Common.Client
{
    public class DexterClient : IDexterClient
    {
        private IHttpClient httpClient;
        private static IDexterClient instance;
        private static readonly string POST_ANALYSIS_RESULT_V3 = "/api/v3/csharp/analysis/result";

        public static IDexterClient Instance
        {
            get
            {
                if (instance == null)
                    throw new NullReferenceException("No Instance");

                return instance;
            }
            set
            {
                instance = value;
            }
        }

        public DexterClient(IHttpClient httpClient)
        {
            this.httpClient = httpClient;
        }

        public string SourceCode(string modulePath, string fileName)
        {
            return @"";
        }

        public Task SendAnalysisResult(string resultJson)
        {
            throw new NotImplementedException();
        }

        public Task StoreSourceCodeCharSequence(long snapshotId, long groupId, string modulePath, string fileName, string sourcecode)
        {
            throw new NotImplementedException();
        }

        public async Task SendAnalysisResult(DexterResult result)
        {
            HttpResponseMessage response = await httpClient.PostAsJsonAsync(POST_ANALYSIS_RESULT_V3,
                       new ResultJsonFormat { Result = result});

            if (!response.IsSuccessStatusCode.Equals(true))
            {
                Debug.WriteLine(response, "Failed to SendAnalysisResult");
            }
        }
    }

    public class ResultJsonFormat
    {
        [JsonProperty("result")]
        public DexterResult Result { get; set; }
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
