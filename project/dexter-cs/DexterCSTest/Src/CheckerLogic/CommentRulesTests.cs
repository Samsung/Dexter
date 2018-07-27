using Microsoft.VisualStudio.TestTools.UnitTesting;
using DexterCRC.Src.CheckerLogic;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DexterCRC.Src.CheckerLogic.Tests
{
    [TestClass()]
    public class CommentRulesTests
    {
        CommentRules commentRules;

        private void Init()
        {
            commentRules = new CommentRules();
        }

        [TestMethod()]
        public void HasDefectTest_CommentWithOutSummaryTag_ReturnsTrue()
        {
            Init();

            // Given
            string comment = @"
        /// Comment content";

            // When
            bool result = commentRules.HasDefect(comment);

            // Then
            Assert.IsTrue(result);
        }

        [TestMethod()]
        public void HasDefectTest_CommentWithSummaryTagIncorrectSlashes_ReturnsTrue()
        {
            Init();

            // Given
            string comment = @"
        // <summary>
        // Summary content
        // </summary>";

            // When
            bool result = commentRules.HasDefect(comment);

            // Then
            Assert.IsTrue(result);
        }

        [TestMethod()]
        public void HasDefectTest_CommentWithTypoInSummaryTag_ReturnsTrue()
        {
            Init();

            // Given
            string comment = @"
        // <sumary>
        // Summary content
        // </summary>";

            // When
            bool result = commentRules.HasDefect(comment);

            // Then
            Assert.IsTrue(result);
        }

        [TestMethod()]
        public void HasDefectTest_LongCommentWithSummaryTagAndOtherTags_ReturnsFalse()
        {
            Init();

            // Given
            string comment = @"
        /// <seealso cref=""member""/>  
        /// <summary>
        /// Summary content
        /// </summary>
        /// <remarks>Remarks</remarks>
        /// <example>Example</example>
        /// <param name=""pluginToAdd"">The Plugin to Add</param>";

            // When
            bool result = commentRules.HasDefect(comment);

            // Then
            Assert.IsFalse(result);
        }

        [TestMethod()]
        public void HasDefectTest_CommentWithSummaryTag_ReturnsFalse()
        {
            Init();

            // Given
            string comment = @"
        /// <summary>
        /// Summary content
        /// </summary>";

            // When
            bool result = commentRules.HasDefect(comment);

            // Then
            Assert.IsFalse(result);
        }
    }
}