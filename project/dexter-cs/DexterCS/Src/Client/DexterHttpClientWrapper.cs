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
