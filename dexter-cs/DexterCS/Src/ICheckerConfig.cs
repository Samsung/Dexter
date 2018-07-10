using System.Collections.Generic;
using static DexterCS.DexterConfig;

namespace DexterCS
{
    public interface ICheckerConfig
    {
        string ToolName { get; set; }
        LANGUAGE Language { get; set; }
        List<Checker> CheckerList { get; }
        bool IsActiveChecker(string checkerCode);
        IChecker GetChecker(string checkerCode);
        void AddCheckerList(Checker checker);
    }
}