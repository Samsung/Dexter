using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using DexterCS;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp;
using Microsoft.CodeAnalysis.CSharp.Symbols;
using Microsoft.CodeAnalysis.CSharp.Syntax;

namespace DexterCRC
{
    public class InterfaceCRC : ICRCLogic
    {
        PascalCasing pascalCasing;
        WithoutUnderscore underscore;
        PrefixNaming prefixNaming;

        public InterfaceCRC() {
            pascalCasing = new PascalCasing();
            underscore = new WithoutUnderscore();
            //naming = new Naming();
            prefixNaming = new PrefixNaming();
           
        }
        public void Analyze(AnalysisConfig config, AnalysisResult result, Checker checker, SyntaxNode syntaxRoot)
        {
            var interfaceRaws = syntaxRoot.DescendantNodes().OfType<InterfaceDeclarationSyntax>();
            if (!interfaceRaws.Any())
            {
                return;
            }
            foreach (var interfaceRaw in interfaceRaws)
            {
                string interfaceName = interfaceRaw.Identifier.ToString();

                if (prefixNaming.HasDefect(new NamingSet {
                    currentName = interfaceName,
                    basicWord = "I" }))
                {
                    PreOccurence preOcc = prefixNaming.MakeDefect(config, checker, interfaceRaw);
                    result.AddDefectWithPreOccurence(preOcc);
                }

                if (underscore.HasDefect(interfaceName)) // Underscore Naming
                {
                    PreOccurence preOcc = underscore.MakeDefect(config, checker, interfaceRaw);
                    result.AddDefectWithPreOccurence(preOcc);
                }

                if (pascalCasing.HasDefect(interfaceName))
                {
                    PreOccurence preOcc = pascalCasing.MakeDefect(config, checker, interfaceRaw);
                    result.AddDefectWithPreOccurence(preOcc);
                }
            }
        }
       
    }
}
