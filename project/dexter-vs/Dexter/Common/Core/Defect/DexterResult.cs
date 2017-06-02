using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Dexter.Common.Defect
{
    /// <summary>
    /// Container of dexter defects for one file
    /// </summary>
    public class DexterResult
    {
        [JsonProperty("snapshotId")]
        public string SnapshotId { get; set; }
        [JsonProperty("fileName")]
        public string FileName { get; set; }
        [JsonProperty("fullFilePath")]
        public string FullFilePath { get; set; }
        [JsonProperty("groupId")]
        public string GroupId { get; set; }
        [JsonProperty("defectCount")]
        public int DefectCount { get; set; }
        [JsonProperty("defectList")]
        public IList<DexterDefect> DefectList { get; set; }

        public DexterResult()
        {
            SnapshotId = "";
            FileName = "";
            FullFilePath = "";
            GroupId = "";
            DefectCount = 0;
            DefectList = null;
        }
    }

    /// <summary>
    /// Provides dexter defect information
    /// </summary>
    public class DexterDefect
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
        public IList<DexterOccurence> Occurences { get; set; }
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
        [JsonProperty("fileName")]
        public string FileName { get; set; }
        [JsonProperty("fileStatus")]
        public string FileStatus { get; set; }
        [JsonProperty("modulePath")]
        public string ModulePath { get; set; }

        public DexterDefect()
        {
            Message = "";
            SeverityCode = "";
            CategoryName = "";
            AnalysisType = "";
            CheckerCode = "";
            ClassName = "";
            MethodName = "";
            ToolName = "";
            Language = "";
            FileName = "";
            FileStatus = "";
            ModulePath = "";
        }
    }

    /// <summary>
    /// Provides defect occurence information
    /// </summary>
    public class DexterOccurence
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

        public DexterOccurence()
        {
            Code = "";
            VariableName = "";
            StringValue = "";
            FieldName = "";
            Message = "";
        }
    }
}
