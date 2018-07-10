using System;
using System.Collections.Generic;
using System.IO;

namespace DexterCS
{
    public interface ICLIResultFile
    {
        void WriteJsonResultFilePrefix(FileInfo file);
        void WriteXmlResultFilePrefix(FileInfo xmlResultFile);
        void WriteXml2ResultFilePrefix(FileInfo xml2ResultFile);
        void WriteJsonResultFilePostfix(FileInfo file);
        void WriteXmlResultFilePostfix(FileInfo xmlResultFile);
        void WriteXml2ResultFilePostfix(FileInfo xml2ResultFile);
        void WriteJsonResultFileBody(FileInfo jsonResultFile, List<Defect> allDefectList);
        void WriteXml2ResultFileBody(FileInfo xml2ResultFile, List<Defect> allDefectList, string sourceFileFullPath);
        void WriteXmlResultFileBody(FileInfo xmlResultFile, List<Defect> allDefectList, string sourceFileFullPath);
    }
}