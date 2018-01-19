using System;
using System.Threading.Tasks;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Text;
using Dexter.Common.Config.Providers;

namespace Dexter.Common.Client
{
    /// <summary>
    /// Comunicates with the dexter server
    /// </summary>
    public interface IHttpClient
    {
        /// <summary>
        /// Sends GET request to the dexter server
        /// </summary>
        /// <param name="requestUri">dexter server URI</param>
        /// <returns>Http response</returns>
        Task<HttpResponseMessage> GetAsync(string requestUri);

        /// <summary>
        /// Sends POST request to the dexter server
        /// </summary>
        /// <param name="requestUri">dexter server URI</param>
        /// <param name="content">JSON string to send</param>
        /// <returns>Http response</returns>
        Task<HttpResponseMessage> PostAsync(string requestUri, string content);
    }

    /// <summary>
    /// Comunicates with the dexter server
    /// </summary>
    public class DexterHttpClientWrapper : IHttpClient
    {
        private static string APPLICATION_TYPE_JSON = "application/json";
        private readonly HttpClient httpClient = new HttpClient();

        public DexterHttpClientWrapper(IDexterInfoProvider dexterInfoProvider)
        {
            setRequestHeader(dexterInfoProvider);
        }

        public Task<HttpResponseMessage> GetAsync(string requestUri)
        {
            return httpClient.GetAsync(requestUri);
        }

        public Task<HttpResponseMessage> PostAsync(string requestUri, string jsonString)
        {
            return httpClient.PostAsync(requestUri, new StringContent(jsonString, Encoding.UTF8, "application/json"));
        }

        private void setRequestHeader(IDexterInfoProvider dexterInfoProvider)
        {
            var dexterInfo = dexterInfoProvider.Load();

            httpClient.BaseAddress = new Uri($"http://{dexterInfo.dexterServerIp}:{dexterInfo.dexterServerPort}");
            httpClient.DefaultRequestHeaders.Accept.Clear();
            var credentials = Convert.ToBase64String(Encoding.ASCII.GetBytes($"{dexterInfo.userName}:{dexterInfo.userPassword}"));
            httpClient.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Basic", credentials);
            httpClient.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue(APPLICATION_TYPE_JSON));
        }
    }
}
