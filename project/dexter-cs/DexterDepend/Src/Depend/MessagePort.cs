using DexterCS;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp.Syntax;
using System.Collections.Generic;
using System.Linq;

namespace DexterDepend
{
    public class MessagePort : IDependLogic
    {
        MessageMethod messageMethod = new MessageMethod();
        public MessagePort() { }
        public void Analyze(AnalysisConfig config, AnalysisResult result, Checker checker, SyntaxNode syntaxRoot)
        {
            var statementRaws = syntaxRoot.DescendantNodes().OfType<ObjectCreationExpressionSyntax>();
            if (!statementRaws.Any())
            {
                return;
            }

            char delimiters = ',';
            string[] subMethod = checker.Properties.Value.Split(delimiters);
            List<string> subMethodList = new List<string>();
            subMethodList.AddRange(subMethod);

            foreach (var statementRaw in statementRaws)
            {
                string statement = statementRaw.ToString();
                if (messageMethod.HasVconfMethod(subMethodList, statement))
                {
                    List<string> args = new List<string>(2) { "", "" };
                    PreOccurence preOcc = messageMethod.MakeVConfList(config, checker, statementRaw, args);
                    result.AddDefectWithPreOccurence(preOcc);
                }
            }
        }
    }
}
