using System;
using System.IO;
using System.Net;

namespace Dexter.Config.Validation
{
    /// <summary>
    /// Validates correctness of DexterInfo
    /// </summary>
    internal sealed class DexterInfoValidator
    {
        /// <summary>
        /// Dexter path validation
        /// </summary>
        /// <returns>true, if dexter was found in given path</returns>
        public bool ValidateDexterPath(DexterInfo dexterInfo)
        {
            return dexterInfo.IsDexterFound;
        }

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
                return handleWrongServerAddress(out validationResult);
            }

            WebRequest request = WebRequest.Create(uri);
            request.Timeout = 5000;
            HttpWebResponse response;
            
            try
            {
                response = (HttpWebResponse)request.GetResponse();
            }
            catch (WebException e)
            {
                return handleWebException(e, out validationResult);
            }
            
            if (response.StatusCode != HttpStatusCode.OK)
            {
                return handleWrongServerResponse(response, out validationResult);
            }

            string html = string.Empty;

            using (StreamReader reader = new StreamReader(response.GetResponseStream()))
            {
                html = reader.ReadToEnd();
            }

            if (html == "ok")
            {
                return handleServerOk(out validationResult);
            }
            else
            {
                return handleWrongServerMessage(html, out validationResult);
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
                return handleWrongServerAddress(out validationResult);
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
                return handleWebException(e, out validationResult);
            }

            return handleServerOk(out validationResult);
        }

        private bool handleWebException(WebException e, out string validationResult)
        {
            HttpWebResponse errorResponse = (HttpWebResponse)e.Response;

            if (errorResponse == null)
            {
                return handleWrongServerMessage(e.Message, out validationResult);
            }
            else if (errorResponse.StatusCode == HttpStatusCode.Unauthorized)
            {
                return handleWrongCredentials(out validationResult);
            }
            else
            {
                return handleWrongServerResponse(errorResponse, out validationResult);
            }
        }

        private bool handleWrongServerAddress(out string validationResult)
        {
            validationResult = "Error: Wrong server address";
            return false;
        }

        private bool handleWrongServerResponse(HttpWebResponse response, out string validationResult)
        {
            int statusCode = (int) response.StatusCode;
            string statusMessage = response.StatusCode.ToString();
            validationResult = string.Format("Error: server returned code {0} ({1})", statusCode, statusMessage);
            return false;
        }

        private bool handleWrongServerMessage(string message, out string validationResult)
        {
            validationResult = string.Format("Error: {0}", message);
            return false;
        }

        private bool handleWrongCredentials(out string validationResult)
        {
            validationResult = "Error: Wrong user name or password";
            return false;
        }

        private bool handleServerOk(out string validationResult)
        {
            validationResult = "Connection ok!";
            return true;
        }
    }
}
