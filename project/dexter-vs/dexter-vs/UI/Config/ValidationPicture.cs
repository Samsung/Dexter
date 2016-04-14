using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace dexter_vs.UI.Config
{
    /// <summary>
    /// Picture with two states "valid" and "wrong"
    /// </summary>
    internal class ValidationPicture: PictureBox
    {
        /// <summary>
        /// Image to display in "valid" state
        /// </summary>
        public Image ValidImage { get; set; }

        /// <summary>
        /// Image to display in "wrong" state
        /// </summary>
        public Image WrongImage { get; set; }

        private bool valid;

        public bool Valid
        {
            get
            {
                return valid;
            }
            set
            {
                valid = value;

                BackgroundImage = valid ? ValidImage : WrongImage;
            }
        }

        public ValidationPicture() : base()
        {
            // Update image
            Valid = Valid;
        }

    }
}
