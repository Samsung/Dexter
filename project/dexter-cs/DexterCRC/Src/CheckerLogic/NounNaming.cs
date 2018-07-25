using DexterCRC.Src.Util;
using DexterCS;
using Microsoft.CodeAnalysis.CSharp;

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

            string[] words = SplitOnCamelCase(name);

            return !OpenNLPUtil.IsNoun(words);
        }

        public PreOccurence MakeDefect(AnalysisConfig config, Checker checker, CSharpSyntaxNode raw)
        {
            var lineSpan = raw.GetLocation().GetLineSpan();
            PreOccurence preOcc = DexterCRCUtil.MakePreOccurence(raw, lineSpan, checker, config, CheckerName, Description);
            return preOcc;
        }

        public string[] SplitOnCamelCase(string input)
        {
            return System.Text.RegularExpressions.Regex.Replace(input, "([A-Z])", " $1", System.Text.RegularExpressions.RegexOptions.Compiled).Trim().Split(' ');
        }
    }
}
