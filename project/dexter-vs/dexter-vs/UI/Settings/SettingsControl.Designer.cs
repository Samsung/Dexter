namespace dexter_vs.UI.Settings
{
    partial class SettingsControl
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(SettingsControl));
            this.serverSettingsGroupBox = new System.Windows.Forms.GroupBox();
            this.connectionStatusLabel = new System.Windows.Forms.Label();
            this.testConnectionButton = new System.Windows.Forms.Button();
            this.standaloneCheckBox = new System.Windows.Forms.CheckBox();
            this.userPasswordLabel = new System.Windows.Forms.Label();
            this.userNameLabel = new System.Windows.Forms.Label();
            this.serverLabel = new System.Windows.Forms.Label();
            this.dexterSettingsGroupBox = new System.Windows.Forms.GroupBox();
            this.dexterPathButton = new System.Windows.Forms.Button();
            this.dexterPathLabel = new System.Windows.Forms.Label();
            this.tableLayoutPanel1 = new System.Windows.Forms.TableLayoutPanel();
            this.dexterPathIndicator = new dexter_vs.UI.Settings.ValidationPicture();
            this.dexterPathTextBox = new dexter_vs.UI.Settings.PlaceholderTextBox();
            this.userNameTextBox = new dexter_vs.UI.Settings.PlaceholderTextBox();
            this.userPasswordTextBox = new dexter_vs.UI.Settings.PlaceholderTextBox();
            this.userIndicator = new dexter_vs.UI.Settings.ValidationPicture();
            this.serverIndicator = new dexter_vs.UI.Settings.ValidationPicture();
            this.serverTextBox = new dexter_vs.UI.Settings.PlaceholderTextBox();
            this.serverSettingsGroupBox.SuspendLayout();
            this.dexterSettingsGroupBox.SuspendLayout();
            this.tableLayoutPanel1.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dexterPathIndicator)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.userIndicator)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.serverIndicator)).BeginInit();
            this.SuspendLayout();
            // 
            // serverSettingsGroupBox
            // 
            this.serverSettingsGroupBox.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
            | System.Windows.Forms.AnchorStyles.Right)));
            this.serverSettingsGroupBox.Controls.Add(this.tableLayoutPanel1);
            this.serverSettingsGroupBox.Controls.Add(this.connectionStatusLabel);
            this.serverSettingsGroupBox.Controls.Add(this.testConnectionButton);
            this.serverSettingsGroupBox.Controls.Add(this.standaloneCheckBox);
            this.serverSettingsGroupBox.Controls.Add(this.userIndicator);
            this.serverSettingsGroupBox.Controls.Add(this.serverIndicator);
            this.serverSettingsGroupBox.Controls.Add(this.serverLabel);
            this.serverSettingsGroupBox.Controls.Add(this.serverTextBox);
            this.serverSettingsGroupBox.Location = new System.Drawing.Point(3, 105);
            this.serverSettingsGroupBox.Name = "serverSettingsGroupBox";
            this.serverSettingsGroupBox.Size = new System.Drawing.Size(481, 225);
            this.serverSettingsGroupBox.TabIndex = 1;
            this.serverSettingsGroupBox.TabStop = false;
            this.serverSettingsGroupBox.Text = "Server Settings";
            // 
            // connectionStatusLabel
            // 
            this.connectionStatusLabel.AutoSize = true;
            this.connectionStatusLabel.Location = new System.Drawing.Point(108, 150);
            this.connectionStatusLabel.Name = "connectionStatusLabel";
            this.connectionStatusLabel.Size = new System.Drawing.Size(0, 13);
            this.connectionStatusLabel.TabIndex = 17;
            // 
            // testConnectionButton
            // 
            this.testConnectionButton.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.testConnectionButton.Location = new System.Drawing.Point(9, 144);
            this.testConnectionButton.Name = "testConnectionButton";
            this.testConnectionButton.Size = new System.Drawing.Size(93, 24);
            this.testConnectionButton.TabIndex = 16;
            this.testConnectionButton.Text = "Test connection";
            this.testConnectionButton.UseVisualStyleBackColor = true;
            this.testConnectionButton.Click += new System.EventHandler(this.testConnectionButton_Click);
            // 
            // standaloneCheckBox
            // 
            this.standaloneCheckBox.AutoSize = true;
            this.standaloneCheckBox.Location = new System.Drawing.Point(9, 183);
            this.standaloneCheckBox.Name = "standaloneCheckBox";
            this.standaloneCheckBox.Size = new System.Drawing.Size(120, 17);
            this.standaloneCheckBox.TabIndex = 15;
            this.standaloneCheckBox.Text = "Standalone analysis";
            this.standaloneCheckBox.UseVisualStyleBackColor = true;
            this.standaloneCheckBox.CheckedChanged += new System.EventHandler(this.standaloneCheckBox_CheckedChanged);
            // 
            // userPasswordLabel
            // 
            this.userPasswordLabel.AutoSize = true;
            this.userPasswordLabel.Location = new System.Drawing.Point(221, 0);
            this.userPasswordLabel.Name = "userPasswordLabel";
            this.userPasswordLabel.Size = new System.Drawing.Size(77, 12);
            this.userPasswordLabel.TabIndex = 13;
            this.userPasswordLabel.Text = "User password";
            // 
            // userNameLabel
            // 
            this.userNameLabel.AutoSize = true;
            this.userNameLabel.Location = new System.Drawing.Point(3, 0);
            this.userNameLabel.Name = "userNameLabel";
            this.userNameLabel.Size = new System.Drawing.Size(58, 12);
            this.userNameLabel.TabIndex = 11;
            this.userNameLabel.Text = "User name";
            // 
            // serverLabel
            // 
            this.serverLabel.AutoSize = true;
            this.serverLabel.Location = new System.Drawing.Point(6, 35);
            this.serverLabel.Name = "serverLabel";
            this.serverLabel.Size = new System.Drawing.Size(115, 13);
            this.serverLabel.TabIndex = 7;
            this.serverLabel.Text = "Dexter Server address:";
            // 
            // dexterSettingsGroupBox
            // 
            this.dexterSettingsGroupBox.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
            | System.Windows.Forms.AnchorStyles.Right)));
            this.dexterSettingsGroupBox.Controls.Add(this.dexterPathIndicator);
            this.dexterSettingsGroupBox.Controls.Add(this.dexterPathButton);
            this.dexterSettingsGroupBox.Controls.Add(this.dexterPathLabel);
            this.dexterSettingsGroupBox.Controls.Add(this.dexterPathTextBox);
            this.dexterSettingsGroupBox.Location = new System.Drawing.Point(3, 0);
            this.dexterSettingsGroupBox.Name = "dexterSettingsGroupBox";
            this.dexterSettingsGroupBox.Size = new System.Drawing.Size(481, 87);
            this.dexterSettingsGroupBox.TabIndex = 0;
            this.dexterSettingsGroupBox.TabStop = false;
            this.dexterSettingsGroupBox.Text = "Dexter Settings";
            // 
            // dexterPathButton
            // 
            this.dexterPathButton.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.dexterPathButton.Font = new System.Drawing.Font("Microsoft Sans Serif", 9.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.dexterPathButton.Location = new System.Drawing.Point(411, 44);
            this.dexterPathButton.Name = "dexterPathButton";
            this.dexterPathButton.Size = new System.Drawing.Size(34, 24);
            this.dexterPathButton.TabIndex = 4;
            this.dexterPathButton.Text = "...";
            this.dexterPathButton.UseVisualStyleBackColor = true;
            this.dexterPathButton.Click += new System.EventHandler(this.dexterPathButton_Click);
            // 
            // dexterPathLabel
            // 
            this.dexterPathLabel.AutoSize = true;
            this.dexterPathLabel.Location = new System.Drawing.Point(6, 32);
            this.dexterPathLabel.Name = "dexterPathLabel";
            this.dexterPathLabel.Size = new System.Drawing.Size(65, 13);
            this.dexterPathLabel.TabIndex = 1;
            this.dexterPathLabel.Text = "Dexter path:";
            // 
            // tableLayoutPanel1
            // 
            this.tableLayoutPanel1.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
            | System.Windows.Forms.AnchorStyles.Right)));
            this.tableLayoutPanel1.ColumnCount = 2;
            this.tableLayoutPanel1.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Percent, 50F));
            this.tableLayoutPanel1.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Percent, 50F));
            this.tableLayoutPanel1.Controls.Add(this.userNameTextBox, 0, 1);
            this.tableLayoutPanel1.Controls.Add(this.userPasswordTextBox, 1, 1);
            this.tableLayoutPanel1.Controls.Add(this.userNameLabel, 0, 0);
            this.tableLayoutPanel1.Controls.Add(this.userPasswordLabel, 1, 0);
            this.tableLayoutPanel1.Location = new System.Drawing.Point(9, 87);
            this.tableLayoutPanel1.Margin = new System.Windows.Forms.Padding(0);
            this.tableLayoutPanel1.Name = "tableLayoutPanel1";
            this.tableLayoutPanel1.RowCount = 2;
            this.tableLayoutPanel1.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 12F));
            this.tableLayoutPanel1.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 20F));
            this.tableLayoutPanel1.Size = new System.Drawing.Size(436, 41);
            this.tableLayoutPanel1.TabIndex = 4;
            // 
            // dexterPathIndicator
            // 
            this.dexterPathIndicator.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.dexterPathIndicator.BackgroundImageLayout = System.Windows.Forms.ImageLayout.Center;
            this.dexterPathIndicator.Location = new System.Drawing.Point(451, 44);
            this.dexterPathIndicator.Name = "dexterPathIndicator";
            this.dexterPathIndicator.Size = new System.Drawing.Size(24, 24);
            this.dexterPathIndicator.TabIndex = 5;
            this.dexterPathIndicator.TabStop = false;
            this.dexterPathIndicator.Valid = false;
            this.dexterPathIndicator.ValidImage = ((System.Drawing.Image)(resources.GetObject("dexterPathIndicator.ValidImage")));
            this.dexterPathIndicator.WrongImage = ((System.Drawing.Image)(resources.GetObject("dexterPathIndicator.WrongImage")));
            // 
            // dexterPathTextBox
            // 
            this.dexterPathTextBox.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
            | System.Windows.Forms.AnchorStyles.Right)));
            this.dexterPathTextBox.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.dexterPathTextBox.ForeColor = System.Drawing.SystemColors.InactiveCaption;
            this.dexterPathTextBox.Location = new System.Drawing.Point(9, 48);
            this.dexterPathTextBox.Name = "dexterPathTextBox";
            this.dexterPathTextBox.PlaceholderColor = System.Drawing.SystemColors.InactiveCaption;
            this.dexterPathTextBox.PlaceholderFont = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.dexterPathTextBox.PlaceholderText = "e.g. D:\\Dexter";
            this.dexterPathTextBox.Size = new System.Drawing.Size(396, 20);
            this.dexterPathTextBox.TabIndex = 0;
            this.dexterPathTextBox.Text = "e.g. D:\\Dexter";
            this.dexterPathTextBox.WordWrap = false;
            this.dexterPathTextBox.TextChanged += new System.EventHandler(this.dexterPathTextBox_TextChanged);
            this.dexterPathTextBox.Validating += new System.ComponentModel.CancelEventHandler(this.dexterPathTextBox_Validating);
            // 
            // userNameTextBox
            // 
            this.userNameTextBox.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
            | System.Windows.Forms.AnchorStyles.Right)));
            this.userNameTextBox.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.userNameTextBox.ForeColor = System.Drawing.SystemColors.InactiveCaption;
            this.userNameTextBox.Location = new System.Drawing.Point(0, 15);
            this.userNameTextBox.Margin = new System.Windows.Forms.Padding(0, 3, 3, 0);
            this.userNameTextBox.Name = "userNameTextBox";
            this.userNameTextBox.PlaceholderColor = System.Drawing.SystemColors.InactiveCaption;
            this.userNameTextBox.PlaceholderFont = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.userNameTextBox.PlaceholderText = "e.g. JohnDoe";
            this.userNameTextBox.Size = new System.Drawing.Size(215, 20);
            this.userNameTextBox.TabIndex = 10;
            this.userNameTextBox.Text = "e.g. JohnDoe";
            this.userNameTextBox.WordWrap = false;
            // 
            // userPasswordTextBox
            // 
            this.userPasswordTextBox.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
            | System.Windows.Forms.AnchorStyles.Right)));
            this.userPasswordTextBox.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.userPasswordTextBox.ForeColor = System.Drawing.SystemColors.InactiveCaption;
            this.userPasswordTextBox.Location = new System.Drawing.Point(221, 15);
            this.userPasswordTextBox.Margin = new System.Windows.Forms.Padding(3, 3, 0, 0);
            this.userPasswordTextBox.Name = "userPasswordTextBox";
            this.userPasswordTextBox.PasswordChar = '•';
            this.userPasswordTextBox.PlaceholderColor = System.Drawing.SystemColors.InactiveCaption;
            this.userPasswordTextBox.PlaceholderFont = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.userPasswordTextBox.PlaceholderText = "e.g. myPassword";
            this.userPasswordTextBox.Size = new System.Drawing.Size(215, 20);
            this.userPasswordTextBox.TabIndex = 12;
            this.userPasswordTextBox.Text = "e.g. myPassword";
            this.userPasswordTextBox.WordWrap = false;
            // 
            // userIndicator
            // 
            this.userIndicator.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.userIndicator.BackgroundImageLayout = System.Windows.Forms.ImageLayout.Center;
            this.userIndicator.Location = new System.Drawing.Point(451, 104);
            this.userIndicator.Name = "userIndicator";
            this.userIndicator.Size = new System.Drawing.Size(24, 24);
            this.userIndicator.TabIndex = 14;
            this.userIndicator.TabStop = false;
            this.userIndicator.Valid = false;
            this.userIndicator.ValidImage = ((System.Drawing.Image)(resources.GetObject("userIndicator.ValidImage")));
            this.userIndicator.WrongImage = ((System.Drawing.Image)(resources.GetObject("userIndicator.WrongImage")));
            // 
            // serverIndicator
            // 
            this.serverIndicator.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.serverIndicator.BackgroundImageLayout = System.Windows.Forms.ImageLayout.Center;
            this.serverIndicator.Location = new System.Drawing.Point(451, 47);
            this.serverIndicator.Name = "serverIndicator";
            this.serverIndicator.Size = new System.Drawing.Size(24, 24);
            this.serverIndicator.TabIndex = 9;
            this.serverIndicator.TabStop = false;
            this.serverIndicator.Valid = false;
            this.serverIndicator.ValidImage = ((System.Drawing.Image)(resources.GetObject("serverIndicator.ValidImage")));
            this.serverIndicator.WrongImage = ((System.Drawing.Image)(resources.GetObject("serverIndicator.WrongImage")));
            // 
            // serverTextBox
            // 
            this.serverTextBox.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
            | System.Windows.Forms.AnchorStyles.Right)));
            this.serverTextBox.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.serverTextBox.ForeColor = System.Drawing.SystemColors.InactiveCaption;
            this.serverTextBox.Location = new System.Drawing.Point(9, 51);
            this.serverTextBox.Name = "serverTextBox";
            this.serverTextBox.PlaceholderColor = System.Drawing.SystemColors.InactiveCaption;
            this.serverTextBox.PlaceholderFont = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.serverTextBox.PlaceholderText = "e.g. 127.0.0.2:8081";
            this.serverTextBox.Size = new System.Drawing.Size(436, 20);
            this.serverTextBox.TabIndex = 6;
            this.serverTextBox.Text = "e.g. 127.0.0.2:8081";
            this.serverTextBox.WordWrap = false;
            // 
            // SettingsPage
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.Controls.Add(this.dexterSettingsGroupBox);
            this.Controls.Add(this.serverSettingsGroupBox);
            this.Name = "SettingsPage";
            this.Size = new System.Drawing.Size(487, 334);
            this.serverSettingsGroupBox.ResumeLayout(false);
            this.serverSettingsGroupBox.PerformLayout();
            this.dexterSettingsGroupBox.ResumeLayout(false);
            this.dexterSettingsGroupBox.PerformLayout();
            this.tableLayoutPanel1.ResumeLayout(false);
            this.tableLayoutPanel1.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dexterPathIndicator)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.userIndicator)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.serverIndicator)).EndInit();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.GroupBox serverSettingsGroupBox;
        private System.Windows.Forms.GroupBox dexterSettingsGroupBox;
        private ValidationPicture dexterPathIndicator;
        private System.Windows.Forms.Button dexterPathButton;
        private System.Windows.Forms.Label dexterPathLabel;
        private PlaceholderTextBox dexterPathTextBox;
        private ValidationPicture userIndicator;
        private System.Windows.Forms.Label userPasswordLabel;
        private PlaceholderTextBox userPasswordTextBox;
        private System.Windows.Forms.Label userNameLabel;
        private PlaceholderTextBox userNameTextBox;
        private ValidationPicture serverIndicator;
        private System.Windows.Forms.Label serverLabel;
        private PlaceholderTextBox serverTextBox;
        private System.Windows.Forms.CheckBox standaloneCheckBox;
        private System.Windows.Forms.Button testConnectionButton;
        private System.Windows.Forms.Label connectionStatusLabel;
        private System.Windows.Forms.TableLayoutPanel tableLayoutPanel1;
    }
}