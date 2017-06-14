using NUnit.Framework;
using Dexter.Common.Client;
using Dexter.Common.Defect;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Moq;
using System.Net;
using System.Net.Http;
using Dexter.Common.Config.Providers;
using Dexter.Common.Config;

namespace Dexter.Common.Tests.Client
{
    [TestFixture()]
    public class DexterClientTest
    {
        Mock<IHttpClient> httpClientMock;
        Mock<IDexterInfoProvider> dexterInfoProviderMock;
        DexterClient client;

        [SetUp]
        public void SetUp()
        {
            httpClientMock = new Mock<IHttpClient>(MockBehavior.Strict);
            dexterInfoProviderMock = new Mock<IDexterInfoProvider>();
            httpClientMock.Setup(http => http.PostAsync(It.IsAny<string>(), It.IsAny<string>()))
                .ReturnsAsync(new HttpResponseMessage(HttpStatusCode.OK));

            client = new DexterClient(httpClientMock.Object);
        }

        [Test]
        public void SendAnalysisResult_callPostAsync()
        {
            // given
            var dexterResult = new DexterResult();

            // when
            client.SendAnalysisResult(dexterResult).Wait();

            // then
            httpClientMock.Verify(http => http.PostAsync(It.IsAny<string>(),
                It.IsAny<string>()));
        }

        [Test]
        public void AddAccount_callPostAsync()
        {
            // when
            client.AddAccount("testUser", "testPassword", false).Wait();

            // then
            httpClientMock.Verify(http => http.PostAsync(It.IsAny<string>(),
                It.IsAny<string>()));
        }
    }
}