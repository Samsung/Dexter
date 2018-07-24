using System.Collections.Generic;

namespace DexterCS
{
    public class CheckerConfig : ICheckerConfig
    {
        public DexterConfig.LANGUAGE Language { get; set; }

        public string ToolName { get; set; }


        public Dictionary<string, string> properties = new Dictionary<string, string>();
        public Dictionary<string, string> Properties { get; set; }

        private List<Checker> checkerList = new List<Checker>();
        public List<Checker> CheckerList { get { return checkerList; } }
        public void AddCheckerList(Checker checker)
        {
            checkerList.Add(checker);
        }

        public CheckerConfig(string toolName, DexterConfig.LANGUAGE language)
        {
            ToolName = toolName;
            Language = language;
        }

        public bool IsActiveChecker(string checkerCode)
        {
            IChecker checker = GetChecker(checkerCode);
            return checker.IsActive;
        }

        public IChecker GetChecker(string checkerCode)
        {
            IChecker checker = CheckerList.Find(c => c.Code == checkerCode);
            return checker == null ? new EmptyChecker() : checker;
        }
    }
}