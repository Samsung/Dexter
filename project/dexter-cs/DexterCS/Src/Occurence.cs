using Newtonsoft.Json;
using System;

namespace DexterCS
{
    public class Occurence
    {
        public Occurence() { }
        [JsonProperty("code")]
        public string Code { get; set; }
        [JsonProperty("startLine")]
        public int StartLine { get; set; }
        [JsonProperty("endLine")]
        public int EndLine { get; set; }
        [JsonProperty("charStart")]
        public int CharStart { get; set; }
        [JsonProperty("charEnd")]
        public int CharEnd{get;set;}
        [JsonProperty("variableName")]
        public string VariableName { get; set; }
        [JsonProperty("stringValue")]
        public string StringValue { get; set; }
        [JsonProperty("fieldName")]
        public string FieldName { get; set; }
        [JsonProperty("message")]
        public string Message { get; set; }

        public override int GetHashCode()
        {
            return base.GetHashCode();
        }

        public override bool Equals(Object obj)
        {
            if (obj == null)
            {
                return false;
            }
            PreOccurence other = (PreOccurence)obj;
            
            if(!Object.Equals(StartLine, other.StartLine))
            {
                return false;
            }
            if (!Object.Equals(EndLine, other.EndLine))
            {
                return false;
            }
            if (!string.IsNullOrEmpty(FieldName) && !Object.Equals(FieldName, other.FieldName))
            {
                return false;
            }
            if (!string.IsNullOrEmpty(VariableName) && !Object.Equals(VariableName, other.VariableName))
            {
                return false;
            }
            if (!string.IsNullOrEmpty(StringValue) && !Object.Equals(StringValue, other.StringValue))
            {
                return false;
            }
            if (!string.IsNullOrEmpty(Message) && !Object.Equals(Message, other.Message))
            {
                return false;
            }

            return true;
        }
    }
}
