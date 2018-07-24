using Newtonsoft.Json;

namespace DexterCS
{
    public interface IChecker
    {
        string Code { get; set; }
        string Name { get; set; }
        string Type { get; set; }
        string CategoryName { get; set; }
        string SeverityCode { get; set; }
        string Version { get; set; }
        string Description { get; set; }
        bool IsActive { get; set; }
        Properties Properties { get; set; }
        string Cwe { get; set; }
    }
    public interface IProperties
    {
        [JsonProperty("value")]
        string Value { get; set; }
    }
    public class Properties : IProperties
    {
        [JsonProperty("value")]
        public string Value { get; set; }
    }
    public class EmptyProperties : IProperties
    {
        string tempValue = "";
        [JsonProperty("value")]
        public string Value { get { return ""; } set { tempValue = value; } }
    }

}
