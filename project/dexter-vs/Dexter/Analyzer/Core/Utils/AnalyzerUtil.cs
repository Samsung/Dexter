using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp;
using Microsoft.CodeAnalysis.CSharp.Syntax;
using Microsoft.CodeAnalysis.Diagnostics;

namespace Dexter.Analyzer.Utils
{
    /// <summary>
    /// Provides common functions used by the analyzer
    /// </summary>
    public static class AnalyzerUtil
    {
        /// <summary>
        /// Creates a diagnostic descriptor 
        /// </summary>
        /// <param name="ruleId">Diagnostic rule ID</param>
        /// <param name="titleId">String ID of rule title</param>
        /// <param name="messageId">String ID of rule message</param>
        /// <param name="descriptionId">String ID of rule description</param>
        /// <param name="category">Rule category</param>
        /// <returns>An instance of Diagnostic descriptor created</returns>
        public static DiagnosticDescriptor CreateDiagnosticDescriptor(string ruleId, string titleId, string messageId, string descriptionId, string category)
        {
            var title = new LocalizableResourceString(titleId, Resources.ResourceManager, typeof(Resources));
            var messageFormat = new LocalizableResourceString(messageId, Resources.ResourceManager, typeof(Resources));
            var description = new LocalizableResourceString(descriptionId, Resources.ResourceManager, typeof(Resources));

            return new DiagnosticDescriptor(ruleId, title, messageFormat, category, DiagnosticSeverity.Warning, isEnabledByDefault: true, description: description);
        }

        /// <summary>
        /// Gets the declaration syntax node of the type T belonging to the diagnostic
        /// </summary>
        /// <typeparam name="T">Type of the declaration syntax node</typeparam>
        /// <param name="root">Root syntax node</param>
        /// <param name="diagnostic">Diagnostic with node</param>
        /// <returns>Declaration syntax node matched</returns>
        public static T GetDeclaration<T>(SyntaxNode root, Diagnostic diagnostic) where T : CSharpSyntaxNode
        {
            var diagnosticSpan = diagnostic.Location.SourceSpan;

            return root.FindToken(diagnosticSpan.Start).Parent.AncestorsAndSelf().OfType<T>().First();
        }

        /// <summary>
        /// Determines if method's return value is void
        /// </summary>
        /// <param name="declaration">Method declaration</param>
        /// <returns>True if method's return value is void, or false </returns>
        public static bool IsVoidReturnType(MethodDeclarationSyntax declaration)
        {
            var predefinedType = declaration.ReturnType as PredefinedTypeSyntax;
            if (predefinedType == null)
                return false;

            if (predefinedType.Keyword.Text.Equals("void"))
                return true;

            return false;
        }

        /// <summary>
        /// Replaces a syntax node in the document
        /// </summary>
        /// <param name="oldNode">Old syntax node</param>
        /// <param name="newNode">New syntax node</param>
        /// <param name="document">Document with syntax node</param>
        /// <returns>Document with updated node</returns>
        public static async Task<Document> ReplaceNode(SyntaxNode oldNode, SyntaxNode newNode, Document document)
        {
            SyntaxNode root = await document.GetSyntaxRootAsync().ConfigureAwait(false);
            SyntaxNode newRoot = root.ReplaceNode(oldNode, newNode);
            Document newDocument = document.WithSyntaxRoot(newRoot);
            return newDocument;
        }

        /// <summary>
        /// Concats enumerable of doxygenComments with whitespaces
        /// </summary>
        /// <param name="doxygenComments">Enumerable of doxygenComments</param>
        /// <param name="whitespaceCount">Whitespace count</param>
        /// <returns>Concated doxygen comment string</returns>
        public static string ConcatCommentString(IEnumerable<string> doxygenComments, int whitespaceCount)
        {
            string whiteSpaces = new String(' ', whitespaceCount);
            StringBuilder sb = new StringBuilder();
            foreach (var doxygenComment in doxygenComments)
            {
                sb.Append(whiteSpaces);
                sb.Append(doxygenComment);
                sb.Append("\r\n");
            }
            return sb.ToString();
        }

        /// <summary>
        /// Get new leading trivia with doxygen comments inserted
        /// </summary>
        /// <param name="leadingTrivias">Original leading trivia</param>
        /// <param name="doxygenComments">Enumerable of doxygen comments</param>
        /// <param name="whitespaceCount">Whitespace count</param>
        /// <returns>new leading trivia with doxygen comments inserted</returns>
        public static SyntaxTriviaList GetNewLeadingTrivia(SyntaxTriviaList leadingTrivias, IEnumerable<string> doxygenComments, int whitespaceCount)
        {
            var commentTrivias = SyntaxFactory.ParseLeadingTrivia(ConcatCommentString(doxygenComments, whitespaceCount));

            return leadingTrivias.InsertRange(leadingTrivias.Count - 1, commentTrivias);
        }

        /// <summary>
        /// Verifies whether attributeLists contain a test-case attribute
        /// </summary>
        /// <param name="attributeLists">List of AttributeListSyntax</param>
        /// <returns>True if attributeLists contain a test-case attribute, or False</returns>
        public static bool IsTestAttribute(SyntaxList<AttributeListSyntax> attributeLists)
        {
            return attributeLists.Any(attributeList =>
            {
                return attributeList.Attributes.Any(attribute => attribute.Name.ToString().Equals("TestFixture"));
            });
        }

        /// <summary>
        /// Get new leading trivia with summary comments inserted
        /// </summary>
        /// <param name="leadingTrivias">Original leading trivia</param>
        /// <param name="summaryComments">Enumerable of summary comments</param>
        /// <param name="whitespaceCount">Whitespace count</param>
        /// <returns>new leading trivia with summary comments inserted</returns>
        public static IEnumerable<SyntaxTrivia> GetNewLeadingTriviaWithSummary(SyntaxTriviaList leadingTrivias, IEnumerable<string> summaryComments, int whitespaceCount)
        {
            var summaryTrivias = SyntaxFactory.ParseLeadingTrivia(ConcatCommentString(summaryComments, whitespaceCount));
            var index = leadingTrivias.IndexOf(SyntaxKind.SingleLineDocumentationCommentTrivia);

            if (index == -1)
                index = 0;

            if (index > 0 &&
                leadingTrivias[index - 1].Kind() == SyntaxKind.WhitespaceTrivia)
            {
                index -= 1;
            }

            return leadingTrivias.InsertRange(index, summaryTrivias);
        }

        /// <summary>
        /// Get an exception string for the throw statement
        /// </summary>
        /// <param name="throwStatementSyntax">Throw statement syntax</param>
        /// <returns>Exception string</returns>
        public static string GetExceptionString(ThrowStatementSyntax throwStatementSyntax)
        {
            if (throwStatementSyntax.Expression == null)
            {
                return "default";
            }

            if (!(throwStatementSyntax.Expression is ObjectCreationExpressionSyntax))
            {
                return "unknown";
            }

            var expression = throwStatementSyntax.Expression as ObjectCreationExpressionSyntax;

            if (!(expression.Type is IdentifierNameSyntax))
            {
                return "unknown";
            }

            var type = expression.Type as IdentifierNameSyntax;

            return type.Identifier.Text;
        }
    }
}
