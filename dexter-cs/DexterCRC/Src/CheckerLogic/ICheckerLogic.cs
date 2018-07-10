using DexterCS;
using Microsoft.CodeAnalysis.CSharp;

namespace DexterCRC
{
    public interface ICheckerLogic
    {
        string CheckerName { get; set; }
        string Description { get; set; }
        bool HasDefect(object value);
        PreOccurence MakeDefect(AnalysisConfig config, Checker checker, CSharpSyntaxNode raw);
    }
}
