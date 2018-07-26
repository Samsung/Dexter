using DexterCS;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp;
using Microsoft.CodeAnalysis.CSharp.Syntax;
using System;
using System.Collections.Generic;
using System.Linq;

namespace DexterDepend
{
    public class DexterDependUtil
    {
        public static string ToolName { get; set; }

        internal static string GetClassName(IEnumerable<TypeDeclarationSyntax> typeEnumerable)
        {
            string className = null;
            try
            {
                className = typeEnumerable.First().Identifier.ToString();
            }
            catch (InvalidOperationException)
            {
                className = "";
            }
            return className;
        }

        private static string GetMethodName(IEnumerable<MethodDeclarationSyntax> methodEnumrable)
        {
            string methodName = null;
            try
            {
                methodName = methodEnumrable.First().Identifier.ToString();
            }
            catch (InvalidOperationException)
            {
                methodName = "";
            }
            return methodName;
        }
        internal static PreOccurence MakePreOccurence(CSharpSyntaxNode raw, FileLinePositionSpan lineSpan,
            List<string> args, Checker checker, AnalysisConfig config, string baseCheckerName, string baseDescription)
        {
            PreOccurence preOcc = new PreOccurence();
            preOcc.CheckerCode = checker.Code + baseCheckerName;
            preOcc.FileName = config.FileName;
            preOcc.ModulePath = config.ModulePath;
            preOcc.ClassName = GetClassName(raw.AncestorsAndSelf().OfType<ClassDeclarationSyntax>());
            preOcc.MethodName = GetMethodName(raw.AncestorsAndSelf().OfType<MethodDeclarationSyntax>());
            preOcc.Language = config.GetLanguageEnum().ToString();
            preOcc.SeverityCode = checker.SeverityCode;
            preOcc.CategoryName = checker.CategoryName;
            preOcc.Message = GetOccurenceMessage(baseDescription, checker.Name);
            preOcc.ToolName = ToolName;
            preOcc.StartLine = lineSpan.StartLinePosition.Line + 1;
            preOcc.EndLine = lineSpan.EndLinePosition.Line + 1;
            preOcc.CharStart = lineSpan.StartLinePosition.Character;
            preOcc.CharEnd = lineSpan.EndLinePosition.Character;
            preOcc.VariableName = (!string.IsNullOrEmpty(args[0])) ? args[0] : "";
            preOcc.StringValue = (!string.IsNullOrEmpty(args[1])) ? args[1] : "";
            return preOcc;
        }
        private static string GetOccurenceMessage(string baseName, string name)
        {
            return baseName + name;
        }
    }
}
