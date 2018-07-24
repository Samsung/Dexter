using DexterCS;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp.Syntax;
using System.Linq;

namespace DexterCRC
{
    class ForeachStatementsCRC : ICRCLogic
    {
        WithBrace bracket = new WithBrace();
        public ForeachStatementsCRC() { }
        public void Analyze(AnalysisConfig config, AnalysisResult result, Checker checker, SyntaxNode syntaxRoot)
        {
            var foreachRaws = syntaxRoot.DescendantNodes().OfType<ForEachStatementSyntax>();
            foreach (var foreachRaw in foreachRaws)
            {
                if (bracket.HasDefect(foreachRaw.Statement.ToString()))
                {
                    PreOccurence preOcc = bracket.MakeDefect(config, checker, foreachRaw);
                    result.AddDefectWithPreOccurence(preOcc);
                }
            }
        }
    }
}
