using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using dexter_vs.Analysis.Config;
using System.Net;
using System.IO;

namespace dexter_vs.UI.Config
{
    public partial class SettingsWindow : Form, IDexterInfoProvider
    {
        /// <summary>
        /// DexterInfo validator
        /// </summary>
        private readonly DexterInfoValidator validator = new DexterInfoValidator();

        public SettingsWindow()
        {
            InitializeComponent();
            Shown += SettingsWindow_Shown;
        }


        private void SettingsWindow_Shown(object sender, EventArgs e)
        {
            loadConfiguration();
        }

        /// <summary>
        /// Load configuration values from file 
        /// </summary>
        private void loadConfiguration()
        {
            if (File.Exists(Configuration.DefaultConfigurationPath))
            {
                Configuration configuration = Configuration.Load();
                dexterPathTextBox.Text = configuration.dexterHome;
                serverTextBox.Text = string.Format("http://{0}:{1}",configuration.dexterServerIp, configuration.dexterServerPort);
                userNameTextBox.Text = configuration.userName;
                userPasswordTextBox.Text = configuration.userPassword;
                standaloneCheckBox.Checked = configuration.standalone;
            }
        }

        /// <summary>
        /// Saves configuration to file
        /// </summary>
        private void saveConfiguration()
        {
            DexterInfo dexterInfo = Create();
            Configuration configuration = new Configuration(new ProjectInfo(), dexterInfo);
            configuration.Save();
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

        private void cancelButton_Click(object sender, EventArgs e)
        {
            Close();
        }

        private void dexterPathTextBox_Validating(object sender, CancelEventArgs e)
        {
            dexterPathIndicator.Valid =  validator.ValidateDexterPath(Create());
        }

        private void dexterPathTextBox_TextChanged(object sender, EventArgs e)
        {
            dexterPathIndicator.Valid = validator.ValidateDexterPath(Create());
        }
        
        private void testConnectionButton_Click(object sender, EventArgs e)
        {
            DexterInfo dexterInfo = Create();
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

        /// <summary>
        /// Creates DexterInfo object from user settings 
        /// </summary>
        /// <returns></returns>
        public DexterInfo Create()
        {
            Uri serverAddress;
            
            bool uriCreated = Uri.TryCreate(serverTextBox.Text, UriKind.Absolute, out serverAddress);

            string username = userNameTextBox.IsPlaceholderUsed ? "" : userNameTextBox.Text;
            string password = userPasswordTextBox.IsPlaceholderUsed ? " " : userPasswordTextBox.Text;

            return new DexterInfo()
            {
                dexterHome = dexterPathTextBox.Text,
                dexterServerIp = uriCreated ? serverAddress.Host : "http:\\dexter-server",
                dexterServerPort = uriCreated ? serverAddress.Port.ToString() : "0000",
                userName = username,
                userPassword = password,
                standalone = standaloneCheckBox.Checked
            };
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

        private void okButton_Click(object sender, EventArgs e)
        {
            string result;
            DexterInfo dexterInfo = Create();

            if (!validator.ValidateDexterPath(dexterInfo))
            {
                MessageBox.Show("Dexter wasn't found in given path. You cannot perform analysis until you set a proper path.", "Dexter error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }
            
            if (!standaloneCheckBox.Checked && !validator.ValidateServerConnection(dexterInfo, out result))
            {
                MessageBox.Show("Couldn't connect to Dexter server. Setting analysis mode to standalone", "Dexter warning", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                standaloneCheckBox.Checked = true;
            }
            else
            {
                if (!validator.ValidateUserCredentials(dexterInfo, out result))
                {
                    MessageBox.Show("Couldn't login to Dexter server. Setting analysis mode to standalone", "Dexter warning", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                    standaloneCheckBox.Checked = true;
                }
            }
            saveConfiguration();
            Close();
        }

    }
}
