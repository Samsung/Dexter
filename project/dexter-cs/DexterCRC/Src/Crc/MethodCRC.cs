using DexterCS;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp.Syntax;
using System.Linq;

namespace DexterCRC
{
    class MethodCRC : ICRCLogic
    {
        PascalCasing pascalCasing;
        WithoutUnderscore underscore;

        public MethodCRC() {
            pascalCasing = new PascalCasing();
            underscore = new WithoutUnderscore();
        }
        public void Analyze(AnalysisConfig config, AnalysisResult result, Checker checker, SyntaxNode syntaxRoot)
        {
            var methodRaws = syntaxRoot.DescendantNodes().OfType<MethodDeclarationSyntax>();
            if (!methodRaws.Any())
            {
                return;
            }

            foreach(var methodRaw in methodRaws)
            {
                string methodName = methodRaw.Identifier.ToString();

                if (underscore.HasDefect(methodName))
                {
                    PreOccurence preOcc = underscore.MakeDefect(config, checker, methodRaw);
                    result.AddDefectWithPreOccurence(preOcc);
                }
                if (pascalCasing.HasDefect(methodName))
                {
                    PreOccurence preOcc = pascalCasing.MakeDefect(config, checker, methodRaw);
                    result.AddDefectWithPreOccurence(preOcc);
                }
            }
        }
    }
}
