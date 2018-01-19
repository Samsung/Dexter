using System.Xml.Schema;
using System.Xml.Serialization;

namespace Dexter.Defects
{
    /// <summary>
    /// Code defect found by Dexter
    /// </summary>
    [XmlType(AnonymousType = true)]
    public class Defect
    {

        /// <summary>
        /// List of all occurences of defect
        /// </summary>
        [XmlElement("occurence", Form = XmlSchemaForm.Unqualified)]
        public Occurence[] Occurences
        {
            get;
            set;
        }

        /// <summary>
        /// Name of a checker associated with this defect
        /// </summary>
        [XmlAttribute("checker")]
        public string Checker
        {
            get;
            set;
        }
    }
}