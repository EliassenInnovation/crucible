using Newtonsoft.Json;

namespace Lightwell_Testing_Dashboard_2.Models
{
    public class BuildAction
    {
        [JsonProperty("_class")]
        public string Class { get; set; }
        public BuildAction[] Causes { get; set; }
        public string ShortDescription { get; set; }
        public string UserId { get; set; }
        public string UserName { get; set; }
        public int FailCount { get; set; }
        public int SkipCount { get; set; }
        public int TotalCount { get; set; }
        public string UrlName { get; set; }
    }
}
