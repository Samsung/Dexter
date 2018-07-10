using System;
using System.Linq;
using DexterCS;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp.Symbols;
using Microsoft.CodeAnalysis.CSharp.Syntax;

namespace DexterCRC
{
    public class IfStatementsCRC : ICRCLogic
    {
        WithBrace bracket = new WithBrace();
        public IfStatementsCRC() { }
        public void Analyze(AnalysisConfig config, AnalysisResult result, Checker checker, SyntaxNode syntaxRoot)
        {
            var ifRaws = syntaxRoot.DescendantNodes().OfType<IfStatementSyntax>();

            if (!ifRaws.Any())
            {
                return;
            }

            foreach(var ifRaw in ifRaws)
            {
                if (bracket.HasDefect(ifRaw.Statement.ToString()))
                {
                    PreOccurence preOcc = bracket.MakeDefect(config, checker, ifRaw);
                    result.AddDefectWithPreOccurence(preOcc);
                }
            }

        }
    }
}
