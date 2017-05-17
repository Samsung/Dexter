﻿using NUnit.Framework;
using System;
using System.Xml;
using System.Xml.Serialization;

namespace Dexter.Defects
{
    [TestFixture]
    public class ResultTest
    {
        /// <summary>
        /// Tests deserialization of Result class from XML
        /// </summary>
        [Test]
        public void TestResultDeserialization()
        {
            XmlSerializer serializer = new XmlSerializer(typeof(Result));
            Result result;

            string resultFile = AppDomain.CurrentDomain.BaseDirectory + "../../TestData/dexter-result.xml";

            using (XmlReader reader = XmlReader.Create(resultFile))
            {
                result = (Result) serializer.Deserialize(reader);
            }

            Assert.NotNull(result);
            Assert.NotNull(result.FileDefects);
            Assert.IsNotEmpty(result.FileDefects);
        }
    }
}
