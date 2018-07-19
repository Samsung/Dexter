using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using DexterCRC.Src.CheckerLogic;
using DexterCS;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp;
using Microsoft.CodeAnalysis.CSharp.Syntax;

namespace DexterCRC
{
    class SpacingCRC : ICRCLogic
    {
        MethodSpacing methodSpacing;

        public SpacingCRC()
        {
            methodSpacing = new MethodSpacing();
        }

        public void Analyze(AnalysisConfig config, AnalysisResult result, Checker checker, SyntaxNode syntaxRoot)
        {
            var methodRaws = syntaxRoot.DescendantNodes().OfType<MethodDeclarationSyntax>();
            if (!methodRaws.Any())
            {
                return;
            }

            foreach (var methodRaw in methodRaws)
            {
                SyntaxTriviaList syntaxTriviaList = methodRaw.GetLeadingTrivia();

                if (methodSpacing.HasDefect(syntaxTriviaList))
                {
                    PreOccurence preOcc = methodSpacing.MakeDefect(config, checker, methodRaw);
                    result.AddDefectWithPreOccurence(preOcc);
                }
            }
        }
    }
}
