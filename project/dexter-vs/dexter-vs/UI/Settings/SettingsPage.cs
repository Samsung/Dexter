using dexter_vs.Config;
using dexter_vs.Config.Providers;
using EnvDTE;
using EnvDTE80;
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
                IDexterInfoProvider dexterInfoProvider = new SettingsStoreDexterInfoProvider(ServiceProvider.GlobalProvider);
                settingControl = new SettingsControl(dexterInfoProvider);
                return settingControl;
            }
        }

        protected override void OnActivate(CancelEventArgs e)
        {
            ((IDexterInfoProvider)settingControl).Load();
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
