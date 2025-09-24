using System;
using System.Collections.Generic;
using System.Xml;
using Lightwell_Testing_Dashboard_2.Models;
using Microsoft.Extensions.Configuration;

namespace Lightwell_Testing_Dashboard_2.Workers
{
    public class JobsWorker : WorkerBase
    {
        readonly IConfiguration _configuration;
        TestResultWorker _testResultWorker;

        public JobsWorker(IConfiguration configuration, TestResultWorker testResultWorker) : base(configuration) 
        {
            _configuration = configuration;
            _testResultWorker = testResultWorker;
        }

        public XmlDocument GetJobConfigurationXml(string jobPath)
        {
            return ApiWorker.GetXmlFromApi(jobPath + "/config.xml");
        }

        public List<JenkinsBuild> GetJenkinsJobsAsync()
        {
            return new TestResultWorker(_configuration).GetJenkinsJobsAsync().Result;
        }
    }
}
