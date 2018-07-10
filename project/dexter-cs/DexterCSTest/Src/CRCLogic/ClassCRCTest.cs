using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using DexterCRC;

namespace DexterCSTest
{
    [TestClass]
    public class ClassCRCTest
    {
        ClassCRC classCRC;

        void Init()
        {
            classCRC = new ClassCRC();
        }

        [TestMethod]
        public void CheckEventNaming_Should_True_Invalid_Class_Name()
        {
            Init();
            //given
            string className = @"MyEvent";
            string baseName = @"EventArgs";
            //when
            bool result = classCRC.CheckEventNaming(className, baseName);
            //then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void CheckEventNaming_Should_False_Valid_Class_Name()
        {
            Init();
            //given
            string className = @"MyEventArgs";
            string baseName = @"EventArgs";
            //when
            bool result = classCRC.CheckEventNaming(className, baseName);
            //then
            Assert.IsFalse(result);
        }

        [TestMethod]
        public void CheckAttributeNaming_Should_True_Invalid_Class_Name()
        {
            Init();
            //given
            string className = @"User";
            string baseName = @"Attribute";
            //when
            bool result = classCRC.CheckAttributeNaming(className, baseName);
            //then
            Assert.IsTrue(result);
        }
        [TestMethod]
        public void CheckAttributeNaming_Should_False_Valid_Class_Name()
        {
            Init();
            //given
            string className = @"HelpAttribute";
            string baseName = @"Attribute";
            //when
            bool result = classCRC.CheckAttributeNaming(className, baseName);
            //then
            Assert.IsFalse(result);
        }
    }
}
