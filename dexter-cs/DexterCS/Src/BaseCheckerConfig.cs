using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DexterCS
{
    [JsonObject(MemberSerialization.OptIn)]
    public class BaseCheckerConfig
    {
        [JsonProperty("checkerList")]
        public List<Checker> CheckerList { get; set; }
        [JsonProperty("toolName")]
        public string ToolName { get; set; }
        [JsonProperty("langyage")]
        public DexterConfig.LANGUAGE Language { get; set; }
    }
}
