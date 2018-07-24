using log4net;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.IO;

namespace DexterCS
{
    public class DexterConfigFile : IDexterConfigFile
    {
        private static ILog CliLog = LogManager.GetLogger(typeof(DexterConfigFile));

        public enum Type
        {
            FILE, FOLDER, PROJECT, SNAPSHOT
        }

        public string DexterHome { get; set; }
        public string DexterServerIp { get; set; }
        public int DexterServerPort { get; set; }
        public string ProjectName { get; set; }
        public string ProjectFullPath { get; set; }
        public string SourceEncoding { get; set; }
        public string ConfigType { get; set; }
        public long SnapshotId { get; set; }
        private string modulePath = "";
        public string ModulePath {
            get { return modulePath; }
            set { modulePath = value ?? ""; }
        }
        public bool IsSpecifiedCheckerEnabled { get; set; }
        public bool IsSpecifiedCheckerOptionEnabledByCli { get; set; }

        //private bool hasOneSourceDir = false;
        //private string firstSourceDir = "";
        private List<string> SourceDirList { get; set; }
        private List<string> HeaderDirList { get; set; }
        private string BinDir { get; set; }
        private string Language { get; set; }
        

        private IList<string> fileNameList = new List<string>();
        public IList<string> FileNameList { get; set; }
        public void AddFileNameList(string fileName)
        {
            if (fileNameList.Contains(fileName))
            {
                return;
            }
            fileNameList.Add(fileName);
        }

        private string ResultFileFullPath { get; set; }

        public void LoadFromFile(String dexterConfig)
        {
            try
            {
                dexterConfig = dexterConfig.Replace("\\", "/").Replace("//", "/");
                JObject configMetadata = JObject.Parse(dexterConfig);
                SetFields(configMetadata);
            }
            catch (JsonException e)
            {
                CliLog.Error("Json Exception");
                CliLog.Error(e.StackTrace);
            }
            catch (NullReferenceException e)
            {
                CliLog.Error("Null Exeption");
                CliLog.Error(e.StackTrace);
            }
        }

        private void SetFields(JObject configMetadata)
        {
            CheckDexterConfigMatadata(configMetadata);

            DexterHome = ((string)configMetadata["dexterHome"]);
            DexterServerIp = ((string)configMetadata["dexterServerIp"]);
            DexterServerPort = (Int32.Parse((string)configMetadata["dexterServerPort"]));
            ProjectName = ((string)configMetadata["projectName"]);
            ProjectFullPath= ((string)configMetadata["projectFullPath"] + " / ");
            SourceDirList = GetListFromDictionary(configMetadata["sourceDir"]);
            HeaderDirList = GetListFromDictionary(configMetadata["headerDir"]);
            SourceEncoding=((string)configMetadata["sourceEncoding"]);
            ConfigType=((string)configMetadata["type"]);
            ModulePath = (string)configMetadata["modulePath"];
            
            FileNameList = GetListFromDictionary(configMetadata["fileName"]);
            try
            {
                SnapshotId = (long.Parse((string)configMetadata["snapshotId"]));
            } catch(Exception)
            {
                SnapshotId = -1;
            }
        }

        public List<string> GetListFromDictionary(JToken dirs)
        {
            List<string> tempDirs = new List<string>();
            if (dirs == null)
            {
                return tempDirs;
            }
            
            foreach(var dir in dirs)
            {
                tempDirs.Add(dir.ToString());
            } 
            return tempDirs;
        }

        private void CheckDexterConfigMatadata(dynamic configMetadata)
        {
            CheckNullOfMap(configMetadata);
            CheckFieldExistence(configMetadata);
            CheckFolderExistence(configMetadata);
            CheckTypeAndFollowingFields(configMetadata);
        }

        private void CheckTypeAndFollowingFields(dynamic configMetadata)
        {
            string _type = (string) configMetadata.type;
            if(string.Compare(ResultFileConstant.FILE_TYPE, _type, StringComparison.OrdinalIgnoreCase) != 0 &&
                string.Compare(ResultFileConstant.FOLDER_TYPE, _type, StringComparison.OrdinalIgnoreCase) != 0 &&
                string.Compare(ResultFileConstant.PROJECT_TYPE, _type, StringComparison.OrdinalIgnoreCase) != 0 &&
                string.Compare(ResultFileConstant.SNAPSHOT_TYPE, _type, StringComparison.OrdinalIgnoreCase) != 0 )
            {
                throw new DexterRuntimeException("'type' field can be {FILE,FOLDER,PROJECT,SNAPSHOT}. your input : " + _type);
            }

            if(string.Compare(ResultFileConstant.FILE_TYPE, _type, StringComparison.OrdinalIgnoreCase) == 0)
            {
                CheckFieldEmptyInDexterConfiguration(configMetadata, ResultFileConstant.FILE_NAME);
            }
        }

        private void CheckFolderExistence(dynamic configMetadata)
        {
            DexterUtil.CheckFolderExistence(configMetadata.projectFullPath);
        }

        private void CheckFieldExistence(dynamic configMetadata)
        {
            CheckFieldEmptyInDexterConfiguration(configMetadata.projectName, ResultFileConstant.PROJECT_NAME);
            CheckFieldEmptyInDexterConfiguration(configMetadata.projectFullPath, "projectFullPath");
            CheckFieldEmptyInDexterConfiguration(configMetadata.sourceEncoding, "sourceEncoding");
            CheckFieldEmptyInDexterConfiguration(configMetadata.type, "type");
        }

        private void CheckFieldEmptyInDexterConfiguration(dynamic fieldValue, string key)
        {
            if(object.ReferenceEquals(null, fieldValue))
            {
                throw new DexterRuntimeException("Dexter Configuration Error : '" + key + "' field is empty");
            }
        }

        private void CheckNullOfMap(dynamic configMetadata)
        {
            if(configMetadata == null || configMetadata.Count < 1)
            {
                throw new DexterRuntimeException("Dexter Configuration Error: Empty");
            }
        }

        public AnalysisConfig ToAnalysisConfig()
        {
            AnalysisEntityFactory configFactory = new AnalysisEntityFactory();
            AnalysisConfig analysisConfig = configFactory.CreateAnalysisConfig();

            analysisConfig.ProjectName = ProjectName;
            analysisConfig.ProjectFullPath = ProjectFullPath + "/";
            analysisConfig.ModulePath = ModulePath;
            analysisConfig.SourceBaseDirList = SourceDirList;
            analysisConfig.HeaderBaseDirList = HeaderDirList;
            analysisConfig.SnapshotId = SnapshotId;
            analysisConfig.ResultFileFullPath = ResultFileFullPath;

            string type = ConfigType;
            switch(type){
                case "PROJECT":
                    analysisConfig.AnalysisConfigType = DexterConfig.AnalysisType.PROJECT;
                break;
                case "SNAPSHOT":
                    analysisConfig.AnalysisConfigType = DexterConfig.AnalysisType.SNAPSHOT;
                    break;
                case "FOLDER":
                    analysisConfig.AnalysisConfigType = DexterConfig.AnalysisType.FOLDER;
                    break;
                case "FILE":
                    analysisConfig.AnalysisConfigType = DexterConfig.AnalysisType.FILE;
                    break;
                default:
                    analysisConfig.AnalysisConfigType = DexterConfig.AnalysisType.UNKNOWN;
                    break;
            }

            return analysisConfig;
        }

        public List<string> GenerateSourceFileFullPathList()
        {
            List<String> sourceFileFullPathList = new List<String>(0);
            if (this.FileNameList == null)
            {
                return sourceFileFullPathList;
            }
            if(this.ConfigType == Type.FILE.ToString())
            {
                sourceFileFullPathList = GenerateSourceFileFullPathListAsFileType();
            }
            else if (this.ConfigType == Type.FOLDER.ToString())
            {
                sourceFileFullPathList = GenerateSourceFileFullPathListAsFolderType();
            }
            else
            {
                sourceFileFullPathList = GenerateSourceFileFullPathListAsProjectType();
            }
            return sourceFileFullPathList;
        }

        private List<string> GenerateSourceFileFullPathListAsProjectType()
        {
            List <string> sourceFileFullPathList = new List<string>(50);

            foreach (var srcDir in SourceDirList)
            {
                AddSourceFileFullPathHierachy(srcDir, sourceFileFullPathList);
            }
            return sourceFileFullPathList;
        }

        private void AddSourceFileFullPathHierachy(string basePath, List<string> sourceFileFullPathList)
        {
            try
            {
                FileAttributes attr = File.GetAttributes(basePath);
                if (attr.HasFlag(FileAttributes.Directory)) // IsDirectory
                {
                    foreach (string subFile in DexterUtil.DirectorySearch(basePath))
                    {
                        if (hasValidDirectoryName(subFile))
                        {
                            AddSourceFileFullPathHierachy(subFile, sourceFileFullPathList);
                        } else {
                            continue;
                        }
                    }
                }
                else
                {
                    if (DexterConfig.Instance.IsAnalysisAllowedFile(basePath) == false)
                    {
                        return;
                    }
                    sourceFileFullPathList.Add(DexterUtil.RefinePath(basePath));
                }
            }catch(Exception e)
            {
                CliLog.Error("There is no file :" + e.Message);
                Environment.Exit(0);
            }
        }

        private List<string> GenerateSourceFileFullPathListAsFileType()
        {
            return new List<string>();
        }

        private bool hasValidDirectoryName(string path)
        {
            if(path.Contains("\\Properties") || path.Contains("\\Interop") || path.Contains(".Tests") )
            {
                return false;
            }

            return true;
        }
        private List<string> GenerateSourceFileFullPathListAsFolderType()
        {
            string moduleFullPath = GetExistingModuleFullPathWithSourceDirList();
            if (string.IsNullOrEmpty(moduleFullPath))
            {
                return new List<string>(0);
            }
            List<string> sourceFileFullPathList = new List<String>(10);

            foreach(string filePath in (DexterUtil.getSubFileNames(moduleFullPath)))
            {
                sourceFileFullPathList.Add(filePath);
            }

            return sourceFileFullPathList;
        }

        private string GetExistingModuleFullPathWithSourceDirList()
        {
            foreach(string srcDir in SourceDirList)
            {
                string moduleFullPath = DexterUtil.RefinePath(srcDir + "/" + ModulePath);
                if (File.Exists(moduleFullPath))
                {
                    return moduleFullPath;
                }
            }
            return "";
        }
    }
}