using System;
using System.Collections.Generic;
using System.Linq;
using Microsoft.AspNetCore.Mvc;
using Lightwell_Testing_Dashboard_2.Models;
using Microsoft.Extensions.Configuration;
using Lightwell_Testing_Dashboard_2.Workers;
using Newtonsoft.Json.Linq;
using System.Diagnostics;

namespace Lightwell_Testing_Dashboard_2.Controllers
{
    public class HomeController : Controller
    {
        private readonly IConfiguration _config;
        private TestResultWorker _testResultWorker;

        public bool UseAlternateLandingPage
        {
            get
            {
                return !string.IsNullOrEmpty(_config.GetValue<string>("AlternateLandingPage"));
            }
        }

        public string AlternateLandingPage
        {
            get
            {
                return _config.GetValue<string>("AlternateLandingPage");
            }
        }

        public HomeController(IConfiguration config, TestResultWorker testResultWorker)
        {
            _config = config;
            _testResultWorker = testResultWorker;
        }

        public ActionResult Index()
        {
            //if (UseAlternateLandingPage && !Request.Path.ToString().ToLowerInvariant().Contains("dashboard"))
            //{
            //    return Redirect(AlternateLandingPage);
            //}
            return View();
        }

        /// <summary>
        /// Returns all of the builds on the local Jenkins instance
        /// </summary>
        /// <param name="sort"></param>
        /// <param name="refresh"></param>
        /// <returns></returns>
        [HttpPost("home/GetTestResults")]
        public ActionResult GetTestResults(string sort, bool refresh = true)
        {
            try
            {
                List<BuildResult> testResults = _testResultWorker.GetTestResults(sort, refresh);

                if (testResults != null)
                {
                    BuildResultsCollection buildResults = new BuildResultsCollection(testResults);
                    return Json(buildResults);
                }

                return NoContent();
            }
            catch (UnauthorizedAccessException uae)
            {
                JObject error = new JObject();
                error.Add("error", uae.Message);
                return Json(error);
            }
        }

        [HttpPost("home/GetJunitTestResult")]
        public ActionResult GetJunitTestResult(string jobName, int buildNumber)
        {
            if (!jobName.Contains("/job/"))
            {
                string[] parts = jobName.Split("/");
                jobName = "";
                foreach(String part in parts)
                {
                    if(part != "")
                    {
                        jobName += "/job/" + part;
                    }
                }
            }

            if (!jobName.Contains(ApiWorker.Jenkins))
            {
                jobName = ApiWorker.Jenkins + jobName;
            }
            JunitTestResults results = _testResultWorker.GetJunitTestCaseInfo(jobName, buildNumber).Result;
            return Json(results);
        }

        [HttpGet]
        public ActionResult<SuccessResult> TriggerBuild(string buildPath)
        {
            try
            {
                bool itWorked = ApiWorker.TriggerBuild(buildPath);

                if (itWorked)
                {
                    _testResultWorker.UpdateBuildInfoAsync(buildPath);
                    return new SuccessResult { Success = true };
                }
                else
                {
                    return new SuccessResult { Success = false };
                }

            }
            catch (Exception e)
            {
                LogWorker.LogError(e);
                return Json(e);
            }

        }


        [HttpPost]
        public ActionResult<TriggerBuildsResult> TriggerBuilds(string[] buildPaths)
        {
            bool itWorked = true;
            int jobsSuccessfullyTriggered = 0;

            if (buildPaths != null && buildPaths.Length > 0)
            {
                foreach (string buildPath in buildPaths)
                {
                    try
                    {
                        if (!ApiWorker.TriggerBuild(buildPath))
                        {
                            _testResultWorker.UpdateBuildInfoAsync(buildPath);
                            itWorked = false;
                        }
                        else
                        {
                            jobsSuccessfullyTriggered++;
                        }
                    }
                    catch (Exception e)
                    {

                        LogWorker.LogError(e);
#if DEBUG
                        Debug.WriteLine(e);
#endif
                    }
                }
            }
            else
            {
                itWorked = false;
            }
            return new TriggerBuildsResult { Success = itWorked, JobsSuccessfullyTriggered = jobsSuccessfullyTriggered };
        }

        [HttpGet]
        public ActionResult<SuccessResult> TriggerAllBuilds(string[] ignore)
        {
            List<BuildResult> testResults = _testResultWorker.GetTestResults();
            bool itWorked = true;
            List<string> ignoreList = ignore.ToList();


            foreach (BuildResult buildResult in testResults)
            {
                string buildPath = "";

                if (!ignoreList.Contains(buildResult.Parent))
                {
                    if (!buildResult.Parent.Equals("main"))
                    {
                        buildPath += buildResult.Parent.Replace(" ", "%20") + "/job/";
                    }

                    buildPath += buildResult.BuildName;
                    try
                    {
                        if (!ApiWorker.TriggerBuild(buildPath))
                        {
                            _testResultWorker.UpdateBuildInfoAsync(buildPath);
                            itWorked = false;
                        }
                    }
                    catch (Exception e)
                    {
                        LogWorker.LogError(e);
                        LogWorker.Log("buildPath: " + buildPath);
                        itWorked = false;
                    }
                }
            }

            return new SuccessResult { Success = itWorked };
        }

        [HttpGet]
        public void CreateScenarioByWeekReport()
        {
            new ReportWorker(_config, _testResultWorker).CreateScenariosByWeekCSV();
        }

        [HttpGet]
        public string GetJenkinsHome()
        {
            return WorkerBase.JENKINS_JOBS_DIR;
        }
    }
}
