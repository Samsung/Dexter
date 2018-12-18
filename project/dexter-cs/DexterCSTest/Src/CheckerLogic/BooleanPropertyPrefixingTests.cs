using Microsoft.VisualStudio.TestTools.UnitTesting;
using DexterCRC;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DexterCRC.Tests
{
    [TestClass()]
    public class BooleanPropertyPrefixingTests
    {
        private BooleanPropertyPrefixing booleanPropertyPrefixing;

        private void Init()
        {
            booleanPropertyPrefixing = new BooleanPropertyPrefixing();
        }

        [TestMethod]
        public void HasDefectTest_ContainsIs_ReturnsFalse()
        {
            Init();
            // Given
            string booleanProperty = @"isSomething";
            // When
            bool result = booleanPropertyPrefixing.HasDefect(booleanProperty);
            // Then
            Assert.IsFalse(result);
        }

        [TestMethod]
        public void HasDefectTest_ContainsHas_ReturnsFalse()
        {
            Init();
            // Given
            string booleanProperty = @"hasSomething";
            // When
            bool result = booleanPropertyPrefixing.HasDefect(booleanProperty);
            // Then
            Assert.IsFalse(result);
        }

        [TestMethod]
        public void HasDefectTest_ContainsCan_ReturnsFalse()
        {
            Init();
            // Given
            string booleanProperty = @"canSomething";
            // When
            bool result = booleanPropertyPrefixing.HasDefect(booleanProperty);
            // Then
            Assert.IsFalse(result);
        }

        [TestMethod]
        public void HasDefectTest_IsEmpty_ReturnsTrue()
        {
            Init();
            // Given
            string booleanProperty = @"";
            // When
            bool result = booleanPropertyPrefixing.HasDefect(booleanProperty);
            // Then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasDefectTest_IncorrectName_ReturnsTrue()
        {
            Init();
            // Given
            string booleanProperty = @"Something";
            // When
            bool result = booleanPropertyPrefixing.HasDefect(booleanProperty);
            // Then
            Assert.IsTrue(result);
        }
    }
}