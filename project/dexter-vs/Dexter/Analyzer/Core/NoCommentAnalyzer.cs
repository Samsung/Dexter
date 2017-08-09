using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using System.Linq;
using System.Threading;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp;
using Microsoft.CodeAnalysis.CSharp.Syntax;
using Microsoft.CodeAnalysis.Diagnostics;
using Dexter.Analyzer.Utils;

namespace Dexter.Analyzer
{
    /// <summary>
    /// Dianoses public API without doxygen comment
    /// </summary>
    [DiagnosticAnalyzer(LanguageNames.CSharp)]
    public class NoCommentAnalyzer : DiagnosticAnalyzer
    {
        public const string NoCommentRuleId = "VDNC01";
        public const string NoCommentMethodRuleId = "VDNC02";
        public const string NoCommentPropertyRuleId = "VDNC03";
        public const string NoSummaryRuleId = "VDNC04";
        public const string NoCodeRuleId = "VDNC05";
        public const string NoReturnsRuleId = "VDNC06";
        public const string NoParamRuleId = "VDNC07";
        public const string NoExceptionRuleId = "VDNC08";

        private static readonly DiagnosticDescriptor NoCommentRule;
        private static readonly DiagnosticDescriptor NoCommentMethodRule;
        private static readonly DiagnosticDescriptor NoCommentPropertyRule;
        private static readonly DiagnosticDescriptor NoSummaryRule;
        private static readonly DiagnosticDescriptor NoCodeRule;
        private static readonly DiagnosticDescriptor NoReturnsRule;
        private static readonly DiagnosticDescriptor NoParamRule;
        private static readonly DiagnosticDescriptor NoExceptionRule;

        private const string Category = "Naming";

        private const string summaryTag = "summary";
        private const string codeTag = "code";
        private const string paramTag = "param";
        private const string returnTag = "returns";
        private const string exceptionTag = "exception";

        static NoCommentAnalyzer()
        {
            NoCommentRule = AnalyzerUtil.CreateDiagnosticDescriptor(NoCommentRuleId, 
                nameof(Resources.NoCommentRuleTitle), nameof(Resources.NoCommentRuleMessageFormat), 
                nameof(Resources.NoCommentRuleDescription), Category);

            NoCommentMethodRule = AnalyzerUtil.CreateDiagnosticDescriptor(NoCommentMethodRuleId, 
                nameof(Resources.NoCommentRuleTitle), nameof(Resources.NoCommentRuleMessageFormat), 
                nameof(Resources.NoCommentRuleDescription), Category);

            NoCommentPropertyRule = AnalyzerUtil.CreateDiagnosticDescriptor(NoCommentPropertyRuleId, 
                nameof(Resources.NoCommentRuleTitle), nameof(Resources.NoCommentRuleMessageFormat), 
                nameof(Resources.NoCommentRuleDescription), Category);

            NoSummaryRule = AnalyzerUtil.CreateDiagnosticDescriptor(NoSummaryRuleId,
                nameof(Resources.NoSummaryRuleTitle), nameof(Resources.NoSummaryRuleMessageFormat),
                nameof(Resources.NoSummaryRuleDescription), Category);

            NoCodeRule = AnalyzerUtil.CreateDiagnosticDescriptor(NoCodeRuleId,
                nameof(Resources.NoCodeRuleTitle), nameof(Resources.NoCodeRuleMessageFormat),
                nameof(Resources.NoCodeRuleDescription), Category);

            NoReturnsRule = AnalyzerUtil.CreateDiagnosticDescriptor(NoReturnsRuleId,
                nameof(Resources.NoReturnsRuleTitle), nameof(Resources.NoReturnsRuleMessageFormat),
                nameof(Resources.NoReturnsRuleDescription), Category);

            NoParamRule = AnalyzerUtil.CreateDiagnosticDescriptor(NoParamRuleId,
                nameof(Resources.NoParamRuleTitle), nameof(Resources.NoParamRuleMessageFormat),
                nameof(Resources.NoParamRuleDescription), Category);

            NoExceptionRule = AnalyzerUtil.CreateDiagnosticDescriptor(NoExceptionRuleId,
                nameof(Resources.NoExceptionRuleTitle), nameof(Resources.NoExceptionRuleMessageFormat),
                nameof(Resources.NoExceptionRuleDescription), Category);
        }

        /// <summary>
        /// Returns supported diagnostics
        /// </summary>
        public override ImmutableArray<DiagnosticDescriptor> SupportedDiagnostics
        {
            get
            {
                return ImmutableArray.Create(NoCommentRule, NoCommentMethodRule, NoCommentPropertyRule, NoSummaryRule, NoCodeRule, NoReturnsRule, NoParamRule, NoExceptionRule);
            }
        }

        /// <summary>
        /// Registers syntax node actions to analyze public API types
        /// </summary>
        /// <param name="context">Analysis context</param>
        public override void Initialize(AnalysisContext context)
        {
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

            if (AnalyzerUtil.IsTestAttribute(node.AttributeLists))
                return;

            if (!IsPublicType(node.Modifiers))
                return;

            var xmlTrivia = GetXmlTrivia(node);
            if (xmlTrivia == null)
            {
                context.ReportDiagnostic(Diagnostic.Create(NoCommentRule, node.Identifier.GetLocation(), node.Identifier.Text));
                return;
            } 

            if (!HasXmlNameTag(xmlTrivia, summaryTag))
            {
                context.ReportDiagnostic(Diagnostic.Create(NoSummaryRule, node.Identifier.GetLocation(), node.Identifier.Text));
                return;
            }

            if (!HasXmlNameTag(xmlTrivia, codeTag))
            {
                context.ReportDiagnostic(Diagnostic.Create(NoCodeRule, node.Identifier.GetLocation(), node.Identifier.Text));
                return;
            }
        }

        private bool HasXmlNameTag(DocumentationCommentTriviaSyntax xmlTrivia, string name)
        {
            return xmlTrivia.DescendantNodes().OfType<XmlElementStartTagSyntax>()
                .Any(node => node.Name.ToString().Equals(name));
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

            if (!IsAllParentsValid(node))
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

            if (!IsAllParentsValid(node))
                return;

            var xmlTrivia = GetXmlTrivia(node);
            if (xmlTrivia == null)
            {
                context.ReportDiagnostic(Diagnostic.Create(NoCommentMethodRule, node.Identifier.GetLocation(), node.Identifier.Text));
                return;
            }

            if (!(AnalyzerUtil.IsVoidReturnType(node) || HasXmlNameTag(xmlTrivia, returnTag)))
            {
                context.ReportDiagnostic(Diagnostic.Create(NoReturnsRule, node.Identifier.GetLocation(), node.Identifier.Text));
            }

            ReportNoParamDiagnostics(context, node, xmlTrivia);
            ReportNoExceptionDiagnostics(context, node, xmlTrivia);
        }

        private void ReportNoExceptionDiagnostics(SyntaxNodeAnalysisContext context, SyntaxNode node, DocumentationCommentTriviaSyntax xmlTrivia)
        {
            var exceptionCommentNames = xmlTrivia.DescendantNodes().OfType<XmlNameAttributeSyntax>()
                .Where(attribute => IsNameAttribute(attribute, exceptionTag))
                .Select(attribute => attribute.Identifier.ToString());

            var exceptionTokens = GetExceptionTokens(node);

            foreach (var exceptionToken in exceptionTokens)
            {
                var exceptionName = exceptionToken.Text;

                if (!exceptionCommentNames.Any(commentName => commentName.Equals(exceptionName)))
                {
                    context.ReportDiagnostic(Diagnostic.Create(NoExceptionRule, exceptionToken.GetLocation(), exceptionToken.Text));
                }
            }
        }

        private static IEnumerable<SyntaxToken> GetExceptionTokens(SyntaxNode node)
        {
            var throwStatmentSyntaxs = node.DescendantNodes().OfType<ThrowStatementSyntax>();
            var ObjectCreationExpressions = from throwStatment in throwStatmentSyntaxs
                                            where throwStatment.Expression != null &&
                                            throwStatment.Expression is ObjectCreationExpressionSyntax
                                            select throwStatment.Expression as ObjectCreationExpressionSyntax;

            var exceptionTokens = from expression in ObjectCreationExpressions
                                  let type = expression.Type as IdentifierNameSyntax
                                  where type != null
                                  select type.Identifier;

            return exceptionTokens;
        }

        private void ReportNoParamDiagnostics(SyntaxNodeAnalysisContext context, BaseMethodDeclarationSyntax node, DocumentationCommentTriviaSyntax xmlTrivia)
        {
            var paramCommentNames = xmlTrivia.DescendantNodes().OfType<XmlNameAttributeSyntax>()
                .Where(attribute => IsNameAttribute(attribute, paramTag))
                .Select(attribute => attribute.Identifier.ToString());

            foreach (var parameter in node.ParameterList.Parameters)
            {
                var paramName = parameter.Identifier.Text;

                if (HasThisModifier(parameter))
                    continue;

                if (!paramCommentNames.Any(commentName => commentName.Equals(paramName)))
                {
                    context.ReportDiagnostic(Diagnostic.Create(NoParamRule, parameter.Identifier.GetLocation(), parameter.Identifier.Text));
                }
            }
        }

        private bool HasThisModifier(ParameterSyntax parameter)
        {
            return parameter.Modifiers.Any(modifier => modifier.Text.Equals("this"));
        }

        private bool IsNameAttribute(XmlNameAttributeSyntax xmlNameAttribute, string name)
        {
            var parent = xmlNameAttribute.Parent as XmlElementStartTagSyntax;
            if (parent == null)
                return false;

            if (parent.Name.ToString().Equals(name))
                return true;

            return false;
        }

        private void AnalyzeConstructorSyntaxNode(SyntaxNodeAnalysisContext context)
        {
            var node = (ConstructorDeclarationSyntax)context.Node;

            if (!IsPublicType(node.Modifiers))
                return;

            if (!IsAllParentsValid(node))
                return;

            var xmlTrivia = GetXmlTrivia(node);
            if (xmlTrivia == null)
            {
                context.ReportDiagnostic(Diagnostic.Create(NoCommentMethodRule, node.Identifier.GetLocation(), node.Identifier.Text));
                return;
            }

            ReportNoParamDiagnostics(context, node, xmlTrivia);
        }

        private void AnalyzePropertySyntaxNode(SyntaxNodeAnalysisContext context)
        {
            var node = (PropertyDeclarationSyntax)context.Node;

            if (!IsPublicType(node.Modifiers))
                return;

            if (!IsAllParentsValid(node))
                return;

            var xmlTrivia = GetXmlTrivia(node);
            if (xmlTrivia == null)
            {
                context.ReportDiagnostic(Diagnostic.Create(NoCommentPropertyRule, node.Identifier.GetLocation(), node.Identifier.Text));
                return;
            }

            ReportNoExceptionDiagnostics(context, node, xmlTrivia);
        }

        private bool IsAllParentsValid(SyntaxNode node)
        {
            return node.Ancestors(false).OfType<BaseTypeDeclarationSyntax>()
                .All(ancestor => IsPublicType(ancestor.Modifiers) && 
                                    !AnalyzerUtil.IsTestAttribute(ancestor.AttributeLists));
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
    }
}
