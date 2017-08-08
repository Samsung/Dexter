using NUnit.Framework;
using Dexter.Common.Config;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Dexter.Common.Tests.Config
{
    [TestFixture()]
    public class DexterInfoTest
    {
        [Test()]
        public void fromConfiguration_setIsDexterHomeEnabled()
        {
            // given
            var config = new Configuration()
            {
                IsDexterHomeEnabled = true
            };

            // when
            var dexterInfo = DexterInfo.fromConfiguration(config);

            // then
            Assert.AreEqual(true, dexterInfo.IsDexterHomeEnabled);
        }
    }
}