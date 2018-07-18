using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using DexterCRC.Src.CheckerLogic;
using DexterCS;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp.Syntax;

namespace DexterCRC.Src.Crc
{
    class SpaceCRC : ICRCLogic
    {
        SpaceRules spaceRules;

        public SpaceCRC()
        {
            spaceRules = new SpaceRules();
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
                foreach (object item in methodRaw.GetLeadingTrivia().ToList())
                {
                    if (!item.ToString().Contains(Environment.NewLine))
                    {
                        spaceRules.HasDefect(true);
                        PreOccurence preOcc = spaceRules.MakeDefect(config, checker, methodRaw);
                        result.AddDefectWithPreOccurence(preOcc);
                    }
                        
                }
            }
        }
    }
}
