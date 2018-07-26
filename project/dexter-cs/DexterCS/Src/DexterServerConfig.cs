namespace DexterCS
{
    public class DexterServerConfig
    {
        private static readonly string HTTP_PREFIX = "http://";

        public string Hostname { get; set; }
        public int Port { get; set; }
        public string UserId { get; set; }
        public string UserPassword { get; set; }
        public string ServiceUrl { get; set; }
        public string BaseUrl
        {
            get { return HTTP_PREFIX + Hostname + ":" + Port; }
        }

        public DexterServerConfig(string serverHostIp, int serverPort, string userId, string userPassword)
        {
            UserId = userId;
            UserPassword = userPassword;
            Hostname = serverHostIp;
            Port = serverPort;
        }

        public DexterServerConfig(string serverHostIp, int serverPort)
        {
            Hostname = serverHostIp;
            Port = serverPort;
        }
    }
}