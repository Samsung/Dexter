#region Copyright notice
/**
 * Copyright (c) 2018 Samsung Electronics, Inc.,
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
#endregion
using System.Collections.Generic;

namespace DexterCS
{
    public class AnalysisResult : BaseAnalysisEntity
    {
        public AnalysisResult() { }

        private List<Defect> defectList = new List<Defect>();
        public List<Defect> DefectList { get { return defectList; } }

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
            if (preOcc.StartLine == -1)
            {
                return;
            }
            bool isNewDefect = true;
            foreach (var defect in DefectList)
            {
                defect.AnalysisType = AnalysisType;

                if (defect.Equals(preOcc))
                {
                    bool IsDifferentOcc = true;
                    foreach (var occ in defect.Occurences)
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