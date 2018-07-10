using System;
using System.Linq;
using DexterCS;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp.Syntax;

namespace DexterCRC
{
    public class FieldCRC : ICRCLogic
    {
        PascalCasing pascalCasing;

        public FieldCRC() {
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
                || "static protected".Equals(modifier) || "static public".Equals(modifier) );
        }
    }
}

