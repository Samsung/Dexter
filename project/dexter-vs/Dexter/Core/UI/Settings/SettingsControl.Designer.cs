using System;

namespace Dexter.UI.Settings
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
            this.createUserButton = new System.Windows.Forms.Button();
            this.tableLayoutPanel1 = new System.Windows.Forms.TableLayoutPanel();
            this.userNameTextBox = new Dexter.UI.Settings.PlaceholderTextBox();
            this.userPasswordTextBox = new Dexter.UI.Settings.PlaceholderTextBox();
            this.userNameLabel = new System.Windows.Forms.Label();
            this.userPasswordLabel = new System.Windows.Forms.Label();
            this.connectionStatusLabel = new System.Windows.Forms.Label();
            this.testConnectionButton = new System.Windows.Forms.Button();
            this.standaloneCheckBox = new System.Windows.Forms.CheckBox();
            this.userIndicator = new Dexter.UI.Settings.ValidationPicture();
            this.serverIndicator = new Dexter.UI.Settings.ValidationPicture();
            this.serverLabel = new System.Windows.Forms.Label();
            this.serverTextBox = new Dexter.UI.Settings.PlaceholderTextBox();
            this.dexterSettingsGroupBox = new System.Windows.Forms.GroupBox();
            this.enableDexterHomeCheckBox = new System.Windows.Forms.CheckBox();
            this.dexterPathIndicator = new Dexter.UI.Settings.ValidationPicture();
            this.dexterPathButton = new System.Windows.Forms.Button();
            this.dexterPathLabel = new System.Windows.Forms.Label();
            this.dexterPathTextBox = new Dexter.UI.Settings.PlaceholderTextBox();
            this.analysisOnSaveCheckBox = new System.Windows.Forms.CheckBox();
            this.serverSettingsGroupBox.SuspendLayout();
            this.tableLayoutPanel1.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.userIndicator)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.serverIndicator)).BeginInit();
            this.dexterSettingsGroupBox.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dexterPathIndicator)).BeginInit();
            this.SuspendLayout();
            // 
            // serverSettingsGroupBox
            // 
            this.serverSettingsGroupBox.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.serverSettingsGroupBox.Controls.Add(this.createUserButton);
            this.serverSettingsGroupBox.Controls.Add(this.tableLayoutPanel1);
            this.serverSettingsGroupBox.Controls.Add(this.connectionStatusLabel);
            this.serverSettingsGroupBox.Controls.Add(this.testConnectionButton);
            this.serverSettingsGroupBox.Controls.Add(this.standaloneCheckBox);
            this.serverSettingsGroupBox.Controls.Add(this.userIndicator);
            this.serverSettingsGroupBox.Controls.Add(this.serverIndicator);
            this.serverSettingsGroupBox.Controls.Add(this.serverLabel);
            this.serverSettingsGroupBox.Controls.Add(this.serverTextBox);
            this.serverSettingsGroupBox.Location = new System.Drawing.Point(4, 138);
            this.serverSettingsGroupBox.Margin = new System.Windows.Forms.Padding(4);
            this.serverSettingsGroupBox.Name = "serverSettingsGroupBox";
            this.serverSettingsGroupBox.Padding = new System.Windows.Forms.Padding(4);
            this.serverSettingsGroupBox.Size = new System.Drawing.Size(641, 208);
            this.serverSettingsGroupBox.TabIndex = 1;
            this.serverSettingsGroupBox.TabStop = false;
            this.serverSettingsGroupBox.Text = "Server Settings";
            // 
            // createUserButton
            // 
            this.createUserButton.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.createUserButton.Location = new System.Drawing.Point(143, 140);
            this.createUserButton.Margin = new System.Windows.Forms.Padding(4);
            this.createUserButton.Name = "createUserButton";
            this.createUserButton.Size = new System.Drawing.Size(124, 30);
            this.createUserButton.TabIndex = 18;
            this.createUserButton.Text = "Create user";
            this.createUserButton.UseVisualStyleBackColor = true;
            this.createUserButton.Click += new System.EventHandler(this.createUserButton_Click);
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
            this.tableLayoutPanel1.Location = new System.Drawing.Point(11, 85);
            this.tableLayoutPanel1.Margin = new System.Windows.Forms.Padding(0);
            this.tableLayoutPanel1.Name = "tableLayoutPanel1";
            this.tableLayoutPanel1.RowCount = 2;
            this.tableLayoutPanel1.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 15F));
            this.tableLayoutPanel1.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 25F));
            this.tableLayoutPanel1.Size = new System.Drawing.Size(581, 50);
            this.tableLayoutPanel1.TabIndex = 4;
            // 
            // userNameTextBox
            // 
            this.userNameTextBox.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.userNameTextBox.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.userNameTextBox.ForeColor = System.Drawing.SystemColors.InactiveCaption;
            this.userNameTextBox.Location = new System.Drawing.Point(0, 19);
            this.userNameTextBox.Margin = new System.Windows.Forms.Padding(0, 4, 4, 0);
            this.userNameTextBox.Name = "userNameTextBox";
            this.userNameTextBox.PlaceholderColor = System.Drawing.SystemColors.InactiveCaption;
            this.userNameTextBox.PlaceholderFont = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.userNameTextBox.PlaceholderText = "e.g. JohnDoe";
            this.userNameTextBox.Size = new System.Drawing.Size(286, 23);
            this.userNameTextBox.TabIndex = 10;
            this.userNameTextBox.Text = "e.g. JohnDoe";
            this.userNameTextBox.WordWrap = false;
            this.userNameTextBox.TextChanged += new System.EventHandler(this.connectionTextBoxes_TextChanged);
            // 
            // userPasswordTextBox
            // 
            this.userPasswordTextBox.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.userPasswordTextBox.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.userPasswordTextBox.ForeColor = System.Drawing.SystemColors.InactiveCaption;
            this.userPasswordTextBox.Location = new System.Drawing.Point(294, 19);
            this.userPasswordTextBox.Margin = new System.Windows.Forms.Padding(4, 4, 0, 0);
            this.userPasswordTextBox.Name = "userPasswordTextBox";
            this.userPasswordTextBox.PasswordChar = '•';
            this.userPasswordTextBox.PlaceholderColor = System.Drawing.SystemColors.InactiveCaption;
            this.userPasswordTextBox.PlaceholderFont = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.userPasswordTextBox.PlaceholderText = "e.g. myPassword";
            this.userPasswordTextBox.Size = new System.Drawing.Size(287, 23);
            this.userPasswordTextBox.TabIndex = 12;
            this.userPasswordTextBox.Text = "e.g. myPassword";
            this.userPasswordTextBox.WordWrap = false;
            this.userPasswordTextBox.TextChanged += new System.EventHandler(this.connectionTextBoxes_TextChanged);
            // 
            // userNameLabel
            // 
            this.userNameLabel.AutoSize = true;
            this.userNameLabel.Location = new System.Drawing.Point(4, 0);
            this.userNameLabel.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.userNameLabel.Name = "userNameLabel";
            this.userNameLabel.Size = new System.Drawing.Size(77, 15);
            this.userNameLabel.TabIndex = 11;
            this.userNameLabel.Text = "User name";
            // 
            // userPasswordLabel
            // 
            this.userPasswordLabel.AutoSize = true;
            this.userPasswordLabel.Location = new System.Drawing.Point(294, 0);
            this.userPasswordLabel.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.userPasswordLabel.Name = "userPasswordLabel";
            this.userPasswordLabel.Size = new System.Drawing.Size(102, 15);
            this.userPasswordLabel.TabIndex = 13;
            this.userPasswordLabel.Text = "User password";
            // 
            // connectionStatusLabel
            // 
            this.connectionStatusLabel.AutoSize = true;
            this.connectionStatusLabel.Location = new System.Drawing.Point(280, 148);
            this.connectionStatusLabel.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.connectionStatusLabel.Name = "connectionStatusLabel";
            this.connectionStatusLabel.Size = new System.Drawing.Size(0, 17);
            this.connectionStatusLabel.TabIndex = 17;
            // 
            // testConnectionButton
            // 
            this.testConnectionButton.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.testConnectionButton.Location = new System.Drawing.Point(11, 140);
            this.testConnectionButton.Margin = new System.Windows.Forms.Padding(4);
            this.testConnectionButton.Name = "testConnectionButton";
            this.testConnectionButton.Size = new System.Drawing.Size(124, 30);
            this.testConnectionButton.TabIndex = 16;
            this.testConnectionButton.Text = "Test connection";
            this.testConnectionButton.UseVisualStyleBackColor = true;
            this.testConnectionButton.Click += new System.EventHandler(this.testConnectionButton_Click);
            // 
            // standaloneCheckBox
            // 
            this.standaloneCheckBox.AutoSize = true;
            this.standaloneCheckBox.Location = new System.Drawing.Point(11, 177);
            this.standaloneCheckBox.Margin = new System.Windows.Forms.Padding(4);
            this.standaloneCheckBox.Name = "standaloneCheckBox";
            this.standaloneCheckBox.Size = new System.Drawing.Size(157, 21);
            this.standaloneCheckBox.TabIndex = 15;
            this.standaloneCheckBox.Text = "Standalone analysis";
            this.standaloneCheckBox.UseVisualStyleBackColor = true;
            this.standaloneCheckBox.CheckedChanged += new System.EventHandler(this.standaloneCheckBox_CheckedChanged);
            // 
            // userIndicator
            // 
            this.userIndicator.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.userIndicator.BackgroundImageLayout = System.Windows.Forms.ImageLayout.Center;
            this.userIndicator.Location = new System.Drawing.Point(600, 108);
            this.userIndicator.Margin = new System.Windows.Forms.Padding(4);
            this.userIndicator.Name = "userIndicator";
            this.userIndicator.Size = new System.Drawing.Size(32, 22);
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
            this.serverIndicator.Location = new System.Drawing.Point(600, 36);
            this.serverIndicator.Margin = new System.Windows.Forms.Padding(4);
            this.serverIndicator.Name = "serverIndicator";
            this.serverIndicator.Size = new System.Drawing.Size(32, 30);
            this.serverIndicator.TabIndex = 9;
            this.serverIndicator.TabStop = false;
            this.serverIndicator.Valid = false;
            this.serverIndicator.ValidImage = ((System.Drawing.Image)(resources.GetObject("serverIndicator.ValidImage")));
            this.serverIndicator.WrongImage = ((System.Drawing.Image)(resources.GetObject("serverIndicator.WrongImage")));
            // 
            // serverLabel
            // 
            this.serverLabel.AutoSize = true;
            this.serverLabel.Location = new System.Drawing.Point(7, 21);
            this.serverLabel.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.serverLabel.Name = "serverLabel";
            this.serverLabel.Size = new System.Drawing.Size(154, 17);
            this.serverLabel.TabIndex = 7;
            this.serverLabel.Text = "Dexter Server address:";
            // 
            // serverTextBox
            // 
            this.serverTextBox.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.serverTextBox.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.serverTextBox.ForeColor = System.Drawing.SystemColors.InactiveCaption;
            this.serverTextBox.Location = new System.Drawing.Point(11, 42);
            this.serverTextBox.Margin = new System.Windows.Forms.Padding(4);
            this.serverTextBox.Name = "serverTextBox";
            this.serverTextBox.PlaceholderColor = System.Drawing.SystemColors.InactiveCaption;
            this.serverTextBox.PlaceholderFont = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.serverTextBox.PlaceholderText = "e.g. http://127.0.0.2:8081";
            this.serverTextBox.Size = new System.Drawing.Size(580, 23);
            this.serverTextBox.TabIndex = 6;
            this.serverTextBox.Text = "e.g. http://127.0.0.2:8081";
            this.serverTextBox.WordWrap = false;
            this.serverTextBox.TextChanged += new System.EventHandler(this.connectionTextBoxes_TextChanged);
            // 
            // dexterSettingsGroupBox
            // 
            this.dexterSettingsGroupBox.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.dexterSettingsGroupBox.Controls.Add(this.analysisOnSaveCheckBox);
            this.dexterSettingsGroupBox.Controls.Add(this.enableDexterHomeCheckBox);
            this.dexterSettingsGroupBox.Controls.Add(this.dexterPathIndicator);
            this.dexterSettingsGroupBox.Controls.Add(this.dexterPathButton);
            this.dexterSettingsGroupBox.Controls.Add(this.dexterPathLabel);
            this.dexterSettingsGroupBox.Controls.Add(this.dexterPathTextBox);
            this.dexterSettingsGroupBox.Location = new System.Drawing.Point(4, 0);
            this.dexterSettingsGroupBox.Margin = new System.Windows.Forms.Padding(4);
            this.dexterSettingsGroupBox.Name = "dexterSettingsGroupBox";
            this.dexterSettingsGroupBox.Padding = new System.Windows.Forms.Padding(4);
            this.dexterSettingsGroupBox.Size = new System.Drawing.Size(641, 130);
            this.dexterSettingsGroupBox.TabIndex = 0;
            this.dexterSettingsGroupBox.TabStop = false;
            this.dexterSettingsGroupBox.Text = "Dexter Settings";
            // 
            // enableDexterHomeCheckBox
            // 
            this.enableDexterHomeCheckBox.AutoSize = true;
            this.enableDexterHomeCheckBox.Location = new System.Drawing.Point(11, 27);
            this.enableDexterHomeCheckBox.Margin = new System.Windows.Forms.Padding(4);
            this.enableDexterHomeCheckBox.Name = "enableDexterHomeCheckBox";
            this.enableDexterHomeCheckBox.Size = new System.Drawing.Size(221, 21);
            this.enableDexterHomeCheckBox.TabIndex = 6;
            this.enableDexterHomeCheckBox.Text = "Enable Dexter/DexterCS home";
            this.enableDexterHomeCheckBox.UseVisualStyleBackColor = true;
            this.enableDexterHomeCheckBox.CheckedChanged += new System.EventHandler(this.enableDexterHomeCheckBox_CheckedChanged);
            // 
            // dexterPathIndicator
            // 
            this.dexterPathIndicator.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.dexterPathIndicator.BackgroundImageLayout = System.Windows.Forms.ImageLayout.Center;
            this.dexterPathIndicator.Location = new System.Drawing.Point(601, 65);
            this.dexterPathIndicator.Margin = new System.Windows.Forms.Padding(4);
            this.dexterPathIndicator.Name = "dexterPathIndicator";
            this.dexterPathIndicator.Size = new System.Drawing.Size(32, 30);
            this.dexterPathIndicator.TabIndex = 5;
            this.dexterPathIndicator.TabStop = false;
            this.dexterPathIndicator.Valid = false;
            this.dexterPathIndicator.ValidImage = ((System.Drawing.Image)(resources.GetObject("dexterPathIndicator.ValidImage")));
            this.dexterPathIndicator.Visible = false;
            this.dexterPathIndicator.WrongImage = ((System.Drawing.Image)(resources.GetObject("dexterPathIndicator.WrongImage")));
            // 
            // dexterPathButton
            // 
            this.dexterPathButton.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.dexterPathButton.Enabled = false;
            this.dexterPathButton.Font = new System.Drawing.Font("Microsoft Sans Serif", 9.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.dexterPathButton.Location = new System.Drawing.Point(548, 65);
            this.dexterPathButton.Margin = new System.Windows.Forms.Padding(4);
            this.dexterPathButton.Name = "dexterPathButton";
            this.dexterPathButton.Size = new System.Drawing.Size(45, 30);
            this.dexterPathButton.TabIndex = 4;
            this.dexterPathButton.Text = "...";
            this.dexterPathButton.UseVisualStyleBackColor = true;
            this.dexterPathButton.Click += new System.EventHandler(this.dexterPathButton_Click);
            // 
            // dexterPathLabel
            // 
            this.dexterPathLabel.AutoSize = true;
            this.dexterPathLabel.Location = new System.Drawing.Point(8, 50);
            this.dexterPathLabel.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.dexterPathLabel.Name = "dexterPathLabel";
            this.dexterPathLabel.Size = new System.Drawing.Size(183, 17);
            this.dexterPathLabel.TabIndex = 1;
            this.dexterPathLabel.Text = "Dexter/DexterCS home path";
            // 
            // dexterPathTextBox
            // 
            this.dexterPathTextBox.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.dexterPathTextBox.Enabled = false;
            this.dexterPathTextBox.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.dexterPathTextBox.ForeColor = System.Drawing.SystemColors.InactiveCaption;
            this.dexterPathTextBox.Location = new System.Drawing.Point(12, 69);
            this.dexterPathTextBox.Margin = new System.Windows.Forms.Padding(4);
            this.dexterPathTextBox.Name = "dexterPathTextBox";
            this.dexterPathTextBox.PlaceholderColor = System.Drawing.SystemColors.InactiveCaption;
            this.dexterPathTextBox.PlaceholderFont = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.dexterPathTextBox.PlaceholderText = "e.g. D:\\Dexter";
            this.dexterPathTextBox.Size = new System.Drawing.Size(527, 23);
            this.dexterPathTextBox.TabIndex = 0;
            this.dexterPathTextBox.Text = "e.g. D:\\Dexter";
            this.dexterPathTextBox.WordWrap = false;
            this.dexterPathTextBox.TextChanged += new System.EventHandler(this.dexterPathTextBox_TextChanged);
            this.dexterPathTextBox.Validating += new System.ComponentModel.CancelEventHandler(this.dexterPathTextBox_Validating);
            // 
            // analysisOnSaveCheckBox
            // 
            this.analysisOnSaveCheckBox.AutoSize = true;
            this.analysisOnSaveCheckBox.Location = new System.Drawing.Point(12, 99);
            this.analysisOnSaveCheckBox.Name = "analysisOnSaveCheckBox";
            this.analysisOnSaveCheckBox.Size = new System.Drawing.Size(136, 21);
            this.analysisOnSaveCheckBox.TabIndex = 19;
            this.analysisOnSaveCheckBox.Text = "Analysis on save";
            this.analysisOnSaveCheckBox.UseVisualStyleBackColor = true;
            // 
            // SettingsControl
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.AutoScroll = true;
            this.Controls.Add(this.dexterSettingsGroupBox);
            this.Controls.Add(this.serverSettingsGroupBox);
            this.Margin = new System.Windows.Forms.Padding(4);
            this.Name = "SettingsControl";
            this.Size = new System.Drawing.Size(649, 350);
            this.serverSettingsGroupBox.ResumeLayout(false);
            this.serverSettingsGroupBox.PerformLayout();
            this.tableLayoutPanel1.ResumeLayout(false);
            this.tableLayoutPanel1.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.userIndicator)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.serverIndicator)).EndInit();
            this.dexterSettingsGroupBox.ResumeLayout(false);
            this.dexterSettingsGroupBox.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dexterPathIndicator)).EndInit();
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
        private System.Windows.Forms.CheckBox enableDexterHomeCheckBox;
        private System.Windows.Forms.Button createUserButton;
        private System.Windows.Forms.CheckBox analysisOnSaveCheckBox;
    }
}