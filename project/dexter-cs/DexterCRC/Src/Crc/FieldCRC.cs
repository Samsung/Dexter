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
using Microsoft.CodeAnalysis.CSharp.Syntax;
using System.Linq;

namespace DexterCRC
{
    public class FieldCRC : ICRCLogic
    {
        PascalCasing pascalCasing;

        public FieldCRC()
        {
            pascalCasing = new PascalCasing();
        }
        public void Analyze(AnalysisConfig config, AnalysisResult result, Checker checker, SyntaxNode syntaxRoot)
        {
            var baseFieldRaws = syntaxRoot.DescendantNodes().OfType<FieldDeclarationSyntax>();
            foreach (var fieldRaw in baseFieldRaws)
            {
                if (HasFieldModifier(fieldRaw.Modifiers)
                    && HasCamelCasingDefect(fieldRaw.Declaration.Variables))
                {
                    PreOccurence preOcc = pascalCasing.MakeDefect(config, checker, fieldRaw);
                    result.AddDefectWithPreOccurence(preOcc);
                }
            }
        }

        private bool HasCamelCasingDefect(SeparatedSyntaxList<VariableDeclaratorSyntax> variables)
        {
            foreach (var variable in variables)
            {
                if (pascalCasing.HasDefect(variable.Identifier.ToString()))
                {
                    return true;
                }
            }
            return false;
        }

        private bool HasFieldModifier(SyntaxTokenList modifiers)
        {
            foreach (var modifier in modifiers)
            {
                if (HasInvalidModifier(modifier.ToString()))
                {
                    return true;
                }
            }
            return false;
        }

        public bool HasInvalidModifier(string modifier)
        {
            modifier = modifier.Trim();
            return ("protected static".Equals(modifier) || "public static".Equals(modifier)
                || "static protected".Equals(modifier) || "static public".Equals(modifier));
        }
    }
}

