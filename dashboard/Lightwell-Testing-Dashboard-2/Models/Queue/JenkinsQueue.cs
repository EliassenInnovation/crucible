using System.Collections.Generic;
using Newtonsoft.Json;

namespace Lightwell_Testing_Dashboard_2.Models.Queue;

public class JenkinsQueue
{
    [JsonProperty(JenkinsBuild._CLASS)]
    public string Class { get; set; }
    public List<JenkinsQueueItem> DiscoverableItems { get; set; }
    public List<JenkinsQueueItem> Items { get; set; }
}