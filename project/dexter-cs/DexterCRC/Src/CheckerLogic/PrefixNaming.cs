using DexterCS;
using Microsoft.CodeAnalysis.CSharp;

namespace DexterCRC
{
    public class PrefixNaming : ICheckerLogic
    {
        public PrefixNaming()
        {
            CheckerName = this.GetType().Name;
            Description = "Check Prefix";
        }
        public string CheckerName { get; set; }
        public string Description { get; set; }

        public bool HasDefect(object value)
        {
            var namingSet = (NamingSet)value;
            return !DexterCRCUtil.HasPrefix(namingSet.currentName, namingSet.basicWord);
        }

        public PreOccurence MakeDefect(AnalysisConfig config, Checker checker, CSharpSyntaxNode raw)
        {
            var lineSpan = raw.GetLocation().GetLineSpan();
            PreOccurence preOcc = DexterCRCUtil.MakePreOccurence(raw, lineSpan, checker, config, CheckerName, Description);
            return preOcc;
        }
    }
}
