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
using Dexter.Common.Config.Providers;

namespace Dexter.Common.Client
{
    public class DexterClient : IDexterClient
    {
        IHttpClient httpClient;
        IDexterInfoProvider dexterInfoProvider;
        static IDexterClient instance;
        static readonly string POST_ANALYSIS_RESULT_V3 = "/api/v3/analysis/result";

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

        public DexterClient(IHttpClient httpClient, IDexterInfoProvider dexterInfoProvider)
        {
            this.httpClient = httpClient;
            this.dexterInfoProvider = dexterInfoProvider;
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
            var dexterResultString = JsonConvert.SerializeObject(result);

            HttpResponseMessage response = await httpClient.PostAsync(POST_ANALYSIS_RESULT_V3,
                       JsonConvert.SerializeObject(new ResultJsonFormat { Result = dexterResultString }));

            if (!response.IsSuccessStatusCode.Equals(true))
            {
                Debug.WriteLine(response, "Failed to SendAnalysisResult");
            }
        }

        public bool IsStandAloneMode()
        {
            return dexterInfoProvider.Load().standalone;
        }
    }

    public class ResultJsonFormat
    {
        [JsonProperty(PropertyName = "result")]
        public string Result { get; set; }
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
