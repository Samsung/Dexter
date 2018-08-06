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
namespace DexterCS
{
    public class PreOccurence : BaseDefect
    {
        public string OccurenceCode { get; set; }
        public int CharStart { get; set; }
        public int CharEnd { get; set; }
        public int StartLine { get; set; }
        public int EndLine { get; set; }
        public string VariableName { get; set; }
        public string StringValue { get; set; }
        public string FieldName { get; set; }
        public string Message { get; set; }
        public string SeverityCode { get; set; }
        public string CategoryName { get; set; }

        internal Occurence ToOccurence()
        {
            Occurence occurence = new Occurence();
            occurence.StartLine = StartLine;
            occurence.EndLine = EndLine;
            occurence.FieldName = FieldName;
            occurence.CharStart = CharStart;
            occurence.CharEnd = CharEnd;
            occurence.Message = Message;
            occurence.StringValue = StringValue;
            occurence.VariableName = VariableName;
            occurence.Code = OccurenceCode;

            return occurence;
        }

        internal Defect ToDefect()
        {
            Defect defect = new Defect();
            defect.CheckerCode = CheckerCode;
            defect.ClassName = ClassName;
            defect.FileName = FileName;
            defect.ModulePath = ModulePath;
            defect.MethodName = MethodName;
            defect.SeverityCode = SeverityCode;
            defect.CategoryName = CategoryName;
            defect.Language = Language;
            defect.ToolName = ToolName;

            return defect;
        }
    }
}
