using Lightwell_Testing_Dashboard_2.Models;
using System.Collections.Generic;
using Lightwell_Testing_Dashboard_2.Workers;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using System.Xml;

namespace Lightwell_Testing_Dashboard_2.Controllers
{
    public class JobsController : Controller
    {
        private readonly IConfiguration _config;
        TestResultWorker _testResultWorker;

        public JobsController(IConfiguration config,TestResultWorker testResultWorker)
        {
            _config = config;
            _testResultWorker = testResultWorker;
        }

        private JobsWorker _jobsWorker;
        public JobsWorker JobsWorker 
        { 
            get
            {
                if(_jobsWorker == null)
                {
                    _jobsWorker = new JobsWorker(_config,_testResultWorker);
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
            string jobUrl = "/job/" + jobPath;
            string fullBuildUrl = jobUrl + "/" + buildNumber + "/stop";
            ApiResponse result = ApiWorker.PostApi(fullBuildUrl, true,true);
            if (result.Response == null)
            {
                //apparently no news is good news
                _testResultWorker.UpdateBuildInfoAsync(jobUrl);
                return Ok(new { message = "Job stopped successfully", success = true });
            }
            else
            {
                return StatusCode(((int)result.Response.StatusCode));
            }
        }
    }
}
