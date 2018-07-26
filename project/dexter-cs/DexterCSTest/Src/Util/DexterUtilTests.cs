using Microsoft.VisualStudio.TestTools.UnitTesting;
using DexterCS;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DexterCS.Tests
{
    [TestClass()]
    public class DexterUtilTests
    {

        [TestMethod()]
        public void SplitTest_CamelCasingWord_ReturnsSplitWord()
        {
            // Given
            string camelCasingWord = "camelCasingWord";

            // When
            string[] splitcamelCasingWord = DexterUtil.Split(camelCasingWord);

            // Then
            Assert.IsTrue(splitcamelCasingWord.SequenceEqual(new string[] { "camel", "Casing", "Word" }));
        }

        [TestMethod()]
        public void SplitTest_PascalCasingWord_ReturnsSplitWord()
        {
            // Given
            string pascalCasingWord = "PascalCasingWord";

            // When
            string[] splitPascalCasingWord = DexterUtil.Split(pascalCasingWord);

            // Then
            Assert.IsTrue(splitPascalCasingWord.SequenceEqual(new string[] { "Pascal", "Casing", "Word" }));
        }

        [TestMethod()]
        public void SplitTest_SnakeCasingWord_ReturnsSplitWord()
        {
            // Given
            string snakeCasingWord = "snake_casing_word";

            // When
            string[] splitSnakeCasingWord = DexterUtil.Split(snakeCasingWord);

            // Then
            foreach (string x in splitSnakeCasingWord)
            {
                Console.WriteLine("\"" + x + "\"");
            }
            Assert.IsTrue(splitSnakeCasingWord.SequenceEqual(new string[] { "snake", "casing", "word" }));
        }

        [TestMethod()]
        public void SplitTest_CamelAndSnakeCasingWord_ReturnsSplitWord()
        {
            // Given
            string camelAndSnakeCasingWord = "camelAndSnake_Casing_Word";

            // When
            string[] splitCamelAndSnakeCasingWord = DexterUtil.Split(camelAndSnakeCasingWord);

            // Then
            foreach (string x in splitCamelAndSnakeCasingWord)
            {
                Console.WriteLine("\"" + x + "\"");
            }
            Assert.IsTrue(splitCamelAndSnakeCasingWord.SequenceEqual(new string[] { "camel", "And", "Snake", "Casing", "Word" }));
        }

        [TestMethod()]
        public void SplitTest_PascalAndSnakeCasingWord_ReturnsSplitWord()
        {
            // Given
            string pascalAndSnakeCasingWord = "PascalAndSnake_Casing_Word";

            // When
            string[] splitPascalAndSnakeCasingWord = DexterUtil.Split(pascalAndSnakeCasingWord);

            // Then
            foreach (string x in splitPascalAndSnakeCasingWord)
            {
                Console.WriteLine("\"" + x + "\"");
            }
            Assert.IsTrue(splitPascalAndSnakeCasingWord.SequenceEqual(new string[] { "Pascal", "And", "Snake", "Casing", "Word" }));
        }
    }
}