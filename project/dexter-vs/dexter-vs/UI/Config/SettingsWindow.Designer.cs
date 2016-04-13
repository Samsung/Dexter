namespace dexter_vs.UI.Config
{
    partial class SettingsWindow
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(SettingsWindow));
            this.serverSettingsGroupBox = new System.Windows.Forms.GroupBox();
            this.userIndicator = new System.Windows.Forms.PictureBox();
            this.userPasswordLabel = new System.Windows.Forms.Label();
            this.userPasswordTextBox = new System.Windows.Forms.TextBox();
            this.userNameLabel = new System.Windows.Forms.Label();
            this.userNameTextBox = new System.Windows.Forms.TextBox();
            this.serverIndicator = new System.Windows.Forms.PictureBox();
            this.dexterServerLabel = new System.Windows.Forms.Label();
            this.ServerTextBox = new System.Windows.Forms.TextBox();
            this.dexterSettingsGroupBox = new System.Windows.Forms.GroupBox();
            this.dexterPathIndicator = new System.Windows.Forms.PictureBox();
            this.dexterPathButton = new System.Windows.Forms.Button();
            this.dexterPathLabel = new System.Windows.Forms.Label();
            this.dexterPathTextBox = new System.Windows.Forms.TextBox();
            this.okButton = new System.Windows.Forms.Button();
            this.cancelButton = new System.Windows.Forms.Button();
            this.serverSettingsGroupBox.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.userIndicator)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.serverIndicator)).BeginInit();
            this.dexterSettingsGroupBox.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dexterPathIndicator)).BeginInit();
            this.SuspendLayout();
            // 
            // serverSettingsGroupBox
            // 
            this.serverSettingsGroupBox.Controls.Add(this.userIndicator);
            this.serverSettingsGroupBox.Controls.Add(this.userPasswordLabel);
            this.serverSettingsGroupBox.Controls.Add(this.userPasswordTextBox);
            this.serverSettingsGroupBox.Controls.Add(this.userNameLabel);
            this.serverSettingsGroupBox.Controls.Add(this.userNameTextBox);
            this.serverSettingsGroupBox.Controls.Add(this.serverIndicator);
            this.serverSettingsGroupBox.Controls.Add(this.dexterServerLabel);
            this.serverSettingsGroupBox.Controls.Add(this.ServerTextBox);
            this.serverSettingsGroupBox.Location = new System.Drawing.Point(12, 105);
            this.serverSettingsGroupBox.Name = "serverSettingsGroupBox";
            this.serverSettingsGroupBox.Size = new System.Drawing.Size(472, 150);
            this.serverSettingsGroupBox.TabIndex = 0;
            this.serverSettingsGroupBox.TabStop = false;
            this.serverSettingsGroupBox.Text = "Server Settings";
            // 
            // userIndicator
            // 
            this.userIndicator.BackgroundImage = ((System.Drawing.Image)(resources.GetObject("userIndicator.BackgroundImage")));
            this.userIndicator.BackgroundImageLayout = System.Windows.Forms.ImageLayout.Center;
            this.userIndicator.Location = new System.Drawing.Point(442, 104);
            this.userIndicator.Name = "userIndicator";
            this.userIndicator.Size = new System.Drawing.Size(24, 24);
            this.userIndicator.TabIndex = 14;
            this.userIndicator.TabStop = false;
            // 
            // userPasswordLabel
            // 
            this.userPasswordLabel.AutoSize = true;
            this.userPasswordLabel.Location = new System.Drawing.Point(223, 88);
            this.userPasswordLabel.Name = "userPasswordLabel";
            this.userPasswordLabel.Size = new System.Drawing.Size(77, 13);
            this.userPasswordLabel.TabIndex = 13;
            this.userPasswordLabel.Text = "User password";
            // 
            // userPasswordTextBox
            // 
            this.userPasswordTextBox.Location = new System.Drawing.Point(226, 104);
            this.userPasswordTextBox.Name = "userPasswordTextBox";
            this.userPasswordTextBox.Size = new System.Drawing.Size(210, 20);
            this.userPasswordTextBox.TabIndex = 12;
            this.userPasswordTextBox.TextChanged += new System.EventHandler(this.textBox4_TextChanged);
            // 
            // userNameLabel
            // 
            this.userNameLabel.AutoSize = true;
            this.userNameLabel.Location = new System.Drawing.Point(6, 88);
            this.userNameLabel.Name = "userNameLabel";
            this.userNameLabel.Size = new System.Drawing.Size(58, 13);
            this.userNameLabel.TabIndex = 11;
            this.userNameLabel.Text = "User name";
            // 
            // userNameTextBox
            // 
            this.userNameTextBox.Location = new System.Drawing.Point(9, 104);
            this.userNameTextBox.Name = "userNameTextBox";
            this.userNameTextBox.Size = new System.Drawing.Size(210, 20);
            this.userNameTextBox.TabIndex = 10;
            this.userNameTextBox.TextChanged += new System.EventHandler(this.textBox3_TextChanged);
            // 
            // serverIndicator
            // 
            this.serverIndicator.BackgroundImage = ((System.Drawing.Image)(resources.GetObject("serverIndicator.BackgroundImage")));
            this.serverIndicator.BackgroundImageLayout = System.Windows.Forms.ImageLayout.Center;
            this.serverIndicator.Location = new System.Drawing.Point(442, 47);
            this.serverIndicator.Name = "serverIndicator";
            this.serverIndicator.Size = new System.Drawing.Size(24, 24);
            this.serverIndicator.TabIndex = 9;
            this.serverIndicator.TabStop = false;
            // 
            // dexterServerLabel
            // 
            this.dexterServerLabel.AutoSize = true;
            this.dexterServerLabel.Location = new System.Drawing.Point(6, 35);
            this.dexterServerLabel.Name = "dexterServerLabel";
            this.dexterServerLabel.Size = new System.Drawing.Size(115, 13);
            this.dexterServerLabel.TabIndex = 7;
            this.dexterServerLabel.Text = "Dexter Server address:";
            // 
            // ServerTextBox
            // 
            this.ServerTextBox.Location = new System.Drawing.Point(9, 51);
            this.ServerTextBox.Name = "ServerTextBox";
            this.ServerTextBox.Size = new System.Drawing.Size(427, 20);
            this.ServerTextBox.TabIndex = 6;
            this.ServerTextBox.TextChanged += new System.EventHandler(this.textBox2_TextChanged);
            // 
            // dexterSettingsGroupBox
            // 
            this.dexterSettingsGroupBox.Controls.Add(this.dexterPathIndicator);
            this.dexterSettingsGroupBox.Controls.Add(this.dexterPathButton);
            this.dexterSettingsGroupBox.Controls.Add(this.dexterPathLabel);
            this.dexterSettingsGroupBox.Controls.Add(this.dexterPathTextBox);
            this.dexterSettingsGroupBox.Location = new System.Drawing.Point(12, 12);
            this.dexterSettingsGroupBox.Name = "dexterSettingsGroupBox";
            this.dexterSettingsGroupBox.Size = new System.Drawing.Size(472, 87);
            this.dexterSettingsGroupBox.TabIndex = 1;
            this.dexterSettingsGroupBox.TabStop = false;
            this.dexterSettingsGroupBox.Text = "Dexter Settings";
            // 
            // dexterPathIndicator
            // 
            this.dexterPathIndicator.BackgroundImage = ((System.Drawing.Image)(resources.GetObject("dexterPathIndicator.BackgroundImage")));
            this.dexterPathIndicator.BackgroundImageLayout = System.Windows.Forms.ImageLayout.Center;
            this.dexterPathIndicator.Location = new System.Drawing.Point(442, 44);
            this.dexterPathIndicator.Name = "dexterPathIndicator";
            this.dexterPathIndicator.Size = new System.Drawing.Size(24, 24);
            this.dexterPathIndicator.TabIndex = 5;
            this.dexterPathIndicator.TabStop = false;
            // 
            // dexterPathButton
            // 
            this.dexterPathButton.Font = new System.Drawing.Font("Microsoft Sans Serif", 9.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(238)));
            this.dexterPathButton.Location = new System.Drawing.Point(402, 44);
            this.dexterPathButton.Name = "dexterPathButton";
            this.dexterPathButton.Size = new System.Drawing.Size(34, 24);
            this.dexterPathButton.TabIndex = 4;
            this.dexterPathButton.Text = "...";
            this.dexterPathButton.UseVisualStyleBackColor = true;
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
            // dexterPathTextBox
            // 
            this.dexterPathTextBox.Location = new System.Drawing.Point(9, 48);
            this.dexterPathTextBox.Name = "dexterPathTextBox";
            this.dexterPathTextBox.Size = new System.Drawing.Size(387, 20);
            this.dexterPathTextBox.TabIndex = 0;
            // 
            // okButton
            // 
            this.okButton.Location = new System.Drawing.Point(310, 261);
            this.okButton.Name = "okButton";
            this.okButton.Size = new System.Drawing.Size(84, 24);
            this.okButton.TabIndex = 2;
            this.okButton.Text = "OK";
            this.okButton.UseVisualStyleBackColor = true;
            // 
            // cancelButton
            // 
            this.cancelButton.Location = new System.Drawing.Point(400, 261);
            this.cancelButton.Name = "cancelButton";
            this.cancelButton.Size = new System.Drawing.Size(84, 24);
            this.cancelButton.TabIndex = 3;
            this.cancelButton.Text = "Cancel";
            this.cancelButton.UseVisualStyleBackColor = true;
            // 
            // SettingsWindow
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(496, 294);
            this.Controls.Add(this.cancelButton);
            this.Controls.Add(this.okButton);
            this.Controls.Add(this.dexterSettingsGroupBox);
            this.Controls.Add(this.serverSettingsGroupBox);
            this.MaximizeBox = false;
            this.Name = "SettingsWindow";
            this.Text = "Dexter Settings";
            this.serverSettingsGroupBox.ResumeLayout(false);
            this.serverSettingsGroupBox.PerformLayout();
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
        private System.Windows.Forms.PictureBox dexterPathIndicator;
        private System.Windows.Forms.Button dexterPathButton;
        private System.Windows.Forms.Label dexterPathLabel;
        private System.Windows.Forms.TextBox dexterPathTextBox;
        private System.Windows.Forms.Button okButton;
        private System.Windows.Forms.Button cancelButton;
        private System.Windows.Forms.PictureBox userIndicator;
        private System.Windows.Forms.Label userPasswordLabel;
        private System.Windows.Forms.TextBox userPasswordTextBox;
        private System.Windows.Forms.Label userNameLabel;
        private System.Windows.Forms.TextBox userNameTextBox;
        private System.Windows.Forms.PictureBox serverIndicator;
        private System.Windows.Forms.Label dexterServerLabel;
        private System.Windows.Forms.TextBox ServerTextBox;
    }
}