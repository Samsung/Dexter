using System.Collections.Generic;
using System.IO;

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