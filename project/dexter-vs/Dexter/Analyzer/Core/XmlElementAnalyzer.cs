using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using System.Linq;
using System.Threading;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp;
using Microsoft.CodeAnalysis.CSharp.Syntax;
using Microsoft.CodeAnalysis.Diagnostics;
using System.Text.RegularExpressions;
using Dexter.Analyzer.Utils;

namespace Dexter.Analyzer
{
    [DiagnosticAnalyzer(LanguageNames.CSharp)]
    public class XmlElementAnalyzer : DiagnosticAnalyzer
    {
        public const string NoXmlTextRuleId = "VDNX01";
        public const string NoEndTagRuleId = "VDNX02";

        private static readonly DiagnosticDescriptor NoXmlTextRule;
        private static readonly DiagnosticDescriptor NoEndTagRule;

        private const string Category = "Naming";

        static XmlElementAnalyzer()
        {
            NoXmlTextRule = AnalyzerUtil.CreateDiagnosticDescriptor(NoXmlTextRuleId, nameof(Resources.NoXmlTextRulerTitle), 
                nameof(Resources.NoXmlTextRuleMessageFormat), nameof(Resources.NoXmlTextRuleDescription), Category);
            NoEndTagRule = AnalyzerUtil.CreateDiagnosticDescriptor(NoEndTagRuleId, nameof(Resources.NoEndTagRuleTitle),
                nameof(Resources.NoEndTagRuleMessageFormat), nameof(Resources.NoEndTagRuleDescription), Category);
        }

        public override ImmutableArray<DiagnosticDescriptor> SupportedDiagnostics
        {
            get
            {
                return ImmutableArray.Create(NoXmlTextRule, NoEndTagRule);
            }
        }

        public override void Initialize(AnalysisContext context)
        {
            context.RegisterSyntaxNodeAction(AnalyzeXmlElementSyntaxNode, SyntaxKind.XmlElement);

        }

        private void AnalyzeXmlElementSyntaxNode(SyntaxNodeAnalysisContext context)
        {
            var node = (XmlElementSyntax)context.Node;
            var text = node.Content.ToString();
            var tagName = node.StartTag.Name;

            if (!HasEndTag(node))
            {
                context.ReportDiagnostic(Diagnostic.Create(NoEndTagRule, tagName.GetLocation(), tagName));
                return;
            }

            if (IsEmptyText(text))
            {
                context.ReportDiagnostic(Diagnostic.Create(NoXmlTextRule, tagName.GetLocation(), tagName));
            }

        }

        private bool HasEndTag(XmlElementSyntax node)
        {
            var startTagName = node.StartTag.Name.ToString();
            var endTagName = node.EndTag.Name.ToString();

            return endTagName.Equals(startTagName);
        }

        private bool IsEmptyText(string text)
        {
            var invalidCharPattern = @"(\s+|/+)";
            Regex regex = new Regex(invalidCharPattern);
            var validText = regex.Replace(text, "");

            return validText.Equals("");
        }
    }
}
