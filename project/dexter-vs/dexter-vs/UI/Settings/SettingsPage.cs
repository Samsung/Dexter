using Microsoft.VisualStudio.Shell;
using System;
using System.ComponentModel;
using System.Runtime.InteropServices;
using System.Windows.Forms;

namespace dexter_vs.UI.Settings
{
    /// <summary>
    /// Dexter Settings page (available from 'Tools/Options...' menu)
    /// </summary>
    [Guid("00000000-0000-0000-0000-000000000000")]
    public class SettingsPage : DialogPage
    {
        private SettingsControl settingControl;

        protected override IWin32Window Window
        {
            get
            {
                settingControl = new SettingsControl();
                return settingControl;
            }
        }

        protected override void OnActivate(CancelEventArgs e)
        {
            settingControl.LoadConfiguration();
        }

        protected override void OnApply(PageApplyEventArgs e)
        {
            bool saved = settingControl.ValidateAndSave();

            if (!saved)
            {
                e.ApplyBehavior = ApplyKind.CancelNoNavigate;
            }

            base.OnApply(e);
        }
    }
}
