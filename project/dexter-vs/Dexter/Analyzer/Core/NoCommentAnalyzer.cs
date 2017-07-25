﻿using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using System.Linq;
using System.Threading;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp;
using Microsoft.CodeAnalysis.CSharp.Syntax;
using Microsoft.CodeAnalysis.Diagnostics;

namespace Dexter.Analyzer
{
    [DiagnosticAnalyzer(LanguageNames.CSharp)]
    public class NoCommentAnalyzer : DiagnosticAnalyzer
    {
        public const string NoCommentRuleId = "VDNC01";
        public const string NoMethodCommentRuleId = "VDNC02";
        public const string NoPropertyCommentRuleId = "VDNC03";

        // You can change these strings in the Resources.resx file. If you do not want your analyzer to be localize-able, you can use regular strings for Title and MessageFormat.
        // See https://github.com/dotnet/roslyn/blob/master/docs/analyzers/Localizing%20Analyzers.md for more on localization
        private static readonly LocalizableString Title = new LocalizableResourceString(nameof(Resources.NoCommentAnalyzerTitle), Resources.ResourceManager, typeof(Resources));
        private static readonly LocalizableString MessageFormat = new LocalizableResourceString(nameof(Resources.NoCommentAnalyzerMessageFormat), Resources.ResourceManager, typeof(Resources));
        private static readonly LocalizableString Description = new LocalizableResourceString(nameof(Resources.NoCommentAnalyzerDescription), Resources.ResourceManager, typeof(Resources));
        private const string Category = "Naming";

        private static readonly DiagnosticDescriptor NoCommentRule = new DiagnosticDescriptor(NoCommentRuleId, Title, MessageFormat, Category, DiagnosticSeverity.Warning, isEnabledByDefault: true, description: Description);
        private static readonly DiagnosticDescriptor NoMethodCommentRule = new DiagnosticDescriptor(NoMethodCommentRuleId, Title, MessageFormat, Category, DiagnosticSeverity.Warning, isEnabledByDefault: true, description: Description);
        private static readonly DiagnosticDescriptor NoPropertyCommentRule = new DiagnosticDescriptor(NoPropertyCommentRuleId, Title, MessageFormat, Category, DiagnosticSeverity.Warning, isEnabledByDefault: true, description: Description);

        public override ImmutableArray<DiagnosticDescriptor> SupportedDiagnostics
        {
            get
            {
                return ImmutableArray.Create(NoCommentRule, NoMethodCommentRule, NoPropertyCommentRule);
            }
        }

        public override void Initialize(AnalysisContext context)
        {
            // TODO: Consider registering other actions that act on syntax instead of or in addition to symbols
            // See https://github.com/dotnet/roslyn/blob/master/docs/analyzers/Analyzer%20Actions%20Semantics.md for more information
            //context.RegisterSymbolAction(AnalyzeSymbol, SymbolKind.NamedType);
            context.RegisterSyntaxNodeAction(AnalyzeClassSyntaxNode, SyntaxKind.ClassDeclaration);
            context.RegisterSyntaxNodeAction(AnalyzeInterfaceSyntaxNode, SyntaxKind.InterfaceDeclaration);
            context.RegisterSyntaxNodeAction(AnalyzeStructSyntaxNode, SyntaxKind.StructDeclaration);
            context.RegisterSyntaxNodeAction(AnalyzeEnumSyntaxNode, SyntaxKind.EnumDeclaration);
            context.RegisterSyntaxNodeAction(AnalyzeMethodSyntaxNode, SyntaxKind.MethodDeclaration);
            context.RegisterSyntaxNodeAction(AnalyzeConstructorSyntaxNode, SyntaxKind.ConstructorDeclaration);
            context.RegisterSyntaxNodeAction(AnalyzePropertySyntaxNode, SyntaxKind.PropertyDeclaration);
        }

        private void AnalyzeClassSyntaxNode(SyntaxNodeAnalysisContext context)
        {
            var node = (ClassDeclarationSyntax)context.Node;

            if (!IsPublicType(node.Modifiers))
                return;

            var xmlTrivia = GetXmlTrivia(node);
            if (xmlTrivia == null)
                context.ReportDiagnostic(Diagnostic.Create(NoCommentRule, node.Identifier.GetLocation(), node.Identifier.Text));
        }

        private void AnalyzeInterfaceSyntaxNode(SyntaxNodeAnalysisContext context)
        {
            var node = (InterfaceDeclarationSyntax)context.Node;

            if (!IsPublicType(node.Modifiers))
                return;

            var xmlTrivia = GetXmlTrivia(node);
            if (xmlTrivia == null)
                context.ReportDiagnostic(Diagnostic.Create(NoCommentRule, node.Identifier.GetLocation(), node.Identifier.Text));
        }

        private void AnalyzeStructSyntaxNode(SyntaxNodeAnalysisContext context)
        {
            var node = (StructDeclarationSyntax)context.Node;

            if (!IsPublicType(node.Modifiers))
                return;

            var xmlTrivia = GetXmlTrivia(node);
            if (xmlTrivia == null)
                context.ReportDiagnostic(Diagnostic.Create(NoCommentRule, node.Identifier.GetLocation(), node.Identifier.Text));
        }

        private void AnalyzeEnumSyntaxNode(SyntaxNodeAnalysisContext context)
        {
            var node = (EnumDeclarationSyntax)context.Node;

            if (!IsPublicType(node.Modifiers))
                return;

            if (!IsAllParentsPublic(node))
                return;

            var xmlTrivia = GetXmlTrivia(node);
            if (xmlTrivia == null)
                context.ReportDiagnostic(Diagnostic.Create(NoCommentRule, node.Identifier.GetLocation(), node.Identifier.Text));
        }

        private void AnalyzeMethodSyntaxNode(SyntaxNodeAnalysisContext context)
        {
            var node = (MethodDeclarationSyntax)context.Node;

            if (!IsPublicType(node.Modifiers))
                return;

            if (!IsAllParentsPublic(node))
                return;

            var xmlTrivia = GetXmlTrivia(node);
            if (xmlTrivia == null)
                context.ReportDiagnostic(Diagnostic.Create(NoMethodCommentRule, node.Identifier.GetLocation(), node.Identifier.Text));
        }

        private void AnalyzeConstructorSyntaxNode(SyntaxNodeAnalysisContext context)
        {
            var node = (ConstructorDeclarationSyntax)context.Node;

            if (!IsPublicType(node.Modifiers))
                return;

            if (!IsAllParentsPublic(node))
                return;

            var xmlTrivia = GetXmlTrivia(node);
            if (xmlTrivia == null)
                context.ReportDiagnostic(Diagnostic.Create(NoMethodCommentRule, node.Identifier.GetLocation(), node.Identifier.Text));
        }

        private void AnalyzePropertySyntaxNode(SyntaxNodeAnalysisContext context)
        {
            var node = (PropertyDeclarationSyntax)context.Node;

            if (!IsPublicType(node.Modifiers))
                return;

            if (!IsAllParentsPublic(node))
                return;

            var xmlTrivia = GetXmlTrivia(node);
            if (xmlTrivia == null)
                context.ReportDiagnostic(Diagnostic.Create(NoPropertyCommentRule, node.Identifier.GetLocation(), node.Identifier.Text));
        }

        private bool IsAllParentsPublic(SyntaxNode node)
        {
            return node.Ancestors(false).OfType<BaseTypeDeclarationSyntax>()
                .All(ancestor => IsPublicType(ancestor.Modifiers));
        }

        private bool IsPublicType(SyntaxTokenList modifiers)
        {
            return modifiers.Any(SyntaxKind.PublicKeyword) ||
                modifiers.Any(SyntaxKind.ProtectedKeyword);
        }

        private static DocumentationCommentTriviaSyntax GetXmlTrivia(CSharpSyntaxNode node)
        {
            return node.GetLeadingTrivia()
                .Select(i => i.GetStructure())
                .OfType<DocumentationCommentTriviaSyntax>()
                .FirstOrDefault();
        }

        private static void AnalyzeSymbol(SymbolAnalysisContext context)
        {
            // TODO: Replace the following code with your own analysis, generating Diagnostic objects for any issues you find
            var namedTypeSymbol = (INamedTypeSymbol)context.Symbol;

            // Find just those named type symbols with names containing lowercase letters.
            if (namedTypeSymbol.Name.ToCharArray().Any(char.IsLower))
            {
                // For all such symbols, produce a diagnostic.
                var diagnostic = Diagnostic.Create(NoCommentRule, namedTypeSymbol.Locations[0], namedTypeSymbol.Name);

                context.ReportDiagnostic(diagnostic);
            }
        }
    }
}