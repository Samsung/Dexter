using NUnit.Framework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.VisualStudio.Text.Editor;
using Dexter.PeerReview;
using System.Threading;

namespace Dexter.PeerReview.Tests
{
    [TestFixture]
    public class PReviewGlyphFactoryTest
    {
        IGlyphFactory glyphFactory;

        [SetUp]
        public void SetUp()
        {
            glyphFactory = new PeerReviewGlyphFactory();
        }

        [Test]
        public void GenerateGlyph_returnNull_GivenNullArguments()
        {
            // when
            var glyphElement = glyphFactory.GenerateGlyph(null, null);

            // then
            Assert.Null(glyphElement);
        }

        [Test, Apartment(ApartmentState.STA)]
        public void GenerateGlyph_returnGlyphElement_GivenValidArguments()
        {
            // when
            var glyphElement = glyphFactory.GenerateGlyph(null, new PReviewTag());

            // then
            Assert.NotNull(glyphElement);
        }
    }
}
