using DexterCRC.Src.Util;
using DexterCS;
using Microsoft.CodeAnalysis.CSharp;
using System.Text.RegularExpressions;

namespace DexterCRC
{
    public class NounNaming : ICheckerLogic
    {
        public NounNaming()
        {
            CheckerName = this.GetType().Name;
            Description = "Use Noun or Noun phrase for a Class Name";
        }

        public string CheckerName { get; set; }
        public string Description { get; set; }

        public bool HasDefect(object value)
        {
            string name = value.ToString();

            string[] words = DexterUtil.Split(name);

            return !OpenNLPUtil.AreNouns(words);
        }

        public PreOccurence MakeDefect(AnalysisConfig config, Checker checker, CSharpSyntaxNode raw)
        {
            var lineSpan = raw.GetLocation().GetLineSpan();
            PreOccurence preOcc = DexterCRCUtil.MakePreOccurence(raw, lineSpan, checker, config, CheckerName, Description);
            return preOcc;
        }
    }
}
