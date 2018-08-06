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
using System.Collections.Generic;

namespace DexterCS
{
    public class CheckerConfig : ICheckerConfig
    {
        public DexterConfig.LANGUAGE Language { get; set; }

        public string ToolName { get; set; }


        public Dictionary<string, string> properties = new Dictionary<string, string>();
        public Dictionary<string, string> Properties { get; set; }

        private List<Checker> checkerList = new List<Checker>();
        public List<Checker> CheckerList { get { return checkerList; } }
        public void AddCheckerList(Checker checker)
        {
            checkerList.Add(checker);
        }

        public CheckerConfig(string toolName, DexterConfig.LANGUAGE language)
        {
            ToolName = toolName;
            Language = language;
        }

        public bool IsActiveChecker(string checkerCode)
        {
            IChecker checker = GetChecker(checkerCode);
            return checker.IsActive;
        }

        public IChecker GetChecker(string checkerCode)
        {
            IChecker checker = CheckerList.Find(c => c.Code == checkerCode);
            return checker == null ? new EmptyChecker() : checker;
        }
    }
}