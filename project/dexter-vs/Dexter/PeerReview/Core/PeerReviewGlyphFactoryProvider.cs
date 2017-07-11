using System.ComponentModel.Composition;
using System.Windows;
using System.Windows.Shapes;
using System.Windows.Media;
using System.Windows.Controls;
using Microsoft.VisualStudio.Text;
using Microsoft.VisualStudio.Text.Editor;
using Microsoft.VisualStudio.Text.Formatting;
using Microsoft.VisualStudio.Text.Tagging;
using Microsoft.VisualStudio.Text.Classification;
using Microsoft.VisualStudio.Utilities;

namespace Dexter.PeerReview
{
    /// <summary>
    /// Provides factory function for PeerReviewGlyphFactory
    /// </summary>
    [Export(typeof(IGlyphFactoryProvider))]
    [Name("PReviewGlyph")]
    [Order(After = Priority.Default)]
    [ContentType("code")]
    [TagType(typeof(PReviewTag))]
    internal sealed class PeerReviewGlyphFactoryProvider : IGlyphFactoryProvider
    {
        /// <summary>
        /// Provides a instance of PeerReviewGlyphFactory
        /// </summary>
        public IGlyphFactory GetGlyphFactory(IWpfTextView view, IWpfTextViewMargin margin)
        {
            return new PeerReviewGlyphFactory();
        }
    }
}
