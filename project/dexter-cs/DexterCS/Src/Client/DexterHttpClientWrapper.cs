using System;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Text;
using System.Threading.Tasks;

namespace DexterCS.Client
{
    public interface IHttpClient
    {
        void Init(string serverAddress);
        Task<HttpResponseMessage> GetAsync(string requestUri);
        Task<HttpResponseMessage> PostAsJsonAsync<T>(string requestUri, T value);
    }

    public class DexterHttpClientWrapper : IHttpClient
    {
        private static string APPLICATION_TYPE_JSON = "application/json";
        private DexterServerConfig dexterServerConfig;
        private HttpClient httpClient;

        public DexterHttpClientWrapper()
        {
            httpClient = new HttpClient();
        }

        public DexterHttpClientWrapper(DexterServerConfig dexterServerConfig)
        {
            httpClient = new HttpClient();
            this.dexterServerConfig = dexterServerConfig;
            Init(dexterServerConfig.BaseUrl);
        }

        public Task<HttpResponseMessage> GetAsync(string requestUri)
        {
            return httpClient.GetAsync(requestUri);
        }

        public void Init(string serverAddress)
        {
            httpClient.BaseAddress = new Uri(serverAddress);
            httpClient.DefaultRequestHeaders.Accept.Clear();
            var credentials = Convert.ToBase64String(Encoding.ASCII.GetBytes(
                string.Format("{0}:{1}", dexterServerConfig.UserId, dexterServerConfig.UserPassword)));
            httpClient.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Basic", credentials);
            httpClient.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue(APPLICATION_TYPE_JSON));
        }

        public Task<HttpResponseMessage> PostAsJsonAsync<T>(string requestUri, T value)
        {
            return httpClient.PostAsJsonAsync(requestUri, value);
        }
    }
}
