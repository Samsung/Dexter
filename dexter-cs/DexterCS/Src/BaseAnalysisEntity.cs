namespace DexterCS
{
    public class BaseAnalysisEntity : TargetFile
    {
        public string ProjectName { get; set; }
        public string ProjectFullPath { get; set; }
        public string SourceFileFullPath { get; set; }
        public string ResultFileFullPath { get; set; }
        public long SnapshotId { get; set; }
        public DexterConfig.AnalysisType AnalysisType { get;  set; }

        protected BaseAnalysisEntity() { }
        protected BaseAnalysisEntity(BaseAnalysisEntity entitiy) : base(entitiy)
        {
            ProjectName = entitiy.ProjectName;
            ProjectFullPath = entitiy.ProjectFullPath;
            SnapshotId = entitiy.SnapshotId;
            SourceFileFullPath = entitiy.SourceFileFullPath;
            ResultFileFullPath = entitiy.ResultFileFullPath;
        }
    }
}