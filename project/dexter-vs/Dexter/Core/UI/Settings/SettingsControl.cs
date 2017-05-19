using System;
using System.ComponentModel;
using System.Windows.Forms;
using Dexter.Common.Config;
using Dexter.Config.Providers;
using Dexter.Config.Validation;

namespace Dexter.UI.Settings
{
    /// <summary>
    /// Control for loading and saving Dexter settings by user
    /// </summary>
    public sealed partial class SettingsControl : UserControl, IDexterInfoProvider
    {
        private readonly IDexterInfoProvider dexterInfoProvider;

        /// <summary>
        /// DexterInfo validator
        /// </summary>
        private readonly DexterInfoValidator validator = new DexterInfoValidator();

        /// <summary>
        /// Creates new SettingsControl
        /// </summary>
        /// <param name="dexterInfoProvider">object to load and save Dexter settings </param>
        public SettingsControl(IDexterInfoProvider dexterInfoProvider) : base()
        {
            this.dexterInfoProvider = dexterInfoProvider;
            InitializeComponent();
        }

        /// <summary>
        /// Disable/Enable server settings controls if standalone chceckbox is changed
        /// </summary>
        private void standaloneCheckBox_CheckedChanged(object sender, EventArgs e)
        {
            bool standaloneAnalysis = standaloneCheckBox.Checked;
            serverTextBox.Enabled = !standaloneAnalysis;
            serverLabel.Enabled = !standaloneAnalysis;
            userNameTextBox.Enabled = !standaloneAnalysis;
            userNameLabel.Enabled = !standaloneAnalysis;
            userPasswordTextBox.Enabled = !standaloneAnalysis;
            userPasswordLabel.Enabled = !standaloneAnalysis;
            serverIndicator.Visible = !standaloneAnalysis;
            userIndicator.Visible = !standaloneAnalysis;
            testConnectionButton.Enabled = !standaloneAnalysis;
        }

        private void dexterPathTextBox_Validating(object sender, CancelEventArgs e)
        {
            dexterPathIndicator.Valid =  validator.ValidateDexterPath(GetDexterInfoFromSettings());
        }

        private void dexterPathTextBox_TextChanged(object sender, EventArgs e)
        {
            dexterPathIndicator.Valid = validator.ValidateDexterPath(GetDexterInfoFromSettings());
        }
        
        private void testConnectionButton_Click(object sender, EventArgs e)
        {
            DexterInfo dexterInfo = GetDexterInfoFromSettings();
            string message;

            bool serverValid = validator.ValidateServerConnection(dexterInfo, out message);
            bool userValid = false;

            if (serverValid)
            {
                userValid = validator.ValidateUserCredentials(dexterInfo, out message);
            }

            serverIndicator.Valid = serverValid;
            userIndicator.Valid = userValid;
            connectionStatusLabel.Text = message;
        }

        private void dexterPathButton_Click(object sender, EventArgs e)
        {
            FolderBrowserDialog fbd = new FolderBrowserDialog();
            DialogResult result = fbd.ShowDialog();
            if (result == DialogResult.OK)
            {
                string dexterPath = fbd.SelectedPath;
                dexterPathTextBox.Text = dexterPath;
            }
        }

        /// <summary>
        /// Creates DexterInfo object from user settings 
        /// </summary>
        /// <returns></returns>
        DexterInfo GetDexterInfoFromSettings()
        {
            Uri serverAddress;

            bool uriCreated = Uri.TryCreate(serverTextBox.Text, UriKind.Absolute, out serverAddress);

            string username = userNameTextBox.IsPlaceholderUsed ? "" : userNameTextBox.Text;
            string password = userPasswordTextBox.IsPlaceholderUsed ? "" : userPasswordTextBox.Text;

            return new DexterInfo()
            {
                dexterHome = dexterPathTextBox.Text,
                dexterServerIp = uriCreated ? serverAddress.Host : "",
                dexterServerPort = uriCreated ? serverAddress.Port : 0,
                userName = username,
                userPassword = password,
                standalone = standaloneCheckBox.Checked
            };
        }

        /// <summary>
        /// Validates and saves user settings 
        /// </summary>
        /// <returns>true, if settings were saved</returns>
        public bool ValidateAndSave()
        {
            string result;
            DexterInfo dexterInfo = GetDexterInfoFromSettings();

            if (!validator.ValidateDexterPath(dexterInfo))
            {
                MessageBox.Show("Dexter wasn't found in given path. You cannot perform analysis until you set a proper path.", "Dexter error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return false;
            }

            if (!standaloneCheckBox.Checked)
            {
                if (!validator.ValidateServerConnection(dexterInfo, out result))
                {
                    MessageBox.Show("Couldn't connect to Dexter server. Setting analysis mode to standalone", "Dexter warning", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                    standaloneCheckBox.Checked = true;
                    dexterInfo.standalone = true;
                }
                else if (!validator.ValidateUserCredentials(dexterInfo, out result))
                {
                    MessageBox.Show("Couldn't login to Dexter server. Setting analysis mode to standalone", "Dexter warning", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                    standaloneCheckBox.Checked = true;
                    dexterInfo.standalone = true;
                }
            }
            Save(dexterInfo);
            return true;
        }

        public void Save(DexterInfo dexterInfo)
        {
            dexterInfoProvider.Save(dexterInfo);
        }

        DexterInfo IDexterInfoProvider.Load()
        {
            DexterInfo dexterInfo = dexterInfoProvider.Load();

            dexterPathTextBox.Text = dexterInfo.dexterHome;
            serverTextBox.Text = string.IsNullOrEmpty(dexterInfo.dexterServerIp) ? "" : string.Format("http://{0}:{1}", dexterInfo.dexterServerIp, dexterInfo.dexterServerPort);
            userNameTextBox.Text = dexterInfo.userName;
            userPasswordTextBox.Text = dexterInfo.userPassword;
            standaloneCheckBox.Checked = dexterInfo.standalone;

            return dexterInfo;
        }
    }
}
