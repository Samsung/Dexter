using System;
using System.Collections.Generic;
using CommandLine;
using CommandLine.Text;
using System.IO;
using System.ComponentModel;
using System.Text;
using System.Collections;
using log4net;

namespace DexterCS
{
    public class DexterCLIOption : IDexterCLIOption
    {
        private static ILog CliLog = LogManager.GetLogger(typeof(DexterCLIOption));

        [DefaultValue(false)]
        public Boolean IsAsynchronous { get; set; }

        public DexterCLIOption(string[] args)
        {
            CreateCliOptionFromArguments(args);
        }

        public int ServerPort { get; set; }
        public string UserId { get; set; }
        public string UserPassword { get; set;}
        public string ServerHostIp { get; set; }
        private string configFilePath;
        public string ConfigFilePath {
            get {
                return this.configFilePath;
            }
            set {
                try
                {
                    if (string.IsNullOrEmpty(value))
                    {
                        throw new DexterRuntimeException("Invalid CommandLine Option for filePath(null or empty)");
                    }
                }
                catch (DexterRuntimeException e)
                {
                    CliLog.Error(e.StackTrace);
                    Environment.Exit(0);
                }
                this.configFilePath = value;
            }
        }
        public bool IsSpisSpecifiedCheckerEnabledMode { get; set; }
        public bool IsTargetFileOptionEnabled { get; set; }

        public FileInfo JsonResultFile { get; set; }
        public FileInfo XmlResultFile { get; set; }
        public FileInfo Xml2ResultFile { get; set; }

        public List<EnabledChecker> enabledCheckerList = new List<EnabledChecker>();

        public bool IsJsonResultFile { get; set; }
        public bool IsXmlResultFile { get; set; }
        public bool IsXml2ResultFile { get; set; }
        public List<string> TargetFileFullPathList
        {
            get
            {
                if (IsTargetFileOptionEnabled == false)
                {
                    return new List<string>(0);
                }
                return new List<string>((string[])TargetFiles);
            }
            set { }
        }

        public string[] TargetFiles { get;  set; }

        private void CreateCliOptionFromArguments(string[] args)
        {
            try
            {
                var command = Parser.Default.ParseArguments<DexterCLIOptionSet>(args);
                DexterUtil.STATUS_CODE exitCode = command
                    .Return(opts => {
                        var parsed = (Parsed<DexterCLIOptionSet>)command;
                        var options = parsed.Value;
                        SetFieldsByCommandLine(options);
                        return DexterUtil.STATUS_CODE.SUCCESS;
                    }, code => DexterUtil.STATUS_CODE.ERROR // means NOT OK
                 );

                if (exitCode != DexterUtil.STATUS_CODE.SUCCESS)
                {
                    throw new Exception("Parse Error");
                }
            }
            catch (Exception e)
            {
                CliLog.Error("Invalid CLI Option");
                CliLog.Error(e.StackTrace);
                Environment.Exit(0);
            }
        }

        public void SetFieldsByCommandLine(DexterCLIOptionSet options)
        {
            String ResultFileName = "." + DexterUtil.FILE_SEPARATOR + "dexter-result";
            SetHostAndPort(options.ServerIp, options.ServerPort);
            this.ServerHostIp = options.ServerIp;
            this.ServerPort = options.ServerPort;
            this.UserId = options.UserId;
            this.UserPassword = options.UserPassword;

            if (DexterUtil.HasOption(options.SpecifiedDexterConfigFile)){
                this.ConfigFilePath = options.SpecifiedDexterConfigFile;
            } else
            {
                this.ConfigFilePath = "./" + DexterConfig.DEXTER_CFG_FILENAME;
            }

            DexterUtil.ThrowExceptionWhenFileNotExist(ConfigFilePath);

            if (DexterUtil.HasOption(options.ResultFormat))
            {
                switch (options.ResultFormat)
                {
                    case "xml":
                        this.IsXmlResultFile = true;
                        CreateXmlResultFile(ResultFileName);
                        break;
                    case "xml2":
                        this.IsXml2ResultFile = true;
                        break;
                    case "json":
                        this.IsJsonResultFile = true;
                        CreateJsonResultFile(ResultFileName);
                        break;
                }
            }
        }

        private void SetHostAndPort(string host, int port)
        {
            try
            {
                if (string.IsNullOrEmpty(host))
                {
                    throw new DexterRuntimeException(
                            "You have to use both -h dexter_server_host_ip and -o dexter_server_port_number options");
                }
            }
            catch(DexterRuntimeException e)
            {
                CliLog.Error(e.Message);
                Environment.Exit(0);
            }
            ServerHostIp = host;
            ServerPort = port;
        }

        private void CreateXmlResultFile(string fileName)
        {
            try
            {
                XmlResultFile = new FileInfo(fileName + ".xml");
                File.WriteAllText(XmlResultFile.FullName, "", Encoding.UTF8);
            }
            catch (IOException e)
            {
                throw new DexterRuntimeException(e.Message);
            }
        }

        private void CreateJsonResultFile(string fileName)
        {
            try
            {
                JsonResultFile = new FileInfo(fileName + ".json");
                File.WriteAllText(JsonResultFile.FullName, "" , Encoding.UTF8);
            }
            catch (IOException e)
            {
                throw new DexterRuntimeException(e.Message);
            }
        }

        public static void CheckValidationOfOptionCombination(DexterCLIOptionSet command)
        {
            // Method intentionally left empty.
        }
    }
}