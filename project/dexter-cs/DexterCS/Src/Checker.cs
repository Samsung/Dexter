using Newtonsoft.Json;
using System;

namespace DexterCS
{

    [JsonObject(MemberSerialization.OptIn)]
    public class Checker : IChecker
    {
        [JsonProperty("code")]
        public string Code { get; set; }
        [JsonProperty("name")]
        public string Name { get; set; }
        [JsonProperty("type")]
        public string Type { get; set; }
        [JsonProperty("categoryName")]
        public string CategoryName { get; set; }
        [JsonProperty("severityCode")]
        public string SeverityCode { get; set; }
        [JsonProperty("version")]
        public string Version { get; set; }
        [JsonProperty("description")]
        public string Description { get; set; }
        [JsonProperty("isActive")]
        public bool IsActive { get; set; }
        [JsonProperty("properties")]
        public Properties Properties { get; set; }
        [JsonProperty("cwe")]
        public string Cwe { get; set; }

        public IProperties GetProperties(string key)
        {
            if (string.IsNullOrEmpty(key))
            {
                return new EmptyProperties();
            }
            return Properties;
        }

        public Checker() { }

        public Checker(string code, string name, string version, Boolean isActive)
        {
            Name = name;
            Code = code;
            Version = version;
            IsActive = isActive;
        }

        public Checker(string code, string name, string type, 
            string version, string description, Boolean isActive, Properties properties, string cwe)
        {
            Code = code;
            Name = name;
            Type = type;
            Version = version;
            Description = description;
            IsActive = isActive;
            Properties = properties;
            Cwe = cwe;
        }
    }
}
