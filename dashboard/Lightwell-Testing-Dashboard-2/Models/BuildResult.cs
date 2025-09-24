namespace Lightwell_Testing_Dashboard_2.Models
{
    public class BuildResult
    {
        public string BuildName { get; set; }
        public string Duration { get; set; }
        public double JunitDuration
        {
            get
            {
                if (JunitTestResult != null)
                {
                    return JunitTestResult.Duration;
                }
                else { return 0; }
            }
        }
        public string JunitDurationString
        {
            get
            {
                if (JunitTestResult != null)
                {
                    return JunitTestResult.DurationString;
                }
                else
                {
                    return "-";
                }
            }
        }
        public string Result { get; set; }
        public string Link { get; set; }
        public string RunDate { get; set; }
        public string Parent { get; set; }
        public int PassedTests { get; set; }
        public int TotalTests { get; set; }
        public string FullName { get; set; }
        public string Description { get; set; }

        public int FailedTests
        {
            get
            {
                return TotalTests - PassedTests;
            }
        }

        public int BuildNumber { get; set; }
        public double ActualDuration { get; internal set; }
        public JunitTestResults JunitTestResult { get; set; }

        public string[] Tags { get; set; }

        public void UpdateFrom(BuildResult other)
        {
            if (other == null) return;

            BuildName = other.BuildName;
            Duration = other.Duration;
            Result = other.Result;
            Link = other.Link;
            RunDate = other.RunDate;
            Parent = other.Parent;
            PassedTests = other.PassedTests;
            TotalTests = other.TotalTests;
            FullName = other.FullName;
            Description = other.Description;
            BuildNumber = other.BuildNumber;
            ActualDuration = other.ActualDuration;

            JunitTestResult = other.JunitTestResult;

            // Clone the tags array
            Tags = other.Tags != null ? (string[])other.Tags.Clone() : null;
        }


    }
}
