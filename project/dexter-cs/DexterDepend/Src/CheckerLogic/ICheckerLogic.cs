using DexterCS;
using Microsoft.CodeAnalysis.CSharp;
using System.Collections.Generic;

namespace DexterDepend
{
    public interface ICheckerLogic
    {
        string CheckerName { get; set; }
        string Description { get; set; }
        bool HasVconfMethod(List<string> list, object value);
        PreOccurence MakeVConfList(AnalysisConfig config, Checker checker, CSharpSyntaxNode raw, List<string> args);
    }
}

