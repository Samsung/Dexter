using Newtonsoft.Json;
using System;
using System.Collections.Generic;

namespace DexterCS
{
    public class Defect : BaseDefect
    {
        [JsonProperty("message")]
        public string Message { get; set; }
        [JsonProperty("severityCode")]
        public string SeverityCode { get; set; }
        [JsonProperty("categoryName")]
        public string CategoryName { get; set; }
        [JsonProperty("analysisType")]
        public DexterConfig.AnalysisType AnalysisType { get; set; }

        private List<Occurence> occurences = new List<Occurence>();

        [JsonProperty("occurences")]
        public List<Occurence> Occurences
        {
            get
            {
                return occurences;
            }
            set
            {
                occurences = value;
            }
        }

        public void AddOccurence(Occurence occ)
        {
            string key = @"[#"+ (Occurences.Count+1) + "@" + occ.StartLine+"]";
            Message += key + occ.Message + " ";
            Occurences.Add(occ);
        }

        public override int GetHashCode()
        {
            return base.GetHashCode();
        }

        public override bool Equals(Object obj)
        {
            if(obj == null)
            {
                return false;
            }
            return base.Equals(obj);
        }
    }
}
