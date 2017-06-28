using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Dexter.Common.Defect;
using System.Net.Http;
using Newtonsoft.Json;

namespace Dexter.Common.Client
{
    /// <summary>
    /// Communicates with the dexter server
    /// </summary>
    public interface IDexterClient
    {
        /// <summary>
        /// Sends dexter defects to the dexter server
        /// </summary>
        /// <param name="result">Container of dexter defects</param>
        Task<HttpResponseMessage> SendAnalysisResult(DexterResult result);

        /// <summary>
        /// Adds new user account
        /// </summary>
        /// <param name="userName">User name to ocreate</param>
        /// <param name="userPassword">User password to ocreate</param>
        /// <param name="isAdmin">True, if new user should have administrator rights</param>
        Task<HttpResponseMessage> AddAccount(string userName, string userPassword, bool isAdmin);
        /// <summary>
        /// Returns whether stand-alone mode is checked
        /// </summary>
        Task SendSourceCode(SourceCodeJsonFormat source);
    }

    /// <summary>
    /// JSON Container of source codes
    /// </summary>
    public class SourceCodeJsonFormat
    {
        [JsonProperty("snapshotId")]
        public long SnapshotId { get; set; }
        [JsonProperty("gouprId")]
        public long GroupId { get; set; }
        [JsonProperty("modulePath")]
        public string ModulePath { get; set; }
        [JsonProperty("fileName")]
        public string FileName { get; set; }
        [JsonProperty("sourceCode")]
        public string SourceCode { get; set; }
    }
}
