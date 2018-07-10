using DexterCS;
using Microsoft.CodeAnalysis.CSharp;

namespace DexterCRC
{
    public class SuffixNaming : ICheckerLogic
    {
        public SuffixNaming() {
            CheckerName = this.GetType().Name;
            Description = "Check Suffix";
        }
        public string CheckerName { get; set; }
        public string Description { get; set; }
        public bool HasDefect(object value)
        {
            var namingSet = (NamingSet)value;
            return !DexterCRCUtil.HasSuffix(namingSet.currentName, namingSet.basicWord);
        }

        public PreOccurence MakeDefect(AnalysisConfig config, Checker checker, CSharpSyntaxNode raw)
        {
            var lineSpan = raw.GetLocation().GetLineSpan();
            var baseCheckerName = this.GetType().Name;
            PreOccurence preOcc = DexterCRCUtil.MakePreOccurence(raw, lineSpan, checker, config, CheckerName, Description);
            return preOcc;
        }
    }
}
