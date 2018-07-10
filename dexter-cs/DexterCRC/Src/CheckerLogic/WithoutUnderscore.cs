using DexterCS;
using Microsoft.CodeAnalysis.CSharp;

namespace DexterCRC
{
    public class WithoutUnderscore : ICheckerLogic
    {
        public WithoutUnderscore() {
            CheckerName = this.GetType().Name;
            Description = "Without Underscore";
        }
        public string CheckerName { get; set; }
        public string Description { get; set; }

        public bool HasDefect(object value)
        {
            string name = value.ToString();
            return !string.IsNullOrEmpty(name) && name.Contains("_");
        }

        public PreOccurence MakeDefect(AnalysisConfig config, Checker checker, CSharpSyntaxNode raw)
        {
            var lineSpan = raw.GetLocation().GetLineSpan();
            PreOccurence preOcc = DexterCRCUtil.MakePreOccurence(raw, lineSpan, checker, config, CheckerName, Description);
            return preOcc;
        }
    }
}
