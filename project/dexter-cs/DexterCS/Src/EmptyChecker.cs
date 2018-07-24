using System.ComponentModel;

namespace DexterCS
{
    public class EmptyChecker : IChecker
    {
        [DefaultValue("")]
        public string Type { get; set; }
        [DefaultValue("")]
        public string CategoryName { get; set; }
        [DefaultValue("")]
        public string Cwe { get; set; }
        public string Description { get; set; }
        [DefaultValue("")]
        public string Code { get; set; }
        [DefaultValue("")]
        public string SeverityCode { get; set; }
        [DefaultValue("false")]
        public bool IsActive { get; set; }
        [DefaultValue("0.0.0")]
        public string Version { get; set; }
        [DefaultValue("")]
        public string Name { get; set; }
        [DefaultValue("")]
        public Properties Properties { get; set; }
        public string GetProperties(string key) { return ""; }
    }
}