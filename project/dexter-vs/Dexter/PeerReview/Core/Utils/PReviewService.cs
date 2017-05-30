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
    public interface IPReviewService
    {
        DexterResult ConvertToDexterResult(ITextDocument textDocument, IList<PReviewComment> comments);
    }

    public class PReviewService : IPReviewService
    {
        IDexterTextService textService;

        static IPReviewService instace;

        static public IPReviewService Instance
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

        public PReviewService(IDexterTextService textService)
        {
            this.textService = textService;
        }

        public DexterResult ConvertToDexterResult(ITextDocument textDocument, IList<PReviewComment> comments)
        {
            string fileName = Path.GetFileName(textDocument.FilePath);

            return  new DexterResult()
            {
                FullFilePath = textDocument.FilePath,
                FileName = fileName,
                DefectCount = comments.Count,
                DefectList = ConvertToDefectList(fileName, comments)
            };
        }

        private IList<DexterDefect> ConvertToDefectList(string fileName, IList<PReviewComment> comments)
        {
            var defects = new List<DexterDefect>();

            foreach (var comment in comments)
            {
                var commentText = textService.getText(comment.Span);
                var serverityCode = getServerityCode(commentText);
                var defect = new DexterDefect()
                {
                    CategoryName = "PeerReview",
                    AnalysisType = "FILE",
                    Language = "C_SHARP",
                    ToolName = "dexter-peerreview",
                    FileName = fileName,
                    SeverityCode = serverityCode,
                    CheckerCode = "DPR_" + serverityCode,
                    Message = "",
                    Occurences = createDexterOccurences(comment)
                };

                defects.Add(defect);
            }

            return defects;
        }

        private IList<DexterOccurence> createDexterOccurences(PReviewComment comment)
        {
            var commentText = textService.getText(comment.Span);
            var occurences = new List<DexterOccurence>();

            occurences.Add(new DexterOccurence()
            {
                StringValue = "DPR",
                Message = getCommentMessage(commentText),
                StartLine = textService.getStartLineNumber(comment.Span),
                EndLine = textService.getEndLineNumber(comment.Span)
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
    }
}
