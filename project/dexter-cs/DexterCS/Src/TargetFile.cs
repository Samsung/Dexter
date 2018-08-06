#region Copyright notice
/**
 * Copyright (c) 2018 Samsung Electronics, Inc.,
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
#endregion
using log4net;
using Newtonsoft.Json;
using System;

namespace DexterCS
{
    public class TargetFile
    {
        private static ILog CliLog = LogManager.GetLogger(typeof(TargetFile));

        private string fileName = "";
        [JsonProperty("fileName")]
        public string FileName
        {
            get { return fileName; }
            set { this.fileName = value; }
        }

        [JsonProperty("fileStatus")]
        public string FileStatus { get; set; }

        protected TargetFile() { }

        public TargetFile(TargetFile other)
        {
            fileName = other.FileName;
            ModulePath = other.ModulePath;
            FileStatus = other.FileStatus;
        }
        private string modulePath = "";
        [JsonProperty("modulePath")]
        public string ModulePath
        {
            get { return modulePath; }
            set
            {
                if (string.IsNullOrEmpty(value))
                {
                    return;
                }
                value = value.Replace("\\", "/");
                if (GetLanguageEnum() == DexterConfig.LANGUAGE.JAVA)
                {
                    value = value.Replace(".", "/");
                }
                value = value.Replace("//", "/");
                if (value.EndsWith("/", StringComparison.CurrentCulture))
                {
                    value = value.Substring(0, value.Length - 1);
                }
                if (value.StartsWith("/", StringComparison.CurrentCulture))
                {
                    value = value.Substring(1, value.Length);
                }

                modulePath = value;
            }
        }


        public DexterConfig.LANGUAGE GetLanguageEnum()
        {
            if (string.IsNullOrEmpty(FileName))
            {
                CliLog.Error("fileName field is not set yet.");
                return DexterConfig.LANGUAGE.UNKNOWN;
            }

            if (FileName.ToLowerInvariant().EndsWith(".java", StringComparison.CurrentCulture))
            {
                return DexterConfig.LANGUAGE.JAVA;
            }
            else if (FileName.ToLowerInvariant().EndsWith(".cpp", StringComparison.CurrentCulture)
                || FileName.ToLowerInvariant().EndsWith(".hpp", StringComparison.CurrentCulture)
                || FileName.ToLowerInvariant().EndsWith(".c", StringComparison.CurrentCulture)
                || FileName.ToLowerInvariant().EndsWith(".h", StringComparison.CurrentCulture))
            {
                return DexterConfig.LANGUAGE.CPP;
            }
            else if (FileName.ToLowerInvariant().EndsWith(".js", StringComparison.CurrentCulture))
            {
                return DexterConfig.LANGUAGE.JAVASCRIPT;
            }
            else if (FileName.ToLowerInvariant().EndsWith(".cs", StringComparison.CurrentCulture))
            {
                return DexterConfig.LANGUAGE.C_SHARP;
            }
            else
            {
                return DexterConfig.LANGUAGE.UNKNOWN;
            }
        }

    }
}
