using DexterCS;
using Microsoft.CodeAnalysis.CSharp;

namespace DexterCRC
{
    public class CamelCasing : ICheckerLogic
    {
        public CamelCasing()
        {
            CheckerName = this.GetType().Name;
            Description = "Use Camel Casing";
        }

        public string CheckerName { get; set; }
        public string Description { get; set; }

        public bool HasDefect(object value)
        {
            string name = value.ToString();
            if (name.Equals("_"))
            {
                return false;
            }
            return !(name[0] >= 'a' && name[0] <= 'z');
        }

        public PreOccurence MakeDefect(AnalysisConfig config, Checker checker, CSharpSyntaxNode raw)
        {
            var lineSpan = raw.GetLocation().GetLineSpan();
            PreOccurence preOcc = DexterCRCUtil.MakePreOccurence(raw, lineSpan, checker, config, CheckerName, Description);
            return preOcc;
        }
    }
}
