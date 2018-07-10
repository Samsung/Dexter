using System.Collections.Generic;
using System.IO;

namespace DexterCS
{
    public interface IDexterCLIOption
    {
        bool IsSpisSpecifiedCheckerEnabledMode { get; set; }
        bool IsTargetFileOptionEnabled { get; set; }
        List<string> TargetFileFullPathList { get; set; }

        FileInfo JsonResultFile { get; set; }
        FileInfo XmlResultFile { get; set; }
        FileInfo Xml2ResultFile { get; set; }

        bool IsJsonResultFile { get; set; }
        bool IsXmlResultFile { get; set; }
        bool IsXml2ResultFile { get; set; }
        string UserId { get; set; }
        string UserPassword { get; set; }
        string ServerHostIp { get; set; }
        int ServerPort { get; set; }
        string ConfigFilePath { get; set; }
    }
}