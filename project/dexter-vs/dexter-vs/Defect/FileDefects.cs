using System;
using System.Xml.Schema;
using System.Xml.Serialization;

namespace dexter_vs.Defect
{
    /// <summary>
    /// A list of defects in a single file
    /// </summary>
    [Serializable()]
    [XmlType(AnonymousType = true)]
    public class FileDefects
    {
        /// <summary>
        /// List of defects
        /// </summary>
        [XmlElement("defect", Form = XmlSchemaForm.Unqualified)]
        public Defect[] Defects
        {
            get;
            set;
        }

        /// <summary>
        /// Name of file where defects were found
        /// </summary>
        [XmlAttribute("filename")]
        public string FileName
        {
            get;
            set;
        }
    }

}
