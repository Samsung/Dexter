using DexterCS;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace DexterCRC.Src.CheckerLogic
{
    public class MethodSpacing : ICheckerLogic
    {
        public MethodSpacing()
        {
            CheckerName = this.GetType().Name;
            Description = "Use single blank line between methods in class.";
        }

        public string CheckerName { get; set; }
        public string Description { get; set; }

        public bool HasDefect(object value)
        {
            SyntaxTriviaList trivia = (SyntaxTriviaList)value;
            return !ContainsOneBlankLine(trivia);
        }

        public PreOccurence MakeDefect(AnalysisConfig config, Checker checker, CSharpSyntaxNode raw)
        {

            var lineSpan = raw.GetLocation().GetLineSpan();
            PreOccurence preOcc = DexterCRCUtil.MakePreOccurence(raw, lineSpan, checker, config, CheckerName, Description);
            return preOcc;
        }

        private bool ContainsOneBlankLine(SyntaxTriviaList syntaxTriviaList)
        {
            int endOfLineCount = syntaxTriviaList.Count(syntaxTrivia => syntaxTrivia.IsKind(SyntaxKind.EndOfLineTrivia));
            int singleCommentLineCount = syntaxTriviaList.Count(syntaxTrivia => syntaxTrivia.IsKind(SyntaxKind.SingleLineCommentTrivia));
            int multiCommentLineCount = syntaxTriviaList.Count(syntaxTrivia => syntaxTrivia.IsKind(SyntaxKind.MultiLineCommentTrivia));

            // Counting only the "blank lines", so lines with comments do not count
            return endOfLineCount - singleCommentLineCount - multiCommentLineCount == 1;
        }
    }
}
