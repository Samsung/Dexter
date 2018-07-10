using System;
using System.Linq;
using DexterCS;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp.Syntax;
using Microsoft.CodeAnalysis.CSharp.Symbols;
namespace DexterCRC
{
    class WhileStatementsCRC : ICRCLogic
    {
        WithBrace bracket = new WithBrace();
        public WhileStatementsCRC() { }
        public void Analyze(AnalysisConfig config, AnalysisResult result, Checker checker, SyntaxNode syntaxRoot)
        {
            var whileRaws = syntaxRoot.DescendantNodes().OfType<WhileStatementSyntax>();
            if (!whileRaws.Any())
            {
                return;
            }
            foreach(var whileRaw in whileRaws)
            {
                if (bracket.HasDefect(whileRaw.Statement.ToString()))
                {
                    PreOccurence preOcc = bracket.MakeDefect(config, checker, whileRaw);
                    result.AddDefectWithPreOccurence(preOcc);
                }
            }
        }
    }
}
