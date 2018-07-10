using System.Collections.Generic;
using System.Linq;
using DexterCS;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp;
using Microsoft.CodeAnalysis.CSharp.Syntax;

namespace DexterDepend
{
    public class VconfMethod : ICheckerLogic
    {
        public VconfMethod()
        {
            CheckerName = this.GetType().Name;
            Description = "Use VConf Method";
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
