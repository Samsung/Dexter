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

namespace Dexter.Common.Client.Tests
{
    [TestFixture()]
    public class DexterClientTest
    {
        Mock<IHttpClient> httpClientMock;
        DexterClient client;

        [SetUp]
        public void SetUp()
        {
            httpClientMock = new Mock<IHttpClient>(MockBehavior.Strict);
            httpClientMock.Setup(http => http.PostAsync(It.IsAny<string>(), It.IsAny<string>()))
                .ReturnsAsync(new HttpResponseMessage(HttpStatusCode.OK));

            client = new DexterClient(httpClientMock.Object);
        }

        [Test()]
        public void Instance_throwException_IfNoInstance()
        {
            // given
            IDexterClient temp;
            DexterClient.Instance = null;

            // when & then
            Assert.Throws<NullReferenceException>(() => { temp = DexterClient.Instance; });
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
    }
}