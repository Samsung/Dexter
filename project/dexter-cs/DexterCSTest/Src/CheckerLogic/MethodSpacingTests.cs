using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp;
using Microsoft.CodeAnalysis.CSharp.Syntax;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Linq;

namespace DexterCRC.Src.CheckerLogic.Tests
{
    [TestClass()]
    public class MethodSpacingTests
    {
        MethodSpacing methodSpacing;

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