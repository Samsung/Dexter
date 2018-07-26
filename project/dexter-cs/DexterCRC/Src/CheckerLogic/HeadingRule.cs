using DexterCS;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DexterCRC.Src.CheckerLogic
{
    public class HeadingRule : ICheckerLogic
    {
        public HeadingRule()
        {
            CheckerName = this.GetType().Name;
            Description = "Describe brief information about the file under a heading comment block";
        }
       
        public string CheckerName { get; set; }
        public string Description { get; set; }

        public bool HasDefect(object value)
        {
            SyntaxTriviaList trivia = (SyntaxTriviaList)value;
            return !(trivia.ToList().ToString().Contains("Copyright") && trivia.ToString().Contains("Samsung Electronics Co., Ltd All Rights Reserved"));                  
        }
        
        public PreOccurence MakeDefect(AnalysisConfig config, Checker checker, CSharpSyntaxNode raw)
        {
            var lineSpan = raw.GetLocation().GetLineSpan();
            PreOccurence preOcc = DexterCRCUtil.MakePreOccurence(raw, lineSpan, checker, config, CheckerName, Description);
            return preOcc;
        }
        
    }
}
