using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace DexterCRC.Tests
{
    [TestClass()]
    public class EventCRCTests
    {
        SuffixNaming suffixNaming;
        void Init()
        {
            suffixNaming = new SuffixNaming();
        }

        [TestMethod]
        public void HasDefectTest_WithInValidEventTypeName_ReturnsTrue()
        {
            Init();
            // Given
            string eventTypeName = @"NameChangedEvent";
            NamingSet namingSet = new NamingSet
            {
                currentName = eventTypeName,
                basicWord = DexterCRCUtil.EVENT_TYPE_SUFFIX
            };
            // When
            bool result = suffixNaming.HasDefect(namingSet);
            // Then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasDefectTest_WithValidEventTypeName_ReturnsFalse()
        {
            Init();
            // Given
            string eventTypeName = @"NameChangedEventHandler";
            NamingSet namingSet = new NamingSet
            {
                currentName = eventTypeName,
                basicWord = DexterCRCUtil.EVENT_TYPE_SUFFIX
            };
            // When
            bool result = suffixNaming.HasDefect(namingSet);
            // Then
            Assert.IsFalse(result);
        }
    }
}