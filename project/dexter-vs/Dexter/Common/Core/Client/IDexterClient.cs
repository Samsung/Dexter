using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Dexter.Common.Defect;
using System.Net.Http;

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
    }
}
