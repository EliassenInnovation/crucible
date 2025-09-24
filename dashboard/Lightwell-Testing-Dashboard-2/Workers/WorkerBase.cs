using Microsoft.Extensions.Configuration;
using System;
using System.IO;
using System.Runtime.InteropServices;

namespace Lightwell_Testing_Dashboard_2.Workers
{
    public class WorkerBase
    {
        public const string JOBS_DIR = "jobs";

        private static string _jenkins_home;
        public static string JENKINS_HOME
        {
            get
            {
                if (string.IsNullOrEmpty(_jenkins_home))
                {
                    if (System.Runtime.InteropServices.RuntimeInformation.IsOSPlatform(OSPlatform.Windows))
                    {
                        _jenkins_home = Environment.GetEnvironmentVariable("JENKINS_HOME");
                    }
                    else
                    {
                        _jenkins_home = Config.GetSection("jenkins").GetSection("jenkins_home_linux").Value;
                    }
                }
                return _jenkins_home;
            }
        }

        public static string JENKINS_JOBS_DIR
        {
            get
            {
                return JENKINS_HOME + Path.DirectorySeparatorChar + JOBS_DIR;
            }
        }

        private static IConfiguration _config;

        protected WorkerBase(IConfiguration config)
        {
            _config = config;
        }

        public static IConfiguration Config
        {
            get
            {
                if(_config == null)
                {
                    IConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
                    // Duplicate here any configuration sources you use.
                    configurationBuilder.AddJsonFile("appsettings.json");
                    _config = configurationBuilder.Build();
                }

                return _config;
            }
        }
    }
}
