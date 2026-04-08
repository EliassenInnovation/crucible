using Newtonsoft.Json;

namespace Lightwell_Testing_Dashboard_2.Models.Queue;

public class JenkinsQueueItem
{
    [JsonProperty(JenkinsBuild._CLASS)]
    public string Class { get; set; }
    public BuildAction[] Actions { get; set; }
    public bool Blocked { get; set; }
    public bool Buildable { get; set; }
    public int Id { get; set; }
    public long InQueueSince { get; set; }
    public string Params { get; set; }
    public bool Stuck { get; set; }
    public JenkinsTask Task { get; set; }
    public string Url { get; set; }
    public string Why { get; set; }
    public long BuildableStartMilliseconds { get; set; }
    public bool Pending { get; set; }
}