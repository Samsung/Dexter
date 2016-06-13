using System.Xml.Serialization;

namespace dexter_vs.Defects
{

    /// <summary>
    /// Occurence of a defect
    /// </summary>
    [XmlType(AnonymousType = true)]
    public class Occurence
    {

        /// <summary>
        /// Start line of occurence
        /// </summary>
        [XmlAttribute("startLine")]
        public string StartLine
        {
            get;
            set;
        }

        /// <summary>
        /// End line of occurence
        /// </summary>
        [XmlAttribute("endLine")]
        public string EndLine
        {
            get;
            set;
        }

        /// <summary>
        /// Ocurrence message
        /// </summary>
        [XmlAttribute("message")]
        public string Message
        {
            get;
            set;
        }
    }
}
