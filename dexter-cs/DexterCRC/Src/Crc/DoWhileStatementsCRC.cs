using System.Linq;
using DexterCS;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp.Symbols;
using Microsoft.CodeAnalysis.CSharp.Syntax;

namespace DexterCRC
{
    class DoWhileStatementsCRC : ICRCLogic
    {
        WithBrace bracket;

        public DoWhileStatementsCRC() {
            bracket = new WithBrace();
        }

        public void Analyze(AnalysisConfig config, AnalysisResult result, Checker checker, SyntaxNode syntaxRoot)
        {
            var doStatementRaws = syntaxRoot.DescendantNodes().OfType<DoStatementSyntax>();
            if (!doStatementRaws.Any())
            {
                return;
            }

            foreach(var doStatementRaw in doStatementRaws)
            {
                if(bracket.HasDefect(doStatementRaw.Statement.ToString()))
                {
                    PreOccurence preOcc = bracket.MakeDefect(config, checker, doStatementRaw);
                    result.AddDefectWithPreOccurence(preOcc);
                }
            }
        }
    }
}
