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
    public static class AnalyzerUtil
    {
        public static DiagnosticDescriptor CreateDiagnosticDescriptor(string ruleId, string titleId, string messageId, string descriptionId, string category)
        {
            var title = new LocalizableResourceString(titleId, Resources.ResourceManager, typeof(Resources));
            var messageFormat = new LocalizableResourceString(messageId, Resources.ResourceManager, typeof(Resources));
            var description = new LocalizableResourceString(descriptionId, Resources.ResourceManager, typeof(Resources));

            return new DiagnosticDescriptor(ruleId, title, messageFormat, category, DiagnosticSeverity.Warning, isEnabledByDefault: true, description: description);
        }

        public static T GetDeclaration<T>(SyntaxNode root, Diagnostic diagnostic) where T : CSharpSyntaxNode
        {
            var diagnosticSpan = diagnostic.Location.SourceSpan;

            return root.FindToken(diagnosticSpan.Start).Parent.AncestorsAndSelf().OfType<T>().First();
        }

        public static bool IsVoidReturnType(MethodDeclarationSyntax declaration)
        {
            var predefinedType = declaration.ReturnType as PredefinedTypeSyntax;
            if (predefinedType == null)
                return false;

            if (predefinedType.Keyword.Text.Equals("void"))
                return true;

            return false;
        }

        public static async Task<Document> ReplaceNode(SyntaxNode oldNode, SyntaxNode newNode, Document document)
        {
            SyntaxNode root = await document.GetSyntaxRootAsync().ConfigureAwait(false);
            SyntaxNode newRoot = root.ReplaceNode(oldNode, newNode);
            Document newDocument = document.WithSyntaxRoot(newRoot);
            return newDocument;
        }

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

        public static SyntaxTriviaList GetNewLeadingTrivia(SyntaxTriviaList leadingTrivias, IEnumerable<string> doxygenComments, int whitespaceCount)
        {
            var commentTrivias = SyntaxFactory.ParseLeadingTrivia(ConcatCommentString(doxygenComments, whitespaceCount));

            return leadingTrivias.InsertRange(leadingTrivias.Count - 1, commentTrivias);
        }

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

        public static string GetExceptionString(ThrowStatementSyntax throwStatmentSyntax)
        {
            if (throwStatmentSyntax.Expression == null)
            {
                return "default";
            }

            if (!(throwStatmentSyntax.Expression is ObjectCreationExpressionSyntax))
            {
                return "unknown";
            }

            var expression = throwStatmentSyntax.Expression as ObjectCreationExpressionSyntax;

            if (!(expression.Type is IdentifierNameSyntax))
            {
                return "unknown";
            }

            var type = expression.Type as IdentifierNameSyntax;

            return type.Identifier.Text;
        }
    }
}
