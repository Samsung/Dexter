using DexterCS;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp.Syntax;
using System.Linq;

namespace DexterCRC
{
    public class ParameterCRC : ICRCLogic
    {
        CamelCasing camelCasing = new CamelCasing();
        public ParameterCRC() { }
        public void Analyze(AnalysisConfig config, AnalysisResult result, Checker checker, SyntaxNode syntaxRoot)
        {
            var parameterRaws = syntaxRoot.DescendantNodes().OfType<ParameterSyntax>();
            if (!parameterRaws.Any())
            {
                return;
            }
            foreach(var parameterRaw in parameterRaws)
            {
                string parameterName = parameterRaw.Identifier.ValueText;
                if (camelCasing.HasDefect(parameterName))
                {
                    PreOccurence preOcc = camelCasing.MakeDefect(config, checker, parameterRaw);
                    result.AddDefectWithPreOccurence(preOcc);
                }
            }
        }
    }
}
