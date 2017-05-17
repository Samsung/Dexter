using Dexter.Defects;
using Dexter.Utils;
using Microsoft.VisualStudio.Shell;
using System;

using System.Runtime.InteropServices;


namespace Dexter.UI.Tasks
{
    /// <summary>
    /// This class provides task items based on Dexter analysis result
    /// </summary>
    [Guid("72de1eAD-a00c-4f57-bff7-57edb162d0be")]
    public class DexterTaskProvider : ErrorListProvider
    {
        private event EventHandler navigateEventHandler;

        /// <inheritDoc/>
        public DexterTaskProvider(IServiceProvider sp) : base(sp)
        {
            ProviderName = "Dexter";
            navigateEventHandler = new NavigateTaskEventHandler(sp).Navigate;
        }

        /// <summary>
        /// Reports tasks from analysis result
        /// </summary>
        /// <param name="result">Analysis result.</param>
        public void ReportResult(Result result)
        {
            if (result == null)
                throw new ArgumentNullException("result");

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
        Task CreateTask(Occurence occurence, string fileName)
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
            defectTask.Navigate += navigateEventHandler;
            return defectTask;
        }
    }
}
