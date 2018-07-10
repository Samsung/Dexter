using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using DexterCRC;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp.Syntax;

namespace DexterCSTest.checkerLogic
{
    [TestClass]
    public class FieldCRCTest
    {
        FieldCRC filedCRC;

        void Init()
        {
            filedCRC = new FieldCRC();   
        }

        [TestMethod]
        public void HasInvalidModifier_Should_True_Has_Invalid_Protected_Modifier()
        {
            Init();
            //given
            string modifier = @" protected static";
            //when
            bool result = filedCRC.HasInvalidModifier(modifier);
            //then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasInvalidModifier_Should_True_Has_Invalid_Static_Modifier()
        {
            Init();
            //given
            string modifier = @"public static";
            //when
            bool result = filedCRC.HasInvalidModifier(modifier);
            //then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasInvalidModifier_Should_True_Has_Invalid_Static_Modifier_withBlank()
        {
            Init();
            //given
            string modifier = @" public static ";
            //when
            bool result = filedCRC.HasInvalidModifier(modifier);
            //then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasInvalidModifier_Should_False_Has_Valid_Public_Modifier()
        {
            Init();
            //given
            string modifier = @"public";
            //when
            bool result = filedCRC.HasInvalidModifier(modifier);
            //then
            Assert.IsFalse(result);
        }

        [TestMethod]
        public void HasInvalidModifier_Should_False_Has_Valid_Private_Modifier()
        {
            Init();
            //given
            string modifier = @"private  ";
            //when
            bool result = filedCRC.HasInvalidModifier(modifier);
            //then
            Assert.IsFalse(result);
        }
    }
}
