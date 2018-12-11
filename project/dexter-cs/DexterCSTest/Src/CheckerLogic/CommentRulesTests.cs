#region Copyright notice
/**
 * Copyright (c) 2018 Samsung Electronics, Inc.,
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
#endregion
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace DexterCRC.Tests
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
        public void HasDefectTest_CommentWithOutClosingTag_ReturnsTrue()
        {
            Init();

            // Given
            string comment = @"
        // <summary>
        // Summary content";

            // When
            bool result = commentRules.HasDefect(comment);

            // Then
            Assert.IsTrue(result);
        }

        [TestMethod()]
        public void HasDefectTest_CommentWithTypoInClosingSummaryTag_ReturnsTrue()
        {
            Init();

            // Given
            string comment = @"
        // <sumary>
        // Summary content
        // <summary>";

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