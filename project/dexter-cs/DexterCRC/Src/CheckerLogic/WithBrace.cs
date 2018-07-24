using DexterCS;
using Microsoft.CodeAnalysis.CSharp;
using System;

namespace DexterCRC
{
    public class WithBrace : ICheckerLogic
    {
        public WithBrace() {
            CheckerName = this.GetType().Name;
            Description = "Even though statement are a single line, brace should be used";
        }

        public string CheckerName { get; set; }
        public string Description { get; set; }

        public bool HasDefect(object value)
        {
            string statement = value.ToString();
            return !statement.StartsWith("{", StringComparison.Ordinal)
                    && !statement.EndsWith("}", StringComparison.Ordinal);
        }

        public PreOccurence MakeDefect(AnalysisConfig config, Checker checker, CSharpSyntaxNode raw)
        {
            var lineSpan = raw.GetLocation().GetLineSpan();
            PreOccurence preOcc = DexterCRCUtil.MakePreOccurence(raw, lineSpan, checker, config, CheckerName, Description);
            return preOcc;
        }
    }
}
