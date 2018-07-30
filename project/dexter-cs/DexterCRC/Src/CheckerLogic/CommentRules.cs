using DexterCS;
using Microsoft.CodeAnalysis.CSharp;
using System;

namespace DexterCRC.Src.CheckerLogic
{
    public class CommentRules : ICheckerLogic
    {
        public CommentRules()
        {
            CheckerName = this.GetType().Name;
            Description = "Class/Interface Comment begins with “///”, and use <summary> tag";
        }

        public string CheckerName { get; set; }
        public string Description { get; set; }

        public bool HasDefect(object value)
        {
            string statement = value.ToString();
            return !statement.StartsWith("///", StringComparison.Ordinal)
                    || !statement.Contains("<summary>");
        }

        public PreOccurence MakeDefect(AnalysisConfig config, Checker checker, CSharpSyntaxNode raw)
        {
            var lineSpan = raw.GetLocation().GetLineSpan();
            PreOccurence preOcc = DexterCRCUtil.MakePreOccurence(raw, lineSpan, checker, config, CheckerName, Description);
            return preOcc;
        }
    }
}
