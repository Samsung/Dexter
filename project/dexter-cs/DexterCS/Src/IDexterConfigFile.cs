using System.Collections.Generic;

namespace DexterCS
{
    public interface IDexterConfigFile
    {
        string DexterHome { get; set; }
        void LoadFromFile(string dexterConfig);
        AnalysisConfig ToAnalysisConfig();
        List<string> GenerateSourceFileFullPathList();
    }
}