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
using Dexter.Analyzer.Utils;

namespace Dexter.Analyzer
{
    /// <summary>
    /// Provides codeFixes for NoCommentAnalyzer
    /// </summary>
    [ExportCodeFixProvider(LanguageNames.CSharp, Name = nameof(NoCommentFixProvider)), Shared]
    public class NoCommentFixProvider : CodeFixProvider
    {
        private static readonly string title = "Add doxygen comment";
        private static readonly string[] summaryComments = new[] {
            "/// <summary>",
            "/// ",
            "/// </summary>"
        };
        private static readonly string[] summaryAndCodeComments = new[] {
            "/// <summary>",
            "/// ",
            "/// </summary>",
            "/// <code>",
            "/// ",
            "/// </code>"
        };
        private static readonly string[] codeComments = new[] {
            "/// <code>",
            "/// ",
            "/// </code>"
        };

        /// <summary>
        /// Returns fixable diagnostic IDs
        /// </summary>
        public sealed override ImmutableArray<string> FixableDiagnosticIds
        {
            get
            {
                return ImmutableArray.Create(
                    NoCommentAnalyzer.NoCommentRuleId,
                    NoCommentAnalyzer.NoCommentMethodRuleId,
                    NoCommentAnalyzer.NoCommentPropertyRuleId,
                    NoCommentAnalyzer.NoSummaryRuleId,
                    NoCommentAnalyzer.NoCodeRuleId,
                    NoCommentAnalyzer.NoReturnsRuleId
                    );
            }
        }

        /// <summary>
        /// Get Fix All Providers
        /// </summary>
        public sealed override FixAllProvider GetFixAllProvider()
        {
            // See https://github.com/dotnet/roslyn/blob/master/docs/analyzers/FixAllProvider.md for more information on Fix All Providers
            return WellKnownFixAllProviders.BatchFixer;
        }

        /// <summary>
        /// Registers code fix action for the diagnostic
        /// </summary>
        /// <param name="context">Code fix context</param>
        /// <returns>Async Task</returns>
        public sealed override async Task RegisterCodeFixesAsync(CodeFixContext context)
        {
            var root = await context.Document.GetSyntaxRootAsync(context.CancellationToken).ConfigureAwait(false);

            foreach (Diagnostic diagnostic in context.Diagnostics)
            {
                CodeAction codeAction;

                switch (diagnostic.Id)
                {
                    case NoCommentAnalyzer.NoCommentMethodRuleId:
                        {
                            var declaration = AnalyzerUtil.GetDeclaration<BaseMethodDeclarationSyntax>(root, diagnostic);

                            codeAction = CodeAction.Create(
                                title: title,
                                createChangedDocument: c => AddMethodDoxygenCommentAsync(context.Document, declaration, c),
                                equivalenceKey: title);
                            break;
                        }
                    case NoCommentAnalyzer.NoReturnsRuleId:
                        {
                            var declaration = AnalyzerUtil.GetDeclaration<BaseMethodDeclarationSyntax>(root, diagnostic);

                            codeAction = CodeAction.Create(
                                title: title,
                                createChangedDocument: c => AddMethodReturnsCommentAsync(context.Document, declaration, c),
                                equivalenceKey: title);
                            break;
                        }
                    case NoCommentAnalyzer.NoCommentPropertyRuleId:
                        {
                            var declaration = AnalyzerUtil.GetDeclaration<BasePropertyDeclarationSyntax>(root, diagnostic);

                            codeAction = CodeAction.Create(
                                title: title,
                                createChangedDocument: c => AddPropertyDoxygenCommentAsync(context.Document, declaration, c),
                                equivalenceKey: title);
                            break;
                        }
                    case NoCommentAnalyzer.NoSummaryRuleId:
                        {
                            var declaration = AnalyzerUtil.GetDeclaration<BaseTypeDeclarationSyntax>(root, diagnostic);

                            codeAction = CodeAction.Create(
                                title: title,
                                createChangedDocument: c => AddSummaryCommentAsync(context.Document, declaration, c),
                                equivalenceKey: title);
                            break;
                        }
                    case NoCommentAnalyzer.NoCodeRuleId:
                        {
                            var declaration = AnalyzerUtil.GetDeclaration<BaseTypeDeclarationSyntax>(root, diagnostic);

                            codeAction = CodeAction.Create(
                                title: title,
                                createChangedDocument: c => AddCodeCommentAsync(context.Document, declaration, c),
                                equivalenceKey: title);
                            break;
                        }
                    default:
                        {
                            var declaration = AnalyzerUtil.GetDeclaration<BaseTypeDeclarationSyntax>(root, diagnostic);

                            codeAction = CodeAction.Create(
                                title: title,
                                createChangedDocument: c => AddDoxygenCommentAsync(context.Document, declaration, c),
                                equivalenceKey: title);
                            break;
                        }
                }

                context.RegisterCodeFix(codeAction, diagnostic);
            }

            
        }

        private Task<Document> AddPropertyDoxygenCommentAsync(Document document, BasePropertyDeclarationSyntax declaration, CancellationToken c)
        {
            var doxygenComments = GetPropertyDoxygenComments(declaration);

            var leadingTrivias = declaration.GetLeadingTrivia();
            var whitespaceCount = leadingTrivias[leadingTrivias.Count - 1].Span.Length;
            var newDeclaration = declaration.WithLeadingTrivia(
                AnalyzerUtil.GetNewLeadingTrivia(leadingTrivias, doxygenComments, whitespaceCount));

            return AnalyzerUtil.ReplaceNode(declaration, newDeclaration, document);
        }

        private Task<Document> AddMethodDoxygenCommentAsync(Document document, BaseMethodDeclarationSyntax declaration, CancellationToken c)
        {
            var doxygenComments = GetMethodDoxygenComments(declaration);

            var leadingTrivias = declaration.GetLeadingTrivia();
            var whitespaceCount = leadingTrivias[leadingTrivias.Count - 1].Span.Length;
            var newDeclaration = declaration.WithLeadingTrivia(
                AnalyzerUtil.GetNewLeadingTrivia(leadingTrivias, doxygenComments, whitespaceCount));

            return AnalyzerUtil.ReplaceNode(declaration, newDeclaration, document);
        }

        private Task<Document> AddMethodReturnsCommentAsync(Document document, BaseMethodDeclarationSyntax declaration, CancellationToken c)
        {
            var returnsComments = GetReturnComments(declaration);

            var leadingTrivias = declaration.GetLeadingTrivia();
            var whitespaceCount = leadingTrivias[leadingTrivias.Count - 1].Span.Length;
            var newDeclaration = declaration.WithLeadingTrivia(
                AnalyzerUtil.GetNewLeadingTrivia(leadingTrivias, returnsComments, whitespaceCount));

            return AnalyzerUtil.ReplaceNode(declaration, newDeclaration, document);
        }

        private Task<Document> AddDoxygenCommentAsync(Document document, BaseTypeDeclarationSyntax declaration, CancellationToken c)
        {
            string[] doxygenComments = GetDoxygenComments(declaration);

            var leadingTrivias = declaration.GetLeadingTrivia();
            var whitespaceCount = leadingTrivias[leadingTrivias.Count - 1].Span.Length;
            var newDeclaration = declaration.WithLeadingTrivia(
                AnalyzerUtil.GetNewLeadingTrivia(leadingTrivias, doxygenComments, whitespaceCount));

            return AnalyzerUtil.ReplaceNode(declaration, newDeclaration, document);
        }

        private Task<Document> AddCodeCommentAsync(Document document, BaseTypeDeclarationSyntax declaration, CancellationToken c)
        {
            string[] doxygenComments = GetDoxygenComments(declaration);

            var leadingTrivias = declaration.GetLeadingTrivia();
            var whitespaceCount = leadingTrivias[leadingTrivias.Count - 1].Span.Length;
            var newDeclaration = declaration.WithLeadingTrivia(
                AnalyzerUtil.GetNewLeadingTrivia(leadingTrivias, codeComments, whitespaceCount));

            return AnalyzerUtil.ReplaceNode(declaration, newDeclaration, document);
        }

        private Task<Document> AddSummaryCommentAsync(Document document, BaseTypeDeclarationSyntax declaration, CancellationToken c)
        {
            var leadingTrivias = declaration.GetLeadingTrivia();
            var whitespaceCount = leadingTrivias[leadingTrivias.Count - 1].Span.Length;
            var newDeclaration = declaration.WithLeadingTrivia(
                AnalyzerUtil.GetNewLeadingTriviaWithSummary(leadingTrivias, summaryComments, whitespaceCount));

            return AnalyzerUtil.ReplaceNode(declaration, newDeclaration, document);
        }

        private IEnumerable<string> GetPropertyDoxygenComments(BasePropertyDeclarationSyntax declaration)
        {
            var throwStatmentSyntaxs = declaration.DescendantNodes().OfType<ThrowStatementSyntax>();
            var exceptionComments = GetExceptionComments(throwStatmentSyntaxs);

            return summaryComments
                .Concat(exceptionComments);
        }

        private IEnumerable<string> GetMethodDoxygenComments(BaseMethodDeclarationSyntax declaration)
        {
            var paramComments = GetParameterComments(declaration);
            var throwStatmentSyntaxs = declaration.DescendantNodes().OfType<ThrowStatementSyntax>();
            var exceptionComments = GetExceptionComments(throwStatmentSyntaxs);
            var returnComments = GetReturnComments(declaration);

            return summaryComments
                .Concat(paramComments)
                .Concat(exceptionComments)
                .Concat(returnComments);
        }

        private IEnumerable<string> GetReturnComments(BaseMethodDeclarationSyntax declaration)
        {
            var methodDeclaration = declaration as MethodDeclarationSyntax;
            if (methodDeclaration == null)
                yield break;

            if (!AnalyzerUtil.IsVoidReturnType(methodDeclaration))
                yield return "/// <returns> </returns>";
        }

        private IEnumerable<string> GetParameterComments(BaseMethodDeclarationSyntax declaration)
        {
            foreach (var parameter in declaration.ParameterList.Parameters)
            {
                yield return $"/// <param name=\"{parameter.Identifier.Text}\"> </param>";
            }
        }

        private IEnumerable<string> GetExceptionComments(IEnumerable<ThrowStatementSyntax> throwStatmentSyntaxs)
        {
            return from throwStatment in throwStatmentSyntaxs
                   let exceptionString = AnalyzerUtil.GetExceptionString(throwStatment)
                   where !exceptionString.Equals("default") && !exceptionString.Equals("unknown")
                   select $"/// <exception cref=\"{exceptionString}\"> </exception>";
        }

        private string[] GetDoxygenComments(BaseTypeDeclarationSyntax declaration)
        {
            if (declaration is ClassDeclarationSyntax)
            {
                return summaryAndCodeComments;
            } else
            {
                return summaryComments;
            }
        }
    }
}