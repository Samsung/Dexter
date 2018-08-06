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
using Microsoft.CodeAnalysis.CSharp;

namespace DexterCRC
{
    public class PrefixNaming : ICheckerLogic
    {
        public PrefixNaming()
        {
            CheckerName = this.GetType().Name;
            Description = "Check Prefix";
        }
        public string CheckerName { get; set; }
        public string Description { get; set; }

        public bool HasDefect(object value)
        {
            var namingSet = (NamingSet)value;
            return !DexterCRCUtil.HasPrefix(namingSet.currentName, namingSet.basicWord);
        }

        public PreOccurence MakeDefect(AnalysisConfig config, Checker checker, CSharpSyntaxNode raw)
        {
            var lineSpan = raw.GetLocation().GetLineSpan();
            PreOccurence preOcc = DexterCRCUtil.MakePreOccurence(raw, lineSpan, checker, config, CheckerName, Description);
            return preOcc;
        }
    }
}
