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
using CommandLine;
using log4net;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.IO;
using System.Text;

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
        public string UserPassword { get; set; }
        public string ServerHostIp { get; set; }
        private string configFilePath;
        public string ConfigFilePath
        {
            get
            {
                return this.configFilePath;
            }
            set
            {
                try
                {
                    if (string.IsNullOrEmpty(value))
                    {
                        throw new DexterRuntimeException("Invalid CommandLine option for configFilePath (null or empty)");
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

        public string[] TargetFiles { get; set; }

        private void CreateCliOptionFromArguments(string[] args)
        {
            try
            {
                var command = Parser.Default.ParseArguments<DexterCLIOptionSet>(args);
                DexterUtil.STATUS_CODE exitCode = command
                    .Return(opts =>
                    {
                        var parsed = (Parsed<DexterCLIOptionSet>)command;
                        var options = parsed.Value;
                        SetFieldsByCommandLine(options);
                        return DexterUtil.STATUS_CODE.SUCCESS;
                    }, code => DexterUtil.STATUS_CODE.ERROR // means NOT OK
                 );

                if (exitCode != DexterUtil.STATUS_CODE.SUCCESS)
                {
                    throw new Exception("Command parsing error");
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

            if (DexterUtil.HasOption(options.SpecifiedDexterConfigFile))
            {
                this.ConfigFilePath = options.SpecifiedDexterConfigFile;
            }
            else
            {
                this.ConfigFilePath = "./" + DexterConfig.DEXTER_CFG_FILENAME;
            }

            DexterUtil.LogErrorAndExitIfFileDoesNotExist(ConfigFilePath);

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
            catch (DexterRuntimeException e)
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
                File.WriteAllText(JsonResultFile.FullName, "", Encoding.UTF8);
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