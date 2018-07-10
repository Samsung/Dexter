using DexterCS;
using Microsoft.CodeAnalysis.CSharp;

namespace DexterCRC
{
    public class PascalCasing : ICheckerLogic
    {
        public PascalCasing() {
            CheckerName = this.GetType().Name;
            Description = "Use Pascal Casing";
        }

        public string CheckerName { get; set; }
        public string Description { get; set; }
        public bool HasDefect(object value)
        {
            string name = value.ToString();
            return !(name[0] >= 'A' && name[0] <= 'Z');
        }

        public PreOccurence MakeDefect(AnalysisConfig config, Checker checker, CSharpSyntaxNode raw)
        {
            var lineSpan = raw.GetLocation().GetLineSpan();
            PreOccurence preOcc = DexterCRCUtil.MakePreOccurence(raw, lineSpan, checker, config, CheckerName, Description);
            return preOcc;
        }
    }
}
