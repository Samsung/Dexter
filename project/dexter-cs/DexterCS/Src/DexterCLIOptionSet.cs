using CommandLine;

namespace DexterCS
{
    public class DexterCLIOptionSet
    {
        [Option('f', "", Required = false,
            HelpText = "Analysis Configuration File. e.g. -f C:/dexter/dexter_cfg.json")]
        public string SpecifiedDexterConfigFile { get; set; }

        [Option('h', "Dexter Server IP", Required = false, HelpText = "Dexter Server IP address. e.g. -h 123.123.123.123")]
        public string ServerIp { get; set; }

        [Option('o', "Dexter Server Port", Required = false, HelpText = "Dexter Server Port. e.g. -p 1234")]
        public int ServerPort { get; set; }

        [Option('u', "Dexter User Id", Required = true, HelpText = "Dexter User Id. e.g. -u j.smith")]
        public string UserId { get; set; }

        [Option('p', "Dexter User Password", Required = true, HelpText = "Dexter User Password. e.g. -p password")]
        public string UserPassword { get; set; }

        [Option('x', "XML Result File", Required = false, HelpText = "Create XML result file - dexter-result.xml. eg -x")]
        public bool XMLResultFile { get; set; }

        [Option('X', "XML Result File with TimeStamp", Required = false,
            HelpText = "Create XML result file with timestamp - dexter-result_yyyyMMddhh:mm:ss.xml. e.g. -X")]
        public bool XML2ResultFile { get; set; }

        [Option('F', Required = true, HelpText = "-F json/ -F xml / -F xml2, default: json")]
        public string ResultFormat { get; set; }
    }
}
