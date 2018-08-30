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
