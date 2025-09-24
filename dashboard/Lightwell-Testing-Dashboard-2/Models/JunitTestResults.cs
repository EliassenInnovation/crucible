using Lightwell_Testing_Dashboard_2.Tools;
using Newtonsoft.Json;

namespace Lightwell_Testing_Dashboard_2.Models
{
    public class JunitTestResults
    {
        [JsonProperty("_class")]
        public string Class { get; set; }
        public BuildAction[] TestActions { get; set; }
        public double Duration { get; set; }

        public string DurationString
        {
            get
            {
                return TimeTools.ConvertSecondsToHoursAndMinutes(Duration);
            }
        }

        public bool Empty { get; set; }
        public int FailCount { get; set; }
        public int PassCount { get; set; }
        public int SkipCount { get; set; }
        public TestSuite[] Suites { get; set; }

        public class TestSuite
        {
            public TestCase[] Cases { get; set; }
        }

        public class TestCase
        {
            public BuildAction[] TestActions { get; set; }
            public int Age { get; set; }
            public string ClassName { get; set; }
            public double Duration { get; set; }
            public string ErrorDetails { get; set; }
            public string ErrorStackTrace { get; set; }
            public int FailedSince { get; set; }
            public string Name { get; set; }
            public bool Skipped { get; set; }
            public string SkippedMessage { get; set; }
            public string Status { get; set; }
            public string Stderr { get; set; }
            public string Stdout { get; set; }
        }
    }
}
