using log4net;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.IO;

namespace DexterCS
{
    public class AnalysisConfig : BaseAnalysisEntity
    {
        private static ILog CliLog = LogManager.GetLogger(typeof(AnalysisConfig));

        private string Sourcecode { get; set; }
        private List<string> sourceBaseDirList = new List<string>(0);
        private List<string> headerBaseDirList = new List<string>(0);
        internal long GroupId { get { return -1; } }
        private string outputDir = "";
        
        public IList<string> SourceBaseDirList { get; set; }
        public IList<string> HeaderBaseDirList { get; set; }
        private string OutputDir
        {
            get { return outputDir; }
            set
            {
                value = value.Replace("\\", "/").Replace("(\r\n|\n)", "/");
                if (value.EndsWith("\\", StringComparison.CurrentCulture)
                    || value.EndsWith("/", StringComparison.CurrentCulture))
                {
                    value = value.Substring(0, value.Length - 1);
                }
                this.outputDir = value;
            }
        }

        [DefaultValue(false)]
        private Boolean ShouldSendSourceCode { get; set; }

        public AnalysisConfig()
        {
            InitAllListTypeField();
        }

        public DexterConfig.AnalysisType AnalysisConfigType { get; set; }
        public AnalysisConfig(AnalysisConfig other) : base(other)
        {
            ShouldSendSourceCode = other.ShouldSendSourceCode;
            SourceBaseDirList = other.SourceBaseDirList;
            HeaderBaseDirList = other.HeaderBaseDirList;
            OutputDir = other.OutputDir;
            Sourcecode = other.Sourcecode;
            AnalysisConfigType = other.AnalysisConfigType;
        }
        public string SourcecodeFromFile {
            get { return DexterUtil.GetSourcecodeFromFile(SourceFileFullPath); }
        }

        public IAnalysisResultHandler ResultHandler { get; set; }

        public void AddHeaderAndSourceConfiguration(List<ProjectAnalysisConfiguration> projectAnalysisConfigurationList)
        {
            foreach(var param in projectAnalysisConfigurationList)
            {
                if (param.ProjectName.Equals(ProjectName) &&
                    DexterUtil.RefinePath(param.ProjectFullPath).Equals(ProjectFullPath))
                {
                    foreach(string dir in param.SourceDirs)
                    {
                        AddSourceBaseDirList(dir);
                    }
                    foreach(string dir in param.HeaderDirs)
                    {
                        AddHeaderBaseDirList(dir);
                    }
                }
            }
        }

        private void AddHeaderBaseDirList(string dir)
        {
            //TODO
        }

        private void AddSourceBaseDirList(string dir)
        {
            if (string.IsNullOrEmpty(dir) || this.sourceBaseDirList.Contains(dir))
            {
                return;
            }

            dir = dir.Replace("\\", "/").Replace(DexterUtil.FILE_SEPARATOR, "/");

            if (dir.EndsWith("\\", StringComparison.CurrentCulture)
                || dir.EndsWith("/", StringComparison.CurrentCulture)
                || dir.EndsWith(DexterUtil.FILE_SEPARATOR, StringComparison.CurrentCulture))
            {
                dir = dir.Substring(0, dir.Length - 1);
            }
            this.sourceBaseDirList.Add(dir);
        }


        private void InitAllListTypeField()
        {
            SourceBaseDirList = new List<string>();
            HeaderBaseDirList = new List<string>();
        }

        public void GenerateFileNameWithSourceFileFullPath()
        {
            FileName = (new FileInfo(SourceFileFullPath)).Name;
        }

        internal void GenerateModulePath()
        {
            foreach(string sourceDir in SourceBaseDirList)
            {
                if (HandleGeneratingModulePath(sourceDir))
                {
                    break;
                }
            }
        }

        private bool HandleGeneratingModulePath(string sourceDir)
        {
            try
            {
                if (SourceFileFullPath.StartsWith(sourceDir, StringComparison.CurrentCulture))
                {
                    int baseIndex = sourceDir.Length + 1 ;
                    int endIndex = SourceFileFullPath.IndexOf(FileName, StringComparison.CurrentCulture);
                    if ( baseIndex > endIndex || baseIndex < 0 || endIndex < 0)
                    {
                        throw new Exception("cannot calculate the positions of module path:"
                            + " sourceDir:" + sourceDir
                            + " sourceFileFullPath:" + SourceFileFullPath
                            + " fileName:" + FileName);
                    }
                    int length = endIndex - baseIndex;
                    ModulePath = SourceFileFullPath.Substring(baseIndex, length);
                    return true;
                }
            }
            catch (Exception e)
            {
                CliLog.Error(e.Message);
                CliLog.Error(e.StackTrace);
                return false;
            }
            return false;
        }
    }
}