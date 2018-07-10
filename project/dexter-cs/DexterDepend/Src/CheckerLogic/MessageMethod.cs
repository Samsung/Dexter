using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using DexterCS;
using Microsoft.CodeAnalysis.CSharp;

namespace DexterDepend
{
    public class MessageMethod : ICheckerLogic
    {
        public MessageMethod()
        {
            CheckerName = this.GetType().Name;
            Description = "Use Message Port";
        }
        public string CheckerName { get; set; }
        public string Description { get; set; }

        public bool HasVconfMethod(List<string> subMethodList, object value)
        {
            var statement = value.ToString();
            return subMethodList.Any(statement.StartsWith);
        }

        public PreOccurence MakeVConfList(AnalysisConfig config, Checker checker, CSharpSyntaxNode raw, List<string> args)
        {
            var lineSpan = raw.GetLocation().GetLineSpan();
            PreOccurence preOcc = DexterDependUtil.MakePreOccurence(raw, lineSpan, args, checker, config, CheckerName, Description);
            return preOcc;
        }
    }
}
