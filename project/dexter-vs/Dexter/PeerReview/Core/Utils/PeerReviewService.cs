using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.IO;
using System.Text.RegularExpressions;
using Microsoft.VisualStudio.Text;
using Dexter.Common.Defect;
using Dexter.Common.Utils;

namespace Dexter.PeerReview.Utils
{
    /// <summary>
    /// Implements service functions for PeerReivew
    /// </summary>
    public interface IPeerReviewService
    {
        /// <summary>
        /// Converts peer review comments into dexter defects
        /// </summary>
        /// <param name="textDocument">TextDocument contains review comments</param>
        /// <param name="comments">Peer review comments</param>
        /// <returns>Dexter defects</returns>
        DexterResult ConvertToDexterResult(ITextDocument textDocument, IList<PeerReviewSnapshotComment> comments);
        /// <summary>
        /// Gets start line of SnapshotSpan
        /// </summary>
        /// <param name="span">snapshotSapn contains review comment</param>
        /// <returns>Start line of snapshotSpan</returns>
        int getStartLineNumber(SnapshotSpan span);
        /// <summary>
        /// Gets end line of SnapshotSpan
        /// </summary>
        /// <param name="span">snapshotSapn contains review comment</param>
        /// <returns>End line of snapshotSpan</returns>
        int getEndLineNumber(SnapshotSpan span);
        /// <summary>
        /// Gets serverity codes from comment text in SnapshotSpan
        /// </summary>
        /// <param name="span">SnapshotSpan contains review comment</param>
        /// <returns>Serverity code</returns>
        string getServerity(SnapshotSpan span);
        /// <summary>
        /// Gets comment message filtered by a serverity code
        /// </summary>
        /// <param name="span">SnapshotSpan contains review comment</param>
        /// <returns>Comment message</returns>
        string getCommentMessage(SnapshotSpan span);
    }

    /// <summary>
    /// Implements service functions for PeerReivew
    /// </summary>
    public class PeerReviewService : IPeerReviewService
    {
        IDexterTextService textService;

        static IPeerReviewService instace;

        static public IPeerReviewService Instance
        {
            get
            {
                if (instace == null)
                {
                    throw new ArgumentNullException("instance is null");
                }
                return instace;
            }
            set
            {
                instace = value;
            }
        }

        public PeerReviewService(IDexterTextService textService)
        {
            this.textService = textService;
        }

        public DexterResult ConvertToDexterResult(ITextDocument textDocument, IList<PeerReviewSnapshotComment> comments)
        {
            string fileName = Path.GetFileName(textDocument.FilePath);

            return  new DexterResult()
            {
                FullFilePath = convertFileDelimiterForDexterServer(textDocument.FilePath),
                FileName = fileName,
                DefectCount = comments.Count,
                DefectList = ConvertToDefectList(textDocument.FilePath, comments)
            };
        }

        private string convertFileDelimiterForDexterServer(string filePath)
        {
            return filePath.Replace(@"\", "/");
        }

        private IList<DexterDefect> ConvertToDefectList(string filePath, IList<PeerReviewSnapshotComment> comments)
        {
            var defectTable = new Dictionary<string, DexterDefect>();

            foreach (var comment in comments)
            {
                var commentText = textService.getText(comment.SnapShotSpan);
                var serverityCode = getServerityCode(commentText);
                var checkerCode = "DPR_" + serverityCode;
                var uniqueDefectKey = getUniqueDefectKey(checkerCode, filePath);
                var occurences = createDexterOccurences(comment);
                var fileName = Path.GetFileName(filePath);
                var directoryName = Path.GetDirectoryName(filePath);
                DexterDefect defect;

                if (defectTable.TryGetValue(uniqueDefectKey, out defect))
                {
                    defect.Occurences = defect.Occurences.Concat(occurences).ToList();
                }
                else
                {
                    defect = new DexterDefect()
                    {
                        CategoryName = "PeerReview",
                        AnalysisType = "FILE",
                        Language = "C_SHARP",
                        ToolName = "dexter-peerreview",
                        FileName = fileName,
                        ModulePath = convertFileDelimiterForDexterServer(directoryName),
                        SeverityCode = serverityCode,
                        CheckerCode = "DPR_" + serverityCode,
                        Message = "",
                        Occurences = occurences
                    };

                    defectTable.Add(uniqueDefectKey, defect);
                }
            }

            return defectTable.Values.ToList();
        }

        private string getUniqueDefectKey(string checkerCode, string serverityCode)
        {
            return checkerCode + " " + serverityCode;
        }

        private IList<DexterOccurence> createDexterOccurences(PeerReviewSnapshotComment comment)
        {
            var commentText = textService.getText(comment.SnapShotSpan);
            var occurences = new List<DexterOccurence>();

            occurences.Add(new DexterOccurence()
            {
                StringValue = "DPR",
                Message = getCommentMessage(commentText),
                StartLine = textService.getStartLineNumber(comment.SnapShotSpan),
                EndLine = textService.getEndLineNumber(comment.SnapShotSpan)
            });

            return occurences;
        }

        private string getServerityCode(string commentText)
        {
            if (commentText.Contains("[MAJ]"))
                return "MAJ";
            else if (commentText.Contains("[CRI]"))
                return "CRI";
            else if (commentText.Contains("[CRC]"))
                return "CRC";
            else
                return "MAJ";
        }

        private string getCommentMessage(string commentText)
        {
            string commentPrefix = @"//\sDPR:\s(\[CRI\]|\[MAJ\]|\[CRC\])";
            Regex rx = new Regex(commentPrefix, RegexOptions.IgnoreCase);

            return rx.Replace(commentText, "").Trim();
        }

        public int getStartLineNumber(SnapshotSpan span)
        {
            return textService.getStartLineNumber(span);
        }

        public int getEndLineNumber(SnapshotSpan span)
        {
            return textService.getEndLineNumber(span);
        }

        public string getServerity(SnapshotSpan span)
        {
            var text = textService.getText(span);
            return getServerityCode(text);
        }

        public string getCommentMessage(SnapshotSpan span)
        {
            var text = textService.getText(span);
            return getCommentMessage(text);
        }
    }
}
