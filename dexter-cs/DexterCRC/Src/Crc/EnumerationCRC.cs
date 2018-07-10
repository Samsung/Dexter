using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using DexterCS;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp.Syntax;

namespace DexterCRC
{
    class EnumerationCRC : ICRCLogic
    {
        PascalCasing pascalCasing;

        public EnumerationCRC() {
            pascalCasing = new PascalCasing();
        }
        public void Analyze(AnalysisConfig config, AnalysisResult result, Checker checker, SyntaxNode syntaxRoot)
        {
           
            var enumRaws = syntaxRoot.DescendantNodes().OfType<EnumDeclarationSyntax>();
            foreach(var enumRaw in enumRaws)
            {
                var enumName = enumRaw.Identifier.ToString();
                if (pascalCasing.HasDefect(enumName))
                {
                    PreOccurence preOcc = pascalCasing.MakeDefect(config, checker, enumRaw);
                    result.AddDefectWithPreOccurence(preOcc);
                }
            }
        }
    }
}
