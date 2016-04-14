using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace dexter_vs.UI.Config
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
            Validating += updatePlaceholder;
            GotFocus += clearPlaceholder;
            VisibleChanged += updatePlaceholder;
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

        /// <summary>
        /// Updates placeholder. If text is empty, then sets placeholder text
        /// </summary>
        public void UpdatePlaceholder()
        {
            if (Text.Length == 0 || Text == PlaceholderText)
            {
                Text = PlaceholderText;
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
        public void ClearPlaceholder()
        {
            if (Text == PlaceholderText)
            {
                Text = "";
                Font = normalFont;
                ForeColor = normalColor;
            }
        }


        private void updatePlaceholder(object sender, EventArgs e)
        {
            UpdatePlaceholder();
        }

        private void clearPlaceholder(object sender, EventArgs e)
        {
            ClearPlaceholder();
        }
        
    }
}
