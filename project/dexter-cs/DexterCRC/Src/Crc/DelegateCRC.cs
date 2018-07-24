using DexterCS;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp.Syntax;
using System.Linq;

namespace DexterCRC
{
    public class DelegateCRC : ICRCLogic
    {
        PascalCasing pascalCasing;
        SuffixNaming suffixNaming;

        public DelegateCRC()
        {
            pascalCasing = new PascalCasing();
            suffixNaming = new SuffixNaming();
        }
        public void Analyze(AnalysisConfig config, AnalysisResult result, Checker checker, SyntaxNode syntaxRoot)
        {
            var delegateRaws = syntaxRoot.DescendantNodes().OfType<DelegateDeclarationSyntax>();
            foreach (var delegateRaw in delegateRaws)
            {
                var delegateName = delegateRaw.Identifier.ToString();

                if (pascalCasing.HasDefect(delegateName))
                {
                    PreOccurence preOcc = pascalCasing.MakeDefect(config, checker, delegateRaw);
                    result.AddDefectWithPreOccurence(preOcc);
                }

                if (!suffixNaming.HasDefect(new NamingSet
                {
                    currentName = delegateName,
                    basicWord = DexterCRCUtil.DELEGATE_SUFFIX
                }))
                {
                    PreOccurence preOcc = suffixNaming.MakeDefect(config, checker, delegateRaw);
                    result.AddDefectWithPreOccurence(preOcc);
                }
            }
        }
    }
}
