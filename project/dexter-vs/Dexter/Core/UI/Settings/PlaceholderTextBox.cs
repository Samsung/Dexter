using System.ComponentModel;
using System.Drawing;
using System.Windows.Forms;

namespace Dexter.UI.Settings
{
    /// <summary>
    /// TextBox with default value 
    /// </summary>
    internal class PlaceholderTextBox: TextBox
    {
        private readonly Font normalFont;

        private readonly Color normalColor;

        public PlaceholderTextBox() : base()
        {
            PlaceholderText = "";
            PlaceholderFont = Font;
            PlaceholderColor = ForeColor;
            normalFont = Font;
            normalColor = ForeColor;
            Validating += (o, p) => updatePlaceholder();
            GotFocus += (o, p) => clearPlaceholder();
            VisibleChanged += (o, p) => updatePlaceholder();
        }

        /// <summary>
        /// Gets or sets placeholder text
        /// </summary>
        [Category("Appearance")]
        public string PlaceholderText
        {
            get;
            set;
        }

        /// <summary>
        /// Gets or sets placeholder font
        /// </summary>
        [Category("Appearance")]
        public Font PlaceholderFont
        {
            get;
            set;
        }

        /// <summary>
        /// Gets or sets placeholder color
        /// </summary>
        [Category("Appearance")]
        public Color PlaceholderColor
        {
            get;
            set;
        }

        /// <summary>
        /// Gets value indicating whther the placeholder is currently used
        /// </summary>
        public bool IsPlaceholderUsed
        {
            get
            {
                return Text == PlaceholderText;
            }
        }

        /// <inheritDoc/>
        public override string Text
        {
            get
            {
                return base.Text;
            }

            set
            {
                base.Text = value;
                updatePlaceholder();
            }
        }

        /// <summary>
        /// Updates placeholder. If text is empty, then sets placeholder text
        /// </summary>
        private void updatePlaceholder()
        {
            if (Text.Length == 0 || Text == PlaceholderText)
            {
                base.Text = PlaceholderText;
                Font = PlaceholderFont;
                ForeColor = PlaceholderColor;
            }
            else
            {
                Font = normalFont;
                ForeColor = normalColor;
            }
        }

        /// <summary>
        /// Clears placeholder 
        /// </summary>
        private void clearPlaceholder()
        {
            if (base.Text == PlaceholderText)
            {
                base.Text = "";
                Font = normalFont;
                ForeColor = normalColor;
            }
        }
    }
}
