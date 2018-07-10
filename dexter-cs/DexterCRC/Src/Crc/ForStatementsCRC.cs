using System;
using System.Linq;
using DexterCS;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp.Syntax;
using Microsoft.CodeAnalysis.CSharp.Symbols;

namespace DexterCRC
{
    public class ForStatementsCRC : ICRCLogic
    {
        WithBrace bracket = new WithBrace();
        public ForStatementsCRC() { }
        public void Analyze(AnalysisConfig config, AnalysisResult result, Checker checker, SyntaxNode syntaxRoot)
        {
            var forRaws = syntaxRoot.DescendantNodes().OfType<ForStatementSyntax> ();
            if (!forRaws.Any())
            {
                return;
            }
            foreach (var forRaw in forRaws)
            {
                if (bracket.HasDefect(forRaw.Statement.ToString()))
                {
                    PreOccurence preOcc = bracket.MakeDefect(config, checker, forRaw);
                    result.AddDefectWithPreOccurence(preOcc);
                }
            }
        }
    }
}
