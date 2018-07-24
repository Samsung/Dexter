using DexterCS;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp.Syntax;
using System.Collections.Generic;
using System.Linq;

namespace DexterDepend
{
    public class VconfGet : IDependLogic
    {
        VconfMethod vconfMethod = new VconfMethod();
        public VconfGet() { }
        public void Analyze(AnalysisConfig config, AnalysisResult result, Checker checker, SyntaxNode syntaxRoot)
        {
            var statementRaws = syntaxRoot.DescendantNodes().OfType<InvocationExpressionSyntax>();
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
                if (vconfMethod.HasVconfMethod(subMethodList, statement))
                {
                    List<string> args = statementRaw.ArgumentList.Arguments.Select(s => s.ToString().Replace("\"", "")).ToList();
                    args.Add(""); // Vconf get only 1 arg

                    List<string> declaredVariables = syntaxRoot.DescendantNodes().OfType<FieldDeclarationSyntax>().Select(d => d.Declaration.Variables.ToString()).ToList();
                    var resultList = declaredVariables.Where(r => r.StartsWith(args[0]));
                    foreach (string s in resultList)
                    {
                        string[] declaredArr = s.Split('=');
                        if(declaredArr.Length == 2)
                        {
                            args[0] = declaredArr[1].Trim().Replace("\"", "");
                        }
                    }
                    PreOccurence preOcc = vconfMethod.MakeVConfList(config, checker, statementRaw, args);
                    result.AddDefectWithPreOccurence(preOcc);
                }
            }
        }
    }
}
