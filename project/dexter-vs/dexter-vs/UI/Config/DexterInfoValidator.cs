using dexter_vs.Analysis.Config;
using System;
using System.IO;
using System.Net;

namespace dexter_vs.UI.Config
{
    /// <summary>
    /// Validates correctness of DexterInfo
    /// </summary>
    internal class DexterInfoValidator
    {
 
        /// <summary>
        /// Server settings validation
        /// </summary>
        /// <param name="validationResult">string with detailed validation result</param>
        /// <returns>true, if successfully connected to Dexter server</returns>
        public bool ValidateServerConnection(DexterInfo dexterInfo, out string validationResult)
        {
            var uriString = string.Format("http://{0}:{1}/api/isServerAlive", dexterInfo.dexterServerIp, dexterInfo.dexterServerPort);
            Uri uri;

            bool uriValid = Uri.TryCreate(uriString, UriKind.Absolute, out uri);

            if (!uriValid)
            {
                validationResult = "Error: Server address is in wrong format";
                return false;
            }

            WebRequest request = WebRequest.Create(uri);
            request.Timeout = 5000;
            HttpWebResponse response = (HttpWebResponse)request.GetResponse();

            if (response.StatusCode != HttpStatusCode.OK)
            {
                validationResult = "Error: server returned code: " + response.StatusCode;
                return false;
            }

            string html = string.Empty;

            using (Stream stream = response.GetResponseStream())
            using (StreamReader reader = new StreamReader(stream))
            {
                html = reader.ReadToEnd();
            }

            if (html == "ok")
            {
                validationResult = "Connection ok!";
                return true;
            }
            else
            {
                validationResult = "Error: server returned message: " + html;
                return false;
            }
        }

        /// <summary>
        /// Validation of user credentials
        /// </summary>
        /// <param name="validationResult">string with detailed validation result</param>
        /// <returns>true, if user successfully logged to Dexter server</returns>
        public bool ValidateUserCredentials(DexterInfo dexterInfo, out string validationResult)
        {
            var uriString = string.Format("http://{0}:{1}/api/accounts/userId", dexterInfo.dexterServerIp, dexterInfo.dexterServerPort);
            Uri uri;

            bool uriValid = Uri.TryCreate(uriString, UriKind.Absolute, out uri);

            if (!uriValid)
            {
                validationResult = "Error: Server address is in wrong format";
                return false;
            }

            WebRequest request = WebRequest.Create(uri);

            string encoded = Convert.ToBase64String(System.Text.Encoding.GetEncoding("ISO-8859-1").GetBytes(dexterInfo.userName + ":" + dexterInfo.userPassword));
            request.Headers.Add("Authorization", "Basic " + encoded);

            HttpWebResponse response;

            try
            {
                response = (HttpWebResponse)request.GetResponse();
            }
            catch (WebException e)
            {
                HttpWebResponse errorResponse = (HttpWebResponse)e.Response;

                if (errorResponse.StatusCode == HttpStatusCode.Unauthorized)
                {
                    validationResult = "Error: Wrong user name or password";
                    return false;
                }
                else
                {
                    validationResult = "Error: server returned code: " + errorResponse.StatusCode;
                    return false;
                }
            }

            validationResult = "Connection ok!";
            return true;
        }

        /// <summary>
        /// Dexter path validation
        /// </summary>
        /// <returns>true, if dexter was found in given path</returns>
        public bool ValidateDexterPath(DexterInfo dexterInfo)
        {
            return dexterInfo.IsDexterFound;
        }
    }
}
