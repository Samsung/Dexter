using System.Xml.Schema;
using System.Xml.Serialization;

namespace dexter_vs.Defect
{
    /// <summary>
    /// Result of dexter analysis
    /// </summary>
    [System.Serializable()]
    [XmlType(AnonymousType = true)]
    [XmlRoot("dexter-result", Namespace = "", IsNullable = false)]
    public class Result
    {
        /// <summary>
        /// Lists of defects (one per file) found during analysis
        /// </summary>
        [XmlElement("error", Form = XmlSchemaForm.Unqualified)]
        public FileDefects[] FileDefects
        {
            get;
            set;
        }

        /// <summary>
        /// Timestamp for creation of this analysis result
        /// </summary>
        [XmlAttribute("created")]
        public string Created
        {
            get;
            set;
        }
    }
}