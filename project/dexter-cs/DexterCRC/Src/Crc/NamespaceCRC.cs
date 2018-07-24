using DexterCS;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp.Syntax;
using System.Linq;

namespace DexterCRC
{
    class NamespaceCRC : ICRCLogic
    {
        PascalCasing pascalCasing;
        public NamespaceCRC() { pascalCasing = new PascalCasing(); }
        public void Analyze(AnalysisConfig config, AnalysisResult result, Checker checker, SyntaxNode syntaxRoot)
        {
            var namespaceRaws = syntaxRoot.DescendantNodes().OfType<NamespaceDeclarationSyntax>();
            foreach(var namespaceRaw in namespaceRaws)
            {
                string namespaceName = namespaceRaw.Name.ToString();
                if (pascalCasing.HasDefect(namespaceName)) 
                {
                    PreOccurence preOcc = pascalCasing.MakeDefect(config, checker, namespaceRaw);
                    result.AddDefectWithPreOccurence(preOcc);
                }
            }
        }
    }
}
