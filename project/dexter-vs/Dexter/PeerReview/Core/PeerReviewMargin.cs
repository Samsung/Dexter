﻿using System;
using System.Windows;
using System.Windows.Media;
using Microsoft.VisualStudio.Text.Editor;
using System.Collections.Generic;

namespace Dexter.PeerReview
{
    /// <summary>
    /// Provides the visual of margin markes for peer review comments
    /// </summary>
    internal class PeerReviewMargin : FrameworkElement, IWpfTextViewMargin
    {
        /// <summary>
        /// Margin name.
        /// </summary>
        public const string MarginName = "PReviewMargin";

        /// <summary>
        /// A value indicating whether the object is disposed.
        /// </summary>
        private bool isDisposed;

        private IWpfTextView textView;
        private IVerticalScrollBar scrollBar;

        private const double markPadding = 1.0;
        private const double markThickness = 4.5;
        private Brush markBrush = new SolidColorBrush(Colors.Purple);

        public PeerReviewMargin(IWpfTextView textView, IVerticalScrollBar scrollBar)
        {
            this.textView = textView;
            this.textView.LayoutChanged += OnLayoutChanged;
            this.scrollBar = scrollBar;
            this.Width = 6.0;
        }

        private void OnLayoutChanged(object sender, TextViewLayoutChangedEventArgs e)
        {
            InvalidateVisual();
        }

        /// <summary>
        /// Provides peer review comment list
        /// </summary>
        /// <returns></returns>
        public IList<PeerReviewSnapshotComment> getPReviewComments()
        {
            try
            {
                var commentGetter = (ICommentsOwner<PeerReviewSnapshotComment>)
                    textView.TextBuffer.Properties.GetProperty(PeerReviewConstants.COMMENT_OWNER);
                return commentGetter.Comments;
            } catch (KeyNotFoundException)
            {
                return new List<PeerReviewSnapshotComment>();
            }
        }

        protected override void OnRender(DrawingContext drawingContext)
        {
            base.OnRender(drawingContext);


            foreach (var comment in getPReviewComments())
            {
                double y = scrollBar.GetYCoordinateOfBufferPosition(comment.SnapShotSpan.Start);
                this.DrawMark(drawingContext, markBrush, y);
            }
        }

        private void DrawMark(DrawingContext drawingContext, Brush brush, double y)
        {
            drawingContext.DrawRectangle(brush, null, new Rect(
                    markPadding, y - markThickness * 0.5, this.Width - markPadding * 2.0, markThickness));
        }

        #region IWpfTextViewMargin

        /// <summary>
        /// Gets the <see cref="Sytem.Windows.FrameworkElement"/> that implements the visual representation of the margin.
        /// </summary>
        /// <exception cref="ObjectDisposedException">The margin is disposed.</exception>
        public FrameworkElement VisualElement
        {
            // Since this margin implements Canvas, this is the object which renders
            // the margin.
            get
            {
                this.ThrowIfDisposed();
                return this;
            }
        }

        #endregion

        #region ITextViewMargin

        /// <summary>
        /// Gets the size of the margin.
        /// </summary>
        /// <remarks>
        /// For a horizontal margin this is the height of the margin,
        /// since the width will be determined by the <see cref="ITextView"/>.
        /// For a vertical margin this is the width of the margin,
        /// since the height will be determined by the <see cref="ITextView"/>.
        /// </remarks>
        /// <exception cref="ObjectDisposedException">The margin is disposed.</exception>
        public double MarginSize
        {
            get
            {
                this.ThrowIfDisposed();

                // Since this is a horizontal margin, its width will be bound to the width of the text view.
                // Therefore, its size is its height.
                return this.ActualHeight;
            }
        }

        /// <summary>
        /// Gets a value indicating whether the margin is enabled.
        /// </summary>
        /// <exception cref="ObjectDisposedException">The margin is disposed.</exception>
        public bool Enabled
        {
            get
            {
                this.ThrowIfDisposed();

                // The margin should always be enabled
                return true;
            }
        }

        /// <summary>
        /// Gets the <see cref="ITextViewMargin"/> with the given <paramref name="marginName"/> or null if no match is found
        /// </summary>
        /// <param name="marginName">The name of the <see cref="ITextViewMargin"/></param>
        /// <returns>The <see cref="ITextViewMargin"/> named <paramref name="marginName"/>, or null if no match is found.</returns>
        /// <remarks>
        /// A margin returns itself if it is passed its own name. If the name does not match and it is a container margin, it
        /// forwards the call to its children. Margin name comparisons are case-insensitive.
        /// </remarks>
        /// <exception cref="ArgumentNullException"><paramref name="marginName"/> is null.</exception>
        public ITextViewMargin GetTextViewMargin(string marginName)
        {
            return string.Equals(marginName, PeerReviewMargin.MarginName, StringComparison.OrdinalIgnoreCase) ? this : null;
        }

        /// <summary>
        /// Disposes an instance of <see cref="EditorMargin1"/> class.
        /// </summary>
        public void Dispose()
        {
            if (!this.isDisposed)
            {
                GC.SuppressFinalize(this);
                this.isDisposed = true;
            }
        }

        #endregion

        /// <summary>
        /// Checks and throws <see cref="ObjectDisposedException"/> if the object is disposed.
        /// </summary>
        private void ThrowIfDisposed()
        {
            if (this.isDisposed)
            {
                throw new ObjectDisposedException(MarginName);
            }
        }
    }
}
