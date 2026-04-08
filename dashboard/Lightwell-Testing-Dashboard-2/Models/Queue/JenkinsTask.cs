using Newtonsoft.Json;

namespace Lightwell_Testing_Dashboard_2.Models.Queue;

public class JenkinsTask
{
    [JsonProperty(JenkinsBuild._CLASS)]
    public string Class { get; set; }

    public string Name { get; set; }
    public string Url { get; set; }
    public string Color { get; set; }
}