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
    [ExportCodeFixProvider(LanguageNames.CSharp, Name = nameof(NoCommentCodeFixProvider)), Shared]
    public class NoCommentCodeFixProvider : CodeFixProvider
    {
        private const string title = "Add doxygen comment";

        public sealed override ImmutableArray<string> FixableDiagnosticIds
        {
            get { return ImmutableArray.Create(NoCommentAnalyzer.DiagnosticId); }
        }

        public sealed override FixAllProvider GetFixAllProvider()
        {
            // See https://github.com/dotnet/roslyn/blob/master/docs/analyzers/FixAllProvider.md for more information on Fix All Providers
            return WellKnownFixAllProviders.BatchFixer;
        }

        public sealed override async Task RegisterCodeFixesAsync(CodeFixContext context)
        {
            var root = await context.Document.GetSyntaxRootAsync(context.CancellationToken).ConfigureAwait(false);

            // TODO: Replace the following code with your own analysis, generating a CodeAction for each fix to suggest
            var diagnostic = context.Diagnostics.First();
            var diagnosticSpan = diagnostic.Location.SourceSpan;

            // Find the type declaration identified by the diagnostic.
            var declaration = root.FindToken(diagnosticSpan.Start).Parent.AncestorsAndSelf().OfType<BaseTypeDeclarationSyntax>().First();


            // Register a code action that will invoke the fix.
            context.RegisterCodeFix(
                CodeAction.Create(
                    title: title,
                    createChangedDocument: c => AddDoxygenCommentAsync(context.Document, declaration, c),
                    equivalenceKey: title),
                diagnostic);
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

        private string ConcatCommentString(string[] doxygenComments, int whitespaceCount)
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