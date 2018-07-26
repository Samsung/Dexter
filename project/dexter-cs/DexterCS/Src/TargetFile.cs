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
