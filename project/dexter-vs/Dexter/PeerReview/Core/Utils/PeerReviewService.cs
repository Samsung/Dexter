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
using Dexter.Common.Client;

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
        int GetStartLineNumber(SnapshotSpan span);
        Span GetLineSpan(SnapshotSpan snapshotSpan);

        /// <summary>
        /// Gets end line of SnapshotSpan
        /// </summary>
        /// <param name="span">snapshotSapn contains review comment</param>
        /// <returns>End line of snapshotSpan</returns>
        int GetEndLineNumber(SnapshotSpan span);
        /// <summary>
        /// Gets serverity codes from comment text in SnapshotSpan
        /// </summary>
        /// <param name="span">SnapshotSpan contains review comment</param>
        /// <returns>Serverity code</returns>
        string GetServerity(SnapshotSpan span);
        string GetServerity(string commentText);
        /// <summary>
        /// Gets comment message filtered by a serverity code
        /// </summary>
        /// <param name="span">SnapshotSpan contains review comment</param>
        /// <returns>Comment message</returns>
        string GetCommentMessage(SnapshotSpan span);
        string GetCommentMessage(string commentText);
        /// <summary>
        /// Converts the souce code information to the format for server transmission
        /// </summary>
        /// <param name="filePath">The path of the file to be transferred</param>
        /// <param name="fileContent">The content of the file to be transferred</param>
        /// <returns></returns>
        SourceCodeJsonFormat ConverToSourceCodeJsonFormat(string filePath, string fileContent);
    }

    /// <summary>
    /// Implements service functions for PeerReivew
    /// </summary>
    public class PeerReviewService : IPeerReviewService
    {
        IDexterTextService textService;

        static IPeerReviewService instance;

        static public IPeerReviewService Instance
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

        public PeerReviewService(IDexterTextService textService)
        {
            this.textService = textService;
        }

        public DexterResult ConvertToDexterResult(ITextDocument textDocument, IList<PeerReviewSnapshotComment> comments)
        {
            string fileName = Path.GetFileName(textDocument.FilePath);

            return  new DexterResult()
            {
                FullFilePath = ConvertFileDelimiterForDexterServer(textDocument.FilePath),
                FileName = fileName,
                DefectCount = comments.Count,
                DefectList = ConvertToDefectList(textDocument.FilePath, comments)
            };
        }

        private string ConvertFileDelimiterForDexterServer(string filePath)
        {
            return filePath.Replace(@"\", "/");
        }

        private IList<DexterDefect> ConvertToDefectList(string filePath, IList<PeerReviewSnapshotComment> comments)
        {
            var defectTable = new Dictionary<string, DexterDefect>();

            foreach (var comment in comments)
            {
                var commentText = textService.GetText(comment.SnapShotSpan);
                var serverityCode = GetServerityCode(commentText);
                var checkerCode = "DPR_" + serverityCode;
                var uniqueDefectKey = GetUniqueDefectKey(checkerCode, filePath);
                var occurences = CreateDexterOccurences(comment);
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
                        ModulePath = ConvertFileDelimiterForDexterServer(directoryName),
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

        private string GetUniqueDefectKey(string checkerCode, string filePath)
        {
            return checkerCode + " " + filePath;
        }

        private IList<DexterOccurence> CreateDexterOccurences(PeerReviewSnapshotComment comment)
        {
            var commentText = textService.GetText(comment.SnapShotSpan);
            var occurences = new List<DexterOccurence>();

            occurences.Add(new DexterOccurence()
            {
                StringValue = "DPR",
                Message = GetCommentMessageInternal(commentText),
                StartLine = textService.GetStartLineNumber(comment.SnapShotSpan),
                EndLine = textService.GetEndLineNumber(comment.SnapShotSpan)
            });

            return occurences;
        }

        private string GetServerityCode(string commentText)
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

        private string GetCommentMessageInternal(string commentText)
        {
            string commentPrefix = @"//\sDPR:\s*(\[CRI\]|\[MAJ\]|\[CRC\])?";
            Regex rx = new Regex(commentPrefix, RegexOptions.IgnoreCase);

            return rx.Replace(commentText, "").Trim();
        }

        public int GetStartLineNumber(SnapshotSpan span)
        {
            return textService.GetStartLineNumber(span);
        }

        public int GetEndLineNumber(SnapshotSpan span)
        {
            return textService.GetEndLineNumber(span);
        }

        public string GetServerity(SnapshotSpan span)
        {
            var text = textService.GetText(span);
            return GetServerityCode(text);
        }

        public string GetCommentMessage(SnapshotSpan span)
        {
            var text = textService.GetText(span);
            return GetCommentMessageInternal(text);
        }

        public string GetServerity(string commentText)
        {
            return GetServerityCode(commentText);
        }

        public string GetCommentMessage(string commentText)
        {
            return GetCommentMessageInternal(commentText);
        }

        public Span GetLineSpan(SnapshotSpan snapshotSpan)
        {
            return textService.GetLineSpan(snapshotSpan);
        }

        public SourceCodeJsonFormat ConverToSourceCodeJsonFormat(string filePath, string fileContent)
        {
            return new SourceCodeJsonFormat()
            {
                SnapshotId = 0,
                GroupId = 0,
                ModulePath = ConvertFileDelimiterForDexterServer(Path.GetDirectoryName(filePath)),
                FileName = Path.GetFileName(filePath),
                SourceCode = textService.Base64Encoding(fileContent)
            };
        }
    }
}
