using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using System.Linq;
using System.IO;
using System.Threading;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp;
using Microsoft.CodeAnalysis.CSharp.Syntax;
using Microsoft.CodeAnalysis.Diagnostics;

namespace Dexter.Analyzer
{
    [DiagnosticAnalyzer(LanguageNames.CSharp)]
    public class NoFileCommentAnalyzer : DiagnosticAnalyzer
    {
        public const string DiagnosticId = "VD0003";

        // You can change these strings in the Resources.resx file. If you do not want your analyzer to be localize-able, you can use regular strings for Title and MessageFormat.
        // See https://github.com/dotnet/roslyn/blob/master/docs/analyzers/Localizing%20Analyzers.md for more on localization
        private static readonly LocalizableString Title = new LocalizableResourceString(nameof(Resources.NoFileCommentAnalyzerTitle), Resources.ResourceManager, typeof(Resources));
        private static readonly LocalizableString MessageFormat = new LocalizableResourceString(nameof(Resources.NoFileCommentAnalyzerMessageFormat), Resources.ResourceManager, typeof(Resources));
        private static readonly LocalizableString Description = new LocalizableResourceString(nameof(Resources.NoFileCommentAnalyzerDescription), Resources.ResourceManager, typeof(Resources));
        private const string Category = "Naming";

        private static DiagnosticDescriptor Rule = new DiagnosticDescriptor(DiagnosticId, Title, MessageFormat, Category, DiagnosticSeverity.Warning, isEnabledByDefault: true, description: Description);

        public override ImmutableArray<DiagnosticDescriptor> SupportedDiagnostics { get { return ImmutableArray.Create(Rule); } }

        public override void Initialize(AnalysisContext context)
        {
            // TODO: Consider registering other actions that act on syntax instead of or in addition to symbols
            // See https://github.com/dotnet/roslyn/blob/master/docs/analyzers/Analyzer%20Actions%20Semantics.md for more information

            context.RegisterSyntaxNodeAction(AnalyzeComilationUnitSyntaxNode, SyntaxKind.CompilationUnit);

        }

        private void AnalyzeComilationUnitSyntaxNode(SyntaxNodeAnalysisContext context)
        {
            var node = (CompilationUnitSyntax)context.Node;

            if (node.Members.Count == 0)
                return;

            var namespaceDeclaration = node.Members[0] as NamespaceDeclarationSyntax;
            if (namespaceDeclaration == null)
                return;

            if (!HasPublicType(namespaceDeclaration))
                return;

            var xmlTrivia = GetXmlTrivia(node);
            if (xmlTrivia == null)
            {
                var fileName = Path.GetFileName(context.SemanticModel.SyntaxTree.FilePath);
                context.ReportDiagnostic(Diagnostic.Create(Rule, node.GetFirstToken().GetLocation(), fileName));
            }
        }

        private static DocumentationCommentTriviaSyntax GetXmlTrivia(CSharpSyntaxNode node)
        {
            return node.GetLeadingTrivia()
                .Select(i => i.GetStructure())
                .OfType<DocumentationCommentTriviaSyntax>()
                .FirstOrDefault();
        }

        private bool HasPublicType(NamespaceDeclarationSyntax node)
        {
            var baseTypes = node.Members.OfType<BaseTypeDeclarationSyntax>();
            if (baseTypes.Any(baseType => baseType.Modifiers.Any(SyntaxKind.PublicKeyword)))
                return true;

            return false;
        }
    }
}
