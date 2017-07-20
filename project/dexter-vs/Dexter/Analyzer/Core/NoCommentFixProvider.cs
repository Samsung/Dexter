using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using System.Composition;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using System.Diagnostics;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CodeFixes;
using Microsoft.CodeAnalysis.CodeActions;
using Microsoft.CodeAnalysis.CSharp;
using Microsoft.CodeAnalysis.CSharp.Syntax;
using Microsoft.CodeAnalysis.Rename;
using Microsoft.CodeAnalysis.Text;
using System.Text;

namespace Dexter.Analyzer
{
    [ExportCodeFixProvider(LanguageNames.CSharp, Name = nameof(NoCommentFixProvider)), Shared]
    public class NoCommentFixProvider : CodeFixProvider
    {
        private const string title = "Add doxygen comment";

        public sealed override ImmutableArray<string> FixableDiagnosticIds
        {
            get { return ImmutableArray.Create(NoCommentAnalyzer.NoCommentRuleId, NoCommentAnalyzer.NoMethodCommentRuleId); }
        }

        public sealed override FixAllProvider GetFixAllProvider()
        {
            // See https://github.com/dotnet/roslyn/blob/master/docs/analyzers/FixAllProvider.md for more information on Fix All Providers
            return WellKnownFixAllProviders.BatchFixer;
        }

        public sealed override async Task RegisterCodeFixesAsync(CodeFixContext context)
        {
            var root = await context.Document.GetSyntaxRootAsync(context.CancellationToken).ConfigureAwait(false);

            foreach (Diagnostic diagnostic in context.Diagnostics)
            {
                var diagnosticSpan = diagnostic.Location.SourceSpan;

                switch (diagnostic.Id)
                {
                    case NoCommentAnalyzer.NoMethodCommentRuleId:
                        var methodDeclaration = root.FindToken(diagnosticSpan.Start).Parent.AncestorsAndSelf().OfType<BaseMethodDeclarationSyntax>().First();

                        context.RegisterCodeFix(
                            CodeAction.Create(
                                title: title,
                                createChangedDocument: c => AddMethodDoxygenCommentAsync(context.Document, methodDeclaration, c),
                                equivalenceKey: title),
                            diagnostic);
                        break;

                    default:
                        var declaration = root.FindToken(diagnosticSpan.Start).Parent.AncestorsAndSelf().OfType<BaseTypeDeclarationSyntax>().First();

                        context.RegisterCodeFix(
                            CodeAction.Create(
                                title: title,
                                createChangedDocument: c => AddDoxygenCommentAsync(context.Document, declaration, c),
                                equivalenceKey: title),
                            diagnostic);
                        break;
                }
            }

            
        }

        private Task<Document> AddMethodDoxygenCommentAsync(Document document, BaseMethodDeclarationSyntax declaration, CancellationToken c)
        {
            var doxygenComments = GetMethodDoxygenComments(declaration);

            var leadingTrivias = declaration.GetLeadingTrivia();
            var whitespaceCount = leadingTrivias[leadingTrivias.Count - 1].Span.Length;
            var newDeclaration = declaration.WithLeadingTrivia(
                GetNewLeadingTrivia(leadingTrivias, SyntaxFactory.ParseLeadingTrivia(
                                                        ConcatCommentString(doxygenComments, whitespaceCount))));

            return ReplaceNode(declaration, newDeclaration, document);
        }

        private Task<Document> AddDoxygenCommentAsync(Document document, BaseTypeDeclarationSyntax declaration, CancellationToken c)
        {
            string[] doxygenComments = GetDoxygenComments(declaration);

            var leadingTrivias = declaration.GetLeadingTrivia();
            var whitespaceCount = leadingTrivias[leadingTrivias.Count - 1].Span.Length;
            var newDeclaration = declaration.WithLeadingTrivia(
                GetNewLeadingTrivia(leadingTrivias, SyntaxFactory.ParseLeadingTrivia(
                                                        ConcatCommentString(doxygenComments, whitespaceCount))));

            return ReplaceNode(declaration, newDeclaration, document);
        }

        private static IEnumerable<string> GetMethodDoxygenComments(BaseMethodDeclarationSyntax declaration)
        {
            string[] summaryComments = new[] {
                "/// <summary>",
                "/// ",
                "/// </summary>"
            };

            var paramComments = GetParameterComments(declaration);
            var throwStatmentSyntaxs = declaration.DescendantNodes().OfType<ThrowStatementSyntax>();
            var exceptionComments = GetExceptionComments(throwStatmentSyntaxs);
            var returnComments = GetReturnComments(declaration);

            return summaryComments
                .Concat(paramComments)
                .Concat(exceptionComments)
                .Concat(returnComments);
        }

        private static IEnumerable<string> GetReturnComments(BaseMethodDeclarationSyntax declaration)
        {
            var methodDeclaration = declaration as MethodDeclarationSyntax;
            if (methodDeclaration == null)
                yield break;

            if (!IsVoidReturnType(methodDeclaration))
                yield return "/// <returns> </returns>";
        }

        private static bool IsVoidReturnType(MethodDeclarationSyntax declaration)
        {
            var predefinedType = declaration.ReturnType as PredefinedTypeSyntax;
            if (predefinedType == null)
                return false;

            if (predefinedType.Keyword.Text.Equals("void"))
                return true;

            return false;
        }

        private static IEnumerable<string> GetParameterComments(BaseMethodDeclarationSyntax declaration)
        {
            foreach (var parameter in declaration.ParameterList.Parameters)
            {
                yield return $"/// <param name=\"{parameter.Identifier.Text}\"> </param>";
            }
        }

        private static IEnumerable<string> GetExceptionComments(IEnumerable<ThrowStatementSyntax> throwStatmentSyntaxs)
        {
            return from throwStatment in throwStatmentSyntaxs
                   let exceptionString = GetExceptionString(throwStatment)
                   where !exceptionString.Equals("default") && !exceptionString.Equals("unknown")
                   select $"/// <exception cref=\"{exceptionString}\"> </exception>";
        }

        private static string GetExceptionString(ThrowStatementSyntax throwStatmentSyntax)
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

        private static string[] GetDoxygenComments(BaseTypeDeclarationSyntax declaration)
        {
            if (declaration is ClassDeclarationSyntax)
            {
                return new[] {
                    "/// <summary>",
                    "/// ",
                    "/// </summary>",
                    "/// <code>",
                    "/// ",
                    "/// </code>"
                };
            } else
            {
                return new[] {
                    "/// <summary>",
                    "/// ",
                    "/// </summary>"
                };
            }
        }

        private string ConcatCommentString(IEnumerable<string> doxygenComments, int whitespaceCount)
        {
            StringBuilder sb = new StringBuilder();
            foreach (var doxygenComment in doxygenComments)
            {
                for (int i = 0; i < whitespaceCount; i++)
                    sb.Append(" ");

                sb.Append(doxygenComment);
                sb.Append("\r\n");
            }
            return sb.ToString();
        }

        private IEnumerable<SyntaxTrivia> GetNewLeadingTrivia(SyntaxTriviaList leadingTrivias, SyntaxTriviaList commentTrivias)
        {
            
            var whitespaceTriva = leadingTrivias[leadingTrivias.Count - 1];

            for (int i=0; i< leadingTrivias.Count-1; i++)
            {
                yield return leadingTrivias[i];
            }

            foreach (var commentTrivia in commentTrivias) {
                yield return commentTrivia;
            }

            yield return whitespaceTriva;
        }

        private async Task<Document> ReplaceNode(SyntaxNode oldNode, SyntaxNode newNode, Document document)
        {
            SyntaxNode root = await document.GetSyntaxRootAsync().ConfigureAwait(false);
            SyntaxNode newRoot = root.ReplaceNode(oldNode, newNode);
            Document newDocument = document.WithSyntaxRoot(newRoot);
            return newDocument;
        }
    }
}