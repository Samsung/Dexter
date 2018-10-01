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
using Microsoft.CodeAnalysis.CSharp.Syntax;
using System.Collections.Generic;
using System.Linq;

namespace DexterDepend
{
    public class VconfETC : IDependLogic
    {
        VconfMethod vconfMethod = new VconfMethod();
        public VconfETC() { }
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

                    List<string> declaredVariables = syntaxRoot.DescendantNodes().OfType<FieldDeclarationSyntax>().Select(d => d.Declaration.Variables.ToString()).ToList();
                    var resultList = declaredVariables.Where(r => r.StartsWith(args[0]));
                    foreach (string s in resultList)
                    {
                        string[] declaredArr = s.Split('=');
                        if (declaredArr.Length == 2)
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
