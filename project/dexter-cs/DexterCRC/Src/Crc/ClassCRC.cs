using System.Linq;
using DexterCS;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp.Symbols;
using Microsoft.CodeAnalysis.CSharp.Syntax;
using System.Linq;

namespace DexterCRC
{
    public class ClassCRC : ICRCLogic
    {
        WithoutUnderscore underscore;
        PascalCasing pascalCasing;
        SuffixNaming suffixNaming;
        NounNaming nounNaming;

        public ClassCRC()
        {
            underscore = new WithoutUnderscore();
            pascalCasing = new PascalCasing();
            suffixNaming = new SuffixNaming();
            nounNaming = new NounNaming();
        }

        public void Analyze(AnalysisConfig config, AnalysisResult result, Checker checker, SyntaxNode syntaxRoot)
        {
            var classRaws = syntaxRoot.DescendantNodes().OfType<ClassDeclarationSyntax>();
            if (!classRaws.Any())
            {
                return;
            }

            foreach (var classRaw in classRaws)
            {
                string className = classRaw.Identifier.ToString();
                if (underscore.HasDefect(className))
                {
                    PreOccurence preOcc = underscore.MakeDefect(config, checker, classRaw);
                    result.AddDefectWithPreOccurence(preOcc);
                }
                if (pascalCasing.HasDefect(className))
                {
                    PreOccurence preOcc = pascalCasing.MakeDefect(config, checker, classRaw);
                    result.AddDefectWithPreOccurence(preOcc);
                }
                if (nounNaming.HasDefect(className))
                {
                    PreOccurence preOcc = nounNaming.MakeDefect(config, checker, classRaw);
                    result.AddDefectWithPreOccurence(preOcc);
                }

                if (classRaw.BaseList == null)
                {
                    continue;
                }
                else if (HasBaseTypeNamingDefect(className, classRaw.BaseList.Types))
                {
                    PreOccurence preOcc = suffixNaming.MakeDefect(config, checker, classRaw);
                    result.AddDefectWithPreOccurence(preOcc);
                }
            }
        }

        private bool HasBaseTypeNamingDefect(string className, SeparatedSyntaxList<BaseTypeSyntax> baseTypes)
        {
            foreach (var baseType in baseTypes)
            {
                if (CheckEventNaming(className, baseType.Type.ToString()))
                {
                    return true;
                }
                if (CheckAttributeNaming(className, baseType.Type.ToString()))
                {
                    return true;
                }
            }
            return false;
        }

        public bool CheckAttributeNaming(string className, string baseName)
        {
            return (DexterCRCUtil.HasSuffix(baseName, DexterCRCUtil.ATTRIBUTE_CLASS_SUFFIX)
                && !DexterCRCUtil.HasSuffix(className, DexterCRCUtil.ATTRIBUTE_CLASS_SUFFIX));
        }

        public bool CheckEventNaming(string className, string baseName)
        {
            return (DexterCRCUtil.HasSuffix(baseName, DexterCRCUtil.EVENT_CLASS_SUFFIX)
                    && !DexterCRCUtil.HasSuffix(className, DexterCRCUtil.EVENT_CLASS_SUFFIX));
        }
    }
}


