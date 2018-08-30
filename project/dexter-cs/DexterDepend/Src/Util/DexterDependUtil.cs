#region Copyright notice
/**
 * Copyright (c) 2018 Samsung Electronics, Inc.,
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
#endregion
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
