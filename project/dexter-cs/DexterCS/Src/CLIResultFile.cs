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
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.IO;
using System.Text;

namespace DexterCS
{
    public class CLIResultFile : ICLIResultFile
    {
        public CLIResultFile() { }

        public void WriteJsonResultFileBody(FileInfo file, List<Defect> allDefectList)
        {
            foreach (var defect in allDefectList)
            {
                using (StreamWriter sw = file.AppendText())
                {
                    sw.Write(JsonConvert.SerializeObject(defect));
                    sw.WriteLine(",");
                }
            }
        }

        public void WriteJsonResultFilePostfix(FileInfo file)
        {
            try
            {
                using (StreamWriter sw = file.AppendText())
                {
                    sw.Write("]");
                }
            }
            catch (IOException)
            {
                throw new DexterRuntimeException("Exception in method " + DexterUtil.GetCurrentMethodName());
            }
        }

        public void WriteJsonResultFilePrefix(FileInfo file)
        {
            try
            {
                using (StreamWriter sw = file.AppendText())
                {
                    sw.Write("[");
                }
            }
            catch (IOException)
            {
                throw new DexterRuntimeException("Exception in method " + DexterUtil.GetCurrentMethodName());
            }
        }

        public void WriteXmlResultFileBody(FileInfo xmlResultFile, List<Defect> allDefectList, string sourceFileFullPath)
        {
            int size = allDefectList.Count * 1024;
            StringBuilder m;
            if (size < Int32.MaxValue)
                m = new StringBuilder(size);
            else
                m = new StringBuilder(Int32.MaxValue);

            m.Append("\t<error filename=\"").Append(sourceFileFullPath).Append("\">\n");

            foreach (Defect defect in allDefectList)
            {
                m.Append("\t\t<defect checker=\"").Append(defect.CheckerCode).Append("\">\n");
                foreach (Occurence o in defect.Occurences)
                {
                    m.Append("\t\t\t<occurence startLine=\"").Append(o.StartLine).Append("\" ")
                            .Append("endLine=\"").Append(o.EndLine).Append("\" ")
                            .Append(" message=\"").Append(o.Message.Replace("\"", "&quot;")).Append("\" />\n");
                }
                m.Append("\t\t</defect>\n");
            }

            m.Append("\t</error>\n");

            try
            {
                using (StreamWriter sw = xmlResultFile.AppendText())
                {
                    sw.Write(m.ToString());
                }
            }
            catch (IOException)
            {
                throw new DexterRuntimeException("Exception in method " + DexterUtil.GetCurrentMethodName());
            }
        }

        public void WriteXmlResultFilePostfix(FileInfo file)
        {
            try
            {
                using (StreamWriter sw = file.AppendText())
                {
                    sw.Write("</dexter-result>");
                }
            }
            catch (IOException)
            {
                throw new DexterRuntimeException("Exception in method " + DexterUtil.GetCurrentMethodName());
            }
        }

        public void WriteXmlResultFilePrefix(FileInfo file)
        {
            try
            {
                using (StreamWriter sw = file.AppendText())
                {
                    sw.Write("<dexter-result created=\"" + DexterUtil.currentDateTime() + "\">\n");
                }
            }
            catch (IOException)
            {
                throw new DexterRuntimeException("Exception in method " + DexterUtil.GetCurrentMethodName());
            }
        }

        public void WriteXml2ResultFileBody(FileInfo file, List<Defect> allDefectList, string sourceFileFullPath)
        {
            foreach (var defect in allDefectList)
            {
                using (StreamWriter sw = file.AppendText())
                {
                    sw.WriteLine(",");
                }
            }
        }

        public void WriteXml2ResultFilePostfix(FileInfo file)
        {
            try
            {
                using (StreamWriter sw = file.AppendText())
                {
                    sw.Write("</dexter-result>");
                }
            }
            catch (IOException)
            {
                throw new DexterRuntimeException("Exception in method " + DexterUtil.GetCurrentMethodName());
            }
        }

        public void WriteXml2ResultFilePrefix(FileInfo file)
        {
            try
            {
                using (StreamWriter sw = file.AppendText())
                {
                    sw.WriteLine("<dexter-result created=\"" + DexterUtil.currentDateTime() + "\">");
                }
            }
            catch (IOException)
            {
                throw new DexterRuntimeException("Exception in method " + DexterUtil.GetCurrentMethodName());
            }
        }
    }
}