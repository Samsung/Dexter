#region Copyright notice
/**
 * Copyright (c) 2018 Samsung Electronics, Inc.,
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
#endregion
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


