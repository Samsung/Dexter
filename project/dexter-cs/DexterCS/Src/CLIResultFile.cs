using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.IO;
using System.Xml;

namespace DexterCS
{
    public class CLIResultFile : ICLIResultFile
    {
        public CLIResultFile() { }

        public void WriteJsonResultFileBody(FileInfo file, List<Defect> allDefectList)
        {
            foreach(var defect in allDefectList)
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

        public void WriteXmlResultFileBody(FileInfo xmlResultFile, List<Defect> allDefectList, string sourceFileFullPath)
        {
            throw new NotImplementedException();
        }

        public void WriteXmlResultFilePostfix(FileInfo file)
        {
            try
            {
                using (StreamWriter sw = file.AppendText())
                {
                    sw.WriteLine(@"</dexter-result>");
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
                File.AppendAllText(file.FullName, "<dexter-result created=\"" + DexterUtil.currentDateTime() + "\">\n");
            }
            catch (IOException)
            {
                throw new DexterRuntimeException("writeXmlResultFilePrefix Exception");
            }
        }
    }
}