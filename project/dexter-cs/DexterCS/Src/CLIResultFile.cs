using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using System.Xml;

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
                throw new DexterRuntimeException("writeJsonResultFilePostfix Exception");
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
                throw new DexterRuntimeException("writeJsonResultFilePrefix Exception");
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
                throw new DexterRuntimeException("WriteXmlResultFileBody Exception");
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
                throw new DexterRuntimeException("writeJsonResultFilePostfix Exception");
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
                throw new DexterRuntimeException("writeXmlResultFilePrefix Exception");
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
                throw new DexterRuntimeException("writeJsonResultFilePostfix Exception");
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
                throw new DexterRuntimeException("writeXmlResultFilePrefix Exception");
            }
        }
    }
}