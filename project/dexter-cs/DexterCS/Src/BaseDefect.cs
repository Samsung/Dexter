using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DexterCS
{
    public class BaseDefect : TargetFile
    {
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


        public override int GetHashCode()
        {
            return base.GetHashCode();
        }

        public override bool Equals(object obj)
        {
            if(obj == null)
            {
                return false;
            }

            BaseDefect other = obj as BaseDefect;
            if((object)other == null)
            {
                return false;
            }

            if (!Object.Equals(CheckerCode, other.CheckerCode))
            {
                return false;
            }
            if (!string.IsNullOrEmpty(MethodName) && !Object.Equals(MethodName, other.MethodName))
            {
                return false;
            }
            if (!string.IsNullOrEmpty(ClassName) && !Object.Equals(ClassName, other.ClassName))
            {
                return false;
            }
            if (!string.IsNullOrEmpty(FileName) && !Object.Equals(FileName, other.FileName))
            {
                return false;
            }
            if (!string.IsNullOrEmpty(ModulePath) && !Object.Equals(ModulePath, other.ModulePath))
            {
                return false;
            }
            if (!string.IsNullOrEmpty(ToolName) && !Object.Equals(ToolName, other.ToolName))
            {
                return false;
            }
            if(Language != null && !Object.Equals(Language, other.Language))
            {
                return false;
            }
            return true;
        }
    }
}
