using Lightwell_Testing_Dashboard_2.Models;
using Lightwell_Testing_Dashboard_2.Workers;
using System.Collections.Generic;
using System;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json.Linq;
using System.Net;
using Microsoft.Extensions.Configuration;

namespace Lightwell_Testing_Dashboard_2.Controllers
{
    public class SummaryController : Controller
    {
        private readonly IConfiguration _config;
        private TestResultWorker _testResultWorker;

        public SummaryController(IConfiguration config, TestResultWorker testResultWorker)
        {
            _config = config;
            _testResultWorker = testResultWorker;
        }

        public IActionResult Index()
        {
            return View();
        }

        [HttpPost("summary/UpdateDescription")]
        public HttpStatusCode UpdateDescription(string buildPath, string updatedDescription)
        {
            var formParams = new Dictionary<string, string>
            {
                {"description",updatedDescription }
            };
            var response = ApiWorker.CallApi(buildPath + ApiWorker.DESCRIPTION_API, true, true, formParams);
            _testResultWorker.UpdateBuildInfoAsync(buildPath);
            return response.Response.StatusCode;
        }
    }
}
