using System.Collections.Generic;

namespace DexterCS
{
    public interface IDexterPluginManager
    {
        void InitDexterPlugins();
        List<AnalysisResult> Analyze(AnalysisConfig config);

    }
}