using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

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
