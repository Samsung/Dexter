using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Dexter.PeerReview;
using Dexter.Common.Utils;
using System.IO;
using Microsoft.VisualStudio.Text;
using Microsoft.VisualStudio;
using System.Diagnostics;

namespace Dexter.PeerReview.Utils
{
    /// <summary>
    /// Manage comments of all source code within Visual Studio Solution
    /// </summary>
    public interface IPeerReviewCommentManager
    {
        /// <summary>
        /// Comments of all source code within Visual Studio Solution
        /// </summary>
        IList<PeerReviewComment> Comments { get; }
        /// <summary>
        /// Reflects the comments made in the peer review tagger.
        /// </summary>
        /// <param name="documentPath">Document path with comments</param>
        /// <param name="documentComments">Commands that belong to the document</param>
        void UpdateReviewCommentOfOneDocument(string documentPath, IList<PeerReviewComment> documentComments);
    }

    /// <summary>
    /// Implements the interface IPeerReviewCommentManager
    /// </summary>
    public class PeerReviewCommentManager : IPeerReviewCommentManager
    {
        IList<PeerReviewComment> comments;
        IDexterFileService fileService;
        IPeerReviewService reviewService;
        IDexterSolutionManager solutionManager;
        IPeerReviewTaskProviderWrapper taskProvider;
        IDexterDocumentService documentService;

        static IPeerReviewCommentManager instance;

        static public IPeerReviewCommentManager Instance
        {
            get
            {
                if (instance == null)
                {
                    throw new ArgumentNullException("instance is null");
                }
                return instance;
            }
            set
            {
                if (instance == null)
                {
                    instance = value;
                }
                else
                {
                    throw new ArgumentException("Instance duplicate setting");
                }
            }
        }

        public PeerReviewCommentManager(IDexterFileService fileService, IPeerReviewService reviewService,IDexterSolutionManager solutionManager, IPeerReviewTaskProviderWrapper taskProvider, IDexterDocumentService documentService)
        {
            this.fileService = fileService;
            this.reviewService = reviewService;
            this.solutionManager = solutionManager;
            this.taskProvider = taskProvider;
            this.documentService = documentService;

            this.solutionManager.SourceFilesChanged += OnSourceFilesChanged;
            comments = new List<PeerReviewComment>();
        }

        public IList<PeerReviewComment> Comments
        {
            get
            {
                return comments;
            }
        }

        public async void OnSourceFilesChanged(object sender, SourceFileEventArgs e)
        {
            await UpdateReviewComments(e.FilePaths, e.IsAdded);
        }

        public async Task UpdateReviewComments(IList<string> filePaths, bool isAdded)
        {
            if (isAdded)
            {
                IList<PeerReviewComment> addedComments = (await getAllReviewComments(filePaths)).ToList();
                comments = comments.Concat(addedComments).ToList();
            }
            else
            {
                var filteredComments = from comment in comments
                                       where !filePaths.Contains(comment.FilePath)
                                       select comment;

                comments = filteredComments.ToList();
            }

            Debug.WriteLine("UpdateReviewComments: " + comments.Count);
            RefreshReviewTasks(comments);
        }

        public void RefreshReviewTasks(IList<PeerReviewComment> comments)
        {
            taskProvider.Tasks.Clear();

            foreach(var comment in comments)
            {
                var task = new Microsoft.VisualStudio.Shell.Task()
                {
                    CanDelete = false,
                    Document = comment.FilePath,
                    Text = GetTaskMessage(comment),
                    Line = comment.StartLine - 1,
                    Column = comment.Span.Start + 1,
                    Category = Microsoft.VisualStudio.Shell.TaskCategory.Comments
                };

                task.Navigate += (s, e) => {
                    var navigatedTask = (Microsoft.VisualStudio.Shell.Task)s;

                    documentService.OpenDocument(navigatedTask.Document);
                    documentService.MoveActivePoint(navigatedTask.Line, navigatedTask.Column);
                };

                taskProvider.Tasks.Add(task);
            }

            taskProvider.Show();
        }

        private static string GetTaskMessage(PeerReviewComment comment)
        {
            return"DPR [" + comment.Serverity + "] " + comment.Message;
        }

        private async Task<IEnumerable<PeerReviewComment>> getAllReviewComments(IList<string> filePaths)
        {
            IEnumerable<PeerReviewComment> allComments = Enumerable.Empty<PeerReviewComment>();

            foreach (var filePath in filePaths)
            {
                var commentsForOneFile = await getReviewCommentsFromOneFilePath(filePath);
                allComments = allComments.Concat(commentsForOneFile);
            }

            return allComments;
        }

        private async Task<IEnumerable<PeerReviewComment>> getReviewCommentsFromOneFilePath(string filePath)
        {
            try
            {
                var fileText = await fileService.ReadTextAsync(filePath);

                return getReviewCommentsFromFileContent(filePath, fileText);
            } catch (FileNotFoundException e)
            {
                Debug.WriteLine(e.Message);
                return Enumerable.Empty<PeerReviewComment>();
            }
        }

        private IEnumerable<PeerReviewComment> getReviewCommentsFromFileContent(string filePath, string fileText)
        {
            using (StringReader reader = new StringReader(fileText))
            {
                string line;
                int lineNum = 0;
                while ((line = reader.ReadLine()) != null)
                {
                    lineNum++;
                    int commentStart = line.ToLower().IndexOf(PeerReviewConstants.COMMENT_DELIMITER);

                    if (commentStart >= 0)
                    {
                        var commentText = line.Substring(commentStart);
                        yield return new PeerReviewComment()
                        {
                            StartLine = lineNum,
                            EndLine = lineNum,
                            Serverity = reviewService.GetServerity(commentText),
                            Message = reviewService.GetCommentMessage(commentText),
                            FilePath = filePath,
                            Span = new Span(commentStart, commentText.Length)
                        };
                    };
                }
            }
        }

        public void UpdateReviewCommentOfOneDocument(string documentPath, IList<PeerReviewComment> documentComments)
        {
            var tempComments = from comment in comments
                               where !comment.FilePath.Equals(documentPath)
                               select comment;

            comments = tempComments.Concat(documentComments).ToList();
            RefreshReviewTasks(comments);
        }
    }
}
