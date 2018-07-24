using DexterCS;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp.Syntax;
using System.Linq;

namespace DexterCRC
{
    public class PropertyCRC : ICRCLogic
    {
        PascalCasing pascalCasing;
        public PropertyCRC() { pascalCasing = new PascalCasing(); }
        public void Analyze(AnalysisConfig config, AnalysisResult result, Checker checker, SyntaxNode syntaxRoot)
        {
            var propertyRaws = syntaxRoot.DescendantNodes().OfType<PropertyDeclarationSyntax>();
            if (!propertyRaws.Any())
            {
                return;
            }
            foreach (var propertyRaw in propertyRaws)
            {
                string propertyName = propertyRaw.Identifier.ToString();
                if (pascalCasing.HasDefect(propertyName))
                {
                    PreOccurence preOcc = pascalCasing.MakeDefect(config, checker, propertyRaw);
                    result.AddDefectWithPreOccurence(preOcc);
                }
            }

        }
    }
}
