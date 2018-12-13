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
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp;
using Microsoft.CodeAnalysis.CSharp.Syntax;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Linq;

namespace DexterCRC.Tests
{
    [TestClass()]
    public class MethodSpacingTests
    {
        private MethodSpacing methodSpacing;

        private void Init()
        {
            methodSpacing = new MethodSpacing();
        }

        [TestMethod()]
        public void HasDefectTest_OneMethodWithCorrectSpacing_ReturnsFalse()
        {
            Init();
            // Given
            var tree = CSharpSyntaxTree.ParseText(@"class Class1
	{
        int variable1 = 0;

		public void Method1()
		{

		}
	}"
                );
            SyntaxNode syntaxRoot = tree.GetRoot();

            var methodRaws = syntaxRoot.DescendantNodes().OfType<MethodDeclarationSyntax>();

            foreach (var methodRaw in methodRaws)
            {
                SyntaxTriviaList syntaxTriviaList = methodRaw.GetLeadingTrivia();

                // When
                bool result = methodSpacing.HasDefect(syntaxTriviaList);
                // Then
                Assert.IsFalse(result);
            }
        }

        [TestMethod()]
        public void HasDefectTest_OneMethodsWithIncorrectSpacing_ReturnsTrue()
        {
            Init();
            // Given
            var tree = CSharpSyntaxTree.ParseText(@"class Class1
	{
        int variable1 = 0;


		public void Method1()
		{

		}
	}"
                );
            SyntaxNode syntaxRoot = tree.GetRoot();

            var methodRaws = syntaxRoot.DescendantNodes().OfType<MethodDeclarationSyntax>();

            SyntaxTriviaList syntaxTriviaList = methodRaws.ElementAt(0).GetLeadingTrivia();
            // When
            bool result = methodSpacing.HasDefect(syntaxTriviaList);
            // Then
            Assert.IsTrue(result);
        }

        [TestMethod()]
        public void HasDefectTest_OneMethodWithCorrectSpacingAndOneLineComment_ReturnsFalse()
        {
            Init();
            // Given
            var tree = CSharpSyntaxTree.ParseText(@"class Class1
	{
        int variable1 = 0;
        // Comment1

		public void Method1()
		{

		}
	}"
                );
            SyntaxNode syntaxRoot = tree.GetRoot();

            var methodRaws = syntaxRoot.DescendantNodes().OfType<MethodDeclarationSyntax>();

            SyntaxTriviaList syntaxTriviaList = methodRaws.ElementAt(0).GetLeadingTrivia();
            // When
            bool result = methodSpacing.HasDefect(syntaxTriviaList);
            // Then
            Assert.IsFalse(result);
        }

        [TestMethod()]
        public void HasDefectTest_OneMethodWithIncorrectSpacingAndOneLineComment_ReturnsTrue()
        {
            Init();
            // Given
            var tree = CSharpSyntaxTree.ParseText(@"class Class1
	{
        int variable1 = 0;
        // Comment1


		public void Method1()
		{

		}
	}"
                );
            SyntaxNode syntaxRoot = tree.GetRoot();

            var methodRaws = syntaxRoot.DescendantNodes().OfType<MethodDeclarationSyntax>();

            SyntaxTriviaList syntaxTriviaList = methodRaws.ElementAt(0).GetLeadingTrivia();

            // When
            bool result = methodSpacing.HasDefect(syntaxTriviaList);

            // Then
            Assert.IsTrue(result);
        }

        [TestMethod()]
        public void HasDefectTest_OneMethodWithCorrectSpacingAndMultiLineComment_ReturnsFalse()
        {
            Init();
            // Given
            var tree = CSharpSyntaxTree.ParseText(@"class Class1
	{
        int variable1 = 0;
        
        /* 
         * Comment1
         * 
         */
		public void Method1()
		{

		}
	}"
                );
            SyntaxNode syntaxRoot = tree.GetRoot();

            var methodRaws = syntaxRoot.DescendantNodes().OfType<MethodDeclarationSyntax>();

            SyntaxTriviaList syntaxTriviaList = methodRaws.ElementAt(0).GetLeadingTrivia();

            // When
            bool result = methodSpacing.HasDefect(syntaxTriviaList);

            // Then
            Assert.IsFalse(result);
        }

        [TestMethod()]
        public void HasDefectTest_OneMethodWithIncorrectSpacingAndMultiLineComment_ReturnsTrue()
        {
            Init();
            // Given
            var tree = CSharpSyntaxTree.ParseText(@"class Class1
	{
        int variable1 = 0;
        
        /* 
         * Comment1
         * 
         */


		public void Method1()
		{

		}
	}"
                );
            SyntaxNode syntaxRoot = tree.GetRoot();

            var methodRaws = syntaxRoot.DescendantNodes().OfType<MethodDeclarationSyntax>();

            SyntaxTriviaList syntaxTriviaList = methodRaws.ElementAt(0).GetLeadingTrivia();

            // When
            bool result = methodSpacing.HasDefect(syntaxTriviaList);

            // Then
            Assert.IsTrue(result);
        }
    }
}