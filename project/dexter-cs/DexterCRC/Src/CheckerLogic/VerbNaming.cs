using DexterCRC.Src.Util;
using DexterCS;
using Microsoft.CodeAnalysis.CSharp;

namespace DexterCRC
{
    public class VerbNaming : ICheckerLogic
    {
        public VerbNaming()
        {
            CheckerName = this.GetType().Name;
            Description = "Use Verb or Verb phrase for a Method Name";
        }

        public string CheckerName { get; set; }
        public string Description { get; set; }

        public bool HasDefect(object value)
        {
            string name = value.ToString();

            string[] words = DexterUtil.Split(name);

            return !OpenNLPUtil.IsVerbPhrase(words);
        }

        public PreOccurence MakeDefect(AnalysisConfig config, Checker checker, CSharpSyntaxNode raw)
        {
            var lineSpan = raw.GetLocation().GetLineSpan();
            PreOccurence preOcc = DexterCRCUtil.MakePreOccurence(raw, lineSpan, checker, config, CheckerName, Description);
            return preOcc;
        }
    }
}
