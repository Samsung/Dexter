using Newtonsoft.Json;
using System.Net.Http;
using System.Threading.Tasks;
using System.Diagnostics;
using Dexter.Common.Defect;
using System.Web;
using System.ComponentModel.Composition;

namespace Dexter.Common.Client
{
    /// <summary>
    /// Communicates with the dexter server
    /// </summary>
    [Export(typeof(IDexterClient))]
    public class DexterClient : IDexterClient
    {
        private const string POST_ANALYSIS_RESULT_V3 = "/api/v3/analysis/result";
        private const string POST_ACCOUNT_ADD_V1 = "/api/v1/accounts/add";

        private readonly IHttpClient httpClient;

        public DexterClient(IHttpClient httpClient)
        {
            this.httpClient = httpClient;
        }

        public async Task<HttpResponseMessage> SendAnalysisResult(DexterResult result)
        {
            var dexterResultString = JsonConvert.SerializeObject(result);
            var dexterResultStringWrapped = JsonConvert.SerializeObject(new { Result = dexterResultString });

            return await httpClient.PostAsync(POST_ANALYSIS_RESULT_V3, dexterResultStringWrapped);
        }

        public async Task<HttpResponseMessage> AddAccount(string userName, string userPassword, bool isAdmin)
        {
            var queryParams = HttpUtility.ParseQueryString("");
            queryParams.Add("userId", userName);
            queryParams.Add("userId2", userPassword);
            queryParams.Add("isAdmin", isAdmin.ToString());

            return await httpClient.PostAsync($"{POST_ACCOUNT_ADD_V1}?{queryParams}","");
        } 
    }

}
