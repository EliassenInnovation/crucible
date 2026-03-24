using Lightwell_Testing_Dashboard_2.Models;
using System.Collections.Generic;
using Lightwell_Testing_Dashboard_2.Workers;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using System.Xml;
using System;

namespace Lightwell_Testing_Dashboard_2.Controllers
{
    public class JobsController : Controller
    {
        private readonly IConfiguration _config;
        TestResultWorker _testResultWorker;

        public JobsController(IConfiguration config, TestResultWorker testResultWorker)
        {
            _config = config;
            _testResultWorker = testResultWorker;
        }

        private JobsWorker _jobsWorker;
        public JobsWorker JobsWorker
        {
            get
            {
                if (_jobsWorker == null)
                {
                    _jobsWorker = new JobsWorker(_config, _testResultWorker);
                }
                return _jobsWorker;
            }
        }

        public IActionResult Index()
        {
            return View();
        }

        /// <summary>
        /// Get all Jenkins builds
        /// </summary>
        /// <returns></returns>
        [HttpPost("/jobs/AllJobs")]
        public IActionResult AllJobs()
        {
            List<JenkinsBuild> jobs = JobsWorker.GetJenkinsJobsAsync();

            return Json(jobs);
        }

        [HttpPost("/jobs/jobconfig")]
        public XmlDocument GetSpecificJobConfiguration(string jobPath)
        {
            return JobsWorker.GetJobConfigurationXml(jobPath);
        }

        [HttpPost("/jobs/stopjob")]
        public IActionResult StopJob(string jobPath, string buildNumber)
        {
            try
            {
                string jobUrl = "/job/" + jobPath;
                string fullBuildUrl = jobUrl + "/" + buildNumber + "/stop";
                ApiResponse result = ApiWorker.PostApi(fullBuildUrl, true, true);

                // Check if the API call was successful (you might need to adjust this condition)
                if (result.Response == null || result.Response.StatusCode == System.Net.HttpStatusCode.OK)
                {
                    _testResultWorker.UpdateBuildInfoAsync(jobPath);
                    return Json(new
                    {
                        ok = true,
                        message = "Job stopped successfully",
                        success = true
                    });
                }
                else
                {
                    int statusCode = (int)result.Response.StatusCode;
                    return Json(new
                    {
                        ok = false,
                        error = $"API call failed with status code: {statusCode}",
                        statusCode = statusCode
                    });
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
                return Json(new
                {
                    ok = false,
                    error = "Internal server error",
                    details = e.Message
                });
            }
        }
    }
}
