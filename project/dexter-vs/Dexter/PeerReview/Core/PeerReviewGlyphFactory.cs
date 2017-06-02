using System.ComponentModel.Composition;
using System.Windows;
using System.Windows.Shapes;
using System.Windows.Media;
using System.Windows.Controls;
using Microsoft.VisualStudio.Text;
using Microsoft.VisualStudio.Text.Editor;
using Microsoft.VisualStudio.Text.Formatting;
using Microsoft.VisualStudio.Text.Tagging;
using Microsoft.VisualStudio.Utilities;

namespace Dexter.PeerReview
{
    /// <summary>
    /// Provides the visual of peer review glyph
    /// </summary>
    public class PeerReviewGlyphFactory : IGlyphFactory
    {
        const double m_glyphSize = 14.0;

        /// <summary>
        /// Provides the visual of peer review glyph
        /// </summary>
        public UIElement GenerateGlyph(IWpfTextViewLine line, IGlyphTag tag)
        {
            // Ensure we can draw a glyph for this marker.
            if (tag == null || !(tag is PReviewTag))
            {
                return null;
            }

            System.Windows.Shapes.Ellipse ellipse = new Ellipse();
            ellipse.Fill = Brushes.Purple;
            ellipse.StrokeThickness = 2;
            ellipse.Stroke = Brushes.White;
            ellipse.Height = m_glyphSize;
            ellipse.Width = m_glyphSize;

            return ellipse;
        }
    }
}
