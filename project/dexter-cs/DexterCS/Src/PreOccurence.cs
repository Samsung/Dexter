namespace DexterCS
{
    public class PreOccurence : BaseDefect
    {
        public string OccurenceCode { get; set; }
        public int CharStart { get; set; }
        public int CharEnd { get; set; }
        public int StartLine { get; set; }
        public int EndLine { get; set; }
        public string VariableName { get; set; }
        public string StringValue { get; set; }
        public string FieldName { get; set; }
        public string Message { get; set; }
        public string SeverityCode { get; set; }
        public string CategoryName { get; set; }

        internal Occurence ToOccurence()
        {
            Occurence occurence = new Occurence();
            occurence.StartLine = StartLine;
            occurence.EndLine = EndLine;
            occurence.FieldName = FieldName;
            occurence.CharStart = CharStart;
            occurence.CharEnd = CharEnd;
            occurence.Message = Message;
            occurence.StringValue = StringValue;
            occurence.VariableName = VariableName;
            occurence.Code = OccurenceCode;

            return occurence;
        }

        internal Defect ToDefect()
        {
            Defect defect = new Defect();
            defect.CheckerCode = CheckerCode;
            defect.ClassName = ClassName;
            defect.FileName = FileName;
            defect.ModulePath = ModulePath;
            defect.MethodName = MethodName;
            defect.SeverityCode = SeverityCode;
            defect.CategoryName = CategoryName;
            defect.Language = Language;
            defect.ToolName = ToolName;

            return defect;
        }
    }
}
