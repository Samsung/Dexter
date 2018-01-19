using System;
using Microsoft.VisualStudio.Shell;
using System.Runtime.InteropServices;

namespace Dexter.PeerReview
{
    /// <summary>
    /// Provides interface wrapper for TaskCollection
    /// </summary>
    public interface IPeerReviewTaskCollectionWrapper
    {
        /// <summary>
        /// Add an Task to Tasks
        /// </summary>
        /// <param name="task">Task to add</param>
        /// <returns>The index of the added task.</returns>
        int Add(Task task);
        /// <summary>
        /// Clear all tasks
        /// </summary>
        void Clear();
    }

    /// <summary>
    /// Implements an wrapper class for TaskCollection
    /// </summary>
    internal class PeerReviewTaskCollectionWrapper : IPeerReviewTaskCollectionWrapper
    {
        PeerReviewTaskProvider taskProvider;

        internal PeerReviewTaskCollectionWrapper(PeerReviewTaskProvider taskProvider)
        {
            this.taskProvider = taskProvider;
        }

        public int Add(Task task)
        {
            return taskProvider.Tasks.Add(task);
        }

        public void Clear()
        {
            taskProvider.Tasks.Clear();
        }
    }

    /// <summary>
    /// Provides wrapping interfaces for TaskProvider 
    /// </summary>
    public interface IPeerReviewTaskProviderWrapper
    {
        /// <summary>
        /// Provides tasks in TaskProvider
        /// </summary>
        IPeerReviewTaskCollectionWrapper Tasks { get; }
        /// <summary>
        /// Shows provider'tasks
        /// </summary>
        void Show();
    }

    /// <summary>
    /// Implements wrapping class for TaskProvider
    /// </summary>
    public class PeerReviewTaskProviderWrapper : IPeerReviewTaskProviderWrapper
    {
        PeerReviewTaskProvider taskProvider;
        IPeerReviewTaskCollectionWrapper tasks;

        public PeerReviewTaskProviderWrapper(IServiceProvider sp)
        {
            taskProvider = new PeerReviewTaskProvider(sp);
            taskProvider.ProviderName = "PeerReview";
            tasks = new PeerReviewTaskCollectionWrapper(taskProvider);
        }

        public IPeerReviewTaskCollectionWrapper Tasks
        {
            get
            {
                return tasks;
            }
        }

        public void Show()
        {
            taskProvider.Show();
        }

    }

    [Guid("72de1eAD-a00c-4f57-bff7-57edb162d0be")]
    internal class PeerReviewTaskProvider : TaskProvider
    {
        public PeerReviewTaskProvider(IServiceProvider sp) : base(sp)
        {
        }
    }
}
