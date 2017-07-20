using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using System.Composition;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using System.IO;
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
    [ExportCodeFixProvider(LanguageNames.CSharp, Name = nameof(NoFileCommentFixProvider)), Shared]
    public class NoFileCommentFixProvider : CodeFixProvider
    {
        private const string title = "Add file doxygen comment";

        public sealed override ImmutableArray<string> FixableDiagnosticIds
        {
            get { return ImmutableArray.Create(NoFileCommentAnalyzer.DiagnosticId); }
        }

        public sealed override FixAllProvider GetFixAllProvider()
        {
            // See https://github.com/dotnet/roslyn/blob/master/docs/analyzers/FixAllProvider.md for more information on Fix All Providers
            return WellKnownFixAllProviders.BatchFixer;
        }

        public sealed override async Task RegisterCodeFixesAsync(CodeFixContext context)
        {
            var root = await context.Document.GetSyntaxRootAsync(context.CancellationToken).ConfigureAwait(false);

            //TODO: Replace the following code with your own analysis, generating a CodeAction for each fix to suggest
            var diagnostic = context.Diagnostics.First();
 
            // Register a code action that will invoke the fix.
            context.RegisterCodeFix(
                CodeAction.Create(
                    title: title,
                    createChangedDocument: c => AddFileDoxygenCommentAsync(context.Document, root, c),
                    equivalenceKey: title),
                diagnostic);
        }

        private Task<Document> AddFileDoxygenCommentAsync(Document document, SyntaxNode node, CancellationToken c)
        {
            string doxygenComment = GetDoxygenComment(document.Name);

            var newNode = node.WithLeadingTrivia(
                SyntaxFactory.ParseLeadingTrivia(doxygenComment));

            return ReplaceNode(node, newNode, document);
        }

        private static string GetDoxygenComment(string fileName)
        {
            return String.Format(@"/// @file {0}
/// <published> N </published>
/// <privlevel> non-privilege </privlevel>
/// <privilege> none </privilege> 
/// <privacy> N </privacy>
/// <product> TV </product>
/// <version> 3.*.* </version>
/// <SDK_Support> Y </SDK_Support>
/// Copyright (c) 2017 Samsung Electronics Co., Ltd All Rights Reserved
/// PROPRIETARY/CONFIDENTIAL 
/// This software is the confidential and proprietary
/// information of SAMSUNG ELECTRONICS (""Confidential Information""). You shall
/// not disclose such Confidential Information and shall use it only in
/// accordance with the terms of the license agreement you entered into with
/// SAMSUNG ELECTRONICS. SAMSUNG make no representations or warranties about the
/// suitability of the software, either express or implied, including but not
/// limited to the implied warranties of merchantability, fitness for a
/// particular purpose, or non-infringement. SAMSUNG shall not be liable for any
/// damages suffered by licensee as a result of using, modifying or distributing
/// this software or its derivatives.
", fileName);
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

            for (int i = 0; i < leadingTrivias.Count - 1; i++)
            {
                yield return leadingTrivias[i];
            }

            foreach (var commentTrivia in commentTrivias)
            {
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