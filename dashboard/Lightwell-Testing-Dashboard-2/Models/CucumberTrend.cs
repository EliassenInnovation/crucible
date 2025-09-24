using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Lightwell_Testing_Dashboard_2.Models
{
    public class CucumberTrend
    {
        public int PassedFeatures { get; set; }
        public int FailedFeatures { get; set; }
        public int TotalFeatures { get; set; }
        public int PassedScenarios { get; set; }
        public int FailedScenarios { get; set; }
        public int TotalScenarios { get; set; }
        public int PassedSteps { get; set; }
        public int FailedSteps { get; set; }
        public int SkippedSteps { get; set; }
        public int PendingSteps { get; set; }
        public int UndefinedSteps { get; set; }
        public int TotalSteps { get; set; }
        public Int64 Duration { get; set; }
    }
}
