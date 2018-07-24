using System.Collections.Generic;

namespace DexterCS
{
    public class AnalysisResult : BaseAnalysisEntity
    {
        public AnalysisResult() { }

        private List<Defect> defectList = new List<Defect>();
        public List<Defect> DefectList { get { return defectList; }}

        public void AddDefect(Defect defect)
        {
            if (defectList.Contains(defect))
            {
                return;
            }
            defectList.Add(defect);
        }

        public void AddDefectWithPreOccurence(PreOccurence preOcc)
        {
            if(preOcc.StartLine == -1)
            {
                return;
            }
            bool isNewDefect = true;
            foreach(var defect in DefectList)
            {
                defect.AnalysisType = AnalysisType;
               
                if (defect.Equals(preOcc))
                {
                    bool IsDifferentOcc = true;
                    foreach(var occ in defect.Occurences)
                    {
                        if (occ.Equals(preOcc))
                        {
                            IsDifferentOcc = false;
                            break;
                        }
                    }
                    if (IsDifferentOcc)
                    {
                        defect.AddOccurence(preOcc.ToOccurence());
                    }
                    isNewDefect = false;
                    break;
                }
            }

            if (isNewDefect)
            {
                Defect defect = preOcc.ToDefect();
                defect.AnalysisType = AnalysisType;
                defect.AddOccurence(preOcc.ToOccurence());
                defectList.Add(defect);
            }
        }
    }
}