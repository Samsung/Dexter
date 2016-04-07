using dexter_vs.Defects;
using dexter_vs.Utils;
using Microsoft.VisualStudio.Shell;
using System;

using System.Runtime.InteropServices;


namespace dexter_vs.UI
{
    /// <summary>
    /// This class provides task items based on Dexter analysis result
    /// </summary>
    [Guid("72de1eAD-a00c-4f57-bff7-57edb162d0be")]
    public class DexterTaskProvider : ErrorListProvider
    {
        /// <inheritDoc/>
        public DexterTaskProvider(IServiceProvider sp) : base(sp)
        {
            ProviderName = "Dexter";
        }

        /// <summary>
        /// Reports tasks from analysis result
        /// </summary>
        /// <param name="result"></param>
        public void ReportResult(Result result)
        {
            foreach (FileDefects fileDefects in result.FileDefects.OrEmptyIfNull())
            {
                foreach (Defect defect in fileDefects.Defects.OrEmptyIfNull())
                {
                    foreach (Occurence occurence in defect.Occurences.OrEmptyIfNull())
                    {
                        var defectTask = CreateTask(occurence, fileDefects.FileName);
                        Tasks.Add(defectTask);
                    }
                }
            }
        }

        /// <summary>
        /// Creates task from occurence
        /// </summary>
        /// <param name="occurence"> occurence of defect</param>
        /// <param name="fileName">file name of occurence</param>
        /// <returns></returns>
        static Task CreateTask(Occurence occurence, string fileName)
        {
            var defectTask = new ErrorTask()
            {
                Category = TaskCategory.User,
                ErrorCategory = TaskErrorCategory.Warning,
                CanDelete = false,
                Text = occurence.Message,
                Document = fileName,
                Line = int.Parse(occurence.StartLine)
            };
            return defectTask;
        }
    }
}
