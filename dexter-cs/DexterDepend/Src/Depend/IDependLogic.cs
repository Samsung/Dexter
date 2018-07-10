using DexterCS;
using Microsoft.CodeAnalysis;

namespace DexterDepend
{
    public interface IDependLogic
    {
        void Analyze(AnalysisConfig config, AnalysisResult result, Checker checker, SyntaxNode syntaxRoot);
    }
}
