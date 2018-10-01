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
using Newtonsoft.Json;
using System.Collections.Generic;

namespace DexterCS
{
    [JsonObject(MemberSerialization.OptIn)]
    public class ResultJsonParsing
    {
        [JsonProperty("snapshotId")]
        public long SnapshotId { get; set; }
        [JsonProperty("modulePath")]
        public string ModulePath { get; set; }
        [JsonProperty("fileName")]
        public string FileName { get; set; }
        [JsonProperty("fullFilePath")]
        public string FullFilePath { get; set; }
        [JsonProperty("groupId")]
        public string GroupId { get; set; }
        [JsonProperty("projectName")]
        public string ProjectName { get; set; }
        [JsonProperty("defectCount")]
        public int defectCount { get; set; }
        [JsonProperty("defectList")]
        public IList<DefectJsonParsing> DefectList { get; set; }
    }

    [JsonObject(MemberSerialization.OptIn)]
    public class DefectJsonParsing
    {
        [JsonProperty("message")]
        public string Message { get; set; }
        [JsonProperty("severityCode")]
        public string SeverityCode { get; set; }
        [JsonProperty("categoryName")]
        public string CategoryName { get; set; }
        [JsonProperty("analysisType")]
        public string AnalysisType { get; set; }
        [JsonProperty("occurences")]
        public IList<OccurenceJsonParsing> Occurences { get; set; }
        [JsonProperty("checkerCode")]
        public string CheckerCode { get; set; }
        [JsonProperty("className")]
        public string ClassName { get; set; }
        [JsonProperty("methodName")]
        public string MethodName { get; set; }
        [JsonProperty("toolName")]
        public string ToolName { get; set; }
        [JsonProperty("language")]
        public string Language { get; set; }
        [JsonProperty("fileStatus")]
        public string FileStatus { get; set; }
        [JsonProperty("modulePath")]
        public string ModulePath { get; set; }
        [JsonProperty("fileName")]
        public string FileName { get; set; }
    }

    [JsonObject(MemberSerialization.OptIn)]
    public class OccurenceJsonParsing
    {
        [JsonProperty("code")]
        public string Code { get; set; }
        [JsonProperty("startLine")]
        public int StartLine { get; set; }
        [JsonProperty("endLine")]
        public int EndLine { get; set; }
        [JsonProperty("charStart")]
        public int CharStart { get; set; }
        [JsonProperty("charEnd")]
        public int CharEnd { get; set; }
        [JsonProperty("variableName")]
        public string VariableName { get; set; }
        [JsonProperty("stringValue")]
        public string StringValue { get; set; }
        [JsonProperty("fieldName")]
        public string FieldName { get; set; }
        [JsonProperty("message")]
        public string Message { get; set; }
    }
}
