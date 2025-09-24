using Lightwell_Testing_Dashboard_2.Models;
using Lightwell_Testing_Dashboard_2.Models.JobFeed;
using Lightwell_Testing_Dashboard_2.Tools;
using Lightwell_Testing_Dashboard_2.Workers;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Linq;

namespace Lightwell_Testing_Dashboard_2.Controllers
{
    public class StatusController : Controller
    {
        private readonly IConfiguration _config;
        private TestResultWorker _testResultWorker;

        public StatusController(IConfiguration config, TestResultWorker testResultWorker)
        {
            _config = config;
            _testResultWorker = testResultWorker;
        }

        public ActionResult Index()
        {
            return View();
        }

        public List<BuildResult> TestResults { get; set; }


        private ConcurrentDictionary<string, List<BuildResult>> BuildsByStatus
        {
            get
            {
                if (TestResultWorker.BuildsByStatus.Count == 0)
                {
                    _testResultWorker.GetTestResults();
                }
                return TestResultWorker.BuildsByStatus;
            }
        }

        /// <summary>
        /// get the job with the most failing scenarios
        /// </summary>
        /// <returns></returns>
        [HttpGet("status/GetWorstOffender")]
        public ActionResult GetWorstOffender()
        {
            TestResults = _testResultWorker.GetTestResults();

            BuildResult worst = null;
            foreach(BuildResult result in TestResults)
            {
                if (worst == null || worst.FailedTests < result.FailedTests)
                {
                    worst = result;
                }
            }

            //if worst is still null, set it to the first one in the list
            if (worst == null)
            {
                worst = TestResults[0];
            }

            return Json(worst);
        }

        /// <summary>
        /// get the job with the most failing scenarios
        /// </summary>
        /// <returns></returns>
        [HttpGet("status/GetFails")]
        public ActionResult GetFails()
        {
            return GetBuildsByStatusCommon(TestResultWorker.FAILURE);
        }

        /// <summary>
        /// get the total amount of failed, passed, and "other" builds
        /// </summary>
        /// <returns></returns>
        [HttpGet("status/GetTotals")]
        public ActionResult GetTotals()
        {
            TestResults = _testResultWorker.GetTestResults();
            int failedBuilds = 0, succesfulBuilds = 0, other = 0, disabledBuilds = 0;
            foreach(BuildResult result in TestResults)
            {
                switch(result.Result)
                {
                    case TestResultWorker.SUCCESS:
                        succesfulBuilds++;
                        break;
                    case TestResultWorker.FAILURE:
                        failedBuilds++;
                        break;
                    case TestResultWorker.DISABLED:
                        disabledBuilds++;
                        break;
                    default:
                        other++;
                        break;
                }
            }

            Totals totals = new Totals();
            totals.Successes = succesfulBuilds;
            totals.Fails = failedBuilds;
            totals.Disabled = disabledBuilds;
            totals.Others = other;

            return Json(totals);
        }

        [HttpGet("status/GetDisabledJobs")]
        public ActionResult GetDisabledJobs()
        {
            return GetBuildsByStatusCommon(TestResultWorker.DISABLED);
        }

        /// <summary>
        /// Get the Building jobs
        /// </summary>
        /// <returns></returns>
        [HttpGet("status/GetBuildingJobs")]
        public ActionResult GetBuildingJobs()
        {
            return GetBuildsByStatusCommon(TestResultWorker.BUILDING);
        }

        /// <summary>
        /// Get new failures
        /// </summary>
        /// <returns></returns>
        [HttpGet("status/GetNewFails")]
        public ActionResult GetNewFailures()
        {
            //JobFeed jobFeed = DataGrabber.GetFailedJobFeed();
            //List<JobFeedEntry> recentFails = jobFeed.BuildsThatJustBroke;

            //TestResults = _testResultWorker.GetTestResultsFromJobFeedEntries(recentFails).Result
            //    .Where(tr =>
            //    {
            //        // Check the result status
            //        if (tr.Result.Equals(TestResultWorker.DISABLED) || tr.Result.Equals(TestResultWorker.SUCCESS) || tr.Result.Equals(TestResultWorker.BUILDING))
            //            return false;

            //        // Attempt to parse the RunDate
            //        if (DateTime.TryParse(tr.RunDate, out DateTime runDate))
            //        {
            //            // Include only if the run date is within the last day
            //            return runDate > DateTime.Now.AddDays(-1);
            //        }

            //        // Exclude if RunDate is invalid (e.g., "-")
            //        return false;
            //    })
            //    .ToList();

            if(TestResultWorker.FailedTestResultsFromJobFeed == null)
            {
                _testResultWorker.BuildTestResultsFromJobFeedEntries();
            }

            return Json(TestResultWorker.FailedTestResultsFromJobFeed);
        }


        //[HttpGet("status/Trend")]
        //public ActionResult 

        [HttpGet("status/GetSimpleTestResults")]
        public ActionResult GetSimpleTestResults()
        {
            int statusCode = 500;

            if (ApiWorker.JenkinsIdle)
            {
                double failedTests = 0;
                double totalTests = 0;

                List<BuildResult> testResults = _testResultWorker.GetTestResults(string.Empty);

                foreach (BuildResult buildResult in testResults)
                {
                    failedTests += buildResult.FailedTests;
                    totalTests += buildResult.TotalTests;
                }

                double passingThreshold = double.Parse(_config.GetSection("PassingThreshold").Value);
                double passPercentage = (totalTests - failedTests) / totalTests;
                if (passPercentage < passingThreshold)
                {
                    //too many tests failed
                    statusCode = 417;
                }
                else
                {
                    //all is good
                    statusCode = 200;
                }

            }
            else
            {
                //Jenkins is not done building/testing
                statusCode = 418;
            }

            return new StatusCodeResult(statusCode);
        }

        [HttpGet("status/GetJenkinsIdle")]
        public ActionResult GetJenkinsIdle()
        {
            bool result = ApiWorker.JenkinsIdle;
            return Json(result);
        }

        private JsonResult GetBuildsByStatusCommon(string status)
        {
            List<BuildResult> builds;
            if (!BuildsByStatus.ContainsKey(status))
            {
                builds = new List<BuildResult>();
            }
            else
            {
                builds = _testResultWorker.SortBuilds(BuildsByStatus[status]);
            }

            return Json(builds);
        }
    }
}
