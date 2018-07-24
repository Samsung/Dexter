using DexterCS;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp.Syntax;
using System.Collections.Generic;
using System.Linq;

namespace DexterCRC
{
    public class EventCRC : ICRCLogic
    {
        SuffixNaming suffixNaming;
        PascalCasing pascalCasing;

        public EventCRC() {
            suffixNaming = new SuffixNaming();
            pascalCasing = new PascalCasing();
        }
        public void Analyze(AnalysisConfig config, AnalysisResult result, Checker checker, SyntaxNode syntaxRoot)
        {
            var eventRaws = syntaxRoot.DescendantNodes().OfType<EventFieldDeclarationSyntax>();
            foreach(var eventRaw in eventRaws)
            {
                string eventTypeName = eventRaw.Declaration.Type.ToString();
                if (suffixNaming.HasDefect(new NamingSet {
                    currentName = eventTypeName,
                    basicWord = DexterCRCUtil.EVENT_TYPE_SUFFIX
                })) 
                {
                    PreOccurence preOcc = suffixNaming.MakeDefect(config, checker, eventRaw);
                    result.AddDefectWithPreOccurence(preOcc);
                }
                
                List<string> variables = GetCamelCasingVariables(eventRaw.Declaration.Variables);
                foreach(string variable in variables)
                {
                    PreOccurence preOcc = pascalCasing.MakeDefect(config, checker, eventRaw);
                    result.AddDefectWithPreOccurence(preOcc);
                }
            }
        }

        private List<string> GetCamelCasingVariables(SeparatedSyntaxList<VariableDeclaratorSyntax> variables)
        {
            List<string> tempVariables = new List<string>();
            foreach (var variable in variables )
            {
                if (pascalCasing.HasDefect(variable.Identifier.ToString()))
                {
                    tempVariables.Add(variable.Identifier.ToString());
                }
            }
            return tempVariables;
        }
    }
}
