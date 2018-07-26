using DexterCS;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp;
using Microsoft.CodeAnalysis.CSharp.Syntax;
using System;
using System.Collections.Generic;
using System.Linq;

namespace DexterCRC
{
    public class DexterCRCUtil
    {
        public const string DELEGATE_SUFFIX = "Delegate";
        public const string EVENT_CLASS_SUFFIX = "EventArgs";
        public const string EVENT_TYPE_SUFFIX = "EventHandler";
        public const string ATTRIBUTE_CLASS_SUFFIX = "Attribute";
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
            Checker checker, AnalysisConfig config, string baseCheckerName, string baseDescription)
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
            preOcc.Message = GetOccurenceMessage(baseDescription, checker.Description);
            preOcc.ToolName = ToolName;
            preOcc.StartLine = lineSpan.StartLinePosition.Line + 1;
            preOcc.EndLine = lineSpan.EndLinePosition.Line + 1;
            preOcc.CharStart = lineSpan.StartLinePosition.Character;
            preOcc.CharEnd = lineSpan.EndLinePosition.Character;
            preOcc.VariableName = "";
            preOcc.StringValue = "";
            return preOcc;
        }

        private static string GetOccurenceMessage(string baseName, string description)
        {
            return baseName + description;
        }

        public static bool HasPrefix(string name, string prefix)
        {
            return (!string.IsNullOrEmpty(name) &&
                !name.StartsWith(prefix, StringComparison.CurrentCulture));
        }

        public static bool HasSuffix(string name, string suffix)
        {
            return name.EndsWith(suffix, StringComparison.Ordinal);
        }
    }
}
