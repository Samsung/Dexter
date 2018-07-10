using DexterCS;
using Microsoft.CodeAnalysis;

namespace DexterCRC
{
    public interface ICRCLogic
    {
        void Analyze(AnalysisConfig config, AnalysisResult result, Checker checker, SyntaxNode syntaxRoot);
    }
}