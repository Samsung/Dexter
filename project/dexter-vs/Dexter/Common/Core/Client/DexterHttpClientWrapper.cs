using System;
using System.Threading.Tasks;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Text;
using Dexter.Common.Config;
using Dexter.Common.Config.Providers;
using Newtonsoft.Json;

namespace Dexter.Common.Client
{
    public interface IHttpClient
    {
        Task<HttpResponseMessage> GetAsync(string requestUri);
        Task<HttpResponseMessage> PostAsJsonAsync<T>(string requestUri, T value);
        Task<HttpResponseMessage> PostAsync(string requestUri, string content);
    }

    public class DexterHttpClientWrapper : IHttpClient
    {
        private static string APPLICATION_TYPE_JSON = "application/json";
        private IDexterInfoProvider dexterInfoProvider;

        public DexterHttpClientWrapper(IDexterInfoProvider dexterInfoProvider)
        {
            this.dexterInfoProvider = dexterInfoProvider;
        }

        public Task<HttpResponseMessage> GetAsync(string requestUri)
        {
            HttpClient httpClient = new HttpClient();
            setRequestHeader(httpClient);
            return httpClient.GetAsync(requestUri);
        }

        private void setRequestHeader(HttpClient httpClient)
        {
            var dexterInfo = dexterInfoProvider.Load();

            httpClient.BaseAddress = new Uri($"http://{dexterInfo.dexterServerIp}:{dexterInfo.dexterServerPort}");
            httpClient.DefaultRequestHeaders.Accept.Clear();
            var credentials = Convert.ToBase64String(Encoding.ASCII.GetBytes(
                $"{dexterInfo.userName}:{dexterInfo.userPassword}"));

            httpClient.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Basic", credentials);
            httpClient.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue(APPLICATION_TYPE_JSON));
        }

        public Task<HttpResponseMessage> PostAsJsonAsync<T>(string requestUri, T value)
        {
            HttpClient httpClient = new HttpClient();
            setRequestHeader(httpClient);
            return httpClient.PostAsJsonAsync(requestUri, value);
        }

        public Task<HttpResponseMessage> PostAsync(string requestUri, string jsonString)
        {
            HttpClient httpClient = new HttpClient();
            setRequestHeader(httpClient);
            return httpClient.PostAsync(requestUri, new StringContent(jsonString, Encoding.UTF8, "application/json"));
        }
    }
}
