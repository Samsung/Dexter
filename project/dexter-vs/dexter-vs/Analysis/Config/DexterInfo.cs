namespace dexter_vs.Analysis.Config
{
    /// <summary>
    /// Information about Dexter 
    /// </summary>
    public sealed class DexterInfo
    {
        /// <summary>
        /// Dexter Home path
        /// </summary>
        public string dexterHome { get; set; }

        /// <summary>
        /// IP address of Dexter server
        /// </summary>
        public string dexterServerIp { get; set; }

        /// <summary>
        /// Port of Dexter server
        /// </summary>
        public string dexterServerPort { get; set; }

        /// <summary>
        /// Creates new DexterInfo instance with default values
        /// </summary>
        public DexterInfo()
        {
            dexterHome = "";
            dexterServerIp = "";
            dexterServerPort = "0";
        }
    }
}
