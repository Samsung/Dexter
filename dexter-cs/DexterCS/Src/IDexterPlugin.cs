using System;

namespace DexterCS
{
    public interface IDexterPlugin
    {
        string PLUGIN_NAME { get; }
        string PLUGIN_DESCRIPTION { get; }
        string PLUGIN_AUTHOR { get; }
        Version VERSION { get; }

        void Dispose();

        void Init();
        bool SupportLanguage(DexterConfig.LANGUAGE language);
        string[] SupportingFileExtensions { get; }
        AnalysisResult Analyze(AnalysisConfig config);
    }
}