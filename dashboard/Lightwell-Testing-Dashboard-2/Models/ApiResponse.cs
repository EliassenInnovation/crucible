using System.Net;

namespace Lightwell_Testing_Dashboard_2.Models
{
    public class ApiResponse
    {
        public HttpWebResponse Response { get; set; }
        public string Content { get; set; }
        public string RequestUrl { get; set; }
    }
}
