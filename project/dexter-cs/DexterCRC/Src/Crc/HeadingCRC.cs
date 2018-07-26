using DexterCRC.Src.CheckerLogic;
using DexterCS;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp.Syntax;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DexterCRC.Src.Crc
{
    class HeadingCRC : ICRCLogic
    {
        HeadingRule headingRule;

        public HeadingCRC()
        {
            headingRule = new HeadingRule();
        }

        public void Analyze(AnalysisConfig config, AnalysisResult result, Checker checker, SyntaxNode syntaxRoot)
        {

            var classRaws = syntaxRoot.DescendantNodes().OfType<UsingDirectiveSyntax>();
                if (!classRaws.Any())
                {
                    return;
                }
                
                foreach (var classRaw in classRaws)
            {
                SyntaxTriviaList syntaxTriviaList = classRaw.GetLeadingTrivia();

                if (headingRule.HasDefect(syntaxTriviaList))
                {
                    PreOccurence preOcc = headingRule.MakeDefect(config, checker, classRaw);
                    result.AddDefectWithPreOccurence(preOcc);
                }                
            }

        }
    }
}

